package example.readme;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;

public class Test4 {
    public static void main(String[] args) {
        // 资源管理器
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        // 标准验证码生成器
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager,new Base64ImageTransform()).init(true);
        // 生成旋转验证码 图片类型为 webp
        // 注意 tianai-captcha 后面默认删除了生成webp格式图片需要用户自定义添加webp转换的工具，需要用户自定义添加和扩展
        // 参考 https://bitbucket.org/luciad/webp-imageio
        ImageCaptchaInfo slideImageInfo = imageCaptchaGenerator.generateCaptchaImage(GenerateParam.builder()
                .type(CaptchaTypeConstant.ROTATE)
                .templateFormatName("webp")
                .backgroundFormatName("webp")
                .build());
        System.out.println(slideImageInfo);
    }
}
