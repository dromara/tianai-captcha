package cloud.tianai.captcha.cache.impl;


import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.common.AnyMap;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 天爱有情
 * @date 2022/3/2 14:39
 * @Description 本地缓存
 */
public class LocalCacheStore implements CacheStore {

    protected ExpiringMap<String, AnyMap> cache;

    public LocalCacheStore() {
        cache = new ConCurrentExpiringMap<>();
        cache.init();
    }

    @Override
    public AnyMap getCache(String key) {
        return cache.get(key);
    }

    @Override
    public AnyMap getAndRemoveCache(String key) {
        return cache.remove(key);
    }

    @Override
    public boolean setCache(String key, AnyMap data, Long expire, TimeUnit timeUnit) {
        cache.remove(key);
        cache.put(key, data, expire, timeUnit);
        return true;
    }

    @Override
    public Long incr(String key, long delta, Long expire, TimeUnit timeUnit) {
        Map<String, Object> value = cache.remove(key);
        if (value != null) {
            Long incr = (Long) value.get("___incr___");
            if (incr == null) {
                incr = 0L;
            }
            incr += delta;
            cache.put(key, AnyMap.of(Collections.singletonMap("___incr___", incr)), expire, timeUnit);
            return incr;
        }
        cache.put(key, AnyMap.of(Collections.singletonMap("___incr___", delta)), expire, timeUnit);
        return delta;
    }

    @Override
    public Long getLong(String key) {
        Map<String, Object> stringObjectMap = cache.get(key);
        if (stringObjectMap != null) {
            return (Long) stringObjectMap.get("___incr___");
        }
        return null;
    }
}
