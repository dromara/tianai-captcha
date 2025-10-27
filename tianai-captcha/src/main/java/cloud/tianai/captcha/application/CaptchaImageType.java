package cloud.tianai.captcha.application;

import lombok.Getter;

/**
 * @Author: 天爱有情
 * @date 2022/2/24 16:01
 * @Description 验证码图片类型
 */
@Getter
public enum CaptchaImageType {

    /** webp类型. */
    WEBP,
    /** jpg+png类型. */
    JPEG_PNG;

    public static CaptchaImageType getType(String bgImageType, String sliderImageType) {
        if ("webp".equalsIgnoreCase(bgImageType) && "webp".equalsIgnoreCase(sliderImageType)) {
            return WEBP;
        }
        if (("jpeg".equalsIgnoreCase(bgImageType) || "jpg".equalsIgnoreCase(bgImageType)) && "png".equalsIgnoreCase(sliderImageType)) {
            return JPEG_PNG;
        }
        return null;
    }
}
