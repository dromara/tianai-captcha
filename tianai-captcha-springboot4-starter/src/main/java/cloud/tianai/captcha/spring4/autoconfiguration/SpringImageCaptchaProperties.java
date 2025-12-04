package cloud.tianai.captcha.spring4.autoconfiguration;

import cloud.tianai.captcha.application.ImageCaptchaProperties;
import cloud.tianai.captcha.resource.DefaultBuiltInResources;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author: 天爱有情
 * @date 2020/10/19 18:41
 * @Description 滑块验证码属性
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "captcha")
public class SpringImageCaptchaProperties extends ImageCaptchaProperties {
    /** 是否初始化默认资源. */
    private Boolean initDefaultResource = false;
    /** 默认资源的位置. */
    private String defaultResourcePrefix = DefaultBuiltInResources.PATH_PREFIX;
    /** 字体包路径. */
    private List<String> fontPath;
    /** 二次验证配置. */
    @NestedConfigurationProperty
    private SecondaryVerificationProperties secondary;
}
