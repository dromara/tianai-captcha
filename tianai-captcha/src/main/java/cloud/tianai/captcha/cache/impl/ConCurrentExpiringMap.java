package cloud.tianai.captcha.cache.impl;


import cloud.tianai.captcha.common.util.NamedThreadFactory;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @Author: 天爱有情
 * @date 2020/10/12 10:02
 */
@Slf4j
@Accessors(chain = true)
public class ConCurrentExpiringMap<K, V> implements ExpiringMap<K, V>, AutoCloseable {

    private ConcurrentHashMap<K, TimeMapEntity<K, V>> storage;
    private SortedMap<Long, LinkedList<K>> sortedMap = new ConcurrentSkipListMap<>();
    private final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("expiring-map-expire"));
    public static final int LIMIT = 500;

    public ConCurrentExpiringMap() {
        this(128);
    }

    @Override
    public void init() {
        scheduledExecutor.scheduleAtFixedRate(new ExpireThread(), 5, 5, TimeUnit.SECONDS);
    }

    public ConCurrentExpiringMap(Integer initialCapacity) {
        storage = new ConcurrentHashMap<>(initialCapacity);

    }

    @Override
    public TimeMapEntity<K, V> put(K k, V v, Long expire, TimeUnit timeUnit) {
        if (expire == null || expire < 1) {
            expire = DEFAULT_EXPIRE;
        }
        TimeMapEntity<K, V> entity;
        if (expire != null && expire > 0) {
            entity = new TimeMapEntity<>(k, v, timeUnit.toNanos(expire), System.nanoTime());
            sortedMap.computeIfAbsent(entity.getTimeout(), (k1) -> new LinkedList<>()).add(k);
        } else {
            entity = new TimeMapEntity<>(k, v, DEFAULT_EXPIRE, System.nanoTime());
        }
        TimeMapEntity<K, V> old = storage.put(k, entity);
        return old;
    }

    @Override
    public Optional<TimeMapEntity<K, V>> getData(K k) {
        return Optional.ofNullable(storage.get(k));
    }

    @Override
    public Long getExpire(K k) {
        return getData(k).map(TimeMapEntity::getExpire).orElse(DEFAULT_EXPIRE);
    }

    @Override
    public boolean incr(K k, Long expire, TimeUnit timeUnit) {
        Optional<TimeMapEntity<K, V>> entityOptional = getData(k);
        if (!entityOptional.isPresent()) {
            return false;
        }
        synchronized (k) {
            // 双重校验
            entityOptional = getData(k);
            if (!entityOptional.isPresent()) {
                return false;
            }
            TimeMapEntity<K, V> entity = entityOptional.get();

            TimeMapEntity<K, V> newEntity = entity;
            newEntity.setExpire(entity.getExpire() + expire);
            if (expire != null && expire > 0) {
                sortedMap.getOrDefault(k, new LinkedList<>()).add(k);
            }
            return true;
        }
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public boolean isEmpty() {
        return storage.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return storage.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        Collection<TimeMapEntity<K, V>> values = storage.values();
        Optional<TimeMapEntity<K, V>> any = values.stream().filter(v -> v.getValue().equals(value)).findAny();
        return any.isPresent();
    }

    @Override
    public V get(Object key) {
        TimeMapEntity<K, V> timeMapEntity = storage.get(key);
        if (isTimeout(timeMapEntity)) {
            removeData(key);
            return null;
        }
        return timeMapEntity.getValue();
    }

    protected boolean isTimeout(K key) {
        Optional<TimeMapEntity<K, V>> data = getData(key);
        return isTimeout(data.orElse(null));
    }

    protected boolean isTimeout(TimeMapEntity<K, V> timeMapEntity) {
        if (timeMapEntity == null || timeMapEntity.getExpire() < 1) {
            return true;
        }
        long currentTimeMillis = System.nanoTime();
        long timeout = timeMapEntity.getTimeout();
        return timeout < currentTimeMillis;
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, DEFAULT_EXPIRE, null).getValue();
    }

    @Override
    public V remove(Object key) {
        return removeData(key).map(TimeMapEntity::getValue).orElse(null);
    }

    protected Optional<TimeMapEntity<K, V>> removeData(Object key) {
        synchronized (key) {
            TimeMapEntity<K, V> oldValue = storage.get(key);
            if (oldValue != null) {
                TimeMapEntity<K, V> entity = storage.remove(key);
                Long expire = oldValue.getExpire();
                if (expire != null && expire > 0) {
                    LinkedList<K> ks = sortedMap.get(expire);
                    if (ks != null) {
                        ks.remove(key);
                    }
                }
                if (entity != null) {
                    return Optional.of(entity);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        Map<K, TimeMapEntity<K, V>> copyStorage = new HashMap<>(storage);
        storage.clear();
        sortedMap.clear();
    }

    /**
     * 这个可能会消耗点cpu
     *
     * @return
     */
    @Override
    public Set<K> keySet() {
        return storage.keySet()
                .stream()
                .parallel()
                .filter(k -> !isTimeout(k))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<V> values() {
        return storage.values().stream().map(TimeMapEntity::getValue).collect(Collectors.toSet());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new IllegalArgumentException("timemap not impl entrySet.");
    }

    /**
     * 销毁方法，关闭定时任务线程池
     * 应该在不再使用此 Map 时调用，以防止线程泄露
     *
     * @since 0.0.3
     */
    public void destroy() {
        try {
            scheduledExecutor.shutdown();
            if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
                if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("ConCurrentExpiringMap 定时任务线程池未能正常关闭");
                }
            }
        } catch (InterruptedException e) {
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
            log.warn("ConCurrentExpiringMap 定时任务线程池关闭时被中断", e);
        }
    }

    /**
     * 实现 AutoCloseable 接口，支持 try-with-resources 语法
     * 调用 destroy() 方法释放资源
     */
    @Override
    public void close() {
        destroy();
    }

    /**
     * 定时执行任务
     *
     * @since 0.0.3
     */
    private class ExpireThread implements Runnable {
        @Override
        public void run() {
            SortedMap<Long, LinkedList<K>> expireMap = ConCurrentExpiringMap.this.sortedMap;
            int limit = ConCurrentExpiringMap.LIMIT;
            //1.判断是否为空
            if (expireMap == null || expireMap.size() < 1) {
                return;
            }
            log.debug("storage-size: {}", ConCurrentExpiringMap.this.storage.size());
            log.debug("expire-size: {}", expireMap.size());
            //2. 获取 key 进行处理
            int count = 0;
            LinkedList<Long> removeKeys = null;
            // 删除的逻辑处理
            long currentTime = System.nanoTime();
            if (currentTime < expireMap.firstKey()) {
                return;
            }
            for (Entry<Long, LinkedList<K>> entry : expireMap.entrySet()) {
                final Long expireAt = entry.getKey();
                LinkedList<K> expireKeys = entry.getValue();
                // 判断队列是否为空
                if (expireKeys == null || expireKeys.size() < 1) {
                    if (removeKeys == null) {
                        removeKeys = new LinkedList<>();
                    }
                    removeKeys.add(expireAt);
                    continue;
                }
                if (count >= limit) {
                    // 检索数量达到z最大值，直接跳出
                    break;
                }

                if (currentTime >= expireAt) {
                    Iterator<K> iterator = expireKeys.iterator();
                    while (iterator.hasNext()) {
                        K key = iterator.next();
                        // 先移除本身
                        iterator.remove();
                        // 再移除缓存，后续可以通过惰性删除做补偿
                        ConCurrentExpiringMap.this.get(key);
                        if (removeKeys == null) {
                            removeKeys = new LinkedList<>();
                        }
                        removeKeys.add(expireAt);
                        count++;
                    }
                }
            }
            if (removeKeys != null && removeKeys.size() > 0) {
                for (Long removeKey : removeKeys) {
                    expireMap.remove(removeKey);
                }
            }
        }
    }
}
