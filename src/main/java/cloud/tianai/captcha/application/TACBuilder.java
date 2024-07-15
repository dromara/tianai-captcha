package cloud.tianai.captcha.application;

import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.cache.impl.LocalCacheStore;
import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.FontWrapper;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.interceptor.EmptyCaptchaInterceptor;
import cloud.tianai.captcha.resource.DefaultBuiltInResources;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
    private List<FontWrapper> fontWrappers = new ArrayList<>();

    public static TACBuilder builder() {
        TACBuilder builder = new TACBuilder();
        // 默认设置本地的
        LocalMemoryResourceStore resourceStore = new LocalMemoryResourceStore();
        builder.resourceStore = resourceStore;
        builder.prop = new ImageCaptchaProperties();
        return builder;
    }

    private TACBuilder() {
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

    public TACBuilder addFont(FontWrapper fontWrapper) {
        this.fontWrappers.add(fontWrapper);
        return this;
    }

    public TACBuilder addFont(Font font) {
        return addFont(new FontWrapper(font));
    }


    public TACBuilder cached(int size, int waitTime, int period, Long expireTime) {
        prop.setLocalCacheEnabled(true);
        prop.setLocalCacheSize(size);
        prop.setLocalCacheWaitTime(waitTime);
        prop.setLocalCachePeriod(period);
        prop.setLocalCacheExpireTime(expireTime);
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

    public TACBuilder setResourceStore(ResourceStore resourceStore) {
        this.resourceStore = resourceStore;
        return this;
    }


    public TACBuilder addResource(String captchaType, Resource imageResource) {
        this.resourceStore.addResource(captchaType, imageResource);
        return this;
    }

    public TACBuilder addTemplate(String captchaType, ResourceMap resourceMap) {
        this.resourceStore.addTemplate(captchaType, resourceMap);
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
        if (generator == null) {
            DefaultImageCaptchaResourceManager resourceManager = new DefaultImageCaptchaResourceManager(resourceStore);
            generator = new MultiImageCaptchaGenerator(resourceManager, imageTransform);
        }
        if (generator instanceof MultiImageCaptchaGenerator) {
            if (CollectionUtils.isEmpty(fontWrappers)) {
                // 添加默认字体
                try {
                    ClassPathResourceProvider resourceProvider = new ClassPathResourceProvider();
                    InputStream stream = resourceProvider.getResourceInputStream(new Resource("classpath", "META-INF/fonts/SIMSUN.TTC"));
                    Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
                    stream.close();
                    fontWrappers.add(new FontWrapper(font));
                } catch (Exception e) {
                    throw new RuntimeException("读取默认字体包报错",e);
                }
            }
            ((MultiImageCaptchaGenerator) generator).setFontWrappers(fontWrappers);
        }
        if (validator == null) {
            validator = new SimpleImageCaptchaValidator();
        }
        if (interceptor == null) {
            interceptor = EmptyCaptchaInterceptor.INSTANCE;
        }

        DefaultImageCaptchaApplication application = new DefaultImageCaptchaApplication(generator, validator, cacheStore, prop, interceptor);
        return application;
    }
}
