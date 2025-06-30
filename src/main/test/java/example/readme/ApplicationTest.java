package example.readme;

import cloud.tianai.captcha.application.DefaultImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaProperties;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.cache.impl.LocalCacheStore;
import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.interceptor.CaptchaInterceptorGroup;
import cloud.tianai.captcha.interceptor.impl.BasicTrackCaptchaInterceptor;
import cloud.tianai.captcha.interceptor.impl.ParamCheckCaptchaInterceptor;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ApplicationTest {

    public static void main(String[] args) {
        ImageCaptchaApplication application = createImageCaptchaApplication();
        // 生成验证码数据， 可以将该数据直接返回给前端 ， 可配合 tianai-captcha-web-sdk 使用
        ApiResponse<ImageCaptchaVO> res = application.generateCaptcha("SLIDER");
        System.out.println(res);

        // 校验验证码， ImageCaptchaTrack 和 id 均为前端传开的参数， 可将 valid数据直接返回给 前端
        // 注意: 该项目只负责生成和校验验证码数据， 至于二次验证等需要自行扩展
        String id =res.getData().getId();
        ImageCaptchaTrack imageCaptchaTrack = null;
        ApiResponse<?> valid = application.matching(id, imageCaptchaTrack);
        System.out.println(valid.isSuccess());


        // 扩展: 一个简单的二次验证
        CacheStore cacheStore = new LocalCacheStore();
        if (valid.isSuccess()) {
            // 如果验证成功，生成一个token并存储, 将该token返回给客户端，客户端下次请求数据时携带该token， 后台判断是否有效
            String token = UUID.randomUUID().toString();
            cacheStore.setCache(token, new AnyMap(), 5L, TimeUnit.MINUTES);
        }

    }

    public static ImageCaptchaApplication createImageCaptchaApplication() {
        // 验证码资源管理器 该类负责管理验证码背景图和模板图等数据
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        // 验证码生成器； 注意: 生成器必须调用init(...)初始化方法 true为加载默认资源，false为不加载，
        ImageCaptchaGenerator generator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager).init();
        // 验证码校验器
        ImageCaptchaValidator imageCaptchaValidator = new SimpleImageCaptchaValidator();
        // 缓存, 用于存放校验数据
        CacheStore cacheStore = new LocalCacheStore();
        // 验证码拦截器， 可以是单个，也可以是一组拦截器，可以嵌套， 这里演示加载参数校验拦截，和 滑动轨迹拦截
        CaptchaInterceptorGroup group = new CaptchaInterceptorGroup();
            group.addInterceptor(new ParamCheckCaptchaInterceptor());
            group.addInterceptor(new BasicTrackCaptchaInterceptor());

        ImageCaptchaProperties prop = new ImageCaptchaProperties();
        // application 验证码封装， prop为所需的一些扩展参数
        ImageCaptchaApplication application = new DefaultImageCaptchaApplication(generator, imageCaptchaValidator, cacheStore, prop, group);
        return application;
    }
}
