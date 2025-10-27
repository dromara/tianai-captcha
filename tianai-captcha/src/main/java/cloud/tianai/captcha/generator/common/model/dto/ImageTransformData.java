package cloud.tianai.captcha.generator.common.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 天爱有情
 * @date 2023/1/5 11:39
 * @Description 图片转换成url后的对象
 */
@Data
@NoArgsConstructor
public class ImageTransformData {
    /** 背景图. */
    private String backgroundImageUrl;
    /** 模板图. */
    private String templateImageUrl;
    /** 留一个扩展数据. */
    private Object data;

    public ImageTransformData(String backgroundImageUrl, String templateImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
        this.templateImageUrl = templateImageUrl;
    }
}
