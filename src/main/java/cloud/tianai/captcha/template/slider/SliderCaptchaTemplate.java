package cloud.tianai.captcha.template.slider;

/**
 * @Author: 天爱有情
 * @date 2020/10/19 18:37
 * @Description 滑块验证码模板
 */
public interface SliderCaptchaTemplate {

    /**
     * 获取滑块验证码
     *
     * @return SliderCaptchaInfo
     */
    SliderCaptchaInfo getSlideImageInfo();


    /**
     * 获取滑块验证码
     *
     * @param targetFormatName jpeg或者webp格式
     * @param matrixFormatName png或者webp格式
     * @return SliderCaptchaInfo
     */
    SliderCaptchaInfo getSlideImageInfo(String targetFormatName, String matrixFormatName);

    /**
     * 百分比对比
     *
     * @param newPercentage 用户百分比
     * @param oriPercentage 原百分比
     * @return true 成功 false 失败
     */
    boolean percentageContrast(Float newPercentage, Float oriPercentage);

    /**
     * 获取滑块验证码资源管理器
     *
     * @return SliderCaptchaResourceManager
     */
    SliderCaptchaResourceManager getSlideImageResourceManager();

}
