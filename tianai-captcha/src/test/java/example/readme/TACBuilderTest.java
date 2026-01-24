package example.readme;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaProperties;
import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.cache.impl.LocalCacheStore;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.interceptor.EmptyCaptchaInterceptor;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;

import java.awt.*;

import static cloud.tianai.captcha.common.constant.CommonConstant.DEFAULT_TAG;
import static cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator.TEMPLATE_ACTIVE_IMAGE_NAME;
import static cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator.TEMPLATE_FIXED_IMAGE_NAME;


public class TACBuilderTest {


    public void test() {
        ResourceMap template1 = new ResourceMap(DEFAULT_TAG, 2);
        // 滑块轮廓图片
        template1.put(TEMPLATE_ACTIVE_IMAGE_NAME, new Resource("classpath", "META-INF/cut-image/template/slider_1/active.png"));
        // 滑块凹槽图片
        template1.put(TEMPLATE_FIXED_IMAGE_NAME, new Resource("classpath", "META-INF/cut-image/template/slider_1/fixed.png"));

        // 给旋转验证码配置模板
        ResourceMap template2 = new ResourceMap(DEFAULT_TAG, 2);
        // 旋转验证码轮廓图片
        template2.put(TEMPLATE_ACTIVE_IMAGE_NAME, new Resource("classpath", "META-INF/cut-image/template/rotate_1/active.png"));
        // 旋转验证码凹槽图片
        template2.put(TEMPLATE_FIXED_IMAGE_NAME, new Resource("classpath", "META-INF/cut-image/template/rotate_1/fixed.png"));

        ImageCaptchaApplication application = TACBuilder.builder()
                // 设置资源存储器，默认是 LocalMemoryResourceStore
                .setResourceStore(new LocalMemoryResourceStore())
                // 配置模板
                .addTemplate(CaptchaTypeConstant.SLIDER,template1)
                .addTemplate(CaptchaTypeConstant.ROTATE,template2)
                // ...其他配置
                .build();
    }

    public static void main(String[] args) throws InterruptedException {
        Font font = null;
//        ResourceMap template1 = new ResourceMap("default", 4);
//        template1.put(StandardSliderImageCaptchaGenerator.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, "/active.png"));
//        template1.put(StandardSliderImageCaptchaGenerator.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, "/fixed.png"));

        ImageCaptchaApplication application = TACBuilder.builder()
                // 设置资源存储器，默认是 LocalMemoryResourceStore
                .setResourceStore(new LocalMemoryResourceStore())
                // 加载系统自带的默认资源(系统内置了几个滑块验证码缺口模板图，调用此函数加载)
                .addDefaultTemplate()
                // 设置验证码过期时间, 单位毫秒, default 是默认验证码过期时间，当前设置为10秒,
                // 可以自定义某些验证码类型单独的过期时间， 比如把点选验证码的过期时间设置为60秒
                .expire("default", 10000L)
                .expire(CaptchaTypeConstant.WORD_IMAGE_CLICK, 60000L)
                // 设置拦截器，默认是 EmptyCaptchaInterceptor.INSTANCE
                .setInterceptor(EmptyCaptchaInterceptor.INSTANCE)
                // 添加验证码背景图片
                // arg1 验证码类型(SLIDER、WORD_IMAGE_CLICK、ROTATE、CONCAT),
                // arg2 验证码背景图片资源
                .addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .addResource(CaptchaTypeConstant.ROTATE, new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .addResource(CaptchaTypeConstant.CONCAT, new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                // 添加验证码模板图片
//                .addTemplate("SLIDER",template1)
                // 设置缓存器,可提前生成验证码，用于增加并发性，可以不设置，默认是不开启缓存
                .cached(10, 1000, 5000, 10000L)
                // 添加字体包，用于给文字点选验证码提供字体
                .addFont(new Resource("file", "C:\\Users\\Thinkpad\\Desktop\\captcha\\手写字体\\ttf\\千图小兔体.ttf"))
                // 设置缓存存储器，如果要支持分布式，需要把这里改成分布式缓存，比如通过redis实现的 CacheStore 缓存
                .setCacheStore(new LocalCacheStore())
                // 图片转换器，默认是将图片转换成base64格式， 背景图为jpg， 模板图为png， 如果想要扩展，可替换成自己实现的
                .setTransform(new Base64ImageTransform())
                .build();

        while (true) {
            long start = System.currentTimeMillis();

            // 生成验证码
            // arg1 验证码类型(SLIDER、WORD_IMAGE_CLICK、ROTATE、CONCAT)
            // response 为生成的验证码数据，可直接返回给前端
            ApiResponse<ImageCaptchaVO> response = application.generateCaptcha(CaptchaTypeConstant.SLIDER);


            // 校验验证码
            // captchaId 和 track 数据应该是前端传来的验证数据
            String captchaId = "";
            ImageCaptchaTrack track = null;
            ApiResponse<?> matching = application.matching(captchaId, track);


            if (matching.isSuccess()) {
                System.out.println("校验成功");
            } else {
                System.out.println("校验失败:" + matching.getMsg());
            }

            System.out.println("耗时:" + (System.currentTimeMillis() - start));
//            System.out.println(response);
            Thread.sleep(1000);
        }
//        System.out.println(response);

    }
}

