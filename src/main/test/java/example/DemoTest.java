package example;

import cloud.tianai.captcha.generator.common.util.CaptchaImageUtils;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DemoTest {

    public static void main(String[] args) throws FileNotFoundException {
        BufferedImage bufferedImage = CaptchaImageUtils.wrapFile2BufferedImage(new FileInputStream("E:\\projects\\tianai-captcha\\src\\main\\resources\\META-INF\\cut-image\\resource\\1.jpg"));
        System.out.println(bufferedImage.getType());
    }
}
