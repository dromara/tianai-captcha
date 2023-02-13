package cloud.tianai.captcha.generator.common.model.dto;

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

    /** 背景图. */
    private String backgroundImage;
    /** 模板图. */
    private String templateImage;
    /** 背景图片所属标签. */
    private String backgroundImageTag;
    /** 模板图片所属标签. */
    private String templateImageTag;
    /** 背景图片宽度. */
    private Integer backgroundImageWidth;
    /** 背景图片高度. */
    private Integer backgroundImageHeight;
    /** 滑块图片宽度. */
    private Integer templateImageWidth;
    /** 滑块图片高度. */
    private Integer templateImageHeight;
    /** 随机值. */
    private Integer randomX;
    /** 容错值, 可以为空 默认 0.02容错,校验的时候用. */
    private Float tolerant;
    /** 验证码类型. */
    private String type;
    /** 透传字段，用于传给前端. */
    private Object data;
    /**
     * 扩展字段
     */
    public Object expand;

    public ImageCaptchaInfo(String backgroundImage,
                            String templateImage,
                            String backgroundImageTag,
                            String templateImageTag,
                            Integer backgroundImageWidth,
                            Integer backgroundImageHeight,
                            Integer templateImageWidth,
                            Integer templateImageHeight,
                            Integer randomX,
                            String type) {
        this.backgroundImage = backgroundImage;
        this.templateImage = templateImage;
        this.backgroundImageTag = backgroundImageTag;
        this.templateImageTag = templateImageTag;
        this.backgroundImageWidth = backgroundImageWidth;
        this.backgroundImageHeight = backgroundImageHeight;
        this.templateImageWidth = templateImageWidth;
        this.templateImageHeight = templateImageHeight;
        this.randomX = randomX;
        this.type = type;
    }

    public static ImageCaptchaInfo of(String backgroundImage,
                                      String templateImage,
                                      String backgroundImageTag,
                                      String templateImageTag,
                                      Integer backgroundImageWidth,
                                      Integer backgroundImageHeight,
                                      Integer templateImageWidth,
                                      Integer templateImageHeight,
                                      Integer randomX,
                                      String type) {
        return new ImageCaptchaInfo(backgroundImage,
                templateImage,
                backgroundImageTag,
                templateImageTag,
                backgroundImageWidth,
                backgroundImageHeight,
                templateImageWidth,
                templateImageHeight,
                randomX, type);
    }

}
