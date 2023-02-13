package cloud.tianai.captcha.generator.common.model.dto;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.constant.CommonConstant;
import lombok.*;

/**
 * @Author: 天爱有情
 * @date 2022/2/11 9:44
 * @Description 生成参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GenerateParam {
    /** 背景格式化类型. */
    private String backgroundFormatName = "jpeg";
    /** 模板图片格式化类型. */
    private String templateFormatName = "png";
    /** 是否混淆. */
    private Boolean obfuscate = false;
    /** 类型. */
    private String type = CaptchaTypeConstant.SLIDER;
    /** 背景图片标签, 用户二级过滤背景图片，或指定某背景图片. */
    private String backgroundImageTag = CommonConstant.DEFAULT_TAG;
    /** 滑动图片标签,用户二级过滤模板图片，或指定某模板图片.. */
    private String templateImageTag = CommonConstant.DEFAULT_TAG;
    /** 扩展参数.*/
    private Object param;
}
