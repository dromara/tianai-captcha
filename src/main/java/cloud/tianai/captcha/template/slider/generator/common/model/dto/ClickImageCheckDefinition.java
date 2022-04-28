package cloud.tianai.captcha.template.slider.generator.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 天爱有情
 * @date 2022/4/28 16:51
 * @Description 点击图片校验描述
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClickImageCheckDefinition {
    /** 提示.*/
    private String tip;
    /** x.*/
    private Integer x;
    /** y.*/
    private Integer y;
    /** 宽.*/
    private Integer width;
    /** 高.*/
    private Integer height;

}