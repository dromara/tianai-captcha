package example.readme;

import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.impl.BasicCaptchaTrackValidator;

import java.util.Map;

public class Test2 {
    public static void main(String[] args) {
        ImageCaptchaValidator sliderCaptchaValidator = new BasicCaptchaTrackValidator();

        ImageCaptchaTrack imageCaptchaTrack = null;
        Map<String, Object> map = null;
        Float percentage = null;
        // 用户传来的行为轨迹和进行校验
        // - imageCaptchaTrack为前端传来的滑动轨迹数据
        // - map 为生成验证码时缓存的map数据
//        boolean check = sliderCaptchaValidator.valid(imageCaptchaTrack, map);
//        // 如果只想校验用户是否滑到指定凹槽即可，也可以使用
//        // - 参数1 用户传来的百分比数据
//        // - 参数2 生成滑块是真实的百分比数据
//        check = sliderCaptchaValidator.checkPercentage(0.2f, percentage);
    }
}
