package cloud.tianai.captcha.generator.common.model.dto;

import cloud.tianai.captcha.resource.common.model.dto.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author: 天爱有情
 * @date 2022/4/28 16:51
 * @Description 点击图片校验描述
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClickImageCheckDefinition {
    /** 提示. */
    private Resource tip;
    private ImgWrapper tipImage;
    /** x. */
    private Integer x;
    /** y. */
    private Integer y;
    /** 宽. */
    private Integer width;
    /** 高. */
    private Integer height;
    /** 颜色. */
    private Color imageColor;

    /**
     * @Author: 天爱有情
     * @date 2022/4/28 14:26
     * @Description 点击图片包装
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImgWrapper {
        /** 图片. */
        private BufferedImage image;
        /** 提示. */
        private Resource tip;
        /** 图片颜色. */
        private Color imageColor;
    }

}
