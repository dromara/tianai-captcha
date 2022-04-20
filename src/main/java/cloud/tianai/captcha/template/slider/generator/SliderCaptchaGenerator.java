package cloud.tianai.captcha.template.slider.generator;

import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.SliderCaptchaInfo;
import cloud.tianai.captcha.template.slider.resource.SliderCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaValidator;

/**
 * @Author: 天爱有情
 * @date 2020/10/19 18:37
 * @Description 滑块验证码模板
 */
public interface SliderCaptchaGenerator {

    /**
     * 获取滑块验证码
     *
     * @return SliderCaptchaInfo
     */
    SliderCaptchaInfo generateSlideImageInfo();


    /**
     * 生成滑块验证码
     *
     * @param targetFormatName jpeg或者webp格式
     * @param matrixFormatName png或者webp格式
     * @return SliderCaptchaInfo
     */
    SliderCaptchaInfo generateSlideImageInfo(String targetFormatName, String matrixFormatName);

    /**
     * 生成滑块验证码
     *
     * @param param 生成参数
     * @return SliderCaptchaInfo
     */
    SliderCaptchaInfo generateSlideImageInfo(GenerateParam param);

    /**
     * 百分比对比
     *
     * @param newPercentage 用户百分比
     * @param oriPercentage 原百分比
     * @return true 成功 false 失败
     * <p>
     * 废除 ， 建议使用
     * @see SliderCaptchaValidator 进行校验
     */
    @Deprecated
    boolean percentageContrast(Float newPercentage, Float oriPercentage);

    /**
     * 获取滑块验证码资源管理器
     *
     * @return SliderCaptchaResourceManager
     */
    SliderCaptchaResourceManager getSlideImageResourceManager();

}
