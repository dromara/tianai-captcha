package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.constant.SliderCaptchaConstant;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.common.model.dto.RotateImageCaptchaInfo;
import cloud.tianai.captcha.generator.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: 天爱有情
 * @date 2022/4/22 16:43
 * @Description 旋转图片验证码生成器
 */
public class StandardRotateImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    public StandardRotateImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        super(imageCaptchaResourceManager);
    }

    public StandardRotateImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, ImageTransform imageTransform) {
        super(imageCaptchaResourceManager);
        setImageTransform(imageTransform);
    }

    @Override
    protected void doInit(boolean initDefaultResource) {
        if (initDefaultResource) {
            initDefaultResource();
        }
    }

    public void initDefaultResource() {
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加一些系统的资源文件
        resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource(ClassPathResourceProvider.NAME, StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg")));

        // 添加一些系统的 模板文件
        Map<String, Resource> template1 = new HashMap<>(4);
        template1.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/active.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/fixed.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/matrix.png")));
        resourceStore.addTemplate(CaptchaTypeConstant.ROTATE, template1);
    }

    @Override
    public ImageCaptchaInfo doGenerateCaptchaImage(GenerateParam param) {
        // 旋转验证码没有混淆
        Map<String, Resource> templateImages = imageCaptchaResourceManager.randomGetTemplate(param.getType());
        if (templateImages == null || templateImages.isEmpty()) {
            return null;
        }
        Collection<InputStream> inputStreams = new LinkedList<>();
        try {
            Resource resourceImage = imageCaptchaResourceManager.randomGetResource(param.getType());
            InputStream resourceInputStream = imageCaptchaResourceManager.getResourceInputStream(resourceImage);
            inputStreams.add(resourceInputStream);
            BufferedImage cutBackground = CaptchaImageUtils.wrapFile2BufferedImage(resourceInputStream);
            // 拷贝一份图片
            BufferedImage targetBackground = CaptchaImageUtils.copyImage(cutBackground, cutBackground.getType());

            InputStream fixedTemplateInput = getTemplateFile(templateImages, SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME);
            inputStreams.add(fixedTemplateInput);
            BufferedImage fixedTemplate = CaptchaImageUtils.wrapFile2BufferedImage(fixedTemplateInput);

            InputStream activeTemplateInput = getTemplateFile(templateImages, SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME);
            inputStreams.add(activeTemplateInput);
            BufferedImage activeTemplate = CaptchaImageUtils.wrapFile2BufferedImage(activeTemplateInput);

            InputStream matrixTemplateInput = getTemplateFile(templateImages, SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME);
            inputStreams.add(matrixTemplateInput);
            BufferedImage matrixTemplate = CaptchaImageUtils.wrapFile2BufferedImage(matrixTemplateInput);

            // 算出居中的x和y
            int x = targetBackground.getWidth() / 2 - fixedTemplate.getWidth() / 2;
            int y = targetBackground.getHeight() / 2 - fixedTemplate.getHeight() / 2;
            CaptchaImageUtils.overlayImage(targetBackground, fixedTemplate, x, y);
            // 抠图部分
            BufferedImage cutImage = CaptchaImageUtils.cutImage(cutBackground, fixedTemplate, x, y);
            CaptchaImageUtils.overlayImage(cutImage, activeTemplate, 0, 0);
            // 随机旋转抠图部分
            // 随机x， 转换为角度
            int randomX = ThreadLocalRandom.current().nextInt(fixedTemplate.getWidth() + 10, targetBackground.getWidth() - 10);
            double degree = 360d - randomX / ((targetBackground.getWidth()) / 360d);
            CaptchaImageUtils.centerOverlayAndRotateImage(matrixTemplate, cutImage, degree);
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
        String backGroundImageBase64 = getImageTransform().transform(backgroundImage, backgroundFormatName);
        String sliderImageBase64 = getImageTransform().transform(sliderImage, sliderFormatName);
        return RotateImageCaptchaInfo.of(degree,
                randomX,
                backGroundImageBase64,
                sliderImageBase64,
                backgroundImage.getWidth(), backgroundImage.getHeight(),
                sliderImage.getWidth(), sliderImage.getHeight()
        );
    }

}
