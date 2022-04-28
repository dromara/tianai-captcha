package cloud.tianai.captcha.template.slider.generator.common.model.dto;

import cloud.tianai.captcha.template.slider.generator.common.constant.CaptchaTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author: 天爱有情
 * @date 2022/4/22 15:49
 * @Description 旋转图片
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RotateImageCaptchaInfo extends ImageCaptchaInfo {
    /**
     * 旋转多少度
     */
    private Double degree;
    /** 旋转图片的容错值大一点. */
    public static final Float DEFAULT_TOLERANT = 0.05F;

    public static RotateImageCaptchaInfo of(Double degree,
                                            Integer randomX,
                                            String backgroundImage,
                                            String sliderImage,
                                            Integer bgImageWidth,
                                            Integer bgImageHeight,
                                            Integer sliderImageWidth,
                                            Integer sliderImageHeight) {
        RotateImageCaptchaInfo rotateImageCaptchaInfo = new RotateImageCaptchaInfo();
        rotateImageCaptchaInfo.setDegree(degree);
        rotateImageCaptchaInfo.setRandomX(randomX);
        rotateImageCaptchaInfo.setBackgroundImage(backgroundImage);
        rotateImageCaptchaInfo.setTolerant(DEFAULT_TOLERANT);
        rotateImageCaptchaInfo.setSliderImage(sliderImage);
        rotateImageCaptchaInfo.setBgImageWidth(bgImageWidth);
        rotateImageCaptchaInfo.setBgImageHeight(bgImageHeight);
        rotateImageCaptchaInfo.setSliderImageWidth(sliderImageWidth);
        rotateImageCaptchaInfo.setSliderImageHeight(sliderImageHeight);
        // 类型为旋转图片验证码
        rotateImageCaptchaInfo.setType(CaptchaTypeConstant.ROTATE);
        return rotateImageCaptchaInfo;
    }

}
