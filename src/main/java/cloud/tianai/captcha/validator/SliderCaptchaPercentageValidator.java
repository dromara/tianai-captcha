package cloud.tianai.captcha.validator;

/**
 * @Author: 天爱有情
 * @date 2023/1/19 10:40
 * @Description 滑动类验证码百分比校验
 */
public interface SliderCaptchaPercentageValidator {

    /**
     * 计算滑块要背景图的百分比，基本校验
     * 用于计算滑动类验证码的缺口位置
     *
     * @param pos    移动的位置
     * @param maxPos 最大可移动的位置
     * @return float
     */
    float calcPercentage(Number pos, Number maxPos);

    /**
     * 校验滑块百分比
     * 用于校验滑动类验证码是否滑动到缺口
     *
     * @param newPercentage 用户滑动的百分比
     * @param oriPercentage 正确的滑块百分比
     * @return boolean
     */
    boolean checkPercentage(Float newPercentage, Float oriPercentage);

    /**
     * 校验滑块百分比
     * 用于校验滑动类验证码是否滑动到缺口
     *
     * @param newPercentage 用户滑动的百分比
     * @param oriPercentage 正确的滑块百分比
     * @param tolerant      容错值
     * @return boolean
     */
    boolean checkPercentage(Float newPercentage, Float oriPercentage, float tolerant);
}
