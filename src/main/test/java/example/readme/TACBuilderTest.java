package example.readme;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaProperties;
import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.cache.impl.LocalCacheStore;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.interceptor.EmptyCaptchaInterceptor;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;

import java.awt.*;


public class TACBuilderTest {

    public static void main(String[] args) {
        Font font= null;
        ResourceMap template1 = new ResourceMap("default", 4);
        template1.put(StandardSliderImageCaptchaGenerator.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, "/active.png"));
        template1.put(StandardSliderImageCaptchaGenerator.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, "/fixed.png"));

        ImageCaptchaApplication application = TACBuilder.builder(new LocalMemoryResourceStore())
                // 加载系统自带的默认资源
                .addDefaultTemplate()
                // 设置验证码过期时间
                .expire("default", 10000L)
                .expire("WORD_IMAGE_CLICK", 60000L)
                // 设置拦截器
                .setInterceptor(EmptyCaptchaInterceptor.INSTANCE)
                // 添加验证码背景图片
                .addResource("SLIDER", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .addResource("WORD_IMAGE_CLICK", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .addResource("ROTATE", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                // 添加验证码模板图片
                .addTemplate("SLIDER",template1)
                // 设置缓冲器,可提前生成验证码，用于增加并发性
                .cached(10, 1000, 5000, 10000L)
                // 添加字体包，用于给文字点选验证码提供字体
                .addFont(new Resource("file", "C:\\Users\\Thinkpad\\Desktop\\captcha\\手写字体\\ttf\\千图小兔体.ttf"))
                // 设置缓存存储器，如果要支持分布式，需要把这里改成分布式缓存，比如通过redis实现的 CacheStore 缓存
                .setCacheStore(new LocalCacheStore())
                // 设置资源存储器，如果想在分布式环境或者想统一管理以及扩展 实现 ResourceStore 接口，自定义
//                .setResourceStore(new LocalMemoryResourceStore())
                // 图片转换器，默认是将图片转换成base64格式， 背景图为jpg， 模板图为png， 如果想要扩展，可替换成自己实现的
                .setTransform(new Base64ImageTransform())
                .build();
        CaptchaResponse<ImageCaptchaVO> response = application.generateCaptcha("ROTATE");
        System.out.println(response);

    }
}

