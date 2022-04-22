package cloud.tianai.captcha.template.slider.generator;

import cloud.tianai.captcha.template.slider.generator.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.resource.SliderCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaValidator;

/**
 * @Author: 天爱有情
 * @date 2020/10/19 18:37
 * @Description 图片验证码生成器
 */
public interface ImageCaptchaGenerator {


    /**
     * 生成验证码图片
     * @param type 类型 {@link CaptchaTypeConstant}
     * @return SliderCaptchaInfo
     */
    ImageCaptchaInfo generateCaptchaImage(String type);


    /**
     * 生成滑块验证码
     * @param type type {@link CaptchaTypeConstant}
     * @param targetFormatName jpeg或者webp格式
     * @param matrixFormatName png或者webp格式
     * @return SliderCaptchaInfo
     */
    ImageCaptchaInfo generateCaptchaImage(String type, String targetFormatName, String matrixFormatName);

    /**
     * 生成滑块验证码
     *
     * @param param 生成参数
     * @return SliderCaptchaInfo
     */
    ImageCaptchaInfo generateCaptchaImage(GenerateParam param);


    /**
     * 获取滑块验证码资源管理器
     *
     * @return SliderCaptchaResourceManager
     */
    SliderCaptchaResourceManager getSlideImageResourceManager();

}
