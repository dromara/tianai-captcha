package cloud.tianai.captcha.template.slider.generator.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 天爱有情
 * @date 2022/2/11 9:44
 * @Description 生成参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateParam {
    /** 背景格式化名称.*/
    private String backgroundFormatName = "jpeg";
    /** 滑块格式化名称.*/
    private String sliderFormatName = "png";
    /** 是否混淆.*/
    private Boolean obfuscate = false;
    /** 类型.*/
    private String type;
}
