package cloud.tianai.captcha.generator;


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
     * @return ImageCaptchaGenerator
     */
    ImageCaptchaGenerator get(ImageCaptchaResourceManager resourceManager);

    /**
     * 验证码类型
     *
     * @return String
     */
    String getType();
}
