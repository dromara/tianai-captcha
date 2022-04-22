package example;

import cloud.tianai.captcha.template.slider.generator.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.template.slider.generator.common.constant.SliderCaptchaConstant;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.RotateImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.generator.impl.StandardRotateCaptchaGenerator;
import cloud.tianai.captcha.template.slider.resource.ResourceStore;
import cloud.tianai.captcha.template.slider.resource.SliderCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.resource.common.model.dto.Resource;
import cloud.tianai.captcha.template.slider.resource.impl.DefaultResourceStore;
import cloud.tianai.captcha.template.slider.resource.impl.DefaultSliderCaptchaResourceManager;

import java.util.HashMap;
import java.util.Map;

public class StandardRotateCaptchaGeneratorTest {

    public static void main(String[] args) {
        ResourceStore resourceStore = new DefaultResourceStore();
//        Map<String, Resource> template = new HashMap<>();
//        template.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource("file", "C:\\Users\\Thinkpad\\Desktop\\a.png"));
//        template.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource("file", "C:\\Users\\Thinkpad\\Desktop\\b.png"));

//        resourceStore.addTemplate(CaptchaTypeConstant.ROTATE, template);
//        resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource("file", "E:\\projects\\tianai-captcha\\src\\main\\resources\\META-INF\\cut-image\\resource\\1.jpg"));

        SliderCaptchaResourceManager sliderCaptchaResourceManager = new DefaultSliderCaptchaResourceManager(resourceStore);

        StandardRotateCaptchaGenerator standardRotateCaptchaGenerator = new StandardRotateCaptchaGenerator(sliderCaptchaResourceManager, true);
        ImageCaptchaInfo imageCaptchaInfo = standardRotateCaptchaGenerator.generateCaptchaImage(CaptchaTypeConstant.ROTATE);
        System.out.println("backgroundImage:" + imageCaptchaInfo.getBackgroundImage());
        System.out.println("sliderImage:" + imageCaptchaInfo.getSliderImage());
        System.out.println(((RotateImageCaptchaInfo)imageCaptchaInfo).getDegree());

    }
}
