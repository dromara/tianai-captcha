package example.readme;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;

public class Test3 {
    public static void main(String[] args) {
        // 资源管理器
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        Base64ImageTransform imageTransform = new Base64ImageTransform();
        // 标准验证码生成器
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager,imageTransform).init();
        // 生成 具有混淆的 滑块验证码 (目前只有滑块验证码支持混淆滑块， 旋转验证，滑动还原，点选验证 均不支持混淆功能)
        ImageCaptchaInfo imageCaptchaInfo = imageCaptchaGenerator.generateCaptchaImage(GenerateParam.builder()
                // 设置验证码类型
                .type(CaptchaTypeConstant.SLIDER)
                .templateFormatName("jpeg")
                // 设置背景图片格式
                .backgroundFormatName("png")
                // 是否添加混淆滑块
                .obfuscate(true)
                .build());
    }
}
