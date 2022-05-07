package cloud.tianai.captcha.common.exception;

/**
 * @Author: 天爱有情
 * @date 2022/5/7 9:04
 * @Description  图片验证码异常
 */
public class ImageCaptchaException extends RuntimeException{
    public ImageCaptchaException() {
    }

    public ImageCaptchaException(String message) {
        super(message);
    }

    public ImageCaptchaException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageCaptchaException(Throwable cause) {
        super(cause);
    }

    public ImageCaptchaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
