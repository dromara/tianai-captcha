package cloud.tianai.captcha.template.slider.generator.common.model.dto;

import lombok.*;

import java.util.List;

/**
 * @Author: 天爱有情
 * @date 2022/4/27 15:33
 * @Description 文字点选
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WordClickImageCaptchaInfo extends ImageCaptchaInfo {

    private List<WordDefinition> checkWords;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WordDefinition {
        private String word;
        private Integer x;
        private Integer y;
        private Integer deg;
        private Integer wordWidth;
        private Integer wordHeight;

    }
}
