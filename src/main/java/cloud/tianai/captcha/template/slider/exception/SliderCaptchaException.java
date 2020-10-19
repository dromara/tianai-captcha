package cloud.tianai.captcha.template.slider.exception;

public class SliderCaptchaException extends RuntimeException{
    public SliderCaptchaException() {
    }

    public SliderCaptchaException(String message) {
        super(message);
    }

    public SliderCaptchaException(String message, Throwable cause) {
        super(message, cause);
    }

    public SliderCaptchaException(Throwable cause) {
        super(cause);
    }

    public SliderCaptchaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
