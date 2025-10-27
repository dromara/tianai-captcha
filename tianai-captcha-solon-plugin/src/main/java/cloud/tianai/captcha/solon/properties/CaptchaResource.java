package cloud.tianai.captcha.solon.properties;

import java.util.List;

/**
 * @Author XT
 * @Date 2024.09.03
 */
public class CaptchaResource {

    private Boolean auto;

    private String autoType;

    private List<String> images;

    private List<String> SLIDER;

    private List<String> WORD_IMAGE_CLICK;

    private List<String> ROTATE;

    private List<String> CONCAT;

    public List<String> getSLIDER() {
        return SLIDER;
    }

    public void setSLIDER(List<String> SLIDER) {
        this.SLIDER = SLIDER;
    }

    public List<String> getWORD_IMAGE_CLICK() {
        return WORD_IMAGE_CLICK;
    }

    public void setWORD_IMAGE_CLICK(List<String> WORD_IMAGE_CLICK) {
        this.WORD_IMAGE_CLICK = WORD_IMAGE_CLICK;
    }

    public List<String> getROTATE() {
        return ROTATE;
    }

    public void setROTATE(List<String> ROTATE) {
        this.ROTATE = ROTATE;
    }

    public List<String> getCONCAT() {
        return CONCAT;
    }

    public void setCONCAT(List<String> CONCAT) {
        this.CONCAT = CONCAT;
    }

    public Boolean getAuto() {
        return auto;
    }

    public void setAuto(Boolean auto) {
        this.auto = auto;
    }

    public String getAutoType() {
        return autoType;
    }

    public void setAutoType(String autoType) {
        this.autoType = autoType;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
