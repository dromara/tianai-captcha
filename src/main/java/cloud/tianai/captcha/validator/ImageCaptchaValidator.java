package cloud.tianai.captcha.validator;

import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;

import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2022/2/17 10:54
 * @Description 图片验证码校验器
 */
public interface ImageCaptchaValidator {

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
     * @param imageCaptchaTrack      包含了滑动轨迹，展示的图片宽高，滑动时间等参数
     * @param imageCaptchaValidData generateImageCaptchaValidData(生成的数据)
     * @return ApiResponse<?>
     */
    ApiResponse<?> valid(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> imageCaptchaValidData);
}
