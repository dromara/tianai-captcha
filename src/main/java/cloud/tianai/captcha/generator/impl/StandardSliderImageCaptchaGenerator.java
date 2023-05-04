package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
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
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static cloud.tianai.captcha.common.constant.CommonConstant.DEFAULT_TAG;
import static cloud.tianai.captcha.generator.common.constant.SliderCaptchaConstant.*;
import static cloud.tianai.captcha.generator.common.constant.SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:06
 * @Description 滑块验证码模板
 */
@Slf4j
public class StandardSliderImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    /**
     * 默认的resource资源文件路径.
     */
    public static final String DEFAULT_SLIDER_IMAGE_RESOURCE_PATH = "META-INF/cut-image/resource";
    /**
     * 默认的template资源文件路径.
     */
    public static final String DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH = "META-INF/cut-image/template";

    public StandardSliderImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        super(imageCaptchaResourceManager);
    }

    public StandardSliderImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, ImageTransform imageTransform) {
        super(imageCaptchaResourceManager);
        setImageTransform(imageTransform);
    }

    @Override
    protected void doInit(boolean initDefaultResource) {
        if (initDefaultResource) {
            initDefaultResource();
        }
    }

    @SneakyThrows
    @Override
    public void doGenerateCaptchaImage(CaptchaTransferData transferData) {
        GenerateParam param = transferData.getParam();
        Boolean obfuscate = param.getObfuscate();
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
        // 获取随机的 x 和 y 轴
        int randomX = randomInt(fixedTemplate.getWidth() + 5, background.getWidth() - fixedTemplate.getWidth() - 10);
        int randomY = randomInt(background.getHeight() - fixedTemplate.getHeight());

        BufferedImage cutImage = CaptchaImageUtils.cutImage(background, maskTemplate, randomX, randomY);
        CaptchaImageUtils.overlayImage(background, fixedTemplate, randomX, randomY);
        if (obfuscate) {
            Optional<BufferedImage> obfuscateFixedTemplate = getTemplateImageOfOptional(templateResource, OBFUSCATE_TEMPLATE_FIXED_IMAGE_NAME);
            BufferedImage obfuscateImage = obfuscateFixedTemplate.orElseGet(() -> createObfuscate(fixedTemplate));
            int obfuscateX = randomObfuscateX(randomX, fixedTemplate.getWidth(), background.getWidth());
            CaptchaImageUtils.overlayImage(background, obfuscateImage, obfuscateX, randomY);
        }
        CaptchaImageUtils.overlayImage(cutImage, activeTemplate, 0, 0);
        // 这里创建一张png透明图片
        BufferedImage matrixTemplate = CaptchaImageUtils.createTransparentImage(activeTemplate.getWidth(), background.getHeight());
        CaptchaImageUtils.overlayImage(matrixTemplate, cutImage, 0, randomY);

        XandY xandY = new XandY();
        xandY.x = randomX;
        xandY.y = randomY;
        transferData.setBackgroundImage(background);
        transferData.setTemplateImage(matrixTemplate);
        transferData.setTemplateResource(templateResource);
        transferData.setResourceImage(resourceImage);
        transferData.setTransferData(xandY);
        // 后处理
//        applyPostProcessorBeforeWrapImageCaptchaInfo(transferData, this);
//        imageCaptchaInfo = wrapSliderCaptchaInfo(randomX, randomY, transferData);
//        applyPostProcessorAfterGenerateCaptchaImage(transferData, imageCaptchaInfo, this);
//        return imageCaptchaInfo;
    }

    protected BufferedImage createObfuscate(BufferedImage fixedImage) {
        // 随机拉伸或缩放宽高, 每次只拉伸高或者宽
        int width = fixedImage.getWidth();
        int height = fixedImage.getHeight();
        int window = randomInt(-3, 4);
        if (randomBoolean()) {
            height = height + window * 5;
        } else {
            width = width + window * 5;
        }
        int type = fixedImage.getColorModel().getTransparency();
        BufferedImage image = new BufferedImage(width, height, type);
        Graphics2D graphics = image.createGraphics();
        // 透明度
        double alpha = ThreadLocalRandom.current().nextDouble(0.5, 0.8);
        AlphaComposite alphaComposite = AlphaComposite.Src.derive((float) alpha);
        graphics.setComposite(alphaComposite);
        graphics.drawImage(fixedImage, 0, 0, width, height, null);
        return image;
    }


    public static class XandY {
        int x;
        int y;
    }

    @SneakyThrows
    @Override
    public SliderImageCaptchaInfo doWrapImageCaptchaInfo(CaptchaTransferData transferData) {
        GenerateParam param = transferData.getParam();
        BufferedImage backgroundImage = transferData.getBackgroundImage();
        BufferedImage sliderImage = transferData.getTemplateImage();
        Resource resourceImage = transferData.getResourceImage();
        ResourceMap templateResource = transferData.getTemplateResource();
        CustomData customData = transferData.getCustomData();
        XandY data = (XandY) transferData.getTransferData();
        ImageTransformData transform = getImageTransform().transform(param, backgroundImage, sliderImage, resourceImage, templateResource, customData);

        SliderImageCaptchaInfo imageCaptchaInfo = SliderImageCaptchaInfo.of(data.x, data.y,
                transform.getBackgroundImageUrl(),
                transform.getTemplateImageUrl(),
                resourceImage.getTag(),
                templateResource.getTag(),
                backgroundImage.getWidth(), backgroundImage.getHeight(),
                sliderImage.getWidth(), sliderImage.getHeight()
        );
        imageCaptchaInfo.setData(customData);
        return imageCaptchaInfo;
    }

    protected int randomObfuscateX(int sliderX, int slWidth, int bgWidth) {
        if (bgWidth / 2 > (sliderX + (slWidth / 2))) {
            // 右边混淆
            return randomInt(sliderX + slWidth, bgWidth - slWidth);
        }
        // 左边混淆
        return randomInt(slWidth, sliderX - slWidth);
    }

    /**
     * 初始化默认资源
     */
    public void initDefaultResource() {
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加一些系统的资源文件
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg"), DEFAULT_TAG));

        // 添加一些系统的 模板文件
        ResourceMap template1 = new ResourceMap(DEFAULT_TAG, 4);
        template1.put(TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/active.png")));
        template1.put(TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/fixed.png")));
        resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template1);

        ResourceMap template2 = new ResourceMap(DEFAULT_TAG, 4);
        template2.put(TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/active.png")));
        template2.put(TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/fixed.png")));
        resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template2);
    }
}
