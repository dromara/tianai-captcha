package example;

import cloud.tianai.captcha.template.slider.generator.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.RotateImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.generator.impl.StandardRotateImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.resource.ResourceStore;
import cloud.tianai.captcha.template.slider.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.resource.impl.DefaultResourceStore;

public class StandardRotateCaptchaGeneratorTest {

    public static void main(String[] args) {
        ResourceStore resourceStore = new DefaultResourceStore();
//        Map<String, Resource> template = new HashMap<>();
//        template.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource("file", "C:\\Users\\Thinkpad\\Desktop\\a.png"));
//        template.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource("file", "C:\\Users\\Thinkpad\\Desktop\\b.png"));

//        resourceStore.addTemplate(CaptchaTypeConstant.ROTATE, template);
//        resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource("file", "E:\\projects\\tianai-captcha\\src\\main\\resources\\META-INF\\cut-image\\resource\\1.jpg"));

        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager(resourceStore);

        StandardRotateImageCaptchaGenerator standardRotateImageCaptchaGenerator = new StandardRotateImageCaptchaGenerator(imageCaptchaResourceManager, true);
        ImageCaptchaInfo imageCaptchaInfo = standardRotateImageCaptchaGenerator.generateCaptchaImage(CaptchaTypeConstant.ROTATE);
        System.out.println("backgroundImage:" + imageCaptchaInfo.getBackgroundImage());
        System.out.println("sliderImage:" + imageCaptchaInfo.getSliderImage());
        System.out.println(((RotateImageCaptchaInfo)imageCaptchaInfo).getDegree());

    }
}
