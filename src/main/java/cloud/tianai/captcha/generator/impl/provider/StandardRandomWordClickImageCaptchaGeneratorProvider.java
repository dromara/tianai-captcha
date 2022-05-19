package cloud.tianai.captcha.generator.impl.provider;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageCaptchaGeneratorProvider;
import cloud.tianai.captcha.generator.impl.StandardRandomWordClickImageCaptchaGenerator;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;

public class StandardRandomWordClickImageCaptchaGeneratorProvider implements ImageCaptchaGeneratorProvider {
    @Override
    public ImageCaptchaGenerator get(ImageCaptchaResourceManager resourceManager) {
        return new StandardRandomWordClickImageCaptchaGenerator(resourceManager);
    }

    @Override
    public String getType() {
        return CaptchaTypeConstant.WORD_IMAGE_CLICK;
    }
}
