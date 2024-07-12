package example.readme;

import cloud.tianai.captcha.application.DefaultImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaProperties;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.cache.impl.LocalCacheStore;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.interceptor.CaptchaInterceptorGroup;
import cloud.tianai.captcha.interceptor.EmptyCaptchaInterceptor;
import cloud.tianai.captcha.interceptor.impl.BasicTrackCaptchaInterceptor;
import cloud.tianai.captcha.interceptor.impl.ParamCheckCaptchaInterceptor;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator;

public class ApplicationTest {

    public static void main(String[] args) {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageCaptchaGenerator generator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager);
        generator.init(true);
        ImageCaptchaValidator imageCaptchaValidator = new SimpleImageCaptchaValidator();
        CacheStore cacheStore = new LocalCacheStore();
        ImageCaptchaProperties prop = new ImageCaptchaProperties();
        CaptchaInterceptorGroup group = new CaptchaInterceptorGroup();
        group.addInterceptor(new ParamCheckCaptchaInterceptor());
        group.addInterceptor(new BasicTrackCaptchaInterceptor());

        ImageCaptchaApplication application = new DefaultImageCaptchaApplication(generator, imageCaptchaValidator, cacheStore, prop, group);

        CaptchaResponse<ImageCaptchaVO> res = application.generateCaptcha("SLIDER");
        System.out.println(res);
    }
}
