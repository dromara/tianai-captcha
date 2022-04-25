package cloud.tianai.captcha.template.slider.generator.impl;

import cloud.tianai.captcha.template.slider.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.resource.ResourceStore;
import cloud.tianai.captcha.template.slider.resource.common.model.dto.Resource;
import cloud.tianai.captcha.template.slider.resource.impl.provider.ClassPathResourceProvider;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import static cloud.tianai.captcha.template.slider.common.util.CaptchaImageUtils.*;
import static cloud.tianai.captcha.template.slider.generator.impl.StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_RESOURCE_PATH;

/**
 * @Author: 天爱有情
 * @date 2022/4/25 15:44
 * @Description 图片拼接滑动验证码生成器
 */
public class StandardConcatImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    protected final ImageCaptchaResourceManager imageCaptchaResourceManager;

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
            int spacing = bgImage.getHeight() / 4;
            BufferedImage[] bgImageSplit = splitImage(ThreadLocalRandom.current().nextInt(spacing, bgImage.getHeight() - spacing), true, bgImage);

            spacing = bgImage.getWidth() / 8;
            int randomX = ThreadLocalRandom.current().nextInt(spacing, bgImage.getWidth() - spacing);
            BufferedImage[] bgImageTopSplit = splitImage(randomX, false, bgImageSplit[0]);

            BufferedImage sliderImage = concatImage(true,
                    bgImageTopSplit[0].getWidth()
                            + bgImageTopSplit[1].getWidth()
                            + bgImageSplit[0].getWidth(), bgImageTopSplit[1].getHeight(), bgImageSplit[0], bgImageTopSplit[0]);
            return wrapConcatCaptchaInfo(randomX, bgImageSplit[1], sliderImage, param);
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
    private ImageCaptchaInfo wrapConcatCaptchaInfo(int randomX, BufferedImage bgImage, BufferedImage sliderImage, GenerateParam param) {
        String backGroundImageBase64 = transform(bgImage, param.getBackgroundFormatName());
        String sliderImageBase64 = transform(sliderImage, param.getSliderFormatName());
        return ImageCaptchaInfo.of(backGroundImageBase64, sliderImageBase64, bgImage.getWidth(), bgImage.getHeight(), sliderImage.getWidth(), sliderImage.getHeight(), randomX);
    }

    @Override
    public ImageCaptchaResourceManager getImageResourceManager() {
        return imageCaptchaResourceManager;
    }
}
