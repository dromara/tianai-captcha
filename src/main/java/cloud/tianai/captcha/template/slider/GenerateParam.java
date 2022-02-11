package cloud.tianai.captcha.template.slider;

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
    private String backgroundFormatName;
    /** 滑块格式化名称.*/
    private String sliderFormatName;
    /** 是否混淆.*/
    private Boolean obfuscate;
}
