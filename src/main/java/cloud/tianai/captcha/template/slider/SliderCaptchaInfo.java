package cloud.tianai.captcha.template.slider;

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
     * x轴百分比
     */
    private Float xPercent;
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
    /**
     * 扩展字段
     */
    public Object expand;
    public SliderCaptchaInfo(Integer x, Float xPercent, Integer y, String backgroundImage, String sliderImage) {
        this.x = x;
        this.xPercent = xPercent;
        this.y = y;
        this.backgroundImage = backgroundImage;
        this.sliderImage = sliderImage;
    }

    public static SliderCaptchaInfo of(Integer x, Float xPercent, Integer y, String backgroundImage, String sliderImage) {
        return new SliderCaptchaInfo(x, xPercent, y, backgroundImage, sliderImage);
    }

    public static SliderCaptchaInfo of(OriginalSliderData originalSliderData, String backgroundImage, String sliderImage) {
        return new SliderCaptchaInfo(originalSliderData.getX(), originalSliderData.getXPercent(), originalSliderData.getY(), backgroundImage, sliderImage);
    }
}
