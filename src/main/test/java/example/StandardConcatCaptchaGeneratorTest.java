package example;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.common.model.dto.RotateImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.CacheImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.StandardConcatImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.StandardRotateImageCaptchaGenerator;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultResourceStore;

import java.util.concurrent.TimeUnit;

public class StandardConcatCaptchaGeneratorTest {

    public static void main(String[] args) throws InterruptedException {
        ResourceStore resourceStore = new DefaultResourceStore();
//        Map<String, Resource> template = new HashMap<>();
//        template.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource("file", "C:\\Users\\Thinkpad\\Desktop\\a.png"));
//        template.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource("file", "C:\\Users\\Thinkpad\\Desktop\\b.png"));

//        resourceStore.addTemplate(CaptchaTypeConstant.ROTATE, template);
//        resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource("file", "E:\\projects\\tianai-captcha\\src\\main\\resources\\META-INF\\cut-image\\resource\\1.jpg"));

        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager(resourceStore);

        StandardConcatImageCaptchaGenerator captchaGenerator = new StandardConcatImageCaptchaGenerator(imageCaptchaResourceManager, true);
        ImageCaptchaInfo imageCaptchaInfo = captchaGenerator.generateCaptchaImage(CaptchaTypeConstant.CONCAT);
        System.out.println("backgroundImage:" + imageCaptchaInfo.getBackgroundImage());
        System.out.println("sliderImage:" + imageCaptchaInfo.getSliderImage());

        TimeUnit.DAYS.sleep(1);

    }
}
