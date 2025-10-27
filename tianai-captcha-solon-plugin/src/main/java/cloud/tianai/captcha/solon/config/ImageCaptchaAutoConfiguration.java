package cloud.tianai.captcha.solon.config;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.cache.impl.LocalCacheStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.solon.plugins.secondary.SecondaryVerificationApplication;
import cloud.tianai.captcha.solon.properties.CaptchaProperties;
import cloud.tianai.captcha.solon.service.CaptchaRedisCacheService;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * @Author XT
 * @Date 2024.09.03
 */
@Configuration
public class ImageCaptchaAutoConfiguration {

    @Bean
    public ImageCaptchaApplication imageCaptchaApplication(CaptchaProperties captchaProperties, @Inject(required = false) CaptchaRedisCacheService cacheService) {
        TACBuilder tacBuilder = TACBuilder.builder();
        tacBuilder.addDefaultTemplate();
        tacBuilder.expire("default", captchaProperties.getExpire());
        tacBuilder.prefix(captchaProperties.getPrefix());
        // 注入背景图资源
        if (captchaProperties.getResources().getAuto()) {
            String[] split = captchaProperties.getResources().getAutoType().split(",");
            List<String> wordImageClickList = captchaProperties.getResources().getImages();
            for (String type : split) {
                for (String path : wordImageClickList) {
                    tacBuilder.addResource(type, new Resource("classpath", path));
                }
            }
        } else {
            List<String> wordImageClickList = captchaProperties.getResources().getWORD_IMAGE_CLICK();
            if (!wordImageClickList.isEmpty()) {
                for (String path : wordImageClickList) {
                    tacBuilder.addResource("WORD_IMAGE_CLICK", new Resource("classpath", path));
                }
            }
            List<String> concatList = captchaProperties.getResources().getCONCAT();
            if (!concatList.isEmpty()) {
                for (String path : concatList) {
                    tacBuilder.addResource("CONCAT", new Resource("classpath", path));
                }
            }
            List<String> sliderList = captchaProperties.getResources().getSLIDER();
            if (!sliderList.isEmpty()) {
                for (String path : sliderList) {
                    tacBuilder.addResource("SLIDER", new Resource("classpath", path));
                }
            }
            List<String> rotateList = captchaProperties.getResources().getROTATE();
            if (!rotateList.isEmpty()) {
                for (String path : rotateList) {
                    tacBuilder.addResource("ROTATE", new Resource("classpath", path));
                }
            }
        }

        // 注入字体包
        if (null != captchaProperties.getFontPath()) {
            List<String> fontPathList = captchaProperties.getFontPath();
            if (!fontPathList.isEmpty()) {
                for (String path : fontPathList) {
                    try {
                        tacBuilder.addFont(new Resource("classpath", path));
                    } catch (Exception e) {
                        throw new RuntimeException("读取字体包失败，path=" + path, e);
                    }
                }
            }
        }

        CacheStore cacheStore = cacheService;
        // 注入缓存器
        if (null == cacheStore) {
            cacheStore = new LocalCacheStore();
        }
        tacBuilder.setCacheStore(cacheStore);
        ImageCaptchaApplication target = tacBuilder.build();

        // 二次验证
        if (captchaProperties.getSecondary().getEnabled()) {
            target = new SecondaryVerificationApplication(target, captchaProperties, cacheStore);
        }
        return target;
    }

}
