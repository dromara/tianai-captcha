package cloud.tianai.captcha.template.slider.generator.impl;

import cloud.tianai.captcha.template.slider.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.template.slider.generator.common.constant.SliderCaptchaConstant;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.SliderImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.resource.ResourceStore;
import cloud.tianai.captcha.template.slider.resource.common.model.dto.Resource;
import cloud.tianai.captcha.template.slider.resource.impl.provider.ClassPathResourceProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static cloud.tianai.captcha.template.slider.generator.common.util.CaptchaImageUtils.*;

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

    protected final ImageCaptchaResourceManager imageCaptchaResourceManager;


    public StandardSliderImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager,
                                               boolean initDefaultResource) {
        this.imageCaptchaResourceManager = imageCaptchaResourceManager;
        if (initDefaultResource) {
            initDefaultResource();
        }
    }

    @SneakyThrows
    @Override
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {
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

//        BufferedImage cutTemplate = warpFile2BufferedImage(getTemplateFile(templateImages, CUT_IMAGE_NAME));

            // 获取随机的 x 和 y 轴
            int randomX = ThreadLocalRandom.current().nextInt(fixedTemplate.getWidth() + 5, targetBackground.getWidth() - fixedTemplate.getWidth() - 10);
            int randomY = ThreadLocalRandom.current().nextInt(targetBackground.getHeight() - fixedTemplate.getHeight());

            overlayImage(targetBackground, fixedTemplate, randomX, randomY);
            if (obfuscate) {
                // 加入混淆滑块
                int obfuscateX = randomObfuscateX(randomX, fixedTemplate.getWidth(), targetBackground.getWidth());
                overlayImage(targetBackground, fixedTemplate, obfuscateX, randomY);
            }
            BufferedImage cutImage = cutImage(cutBackground, fixedTemplate, randomX, randomY);
            overlayImage(cutImage, activeTemplate, 0, 0);
            overlayImage(matrixTemplate, cutImage, 0, randomY);
            return wrapSliderCaptchaInfo(randomX, randomY, targetBackground, matrixTemplate, param);
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

    /**
     * 包装成 SliderCaptchaInfo
     *
     * @param randomX         随机生成的 x轴
     * @param randomY         随机生成的 y轴
     * @param backgroundImage 背景图片
     * @param sliderImage     滑块图片
     * @param param           接口传入参数
     * @return SliderCaptchaInfo
     */
    @SneakyThrows
    public SliderImageCaptchaInfo wrapSliderCaptchaInfo(int randomX,
                                                        int randomY,
                                                        BufferedImage backgroundImage,
                                                        BufferedImage sliderImage,
                                                        GenerateParam param) {
        String backgroundFormatName = param.getBackgroundFormatName();
        String sliderFormatName = param.getSliderFormatName();
        String backGroundImageBase64 = transform(backgroundImage, backgroundFormatName);
        String sliderImageBase64 = transform(sliderImage, sliderFormatName);
        return SliderImageCaptchaInfo.of(randomX, randomY,
                backGroundImageBase64,
                sliderImageBase64,
                backgroundImage.getWidth(), backgroundImage.getHeight(),
                sliderImage.getWidth(), sliderImage.getHeight()
        );
    }

    @Override
    public ImageCaptchaResourceManager getImageResourceManager() {
        return imageCaptchaResourceManager;
    }

    protected int randomObfuscateX(int sliderX, int slWidth, int bgWidth) {
        if (bgWidth / 2 > (sliderX + (slWidth / 2))) {
            // 右边混淆
            return ThreadLocalRandom.current().nextInt(sliderX + slWidth, bgWidth - slWidth);
        }
        // 左边混淆
        return ThreadLocalRandom.current().nextInt(slWidth, sliderX - slWidth);
    }

    /**
     * 初始化默认资源
     */
    public void initDefaultResource() {
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加一些系统的资源文件
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg")));

        // 添加一些系统的 模板文件
        Map<String, Resource> template1 = new HashMap<>(4);
        template1.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/active.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/fixed.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/matrix.png")));
        resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template1);


        Map<String, Resource> template2 = new HashMap<>(4);
        template2.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/active.png")));
        template2.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/fixed.png")));
        template2.put(SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/matrix.png")));
        resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template2);
    }
}
