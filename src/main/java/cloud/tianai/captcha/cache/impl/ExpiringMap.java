package cloud.tianai.captcha.cache.impl;

import lombok.Data;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface ExpiringMap<K, V> extends Map<K, V> {
    /**
     * 默认-1 无超时时间.
     */
    Long DEFAULT_EXPIRE = -1L;

    /**
     * 添加值
     * @param k key
     * @param v value
     * @param timeout 超时时间，
     * @param timeUnit 超时时间单位
     * @return 返回旧的数据，如果没有，返回null
     */
    TimeMapEntity<K, V> put(K k, V v, Long timeout, TimeUnit timeUnit);

    /**
     * 获取value值
     * @param k key
     * @return
     */
    Optional<TimeMapEntity<K, V>> getData(K k);

    /**
     * 获取某个key的过期时间
     * @param k key
     * @return 单位毫秒
     */
    Long getExpire(K k);

    /**
     * 增加过期时间
     * @param k key
     * @param expire 过期时间
     * @param timeUnit 超时时间单位
     * @return
     */
    boolean incr(K k, Long expire,  TimeUnit timeUnit);

    /**
     * 初始化
     */
    void init();

    @Data
    class TimeMapEntity<K, V> {
        private K key;
        private V value;
        private Long expire;
        private Long createTime;
        private long timeout = -1;

        TimeMapEntity(K k, V value, Long expire, Long createTime) {
            this.key = k;
            this.value = value;
            this.expire = expire;
            this.createTime = createTime;
            if (expire > 0) {
                this.timeout = createTime + expire;
            }
        }

        public TimeMapEntity(TimeMapEntity<K, V> entity) {
            this.key = entity.getKey();
            this.value = entity.getValue();
            this.expire = entity.getExpire();
            this.createTime = entity.getCreateTime();
        }
    }
}
