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
public class CaptchaTransferData {
    private ResourceMap templateResource;
    private Resource resourceImage;
    private BufferedImage backgroundImage;
    private BufferedImage templateImage;
    private CustomData customData;
    private GenerateParam param;

    private Object transferData;

    public static CaptchaTransferData create(CustomData customData, GenerateParam param) {
        CaptchaTransferData captchaTransferData = new CaptchaTransferData();
        captchaTransferData.setCustomData(customData);
        captchaTransferData.setParam(param);
        return captchaTransferData;
    }

    public static CaptchaTransferData create(GenerateParam param) {
        return create(new CustomData(), param);
    }
}
