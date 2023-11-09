package cloud.tianai.captcha.generator.common.model.dto;

import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import lombok.Data;

import java.awt.image.BufferedImage;

/**
 * @Author: 天爱有情
 * @date 2023/4/24 15:02
 * @Description 传输用
 */
@Data
public class CaptchaExchange {
    /** 模板对象. */
    private ResourceMap templateResource;
    /** 资源对象. */
    private Resource resourceImage;
    /** 生成好的背景图片. */
    private BufferedImage backgroundImage;
    /** 生成好的模板图片. */
    private BufferedImage templateImage;
    /** 最终要回调给验证器的自定义对象. */
    private CustomData customData;
    /** 用户传来的生成参数. */
    private GenerateParam param;
    /** 传输对象，扩展自定义. */
    private Object transferData;

    public static CaptchaExchange create(CustomData customData, GenerateParam param) {
        CaptchaExchange captchaExchange = new CaptchaExchange();
        captchaExchange.setCustomData(customData);
        captchaExchange.setParam(param);
        return captchaExchange;
    }

    public static CaptchaExchange create(GenerateParam param) {
        return create(new CustomData(), param);
    }
}
