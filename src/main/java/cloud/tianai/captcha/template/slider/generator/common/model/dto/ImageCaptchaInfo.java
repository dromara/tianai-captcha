package cloud.tianai.captcha.template.slider.generator.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:04
 * @Description 滑块验证码
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageCaptchaInfo {

    /**
     * 背景图
     */
    private String backgroundImage;
    /**
     * 移动图
     */
    private String sliderImage;
    /** 背景图片宽度. */
    private Integer bgImageWidth;
    /** 背景图片高度. */
    private Integer bgImageHeight;
    /** 滑块图片宽度. */
    private Integer sliderImageWidth;
    /** 滑块图片高度. */
    private Integer sliderImageHeight;
    /** 随机值.*/
    private Integer randomX;
    /**
     * 扩展字段
     */
    public Object expand;

    public ImageCaptchaInfo(String backgroundImage,
                            String sliderImage,
                            Integer bgImageWidth,
                            Integer bgImageHeight,
                            Integer sliderImageWidth,
                            Integer sliderImageHeight,
                            Integer randomX) {
        this.backgroundImage = backgroundImage;
        this.sliderImage = sliderImage;
        this.bgImageWidth = bgImageWidth;
        this.bgImageHeight = bgImageHeight;
        this.sliderImageWidth = sliderImageWidth;
        this.sliderImageHeight = sliderImageHeight;
        this.randomX = randomX;
    }

    public static ImageCaptchaInfo of(String backgroundImage,
                                      String sliderImage,
                                      Integer bgImageWidth,
                                      Integer bgImageHeight,
                                      Integer sliderImageWidth,
                                      Integer sliderImageHeight,
                                      Integer randomKey) {
        return new ImageCaptchaInfo( backgroundImage, sliderImage, bgImageWidth, bgImageHeight, sliderImageWidth, sliderImageHeight, randomKey);
    }

}
