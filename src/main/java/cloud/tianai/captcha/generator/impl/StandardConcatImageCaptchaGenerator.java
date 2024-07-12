package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.model.dto.*;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;

import static cloud.tianai.captcha.common.constant.CommonConstant.DEFAULT_SLIDER_IMAGE_RESOURCE_PATH;
import static cloud.tianai.captcha.common.constant.CommonConstant.DEFAULT_TAG;
import static cloud.tianai.captcha.generator.common.util.CaptchaImageUtils.concatImage;
import static cloud.tianai.captcha.generator.common.util.CaptchaImageUtils.splitImage;

/**
 * @Author: 天爱有情
 * @date 2022/4/25 15:44
 * @Description 图片拼接滑动验证码生成器
 */
public class StandardConcatImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    public StandardConcatImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        super(imageCaptchaResourceManager);
    }

    public StandardConcatImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, ImageTransform imageTransform) {
        super(imageCaptchaResourceManager);
        setImageTransform(imageTransform);
    }

    public StandardConcatImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, ImageTransform imageTransform, CaptchaInterceptor interceptor) {
        super(imageCaptchaResourceManager);
        setImageTransform(imageTransform);
        setInterceptor(interceptor);
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
        resourceStore.addResource(CaptchaTypeConstant.CONCAT, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg"), DEFAULT_TAG));
    }

    @Override
    public void doGenerateCaptchaImage(CaptchaExchange captchaExchange) {
        GenerateParam param = captchaExchange.getParam();
        // 拼接验证码不需要模板 只需要背景图
        Resource resourceImage = requiredRandomGetResource(param.getType(), param.getBackgroundImageTag());
        BufferedImage bgImage = getResourceImage(resourceImage);
        int spacingY = bgImage.getHeight() / 4;
        int randomY = randomInt(spacingY, bgImage.getHeight() - spacingY);
        BufferedImage[] bgImageSplit = splitImage(randomY, true, bgImage);
        int spacingX = bgImage.getWidth() / 8;
        int randomX = randomInt(spacingX, bgImage.getWidth() - bgImage.getWidth() / 5);
        BufferedImage[] bgImageTopSplit = splitImage(randomX, false, bgImageSplit[0]);

        BufferedImage sliderImage = concatImage(true,
                bgImageTopSplit[0].getWidth()
                        + bgImageTopSplit[1].getWidth(), bgImageTopSplit[0].getHeight(), bgImageTopSplit[1], bgImageTopSplit[0]);
        bgImage = concatImage(false, bgImageSplit[1].getWidth(), sliderImage.getHeight() + bgImageSplit[1].getHeight(),
                sliderImage, bgImageSplit[1]);
        Data data = new Data();
        data.x = randomX;
        data.y = randomY;

        captchaExchange.setTransferData(data);
        captchaExchange.setBackgroundImage(bgImage);
        captchaExchange.setResourceImage(resourceImage);
    }

    public static class Data {
        int x;
        int y;
    }

    @SneakyThrows
    @Override
    public ImageCaptchaInfo doWrapImageCaptchaInfo(CaptchaExchange captchaExchange) {
        GenerateParam param = captchaExchange.getParam();
        BufferedImage bgImage = captchaExchange.getBackgroundImage();
        Resource resourceImage = captchaExchange.getResourceImage();
        CustomData customData = captchaExchange.getCustomData();
        ImageTransformData transform = getImageTransform().transform(param, bgImage, resourceImage, customData);
        Data data = (Data) captchaExchange.getTransferData();
        ImageCaptchaInfo imageCaptchaInfo = ImageCaptchaInfo.of(transform.getBackgroundImageUrl(),
                null,
                resourceImage.getTag(),
                null,
                bgImage.getWidth(),
                bgImage.getHeight(),
                null,
                null,
                data.x,
                CaptchaTypeConstant.CONCAT);
        customData.putViewData("randomY", data.y);
        imageCaptchaInfo.setTolerant(0.05F);
        return imageCaptchaInfo;
    }
}
