package example;

import cloud.tianai.captcha.template.slider.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.generator.impl.StandardRandomWordClickImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.validator.impl.BasicCaptchaTrackValidator;

import java.util.Map;

public class StandardWordClickImageCaptchaGeneratorTest {

    public static void main(String[] args) {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();

        StandardRandomWordClickImageCaptchaGenerator defaultImageCaptchaResourceManager =
                new StandardRandomWordClickImageCaptchaGenerator(imageCaptchaResourceManager, true);

        GenerateParam generateParam = new GenerateParam();
        generateParam.setType(CaptchaTypeConstant.WORD_IMAGE_CLICK);
        generateParam.setSliderFormatName("png");
        ImageCaptchaInfo imageCaptchaInfo = defaultImageCaptchaResourceManager.generateCaptchaImage(generateParam);

        BasicCaptchaTrackValidator basicCaptchaTrackValidator = new BasicCaptchaTrackValidator();
        Map<String, Object> stringObjectMap = basicCaptchaTrackValidator.generateImageCaptchaValidData(imageCaptchaInfo);

        System.out.println(stringObjectMap);

        System.out.println(imageCaptchaInfo);
    }
}
