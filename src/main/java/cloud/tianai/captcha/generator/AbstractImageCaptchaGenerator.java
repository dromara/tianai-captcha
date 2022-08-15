package cloud.tianai.captcha.generator;

import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.generator.common.util.ImgWriter;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2022/4/22 16:30
 * @Description 抽象的验证码生成器
 */
@Slf4j
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

    @Getter
    @Setter
    /** 资源管理器. */
    protected ImageCaptchaResourceManager imageCaptchaResourceManager;
    @Getter
    private boolean init = false;

    public AbstractImageCaptchaGenerator() {
    }

    @Override
    public ImageCaptchaGenerator init(boolean initDefaultResource) {
        if (init) {
            return this;
        }
        init = true;
        try {
            log.info("图片验证码[{}]初始化...", this.getClass().getSimpleName());
            doInit(initDefaultResource);
        } catch (Exception e) {
            init = false;
            log.error("[{}]初始化失败,ex", this.getClass().getSimpleName(), e);
            throw e;
        }
        return this;
    }

    public AbstractImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        this.imageCaptchaResourceManager = imageCaptchaResourceManager;
    }

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

    @Override
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {
        assertInit();
        return doGenerateCaptchaImage(param);
    }


    /**
     * 将图片转换成字符串格式
     *
     * @param bufferedImage 图片
     * @param formatType    格式化类型
     * @return String
     */
    @SneakyThrows(Exception.class)
    public String transform(BufferedImage bufferedImage, String formatType) {
        // 这里判断处理一下,加一些警告日志
        String result = beforeTransform(bufferedImage, formatType);
        if (result != null) {
            return result;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (CaptchaImageUtils.isPng(formatType) || CaptchaImageUtils.isJpeg(formatType)) {
            // 如果是 jpg 或者 png图片的话 用hutool的生成
            ImgWriter.write(bufferedImage, formatType, byteArrayOutputStream, -1);
        } else {
            ImageIO.write(bufferedImage, formatType, byteArrayOutputStream);
        }
        //转换成字节码
        byte[] data = byteArrayOutputStream.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(data);
        return "data:image/" + formatType + ";base64,".concat(base64);
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

    protected InputStream getTemplateFile(Map<String, Resource> templateImages, String imageName) {
        Resource resource = templateImages.get(imageName);
        if (resource == null) {
            throw new IllegalArgumentException("查找模板异常， 该模板下未找到 ".concat(imageName));
        }
        return getImageResourceManager().getResourceInputStream(resource);
    }


    protected void assertInit() {
        if (!init) {
            throw new IllegalStateException("请先调用 init(...) 初始化方法进行初始化");
        }
    }

    /**
     * 初始化
     *
     * @param initDefaultResource 是否初始化默认资源
     */
    protected abstract void doInit(boolean initDefaultResource);

    /**
     * 生成验证码方法
     *
     * @param param param
     * @return ImageCaptchaInfo
     */
    protected abstract ImageCaptchaInfo doGenerateCaptchaImage(GenerateParam param);

    @Override
    public ImageCaptchaResourceManager getImageResourceManager() {
        return imageCaptchaResourceManager;
    }
}
