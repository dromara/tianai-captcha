package cloud.tianai.captcha.cache;

import cloud.tianai.captcha.common.AnyMap;

import java.util.concurrent.TimeUnit;

/**
 * @Author: 天爱有情
 * @date 2022/3/2 14:35
 * @Description 提取出用于缓存的接口
 */
public interface CacheStore {

    /**
     * 读取缓存数据通过key
     *
     * @param key key
     * @return AnyMap
     */
    AnyMap getCache(String key);

    /**
     * 获取并删除数据 通过key
     *
     * @param key key
     * @return AnyMap
     */
    AnyMap getAndRemoveCache(String key);

    /**
     * 添加缓存数据
     *
     * @param key      key
     * @param data     data
     * @param expire   过期时间
     * @param timeUnit 过期时间单位
     * @return boolean
     */
    boolean setCache(String key, AnyMap data, Long expire, TimeUnit timeUnit);


    /**
     * incr 数字
     *
     * @param key      key
     * @param delta    境量
     * @param expire   过期时间
     * @param timeUnit 过期时间单位
     * @return Long
     */
    Long incr(String key, long delta, Long expire, TimeUnit timeUnit);

    /**
     * get 数字
     *
     * @param key key
     * @return Long
     */
    Long getLong(String key);
}
