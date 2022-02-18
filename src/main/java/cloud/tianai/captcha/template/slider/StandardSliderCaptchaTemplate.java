package cloud.tianai.captcha.template.slider;

import cloud.tianai.captcha.template.slider.provider.ClassPathResourceProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static cloud.tianai.captcha.template.slider.CaptchaImageUtils.*;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:06
 * @Description 滑块验证码模板
 */
@Slf4j
public class StandardSliderCaptchaTemplate implements SliderCaptchaTemplate {

    /**
     * 默认的resource资源文件路径.
     */
    public static final String DEFAULT_SLIDER_IMAGE_RESOURCE_PATH = "META-INF/cut-image/resource";
    /**
     * 默认的template资源文件路径.
     */
    public static final String DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH = "META-INF/cut-image/template";

    public static String DEFAULT_BG_IMAGE_TYPE = "jpeg";
    public static String DEFAULT_SLIDER_IMAGE_TYPE = "png";
    public static float DEFAULT_TOLERANT = 0.02f;

    protected final SliderCaptchaResourceManager sliderCaptchaResourceManager;

    @Getter
    @Setter
    /** 容错值. */
    public float tolerant = DEFAULT_TOLERANT;
    @Getter
    @Setter
    /** 默认背景图片类型. */
    public String defaultBgImageType = DEFAULT_BG_IMAGE_TYPE;
    @Getter
    @Setter
    /** 默认滑块图片类型. */
    public String defaultSliderImageType = DEFAULT_SLIDER_IMAGE_TYPE;

    public StandardSliderCaptchaTemplate(SliderCaptchaResourceManager sliderCaptchaResourceManager,
                                         boolean initDefaultResource) {
        this.sliderCaptchaResourceManager = sliderCaptchaResourceManager;
        if (initDefaultResource) {
            initDefaultResource();
        }
    }

    @Override
    public SliderCaptchaInfo getSlideImageInfo() {
        return getSlideImageInfo(defaultBgImageType, defaultSliderImageType);
    }

    @SneakyThrows
    @Override
    public SliderCaptchaInfo getSlideImageInfo(String backgroundFormatName, String sliderFormatName) {
        return getSlideImageInfo(GenerateParam.builder()
                .backgroundFormatName(backgroundFormatName)
                .sliderFormatName(sliderFormatName)
                .obfuscate(false)
                .build());
    }

    @SneakyThrows
    @Override
    public SliderCaptchaInfo getSlideImageInfo(GenerateParam param) {
        Boolean obfuscate = param.getObfuscate();
        Map<String, Resource> templateImages = sliderCaptchaResourceManager.randomGetTemplate();
        if (templateImages == null || templateImages.isEmpty()) {
            return null;
        }
        Collection<InputStream> inputStreams = new LinkedList<>();
        try {
            Resource resourceImage = sliderCaptchaResourceManager.randomGetResource();
            InputStream resourceInputStream = sliderCaptchaResourceManager.getResourceInputStream(resourceImage);
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
    public SliderCaptchaInfo wrapSliderCaptchaInfo(int randomX,
                                                   int randomY,
                                                   BufferedImage backgroundImage,
                                                   BufferedImage sliderImage,
                                                   GenerateParam param) {
        String backgroundFormatName = param.getBackgroundFormatName();
        String sliderFormatName = param.getSliderFormatName();
        String backGroundImageBase64 = transform(backgroundImage, backgroundFormatName);
        String sliderImageBase64 = transform(sliderImage, sliderFormatName);
        return SliderCaptchaInfo.of(randomX, randomY,
                backGroundImageBase64,
                sliderImageBase64,
                backgroundImage.getWidth(), backgroundImage.getHeight(),
                sliderImage.getWidth(), sliderImage.getHeight()
        );
    }

    /**
     * 将图片转换成字符串格式
     *
     * @param bufferedImage 图片
     * @param formatType    格式化类型
     * @return String
     * @throws IOException
     */
    public String transform(BufferedImage bufferedImage, String formatType) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, formatType, byteArrayOutputStream);
        //转换成字节码
        byte[] data = byteArrayOutputStream.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(data);
        return "data:image/" + formatType + ";base64,".concat(base64);
    }

    /**
     * 百分比对比
     *
     * @param newPercentage 用户百分比
     * @param oriPercentage 原百分比
     * @return true 成功 false 失败
     */
    @Override
    public boolean percentageContrast(Float newPercentage, Float oriPercentage) {
        if (newPercentage == null || Float.isNaN(newPercentage) || Float.isInfinite(newPercentage)
                || oriPercentage == null || Float.isNaN(oriPercentage) || Float.isInfinite(oriPercentage)) {
            return false;
        }
        // 容错值
        float maxTolerant = oriPercentage + tolerant;
        float minTolerant = oriPercentage - tolerant;
        return newPercentage >= minTolerant && newPercentage <= maxTolerant;
    }

    @Override
    public SliderCaptchaResourceManager getSlideImageResourceManager() {
        return sliderCaptchaResourceManager;
    }

    protected int randomObfuscateX(int sliderX, int slWidth, int bgWidth) {
        if (bgWidth / 2 > (sliderX + (slWidth / 2))) {
            // 右边混淆
            return ThreadLocalRandom.current().nextInt(sliderX + slWidth, bgWidth - slWidth);
        }
        // 左边混淆
        return ThreadLocalRandom.current().nextInt(slWidth, sliderX - slWidth);
    }

    protected InputStream getTemplateFile(Map<String, Resource> templateImages, String imageName) {
        Resource resource = templateImages.get(imageName);
        if (resource == null) {
            throw new IllegalArgumentException("查找模板异常， 该模板下未找到 ".concat(imageName));
        }
        return sliderCaptchaResourceManager.getResourceInputStream(resource);
    }

    /**
     * 初始化默认资源
     */
    public void initDefaultResource() {
        ResourceStore resourceStore = sliderCaptchaResourceManager.getResourceStore();
        // 添加一些系统的资源文件
        resourceStore.addResource(new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg")));

        // 添加一些系统的 模板文件
        Map<String, Resource> template1 = new HashMap<>(4);
        template1.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/active.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/fixed.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/matrix.png")));
        resourceStore.addTemplate(template1);


        Map<String, Resource> template2 = new HashMap<>(4);
        template2.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/active.png")));
        template2.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/fixed.png")));
        template2.put(SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/matrix.png")));
        resourceStore.addTemplate(template2);
    }
}
