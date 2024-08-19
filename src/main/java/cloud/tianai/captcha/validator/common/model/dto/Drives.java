package cloud.tianai.captcha.validator.common.model.dto;

import lombok.Data;

@Data
public class Drives {
    private Integer hardwareConcurrency;
    private Boolean hasXhr = false;
    private String href;
    private String language;
    private Long start;
    private Long now;
    private String platform;
    private Integer scripts;
    private String userAgent;
    private Integer windowHeight;
    private Integer windowWidth;
}
