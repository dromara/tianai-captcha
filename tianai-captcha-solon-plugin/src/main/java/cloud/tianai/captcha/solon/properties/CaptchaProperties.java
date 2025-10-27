package cloud.tianai.captcha.solon.properties;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * @Author XT
 * @Date 2024.09.03
 */
@Inject("${captcha}")
@Configuration
public class CaptchaProperties {

    /**
     * redis 前缀
     */
    private String prefix;

    /**
     * 有效期
     */
    private Long expire;

    /**
     * 字体路径
     */
    private List<String> fontPath;

    /**
     * 资源路径
     */
    private CaptchaResource resources;

    /**
     * 二次验证
     */
    private CaptchaSecondary secondary;

    /**
     * 每分限流
     */
    private CaptchaLimit limit;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public List<String> getFontPath() {
        return fontPath;
    }

    public void setFontPath(List<String> fontPath) {
        this.fontPath = fontPath;
    }

    public CaptchaResource getResources() {
        return resources;
    }

    public void setResources(CaptchaResource resources) {
        this.resources = resources;
    }

    public CaptchaSecondary getSecondary() {
        return secondary;
    }

    public void setSecondary(CaptchaSecondary secondary) {
        this.secondary = secondary;
    }

    public CaptchaLimit getLimit() {
        return limit;
    }

    public void setLimit(CaptchaLimit limit) {
        this.limit = limit;
    }
}
