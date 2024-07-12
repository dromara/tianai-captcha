package cloud.tianai.captcha.application;

import lombok.Data;

import java.util.Collections;
import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2020/10/19 18:41
 * @Description 滑块验证码属性
 */
@Data
public class ImageCaptchaProperties {
    /** 过期key prefix. */
    private String prefix = "captcha";
    /** 过期时间. */
    private Map<String, Long> expire = Collections.emptyMap();
}
