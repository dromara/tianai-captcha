package cloud.tianai.captcha.spring.autoconfiguration;

import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.cache.impl.LocalCacheStore;
import cloud.tianai.captcha.spring.store.impl.RedisCacheStore;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 缓存存储器的自动配置类
 *
 * @author Hccake
 */
@AutoConfigureAfter({RedisAutoConfiguration.class})
@Configuration(proxyBeanMethods = false)
public class CacheStoreAutoConfiguration {

    /**
     * RedisCacheStoreConfiguration
     *
     * @author 天爱有情
     * @since 2020/10/27 14:06
     */
    @Order(1)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(StringRedisTemplate.class)
    public static class RedisCacheStoreConfiguration {

        @Bean
        @ConditionalOnBean(StringRedisTemplate.class)
        @ConditionalOnMissingBean(CacheStore.class)
        public CacheStore redis(StringRedisTemplate redisTemplate) {
            return new RedisCacheStore(redisTemplate);
        }

    }

    /**
     * LocalCacheStoreConfiguration
     *
     * @author 天爱有情
     * @since 2020/10/27 14:06
     */
    @Order(2)
    @Configuration(proxyBeanMethods = false)
    public static class LocalCacheStoreConfiguration {

        @Bean
        @ConditionalOnMissingBean(CacheStore.class)
        public CacheStore local() {
            return new LocalCacheStore();
        }

    }

}
