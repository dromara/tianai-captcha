package cloud.tianai.captcha.solon.properties;

/**
 * @Author XT
 * @Date 2024.09.03
 */
public class CaptchaSecondary {

    private Boolean enabled;

    private Long expire;

    private String keyPrefix;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}
