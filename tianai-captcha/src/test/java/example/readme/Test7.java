package example.readme;

import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceProvider;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;

import java.io.InputStream;

public class Test7 {
    public static void main(String[] args) {
        // 自定义 ResourceProvider
        ResourceProvider resourceProvider = new ResourceProvider() {
            @Override
            public InputStream getResourceInputStream(Resource data) {
                return null;
            }

            @Override
            public boolean supported(Resource type) {
                return false;
            }

            @Override
            public String getName() {
                return null;
            }
        };
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager,new Base64ImageTransform()).init();
        // 注册
        imageCaptchaResourceManager.registerResourceProvider(resourceProvider);
    }
}
