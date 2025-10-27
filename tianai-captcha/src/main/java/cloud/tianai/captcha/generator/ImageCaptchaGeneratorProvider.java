package cloud.tianai.captcha.generator;


import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;

/**
 * @Author: 天爱有情
 * @date 2022/5/19 14:45
 * @Description ImageCaptchaGenerator 提供者
 */
public interface ImageCaptchaGeneratorProvider {

    /**
     * 生成/获取 ImageCaptchaGenerator
     *
     * @param resourceManager resourceManager
     * @param imageTransform imageTransform
     * @return ImageCaptchaGenerator
     */
    ImageCaptchaGenerator get(ImageCaptchaResourceManager resourceManager, ImageTransform imageTransform, CaptchaInterceptor interceptor);

    /**
     * 验证码类型
     *
     * @return String
     */
    default String getType() {
        return "unknown";
    }
}
