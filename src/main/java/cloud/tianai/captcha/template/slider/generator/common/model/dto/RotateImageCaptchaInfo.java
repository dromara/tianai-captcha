package cloud.tianai.captcha.template.slider.generator.common.model.dto;

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
        rotateImageCaptchaInfo.setSliderImage(sliderImage);
        rotateImageCaptchaInfo.setBgImageWidth(bgImageWidth);
        rotateImageCaptchaInfo.setBgImageHeight(bgImageHeight);
        rotateImageCaptchaInfo.setSliderImageWidth(sliderImageWidth);
        rotateImageCaptchaInfo.setSliderImageHeight(sliderImageHeight);
        return rotateImageCaptchaInfo;
    }

}
