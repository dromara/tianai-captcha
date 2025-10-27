package cloud.tianai.captcha.generator.common.model.dto;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
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
    public static final Float DEFAULT_TOLERANT = 0.03F;

    public static RotateImageCaptchaInfo of(Double degree,
                                            Integer randomX,
                                            String backgroundImage,
                                            String templateImage,
                                            String backgroundImageTag,
                                            String templateImageTag,
                                            Integer bgImageWidth,
                                            Integer bgImageHeight,
                                            Integer templateImageWidth,
                                            Integer templateImageHeight) {
        RotateImageCaptchaInfo rotateImageCaptchaInfo = new RotateImageCaptchaInfo();
        rotateImageCaptchaInfo.setDegree(degree);
        rotateImageCaptchaInfo.setRandomX(randomX);
        rotateImageCaptchaInfo.setBackgroundImage(backgroundImage);
        rotateImageCaptchaInfo.setBackgroundImageTag(backgroundImageTag);
        rotateImageCaptchaInfo.setTemplateImageTag(templateImageTag);
        rotateImageCaptchaInfo.setTolerant(DEFAULT_TOLERANT);
        rotateImageCaptchaInfo.setTemplateImage(templateImage);
        rotateImageCaptchaInfo.setBackgroundImageWidth(bgImageWidth);
        rotateImageCaptchaInfo.setBackgroundImageHeight(bgImageHeight);
        rotateImageCaptchaInfo.setTemplateImageWidth(templateImageWidth);
        rotateImageCaptchaInfo.setTemplateImageHeight(templateImageHeight);
        // 类型为旋转图片验证码
        rotateImageCaptchaInfo.setType(CaptchaTypeConstant.ROTATE);
        return rotateImageCaptchaInfo;
    }

}
