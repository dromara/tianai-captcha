package cloud.tianai.captcha.generator.impl.provider;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageCaptchaGeneratorProvider;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.impl.StandardRotateImageCaptchaGenerator;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;

/**
 * @Author: 天爱有情
 * @date 2022/5/19 15:14
 * @Description 旋转验证码
 */
public class StandardRotateImageCaptchaGeneratorProvider implements ImageCaptchaGeneratorProvider {
    @Override
    public ImageCaptchaGenerator get(ImageCaptchaResourceManager resourceManager, ImageTransform imageTransform) {
        return new StandardRotateImageCaptchaGenerator(resourceManager, imageTransform);
    }

    @Override
    public String getType() {
        return CaptchaTypeConstant.ROTATE;
    }
}
