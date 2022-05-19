package cloud.tianai.captcha.generator.impl.provider;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageCaptchaGeneratorProvider;
import cloud.tianai.captcha.generator.impl.StandardConcatImageCaptchaGenerator;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
/**
 * @Author: 天爱有情
 * @date 2022/5/19 15:12
 * @Description 滑动还原验证码
 */
public class StandardConcatImageCaptchaGeneratorProvider implements ImageCaptchaGeneratorProvider {
    @Override
    public ImageCaptchaGenerator get(ImageCaptchaResourceManager resourceManager) {
        return new StandardConcatImageCaptchaGenerator(resourceManager);
    }

    @Override
    public String getType() {
        return CaptchaTypeConstant.CONCAT;
    }
}
