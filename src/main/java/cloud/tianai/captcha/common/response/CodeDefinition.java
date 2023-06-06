package cloud.tianai.captcha.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: 天爱有情
 * @date 2022/4/13 12:37
 * @Description code 定义
 */
@Data
@AllArgsConstructor
public class CodeDefinition {

    private Integer code;
    private String message;
}
