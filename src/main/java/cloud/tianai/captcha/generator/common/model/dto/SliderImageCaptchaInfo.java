package cloud.tianai.captcha.generator.common.model.dto;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SliderImageCaptchaInfo extends ImageCaptchaInfo {
    /**
     * x轴
     */
    private Integer x;
    /**
     * y轴
     */
    private Integer y;


    public static SliderImageCaptchaInfo of(Integer x,
                                            Integer y,
                                            String backgroundImage,
                                            String templateImage,
                                            String backgroundImageTag,
                                            String templateImageTag,
                                            Integer bgImageWidth,
                                            Integer bgImageHeight,
                                            Integer sliderImageWidth,
                                            Integer sliderImageHeight) {
        SliderImageCaptchaInfo sliderImageCaptchaInfo = new SliderImageCaptchaInfo();
        sliderImageCaptchaInfo.setX(x);
        sliderImageCaptchaInfo.setY(y);
        sliderImageCaptchaInfo.setRandomX(x);
        sliderImageCaptchaInfo.setBackgroundImage(backgroundImage);
        sliderImageCaptchaInfo.setTemplateImage(templateImage);
        sliderImageCaptchaInfo.setBackgroundImageTag(backgroundImageTag);
        sliderImageCaptchaInfo.setTemplateImageTag(templateImageTag);
        sliderImageCaptchaInfo.setBackgroundImageWidth(bgImageWidth);
        sliderImageCaptchaInfo.setBackgroundImageHeight(bgImageHeight);
        sliderImageCaptchaInfo.setTemplateImageWidth(sliderImageWidth);
        sliderImageCaptchaInfo.setTemplateImageHeight(sliderImageHeight);
        sliderImageCaptchaInfo.setType(CaptchaTypeConstant.SLIDER);
        return sliderImageCaptchaInfo;
    }

}
