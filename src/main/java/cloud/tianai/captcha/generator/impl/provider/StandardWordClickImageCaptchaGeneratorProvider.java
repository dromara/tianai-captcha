package cloud.tianai.captcha.generator.impl.provider;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageCaptchaGeneratorProvider;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.impl.StandardWordClickImageCaptchaGenerator;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;

public class StandardWordClickImageCaptchaGeneratorProvider implements ImageCaptchaGeneratorProvider {
    @Override
    public ImageCaptchaGenerator get(ImageCaptchaResourceManager resourceManager, ImageTransform imageTransform) {
        return new StandardWordClickImageCaptchaGenerator(resourceManager, imageTransform);
    }

    @Override
    public String getType() {
        return CaptchaTypeConstant.WORD_IMAGE_CLICK;
    }
}
