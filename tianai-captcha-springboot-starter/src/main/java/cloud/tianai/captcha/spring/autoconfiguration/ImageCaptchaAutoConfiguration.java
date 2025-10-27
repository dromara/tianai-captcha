package cloud.tianai.captcha.spring.autoconfiguration;


import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.interceptor.CaptchaInterceptorGroup;
import cloud.tianai.captcha.interceptor.impl.ParamCheckCaptchaInterceptor;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceProviders;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import cloud.tianai.captcha.spring.plugins.SpringMultiImageCaptchaGenerator;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 9:49
 * @Description 滑块验证码自动装配
 */
@Slf4j
@Order
@Configuration
@AutoConfigureAfter(CacheStoreAutoConfiguration.class)
@EnableConfigurationProperties({SpringImageCaptchaProperties.class})
public class ImageCaptchaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ResourceStore resourceStore() {
        return new LocalMemoryResourceStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImageCaptchaResourceManager imageCaptchaResourceManager(ResourceStore resourceStore) {
        ResourceProviders resourceProviders = new ResourceProviders();
        return new DefaultImageCaptchaResourceManager(resourceStore,resourceProviders);
    }

    @Bean
    @ConditionalOnMissingBean
    public ImageTransform imageTransform() {
        return new Base64ImageTransform();
    }


    @Bean
    @ConditionalOnMissingBean
    public ImageCaptchaGenerator imageCaptchaTemplate(SpringImageCaptchaProperties prop,
                                                      ImageCaptchaResourceManager captchaResourceManager,
                                                      ImageTransform imageTransform,
                                                      BeanFactory beanFactory) {
        return new SpringMultiImageCaptchaGenerator(captchaResourceManager, imageTransform, beanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public ImageCaptchaValidator imageCaptchaValidator() {
        return new SimpleImageCaptchaValidator();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public CaptchaInterceptor captchaInterceptor() {
        CaptchaInterceptorGroup group = new CaptchaInterceptorGroup();
        group.addInterceptor(new ParamCheckCaptchaInterceptor());
//        group.addInterceptor(new BasicTrackCaptchaInterceptor());
        return group;
    }


    @Bean
    @ConditionalOnMissingBean
    public ImageCaptchaApplication imageCaptchaApplication(ImageCaptchaGenerator captchaGenerator,
                                                           ImageCaptchaValidator imageCaptchaValidator,
                                                           CacheStore cacheStore,
                                                           ResourceStore resourceStore,
                                                           SpringImageCaptchaProperties prop,
                                                           CaptchaInterceptor captchaInterceptor,
                                                           ApplicationContext applicationContext) {
        TACBuilder tacBuilder = TACBuilder.builder(resourceStore)
                .setGenerator(captchaGenerator)
                .setValidator(imageCaptchaValidator)
                .setCacheStore(cacheStore)
                .setProp(prop)
                .setInterceptor(captchaInterceptor);

        if (prop.getInitDefaultResource()) {
            tacBuilder.addDefaultTemplate(prop.getDefaultResourcePrefix());
        }
        if (!CollectionUtils.isEmpty(prop.getFontPath())) {
            // 读取字体包
            for (String fontPath : prop.getFontPath()) {
                int index = fontPath.indexOf(":");
                String[] split = index > 0 ? new String[]{fontPath.substring(0, index), fontPath.substring(index + 1)} : new String[]{"", fontPath};
                String type = split[0];
                String path = split[1];
                tacBuilder.addFont(new cloud.tianai.captcha.resource.common.model.dto.Resource(type, path));
            }
        }
        ImageCaptchaApplication target = tacBuilder.build();
        if (prop.getSecondary() != null && Boolean.TRUE.equals(prop.getSecondary().getEnabled())) {
            // 一个简单的二次验证
            target = new SecondaryVerificationApplication(target, prop.getSecondary());
        }
        return target;
    }

}
