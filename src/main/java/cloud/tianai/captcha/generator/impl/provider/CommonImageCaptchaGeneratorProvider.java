package cloud.tianai.captcha.generator.impl.provider;

import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageCaptchaGeneratorProvider;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;

public class CommonImageCaptchaGeneratorProvider implements ImageCaptchaGeneratorProvider {

    private String type;
    private ImageCaptchaGeneratorProvider provider;

    public CommonImageCaptchaGeneratorProvider(String type, ImageCaptchaGeneratorProvider provider) {
        this.type = type;
        this.provider = provider;

    }

    @Override
    public ImageCaptchaGenerator get(ImageCaptchaResourceManager resourceManager, ImageTransform imageTransform) {
        return provider.get(resourceManager, imageTransform);
    }

    @Override
    public String getType() {
        return type;
    }
}
