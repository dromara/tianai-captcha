package cloud.tianai.captcha.generator.impl.transform;

import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.model.dto.CustomData;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageTransformData;
import cloud.tianai.captcha.generator.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.generator.common.util.ImgWriter;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @Author: 天爱有情
 * @date 2022/8/25 10:28
 * @Description base64 实现
 */
public class Base64ImageTransform implements ImageTransform {

    @SneakyThrows(IOException.class)
    public String transform(BufferedImage bufferedImage, String transformType) {
        // 这里判断处理一下,加一些警告日志
        String result = beforeTransform(bufferedImage, transformType);
        if (result != null) {
            return result;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            if (CaptchaImageUtils.isPng(transformType) || CaptchaImageUtils.isJpeg(transformType)) {
                // 如果是 jpg 或者 png图片的话 用hutool的生成
                ImgWriter.write(bufferedImage, transformType, byteArrayOutputStream, -1);
            } else {
                ImageIO.write(bufferedImage, transformType, byteArrayOutputStream);
            }
            //转换成字节码
            byte[] data = byteArrayOutputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(data);
            return "data:image/" + transformType + ";base64,".concat(base64);
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                // 忽略关闭异常
            }
        }
    }

    public String beforeTransform(BufferedImage bufferedImage, String formatType) {
//        int type = bufferedImage.getType();
//        if (BufferedImage.TYPE_4BYTE_ABGR == type) {
//            // png , 如果转换的是jpg的话
//            if (CaptchaImageUtils.isJpeg(formatType)) {
//                // bufferedImage为 png， 但是转换的图片为 jpg
//                if (log.isWarnEnabled()) {
//                    log.warn("图片验证码转换警告， 原图为 png格式时，指定转换的图片为jpg格式时可能会导致转换异常，如果转换的图片为出现错误，请设置指定转换的类型与原图的类型一致");
//                } else {
//                    System.err.println("图片验证码转换警告， 原图为 png格式时，指定转换的图片为jpg格式时可能会导致转换异常，如果转换的图片为出现错误，请设置指定转换的类型与原图的类型一致");
//                }
//            }
//        }
        // 其它的暂时不考虑
        return null;
    }

    @Override
    public ImageTransformData transform(GenerateParam param, BufferedImage backgroundImage, BufferedImage templateImage, Object backgroundResource, Object templateResource, CustomData data) {
        ImageTransformData imageTransformData = new ImageTransformData();
        if (backgroundImage != null) {
            imageTransformData.setBackgroundImageUrl(transform(backgroundImage, param.getBackgroundFormatName()));
        }
        if (templateImage != null) {
            imageTransformData.setTemplateImageUrl(transform(templateImage, param.getTemplateFormatName()));
        }
        return imageTransformData;
    }
}
