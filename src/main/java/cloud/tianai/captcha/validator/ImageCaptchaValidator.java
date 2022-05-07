package cloud.tianai.captcha.validator;

import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;

import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2022/2/17 10:54
 * @Description 图片验证码校验器
 */
public interface ImageCaptchaValidator {

    /**
     * 计算滑块要背景图的百分比，基本校验
     *
     * @param pos    移动的位置
     * @param maxPos 最大可移动的位置
     * @return float
     */
    float calcPercentage(Number pos, Number maxPos);

    /**
     * 校验滑块百分比
     *
     * @param newPercentage 用户滑动的百分比
     * @param oriPercentage 正确的滑块百分比
     * @return boolean
     */
    boolean checkPercentage(Float newPercentage, Float oriPercentage);

    /**
     * 校验滑块百分比
     *
     * @param newPercentage 用户滑动的百分比
     * @param oriPercentage 正确的滑块百分比
     * @param tolerant      容错值
     * @return boolean
     */
    boolean checkPercentage(Float newPercentage, Float oriPercentage, float tolerant);

    /**
     * 用于生成验证码校验时需要的回传参数
     *
     * @param imageCaptchaInfo 生成的验证码数据
     * @return Map<String, Object>
     */
    Map<String, Object> generateImageCaptchaValidData(ImageCaptchaInfo imageCaptchaInfo);

    /**
     * 校验用户滑动滑块是否正确
     *
     * @param imageCaptchaTrack     包含了滑动轨迹，展示的图片宽高，滑动时间等参数
     * @param sliderCaptchaValidData generateSliderCaptchaValidData(生成的数据
     * @return boolean
     */
    boolean valid(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> sliderCaptchaValidData);
}
