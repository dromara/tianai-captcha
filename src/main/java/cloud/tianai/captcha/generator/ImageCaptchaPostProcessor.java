package cloud.tianai.captcha.generator;

import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.CaptchaTransferData;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;

/**
 * @Author: 天爱有情
 * @date 2023/4/24 15:11
 * @Description 图片验证码后处理器
 */
public interface ImageCaptchaPostProcessor {

    default ImageCaptchaInfo beforeGenerateCaptchaImage(CaptchaTransferData transferData, ImageCaptchaGenerator context) {
        return null;
    }

    default void beforeWrapImageCaptchaInfo(CaptchaTransferData transferData, ImageCaptchaGenerator context) {

    }

    default void afterGenerateCaptchaImage(CaptchaTransferData transferData, ImageCaptchaInfo imageCaptchaInfo, ImageCaptchaGenerator context) {

    }
}
