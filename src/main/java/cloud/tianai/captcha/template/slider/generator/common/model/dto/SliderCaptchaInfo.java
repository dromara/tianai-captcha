package cloud.tianai.captcha.template.slider.generator.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:04
 * @Description 滑块验证码
 */
@Data
@AllArgsConstructor
public class SliderCaptchaInfo {

    /**
     * x轴
     */
    private Integer x;
    /**
     * y轴
     */
    private Integer y;
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
    /**
     * 扩展字段
     */
    public Object expand;

    public SliderCaptchaInfo(Integer x, Integer y, String backgroundImage, String sliderImage, Integer bgImageWidth, Integer bgImageHeight, Integer sliderImageWidth, Integer sliderImageHeight) {
        this.x = x;
        this.y = y;
        this.backgroundImage = backgroundImage;
        this.sliderImage = sliderImage;
        this.bgImageWidth = bgImageWidth;
        this.bgImageHeight = bgImageHeight;
        this.sliderImageWidth = sliderImageWidth;
        this.sliderImageHeight = sliderImageHeight;
    }

    public static SliderCaptchaInfo of(Integer x,
                                       Integer y,
                                       String backgroundImage,
                                       String sliderImage,
                                       Integer bgImageWidth,
                                       Integer bgImageHeight,
                                       Integer sliderImageWidth,
                                       Integer sliderImageHeight) {
        return new SliderCaptchaInfo(x, y, backgroundImage, sliderImage, bgImageWidth, bgImageHeight, sliderImageWidth, sliderImageHeight);
    }

}
