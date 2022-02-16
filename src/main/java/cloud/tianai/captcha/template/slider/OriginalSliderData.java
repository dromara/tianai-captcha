package cloud.tianai.captcha.template.slider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.image.BufferedImage;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OriginalSliderData {
    /**
     * x轴
     */
    private Integer x;
    /**
     * y轴
     */
    private Integer y;

    /** 滑块要凹槽的百分比. */
    private float xPercent;
    /**
     * 背景图
     */
    private BufferedImage backgroundImage;
    /**
     * 移动图
     */
    private BufferedImage sliderImage;
    /**
     * 生成参数
     */
    private GenerateParam generateParam;

    public static OriginalSliderData of(Integer x, Integer y, float xPercent, BufferedImage backgroundImage, BufferedImage sliderImage, GenerateParam generateParam) {
        return new OriginalSliderData(x, y, xPercent, backgroundImage, sliderImage, generateParam);
    }
}