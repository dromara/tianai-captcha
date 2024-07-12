package cloud.tianai.captcha.application.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:31
 * @Description 验证码返回对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResponse<T> implements Serializable {
    private String id;
    private T captcha;

    public static <T> CaptchaResponse<T> of(String id, T data) {
        return new CaptchaResponse<T>(id, data);
    }
}
