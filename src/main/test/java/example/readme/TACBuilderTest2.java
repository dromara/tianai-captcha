package example.readme;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.interceptor.Context;
import cloud.tianai.captcha.resource.common.model.dto.Resource;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TACBuilderTest2 {

    public static void main(String[] args) throws IOException, FontFormatException {
        ImageCaptchaApplication application = TACBuilder.builder()
                .addDefaultTemplate()
                .expire("default", 10000L)
                .expire("WORD_IMAGE_CLICK", 60000L)
                .addResource("SLIDER", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .addResource("WORD_IMAGE_CLICK", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .addResource("ROTATE", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .build();
        ApiResponse<ImageCaptchaVO> response = application.generateCaptcha("SLIDER");
        System.out.println(response);

    }
}

