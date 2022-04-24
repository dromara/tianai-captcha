package cloud.tianai.captcha.template.slider.generator.impl;

import cloud.tianai.captcha.template.slider.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.template.slider.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.template.slider.generator.common.constant.SliderCaptchaConstant;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.RotateImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.resource.ResourceStore;
import cloud.tianai.captcha.template.slider.resource.common.model.dto.Resource;
import cloud.tianai.captcha.template.slider.resource.impl.provider.ClassPathResourceProvider;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static cloud.tianai.captcha.template.slider.common.util.CaptchaImageUtils.*;
import static cloud.tianai.captcha.template.slider.generator.impl.StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_RESOURCE_PATH;
import static cloud.tianai.captcha.template.slider.generator.impl.StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH;

/**
 * @Author: 天爱有情
 * @date 2022/4/22 16:43
 * @Description 旋转图片验证码生成器
 */
public class StandardRotateImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    protected final ImageCaptchaResourceManager imageCaptchaResourceManager;

    public StandardRotateImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, boolean initDefaultResource) {
        this.imageCaptchaResourceManager = imageCaptchaResourceManager;
        if (initDefaultResource) {
            initDefaultResource();
        }
    }

    public void initDefaultResource() {
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加一些系统的资源文件
        resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg")));

        // 添加一些系统的 模板文件
        Map<String, Resource> template1 = new HashMap<>(4);
        template1.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/active.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/fixed.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/matrix.png")));
        resourceStore.addTemplate(CaptchaTypeConstant.ROTATE, template1);
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {
        // 旋转验证码没有混淆
        Boolean obfuscate = param.getObfuscate();
        Map<String, Resource> templateImages = imageCaptchaResourceManager.randomGetTemplate(param.getType());
        if (templateImages == null || templateImages.isEmpty()) {
            return null;
        }
        Collection<InputStream> inputStreams = new LinkedList<>();
        try {
            Resource resourceImage = imageCaptchaResourceManager.randomGetResource(param.getType());
            InputStream resourceInputStream = imageCaptchaResourceManager.getResourceInputStream(resourceImage);
            inputStreams.add(resourceInputStream);
            BufferedImage cutBackground = wrapFile2BufferedImage(resourceInputStream);
            // 拷贝一份图片
            BufferedImage targetBackground = deepCopyBufferedImage(cutBackground);

            InputStream fixedTemplateInput = getTemplateFile(templateImages, SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME);
            inputStreams.add(fixedTemplateInput);
            BufferedImage fixedTemplate = wrapFile2BufferedImage(fixedTemplateInput);

            InputStream activeTemplateInput = getTemplateFile(templateImages, SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME);
            inputStreams.add(activeTemplateInput);
            BufferedImage activeTemplate = wrapFile2BufferedImage(activeTemplateInput);

            InputStream matrixTemplateInput = getTemplateFile(templateImages, SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME);
            inputStreams.add(matrixTemplateInput);
            BufferedImage matrixTemplate = wrapFile2BufferedImage(matrixTemplateInput);

            // 算出居中的x和y
            int x = targetBackground.getWidth() / 2 - fixedTemplate.getWidth() / 2;
            int y = targetBackground.getHeight() / 2 - fixedTemplate.getHeight() / 2;
            overlayImage(targetBackground, fixedTemplate, x, y);
            // 抠图部分
            BufferedImage cutImage = cutImage(cutBackground, fixedTemplate, x, y);
            overlayImage(cutImage, activeTemplate, 0, 0);
            // 随机旋转抠图部分
            // 随机x， 转换为角度
            int randomX = ThreadLocalRandom.current().nextInt(fixedTemplate.getWidth() + 10, targetBackground.getWidth() - 10);
            double degree = 360d - randomX / ((targetBackground.getWidth()) / 360d);
//            int degree = ThreadLocalRandom.current().nextInt(10, 350);
            centerOverlayAndRotateImage(matrixTemplate, cutImage, degree);
            return wrapRotateCaptchaInfo(degree, randomX, targetBackground, matrixTemplate, param);
        } finally {
            // 使用完后关闭流
            for (InputStream inputStream : inputStreams) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @SneakyThrows
    private ImageCaptchaInfo wrapRotateCaptchaInfo(double degree, int randomX, BufferedImage backgroundImage, BufferedImage sliderImage, GenerateParam param) {
        String backgroundFormatName = param.getBackgroundFormatName();
        String sliderFormatName = param.getSliderFormatName();
        String backGroundImageBase64 = transform(backgroundImage, backgroundFormatName);
        String sliderImageBase64 = transform(sliderImage, sliderFormatName);
        return RotateImageCaptchaInfo.of(degree,
                randomX,
                backGroundImageBase64,
                sliderImageBase64,
                backgroundImage.getWidth(), backgroundImage.getHeight(),
                sliderImage.getWidth(), sliderImage.getHeight()
        );
    }

//    @Override
//    public String transform(BufferedImage bufferedImage, String formatType) throws IOException {
//        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Thinkpad\\Desktop\\aa" + formatType + "." + formatType);
//        ImageIO.write(bufferedImage, formatType, fileOutputStream);
//        fileOutputStream.close();
////        return super.transform(bufferedImage, formatType);
//        return "";
//    }


    @Override
    public ImageCaptchaResourceManager getImageResourceManager() {
        return imageCaptchaResourceManager;
    }

    public static void main(String[] args) throws IOException {
        BufferedImage bgImage = CaptchaImageUtils.wrapFile2BufferedImage(
                new FileInputStream("E:\\projects\\tianai-captcha\\src\\main\\resources\\META-INF\\cut-image\\resource\\1.jpg"));
        BufferedImage image1 = CaptchaImageUtils.wrapFile2BufferedImage(new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\a.png"));
        BufferedImage image2 = CaptchaImageUtils.wrapFile2BufferedImage(new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\b.png"));

        BufferedImage cutImage = CaptchaImageUtils.cutImage(bgImage, image1, bgImage.getWidth() / 2 - 100, bgImage.getHeight() / 2 - 100);
        CaptchaImageUtils.overlayImage(cutImage, image2, 0, 0);
        cutImage = CaptchaImageUtils.rotateImage(cutImage, 180);
        CaptchaImageUtils.overlayImage(bgImage, image1, bgImage.getWidth() / 2 - 100, bgImage.getHeight() / 2 - 100);
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Thinkpad\\Desktop\\a1.jpg");
        FileOutputStream file2OutputStream = new FileOutputStream("C:\\Users\\Thinkpad\\Desktop\\a2.png");
        ImageIO.write(bgImage, "jpeg", fileOutputStream);
        ImageIO.write(cutImage, "png", file2OutputStream);
        fileOutputStream.close();
        file2OutputStream.close();
    }
}
