package cloud.tianai.captcha.solon.properties;

/**
 * @Author XT
 * @Date 2024.09.04
 */
public class CaptchaLimit {

    private Boolean enable;

    private Long reqLimit;

    private Long errorLimit;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Long getReqLimit() {
        return reqLimit;
    }

    public void setReqLimit(Long reqLimit) {
        this.reqLimit = reqLimit;
    }

    public Long getErrorLimit() {
        return errorLimit;
    }

    public void setErrorLimit(Long errorLimit) {
        this.errorLimit = errorLimit;
    }
}
