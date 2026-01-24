package cloud.tianai.captcha.application;

import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.cache.StoreCacheKeyPrefix;
import cloud.tianai.captcha.cache.impl.LocalCacheStore;
import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.interceptor.EmptyCaptchaInterceptor;
import cloud.tianai.captcha.resource.*;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: 天爱有情
 * @date 2024/7/14 16:41
 * @Description 一个构建ImageCaptchaApplication的工具, 免去一些繁琐的配置，方便新手用户一键使用
 */
public class TACBuilder {

    private CacheStore cacheStore;
    private ImageCaptchaGenerator generator;
    private ImageCaptchaValidator validator;
    private CaptchaInterceptor interceptor = EmptyCaptchaInterceptor.INSTANCE;
    private ImageCaptchaProperties prop = new ImageCaptchaProperties();
    private ResourceStore resourceStore;
    private ImageTransform imageTransform;
    //    private List<FontWrapper> fontWrappers = new ArrayList<>();
    private Map<String, Resource> resourceCache;
    private Map<String, ResourceMap> templateCache;

    public static TACBuilder builder() {
        return new TACBuilder();
    }


    private TACBuilder() {
    }

    public TACBuilder setResourceStore(ResourceStore resourceStore) {
        this.resourceStore = resourceStore;
        return this;
    }

    public TACBuilder addDefaultTemplate(String defaultPathPrefix) {
        DefaultBuiltInResources defaultBuiltInResources = new DefaultBuiltInResources(defaultPathPrefix);
        defaultBuiltInResources.addDefaultTemplate(resourceStore);
        return this;
    }

    public TACBuilder addDefaultTemplate() {
        return addDefaultTemplate(DefaultBuiltInResources.PATH_PREFIX);
    }

    public TACBuilder setCacheStore(CacheStore cacheStore) {
        this.cacheStore = cacheStore;
        return this;
    }

    public TACBuilder setGenerator(ImageCaptchaGenerator generator) {
        this.generator = generator;
        return this;
    }

    public TACBuilder setValidator(ImageCaptchaValidator validator) {
        this.validator = validator;
        return this;
    }

    public TACBuilder setInterceptor(CaptchaInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public TACBuilder addFont(Resource resource) {
        this.addResource(FontCache.FONT_TYPE, resource);
        return this;
    }


    public TACBuilder cached(int size, int waitTime, int period, Long expireTime) {
        cached(size, waitTime, period, expireTime, null);
        return this;
    }

    public TACBuilder cached(int size, int waitTime, int period, Long expireTime, Set<String> ignoredCacheFields) {
        prop.setLocalCacheEnabled(true);
        prop.setLocalCacheSize(size);
        prop.setLocalCacheWaitTime(waitTime);
        prop.setLocalCachePeriod(period);
        prop.setLocalCacheExpireTime(expireTime);
        prop.setLocalCacheIgnoredCacheFields(ignoredCacheFields);
        return this;
    }

    public TACBuilder prefix(String prefix) {
        this.prop.setPrefix(prefix);
        return this;
    }

    public TACBuilder expire(String captchaType, Long expireTime) {
        prop.getExpire().put(captchaType, expireTime);
        return this;
    }

    public TACBuilder setProp(ImageCaptchaProperties prop) {
        this.prop = prop;
        return this;
    }

//    public TACBuilder setResourceStore(ResourceStore resourceStore) {
//        this.resourceStore = resourceStore;
//        return this;
//    }


    public TACBuilder addResource(String captchaType, Resource imageResource) {
//        if (resourceStore instanceof CrudResourceStore) {
//            ((CrudResourceStore) resourceStore).addResource(captchaType, imageResource);
//        }
        cacheResource(captchaType, imageResource);
        return this;
    }

    public TACBuilder addTemplate(String captchaType, ResourceMap resourceMap) {
//        if (resourceStore instanceof CrudResourceStore) {
//            ((CrudResourceStore) resourceStore).addTemplate(captchaType, resourceMap);
//        }
        cacheTemplate(captchaType, resourceMap);
        return this;
    }

    public TACBuilder setTransform(ImageTransform imageTransform) {
        this.imageTransform = imageTransform;
        return this;
    }

    public ImageCaptchaApplication build() {
        if (cacheStore == null) {
            cacheStore = new LocalCacheStore();
        }
        if (resourceStore == null) {
            resourceStore = new LocalMemoryResourceStore();
        }
        if (resourceStore instanceof CrudResourceStore) {
            CrudResourceStore crudResourceStore = (CrudResourceStore) resourceStore;
            if (!CollectionUtils.isEmpty(resourceCache)) {
                resourceCache.forEach(crudResourceStore::addResource);
                resourceCache = null;
            }
            if (!CollectionUtils.isEmpty(templateCache)) {
                templateCache.forEach(crudResourceStore::addTemplate);
                templateCache = null;
            }
        }
        if (generator == null) {
            ResourceProviders resourceProviders = new ResourceProviders();
            DefaultImageCaptchaResourceManager resourceManager = new DefaultImageCaptchaResourceManager(resourceStore, resourceProviders);
            generator = new MultiImageCaptchaGenerator(resourceManager, imageTransform);
        }
//        if (generator instanceof MultiImageCaptchaGenerator) {
//            ((MultiImageCaptchaGenerator) generator).setFontWrappers(fontWrappers);
//        }
        if (validator == null) {
            validator = new SimpleImageCaptchaValidator();
        }
        if (interceptor == null) {
            interceptor = EmptyCaptchaInterceptor.INSTANCE;
        }
        // 增加前缀处理接口
        DefaultImageCaptchaApplication application = new DefaultImageCaptchaApplication(generator, validator, cacheStore, prop, interceptor);
        return application;
    }

    private void cacheResource(String captchaType, Resource imageResource) {
        if (resourceCache == null) {
            resourceCache = new LinkedHashMap<>(8);
        }
        resourceCache.put(captchaType, imageResource);
    }

    private void cacheTemplate(String captchaType, ResourceMap resourceMap) {
        if (templateCache == null) {
            templateCache = new LinkedHashMap<>(8);
        }
        templateCache.put(captchaType, resourceMap);
    }
}
