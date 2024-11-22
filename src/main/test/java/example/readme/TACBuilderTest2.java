package example.readme;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
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
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\captcha\\手写字体\\ttf\\千图小兔体.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, fileInputStream);
        fileInputStream.close();
        ImageCaptchaApplication application = TACBuilder.builder()
                .addDefaultTemplate()
                .expire("default", 10000L)
                .expire("WORD_IMAGE_CLICK", 60000L)
                .addResource("SLIDER", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .addResource("WORD_IMAGE_CLICK", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .addResource("ROTATE", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
                .setInterceptor(new CaptchaInterceptor() {
                    @Override
                    public CaptchaResponse<ImageCaptchaVO> beforeGenerateCaptcha(Context context, String type, GenerateParam param) {
                        System.out.println("before generator");
                        return CaptchaInterceptor.super.beforeGenerateCaptcha(context, type, param);
                    }
                })
                .addFont(new Resource("file", "C:\\Users\\Thinkpad\\Desktop\\captcha\\手写字体\\ttf\\千图小兔体.ttf"))
                .build();
        CaptchaResponse<ImageCaptchaVO> response = application.generateCaptcha("WORD_IMAGE_CLICK");
        System.out.println(response);

    }
}

