package example.readme;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.resource.common.model.dto.Resource;

public class TACBuilderTest {

    public static void main(String[] args) {
        ImageCaptchaApplication application = TACBuilder.builder()
                .addDefaultTemplate()
                .addResource("SLIDER", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .addResource("WORD_IMAGE_CLICK", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .addResource("ROTATE", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .build();
        CaptchaResponse<ImageCaptchaVO> response = application.generateCaptcha("ROTATE");
        System.out.println(response);

    }
}

