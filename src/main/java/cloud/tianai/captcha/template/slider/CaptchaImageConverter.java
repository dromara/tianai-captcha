package cloud.tianai.captcha.template.slider;

/**
 * @Author: 天爱有情
 * @date 2022/2/16 10:04
 * @Description 验证码图片转换器，将生成的  OriginalSliderData 转换成 SliderCaptchaInfo， 可以对齐进行加密，转换成base64、url等扩展
 */
public interface CaptchaImageConverter {

    SliderCaptchaInfo convert(OriginalSliderData originalSliderData);
}
