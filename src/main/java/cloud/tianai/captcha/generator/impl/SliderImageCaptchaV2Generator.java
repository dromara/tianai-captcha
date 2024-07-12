package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.model.dto.CaptchaExchange;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.UUID;

import static cloud.tianai.captcha.common.constant.CommonConstant.*;

public class SliderImageCaptchaV2Generator extends AbstractImageCaptchaGenerator {

    /** 模板滑块固定名称. */
    public static String TEMPLATE_ACTIVE_IMAGE_NAME = "active.png";
    /** 模板凹槽固定名称. */
    public static String TEMPLATE_FIXED_IMAGE_NAME = "fixed.png";
    /** 模板蒙版. */
    public static String TEMPLATE_MASK_IMAGE_NAME = "mask.png";

    public SliderImageCaptchaV2Generator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        super(imageCaptchaResourceManager);
    }

    public SliderImageCaptchaV2Generator(ImageCaptchaResourceManager imageCaptchaResourceManager, ImageTransform imageTransform) {
        super(imageCaptchaResourceManager);
        setImageTransform(imageTransform);
    }

    @Override
    protected void doInit(boolean initDefaultResource) {
        if (initDefaultResource) {
            initDefaultResource();
        }
    }

    @Override
    @SneakyThrows
    protected void doGenerateCaptchaImage(CaptchaExchange captchaExchange) {
        GenerateParam param = captchaExchange.getParam();
        ResourceMap templateResource = requiredRandomGetTemplate(param.getType(), param.getTemplateImageTag());
        Resource resourceImage = requiredRandomGetResource(param.getType(), param.getBackgroundImageTag());
        BufferedImage background = getResourceImage(resourceImage);
        BufferedImage fixedTemplate = getTemplateImage(templateResource, TEMPLATE_FIXED_IMAGE_NAME);
        BufferedImage activeTemplate = getTemplateImage(templateResource, TEMPLATE_ACTIVE_IMAGE_NAME);

        int randomX = randomInt(fixedTemplate.getWidth() + 5, background.getWidth() - background.getHeight() - 10);
        int randomY = randomInt(background.getHeight() - fixedTemplate.getHeight());


        // 随机角度
//        double randomDegree = randomDouble(10, 80);
//        double randomDegree2 = randomDouble(10, 80);

        int randomObfuscateX = randomObfuscateX(randomX, fixedTemplate.getWidth(), background.getWidth() - background.getHeight() - 10);
        int randomObfuscateY = randomInt(background.getHeight() - fixedTemplate.getHeight());

        double rotatePosRight = background.getHeight();


        BufferedImage cutImage = CaptchaImageUtils.cutImage(background, fixedTemplate, randomX, randomY);


        // 正确的图
        Graphics2D backgroundGraphics = background.createGraphics();
//        backgroundGraphics.rotate(Math.toRadians(randomDegree), randomX + fixedTemplate.getWidth(), rotatePosRight);
        backgroundGraphics.drawImage(fixedTemplate, randomX, randomY, null);
//        backgroundGraphics.rotate(Math.toRadians(-randomDegree + randomDegree2), randomX + fixedTemplate.getWidth(), rotatePosRight);
        // 干扰图
        backgroundGraphics.drawImage(fixedTemplate, randomObfuscateX, randomObfuscateY, null);

        backgroundGraphics.dispose();

        CaptchaImageUtils.overlayImage(cutImage, activeTemplate, 0, 0);
        BufferedImage matrixTemplate = CaptchaImageUtils.createTransparentImage(activeTemplate.getWidth(), background.getHeight());
        CaptchaImageUtils.overlayImage(matrixTemplate, cutImage, 0, randomY);


        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Thinkpad\\Desktop\\captcha\\temp\\1\\test-bg" + UUID.randomUUID().toString()
                + ".jpg");
        ImageIO.write(background, "jpg", fileOutputStream);
        fileOutputStream.close();

//        FileOutputStream fileOutputStream2 = new FileOutputStream("C:\\Users\\Thinkpad\\Desktop\\captcha\\temp\\test-slider.png");
//        ImageIO.write(matrixTemplate, "png", fileOutputStream2);
//        fileOutputStream2.close();


        System.out.println("randomX=" + randomX);
        System.out.println("randomY=" + randomY);
//        System.out.println("randomDegree=" + randomDegree);
        System.out.println("randomObfuscateX=" + randomObfuscateX);
        System.out.println("randomObfuscateY=" + randomY);


//        CaptchaImageUtils.overlayImage(background, fixedTemplate, randomX, randomY);
//
//        BufferedImage matrixTemplate = CaptchaImageUtils.createTransparentImage(activeTemplate.getWidth(), background.getHeight());


    }


    protected double randomDegree(double hypotenuse, double adjacent) {
        if (hypotenuse <= adjacent) {
            return 90.0;
        }
        // 使用勾股定理计算对边的长度
        double opposite = Math.sqrt(hypotenuse * hypotenuse - adjacent * adjacent);

        // 使用Math.atan2计算角度（以弧度为单位）
        double angleInRadians = Math.atan2(opposite, adjacent);

        // 将弧度转换为度
        double angleInDegrees = Math.toDegrees(angleInRadians);

        return angleInDegrees;
    }

    protected int randomObfuscateX(int sliderX, int slWidth, int bgWidth) {
        if (bgWidth / 2 > (sliderX + (slWidth / 2))) {
            // 右边混淆
            return randomInt(sliderX + 10, bgWidth - slWidth);
        }
        // 左边混淆
        return randomInt(10, sliderX - slWidth);
    }

    @Override
    protected ImageCaptchaInfo doWrapImageCaptchaInfo(CaptchaExchange captchaExchange) {
        return null;
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
