package cloud.tianai.captcha.spring.exception;

import cloud.tianai.captcha.common.exception.ImageCaptchaException;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: 天爱有情
 * @Date 2020/6/19 16:36
 * @Description 验证码验证失败异常
 */
@Getter
@Setter
public class CaptchaValidException extends ImageCaptchaException {

    private String captchaType;
    private Integer code;
    public CaptchaValidException() {
    }

    public CaptchaValidException(String captchaType,String message) {
        super(message);
        this.captchaType = captchaType;
    }
    public CaptchaValidException(String captchaType,Integer code, String message) {
        super(message);
        this.code = code;
        this.captchaType = captchaType;
    }
    public CaptchaValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptchaValidException(Throwable cause) {
        super(cause);
    }

    public CaptchaValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
