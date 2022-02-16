package cloud.tianai.captcha.template.slider;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @Author: 天爱有情
 * @date 2022/2/16 10:23
 * @Description 默认 CaptchaImageConverter
 */
public class DefaultCaptchaImageConverter implements CaptchaImageConverter {

    @Override
    @SneakyThrows
    public SliderCaptchaInfo convert(OriginalSliderData originalSliderData) {
        GenerateParam param = originalSliderData.getGenerateParam();
        BufferedImage backgroundImage = originalSliderData.getBackgroundImage();
        BufferedImage sliderImage = originalSliderData.getSliderImage();
        String backgroundFormatName = param.getBackgroundFormatName();
        String sliderFormatName = param.getSliderFormatName();
        String backGroundImageBase64 = transform(backgroundImage, backgroundFormatName);
        String sliderImageBase64 = transform(sliderImage, sliderFormatName);
        return SliderCaptchaInfo.of(originalSliderData,
                backGroundImageBase64,
                sliderImageBase64);
    }

    public String transform(BufferedImage bufferedImage, String formatName) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, formatName, byteArrayOutputStream);
        //转换成字节码
        byte[] data = byteArrayOutputStream.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(data);
        return "data:image/" + formatName + ";base64,".concat(base64);
    }
}
