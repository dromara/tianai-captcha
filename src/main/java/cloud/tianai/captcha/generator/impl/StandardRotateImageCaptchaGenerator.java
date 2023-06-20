package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.constant.CommonConstant;
import cloud.tianai.captcha.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.constant.SliderCaptchaConstant;
import cloud.tianai.captcha.generator.common.model.dto.*;
import cloud.tianai.captcha.generator.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static cloud.tianai.captcha.common.constant.CommonConstant.DEFAULT_TAG;
import static cloud.tianai.captcha.generator.common.constant.SliderCaptchaConstant.*;
import static cloud.tianai.captcha.generator.common.constant.SliderCaptchaConstant.TEMPLATE_MASK_IMAGE_NAME;

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
        resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource(ClassPathResourceProvider.NAME, StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg"), DEFAULT_TAG));

        // 添加一些系统的 模板文件
        ResourceMap template1 = new ResourceMap(DEFAULT_TAG, 4);
        template1.put(TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/active.png")));
        template1.put(TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/fixed.png")));
        resourceStore.addTemplate(CaptchaTypeConstant.ROTATE, template1);
    }

    @Override
    public void doGenerateCaptchaImage(CaptchaTransferData transferData) {
        GenerateParam param = transferData.getParam();
        CustomData data = new CustomData();
        ResourceMap templateResource = requiredRandomGetTemplate(param.getType(), param.getTemplateImageTag());
        Resource resourceImage = requiredRandomGetResource(param.getType(), param.getBackgroundImageTag());
        BufferedImage background = getResourceImage(resourceImage);

        BufferedImage fixedTemplate = getTemplateImage(templateResource, TEMPLATE_FIXED_IMAGE_NAME);
        BufferedImage activeTemplate = getTemplateImage(templateResource, TEMPLATE_ACTIVE_IMAGE_NAME);
        BufferedImage maskTemplate = fixedTemplate;
        Optional<BufferedImage> maskTemplateOptional = getTemplateImageOfOptional(templateResource, TEMPLATE_MASK_IMAGE_NAME);
        if (maskTemplateOptional.isPresent()) {
            maskTemplate = maskTemplateOptional.get();
        }

        // 算出居中的x和y
        int x = background.getWidth() / 2 - fixedTemplate.getWidth() / 2;
        int y = background.getHeight() / 2 - fixedTemplate.getHeight() / 2;

        // 抠图部分
        BufferedImage cutImage = CaptchaImageUtils.cutImage(background, maskTemplate, x, y);
        BufferedImage rotateFixed = fixedTemplate;
        BufferedImage rotateActive = activeTemplate;
        if (param.getObfuscate()) {
            int randomDegree = randomInt(10, 350);
            rotateFixed = CaptchaImageUtils.rotateImage(fixedTemplate, randomDegree);
            randomDegree = randomInt(10, 350);
            rotateActive = CaptchaImageUtils.rotateImage(activeTemplate, randomDegree);
        }
        CaptchaImageUtils.overlayImage(background, rotateFixed, x, y);
        CaptchaImageUtils.overlayImage(cutImage, rotateActive, 0, 0);
        // 随机旋转抠图部分
        // 随机x， 转换为角度
        int randomX = randomInt(fixedTemplate.getWidth() + 10, background.getWidth() - 10);
        double degree = 360d - randomX / ((background.getWidth()) / 360d);
        // 旋转的透明图片是一张正方形的
        BufferedImage matrixTemplate = CaptchaImageUtils.createTransparentImage(background.getHeight(), background.getHeight());
        CaptchaImageUtils.centerOverlayAndRotateImage(matrixTemplate, cutImage, degree);

        RotateData rotateData = new RotateData();
        rotateData.degree = degree;
        rotateData.randomX = randomX;
        transferData.setTransferData(rotateData);
        transferData.setBackgroundImage(background);
        transferData.setTemplateImage(matrixTemplate);
        transferData.setTemplateResource(templateResource);
        transferData.setResourceImage(resourceImage);

//        return wrapRotateCaptchaInfo(degree, randomX, background, matrixTemplate, param, templateResource, resourceImage, data);
    }

    public static class RotateData {
        double degree;
        int randomX;
    }

    private String getObfuscateTag(String templateTag) {
        if (templateTag == null) {
            return "obfuscate";
        }
        return templateTag + "_" + "obfuscate";
    }

    @SneakyThrows
    @Override
    public ImageCaptchaInfo doWrapImageCaptchaInfo(CaptchaTransferData transferData) {
        GenerateParam param = transferData.getParam();
        BufferedImage backgroundImage = transferData.getBackgroundImage();
        BufferedImage sliderImage = transferData.getTemplateImage();
        Resource resourceImage = transferData.getResourceImage();
        ResourceMap templateResource = transferData.getTemplateResource();
        CustomData data = transferData.getCustomData();
        RotateData rotateData = (RotateData) transferData.getTransferData();
        ImageTransformData transform = getImageTransform().transform(param, backgroundImage, sliderImage, resourceImage, templateResource, data);
        RotateImageCaptchaInfo imageCaptchaInfo = RotateImageCaptchaInfo.of(rotateData.degree,
                rotateData.randomX,
                transform.getBackgroundImageUrl(),
                transform.getTemplateImageUrl(),
                resourceImage.getTag(),
                templateResource.getTag(),
                backgroundImage.getWidth(), backgroundImage.getHeight(),
                sliderImage.getWidth(), sliderImage.getHeight()
        );
        imageCaptchaInfo.setData(data);
        return imageCaptchaInfo;
    }

}
