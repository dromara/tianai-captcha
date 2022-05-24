package example.readme;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;

public class Test3 {
    public static void main(String[] args) {
        // 资源管理器
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        // 标准验证码生成器
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager).init(true);
        // 生成 具有混淆的 滑块验证码 (目前只有滑块验证码支持混淆滑块， 旋转验证，滑动还原，点选验证 均不支持混淆功能)
        ImageCaptchaInfo imageCaptchaInfo = imageCaptchaGenerator.generateCaptchaImage(GenerateParam.builder()
                .type(CaptchaTypeConstant.SLIDER)
                .sliderFormatName("jpeg")
                .backgroundFormatName("png")
                // 是否添加混淆滑块
                .obfuscate(true)
                .build());
    }
}
