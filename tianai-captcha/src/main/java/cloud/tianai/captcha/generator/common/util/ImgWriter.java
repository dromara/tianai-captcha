package cloud.tianai.captcha.generator.common.util;

import cloud.tianai.captcha.common.util.ObjectUtils;
import lombok.SneakyThrows;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * @Author: 天爱有情
 * @date 2022/5/9 11:47
 * @Description 拷贝from hutool(https://gitee.com/dromara/hutool/blob/v5-master/hutool-core/src/main/java/cn/hutool/core/img/ImgUtil.java)
 * 为了不依赖更多无用包， 单独拷贝出来
 */
public class ImgWriter {

    /**
     * 输出
     *
     * @param image           image
     * @param imageType       imageType
     * @param destImageStream destImageStream
     * @param quality         quality 0~1
     * @return
     */
    public static boolean write(Image image, String imageType, OutputStream destImageStream, float quality) {
        if (ObjectUtils.isEmpty(imageType)) {
            imageType = CaptchaImageUtils.TYPE_JPG;
        }
        ImageOutputStream imageOutputStream = null;
        try {
            imageOutputStream = transformImageOutputStream(destImageStream);
            final BufferedImage bufferedImage = CaptchaImageUtils.toBufferedImage(image, imageType);
            final ImageWriter writer = getWriter(bufferedImage, imageType);
            return write(bufferedImage, writer, imageOutputStream, quality);
        } finally {
            // 关闭 ImageOutputStream 防止资源泄露
            if (imageOutputStream != null) {
                try {
                    imageOutputStream.close();
                } catch (IOException e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    /**
     * 输出
     *
     * @param image   image
     * @param writer  writer
     * @param output  output
     * @param quality quality
     * @return boolean
     */
    public static boolean write(Image image, ImageWriter writer, ImageOutputStream output, float quality) {
        if (writer == null) {
            return false;
        }
        writer.setOutput(output);
        final RenderedImage renderedImage = toRenderedImage(image);
        // 设置质量
        ImageWriteParam imgWriteParams = null;
        if (quality > 0 && quality < 1) {
            imgWriteParams = writer.getDefaultWriteParam();
            if (imgWriteParams.canWriteCompressed()) {
                imgWriteParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                imgWriteParams.setCompressionQuality(quality);
                final ColorModel colorModel = renderedImage.getColorModel();// ColorModel.getRGBdefault();
                imgWriteParams.setDestinationType(new ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
            }
        }

        try {
            if (null != imgWriteParams) {
                writer.write(null, new IIOImage(renderedImage, null, null), imgWriteParams);
            } else {
                writer.write(renderedImage);
            }
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            writer.dispose();
        }
        return true;
    }

    public static RenderedImage toRenderedImage(Image img) {
        if (img instanceof RenderedImage) {
            return (RenderedImage) img;
        }
        return CaptchaImageUtils.copyImage(img, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * 获取 ImageWriter
     *
     * @param img        img
     * @param formatName formatName
     * @return ImageWriter
     */
    public static ImageWriter getWriter(Image img, String formatName) {
        final ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(CaptchaImageUtils.toBufferedImage(img, formatName));
        final Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, formatName);
        return iter.hasNext() ? iter.next() : null;
    }

    /**
     * 将 OutputStream 转换为  ImageOutputStream
     *
     * @param out out
     * @return ImageOutputStream
     * @throws RuntimeException
     */
    @SneakyThrows(IOException.class)
    public static ImageOutputStream transformImageOutputStream(OutputStream out) throws RuntimeException {
        ImageOutputStream result = ImageIO.createImageOutputStream(out);
        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        }
        return result;
    }
}
