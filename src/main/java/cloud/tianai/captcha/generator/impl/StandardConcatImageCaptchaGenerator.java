package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import static cloud.tianai.captcha.generator.common.util.CaptchaImageUtils.*;
import static cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_RESOURCE_PATH;

/**
 * @Author: 天爱有情
 * @date 2022/4/25 15:44
 * @Description 图片拼接滑动验证码生成器
 */
public class StandardConcatImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    protected ImageCaptchaResourceManager imageCaptchaResourceManager;

    public StandardConcatImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, boolean initDefaultResource) {
        this.imageCaptchaResourceManager = imageCaptchaResourceManager;
        if (initDefaultResource) {
            initDefaultResource();
        }
    }

    public void initDefaultResource() {
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加一些系统的资源文件
        resourceStore.addResource(CaptchaTypeConstant.CONCAT, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg")));
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {
        // 拼接验证码不需要模板 只需要背景图
        Collection<InputStream> inputStreams = new LinkedList<>();
        try {
            Resource resourceImage = imageCaptchaResourceManager.randomGetResource(param.getType());
            InputStream resourceInputStream = imageCaptchaResourceManager.getResourceInputStream(resourceImage);
            inputStreams.add(resourceInputStream);
            BufferedImage bgImage = wrapFile2BufferedImage(resourceInputStream);
            int spacingY = bgImage.getHeight() / 4;
            int randomY = ThreadLocalRandom.current().nextInt(spacingY, bgImage.getHeight() - spacingY);
            BufferedImage[] bgImageSplit = splitImage(randomY, true, bgImage);
            int spacingX = bgImage.getWidth() / 8;
            int randomX = ThreadLocalRandom.current().nextInt(spacingX, bgImage.getWidth() - bgImage.getWidth() / 5);
            BufferedImage[] bgImageTopSplit = splitImage(randomX, false, bgImageSplit[0]);

            BufferedImage sliderImage = concatImage(true,
                    bgImageTopSplit[0].getWidth()
                            + bgImageTopSplit[1].getWidth(), bgImageTopSplit[0].getHeight(), bgImageTopSplit[1], bgImageTopSplit[0]);
            bgImage = concatImage(false, bgImageSplit[1].getWidth(), sliderImage.getHeight() + bgImageSplit[1].getHeight(),
                    sliderImage, bgImageSplit[1]);
            return wrapConcatCaptchaInfo(randomX, randomY,bgImage,  param);
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
    private ImageCaptchaInfo wrapConcatCaptchaInfo(int randomX, int randomY, BufferedImage bgImage, GenerateParam param) {
        String backGroundImageBase64 = transform(bgImage, param.getBackgroundFormatName());
        ImageCaptchaInfo imageCaptchaInfo = ImageCaptchaInfo.of(backGroundImageBase64,
                null,
                bgImage.getWidth(),
                bgImage.getHeight(),
                null,
                null,
                randomX,
                CaptchaTypeConstant.CONCAT);
        imageCaptchaInfo.setData(randomY);
        imageCaptchaInfo.setTolerant(0.05F);
        return imageCaptchaInfo;
    }

    @Override
    public ImageCaptchaResourceManager getImageResourceManager() {
        return imageCaptchaResourceManager;
    }
}
