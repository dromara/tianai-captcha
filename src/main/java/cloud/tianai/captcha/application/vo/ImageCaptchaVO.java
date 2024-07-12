package cloud.tianai.captcha.application.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageCaptchaVO implements Serializable {
    /** 验证码类型.*/
    private String type;
    /** 背景图.*/
    private String backgroundImage;
    /** 移动图.*/
    private String templateImage;
    /** 背景图片所属标签. */
    private String backgroundImageTag;
    /** 模板图片所属标签. */
    private String templateImageTag;
    /** 背景图片宽度.*/
    private Integer backgroundImageWidth;
    /** 背景图片高度.*/
    private Integer backgroundImageHeight;
    /** 滑动图片宽度.*/
    private Integer templateImageWidth;
    /** 滑动图片高度.*/
    private Integer templateImageHeight;
    /** data 扩展数据.*/
    private Object data;
}
