package cloud.tianai.captcha.spring4.store.impl;

import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.common.AnyMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 天爱有情
 * @date 2022/3/2 14:42
 * @Description redis实现的缓存
 */
public class RedisCacheStore implements CacheStore {

    private static final RedisScript<String> SCRIPT_GET_CACHE = new DefaultRedisScript<>("local res = redis.call('get',KEYS[1])  if res == nil  then return nil  else  redis.call('del',KEYS[1]) return res end", String.class);
    protected StringRedisTemplate redisTemplate;
    private Gson gson = new Gson();

    public RedisCacheStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public AnyMap getCache(String key) {
        String jsonData = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(jsonData)) {
            return null;
        }
        return gson.fromJson(jsonData, new TypeToken<AnyMap>() {
        }.getType());
    }

    @Override
    public AnyMap getAndRemoveCache(String key) {
        String json = redisTemplate.execute(SCRIPT_GET_CACHE, Collections.singletonList(key));
        if (org.apache.commons.lang3.StringUtils.isBlank(json)) {
            return null;
        }
        return gson.fromJson(json, new TypeToken<AnyMap>() {
        }.getType());
    }

    @Override
    public boolean setCache(String key, AnyMap data, Long expire, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, gson.toJson(data), expire, timeUnit);
        return true;
    }

    @Override
    public Long incr(String key, long delta, Long expire, TimeUnit timeUnit) {
        Long increment = redisTemplate.opsForValue().increment(key, delta);
        redisTemplate.expire(key, expire, timeUnit);
        return increment;
    }

    @Override
    public Long getLong(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return Long.valueOf(value);
    }
}
