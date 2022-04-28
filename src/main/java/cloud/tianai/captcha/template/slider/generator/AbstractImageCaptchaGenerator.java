package cloud.tianai.captcha.template.slider.generator;

import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.resource.common.model.dto.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2022/4/22 16:30
 * @Description 抽象的验证码生成器
 */
public abstract class AbstractImageCaptchaGenerator implements ImageCaptchaGenerator {
    public static String DEFAULT_BG_IMAGE_TYPE = "jpeg";
    public static String DEFAULT_SLIDER_IMAGE_TYPE = "png";

    @Getter
    @Setter
    /** 默认背景图片类型. */
    public String defaultBgImageType = DEFAULT_BG_IMAGE_TYPE;
    @Getter
    @Setter
    /** 默认滑块图片类型. */
    public String defaultSliderImageType = DEFAULT_SLIDER_IMAGE_TYPE;

    @Override
    public ImageCaptchaInfo generateCaptchaImage(String type) {
        return generateCaptchaImage(type, defaultBgImageType, defaultSliderImageType);
    }

    @SneakyThrows
    @Override
    public ImageCaptchaInfo generateCaptchaImage(String type, String backgroundFormatName, String sliderFormatName) {
        return generateCaptchaImage(GenerateParam.builder()
                .type(type)
                .backgroundFormatName(backgroundFormatName)
                .sliderFormatName(sliderFormatName)
                .obfuscate(false)
                .build());
    }

    /**
     * 将图片转换成字符串格式
     *
     * @param bufferedImage 图片
     * @param formatType    格式化类型
     * @return String
     */
    @SneakyThrows(IOException.class)
    public String transform(BufferedImage bufferedImage, String formatType) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, formatType, byteArrayOutputStream);
        //转换成字节码
        byte[] data = byteArrayOutputStream.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(data);
        return "data:image/" + formatType + ";base64,".concat(base64);
    }

    protected InputStream getTemplateFile(Map<String, Resource> templateImages, String imageName) {
        Resource resource = templateImages.get(imageName);
        if (resource == null) {
            throw new IllegalArgumentException("查找模板异常， 该模板下未找到 ".concat(imageName));
        }
        return getImageResourceManager().getResourceInputStream(resource);
    }

}
