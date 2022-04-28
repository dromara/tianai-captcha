package example;

import cloud.tianai.captcha.template.slider.generator.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.generator.impl.StandardClickImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.resource.impl.DefaultImageCaptchaResourceManager;

public class StandardWordClickImageCaptchaGeneratorTest {

    public static void main(String[] args) {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();

        StandardClickImageCaptchaGenerator defaultImageCaptchaResourceManager =
                new StandardClickImageCaptchaGenerator(imageCaptchaResourceManager, true);

        GenerateParam generateParam = new GenerateParam();
        generateParam.setType(CaptchaTypeConstant.WORD_CLICK);
        ImageCaptchaInfo imageCaptchaInfo = defaultImageCaptchaResourceManager.generateCaptchaImage(generateParam);

    }
}
