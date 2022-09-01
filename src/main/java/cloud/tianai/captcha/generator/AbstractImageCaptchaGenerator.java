package cloud.tianai.captcha.generator;

import cloud.tianai.captcha.common.exception.ImageCaptchaException;
import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2022/4/22 16:30
 * @Description 抽象的验证码生成器
 */
@Slf4j
public abstract class AbstractImageCaptchaGenerator implements ImageCaptchaGenerator {
    public static String DEFAULT_BG_IMAGE_TYPE = "jpeg";
    public static String DEFAULT_SLIDER_IMAGE_TYPE = "png";

    @Getter
    @Setter
    /** 默认背景图片类型. */
    public String defaultBgImageType = DEFAULT_BG_IMAGE_TYPE;
    @Getter
    @Setter
    /** 默认滑块图片类型. */
    public String defaultSliderImageType = DEFAULT_SLIDER_IMAGE_TYPE;

    /** 资源管理器. */
    protected ImageCaptchaResourceManager imageCaptchaResourceManager;

    /** 图片转换器. */
    protected ImageTransform imageTransform;

    @Getter
    private boolean init = false;

    public AbstractImageCaptchaGenerator() {
    }

    @Override
    public ImageCaptchaGenerator init(boolean initDefaultResource) {
        if (init) {
            return this;
        }
        init = true;
        try {
            log.info("图片验证码[{}]初始化...", this.getClass().getSimpleName());
            // 设置默认图片转换器
            if (getImageTransform() == null) {
                setImageTransform(new Base64ImageTransform());
            }
            doInit(initDefaultResource);
        } catch (Exception e) {
            init = false;
            log.error("[{}]初始化失败,ex", this.getClass().getSimpleName(), e);
            throw e;
        }
        return this;
    }

    public AbstractImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        this.imageCaptchaResourceManager = imageCaptchaResourceManager;
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(String type) {
        return generateCaptchaImage(type, defaultBgImageType, defaultSliderImageType);
    }

    @SneakyThrows
    @Override
    public ImageCaptchaInfo generateCaptchaImage(String type, String backgroundFormatName, String sliderFormatName) {
        return generateCaptchaImage(GenerateParam.builder()
                .type(type)
                .backgroundFormatName(backgroundFormatName)
                .sliderFormatName(sliderFormatName)
                .obfuscate(false)
                .build());
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {
        assertInit();
        return doGenerateCaptchaImage(param);
    }


    protected Map<String, Resource> requiredRandomGetTemplate(String type) {
        Map<String, Resource> templateMap = imageCaptchaResourceManager.randomGetTemplate(type);
        if (CollectionUtils.isEmpty(templateMap)) {
            throw new ImageCaptchaException("随机获取模板资源失败， 获取到的资源为空, type=" + type);
        }
        return templateMap;
    }

    protected Resource requiredRandomGetResource(String type) {
        Resource resource = imageCaptchaResourceManager.randomGetResource(type);
        if (resource == null) {
            throw new ImageCaptchaException("随机获取资源失败， 获取到的资源为空, type=" + type);
        }
        return resource;
    }


    protected InputStream getTemplateFile(Map<String, Resource> templateImages, String imageName) {
        Resource resource = templateImages.get(imageName);
        if (resource == null) {
            throw new IllegalArgumentException("查找模板异常， 该模板下未找到 ".concat(imageName));
        }
        return getImageResourceManager().getResourceInputStream(resource);
    }


    protected void assertInit() {
        if (!init) {
            throw new IllegalStateException("请先调用 init(...) 初始化方法进行初始化");
        }
    }

    /**
     * 初始化
     *
     * @param initDefaultResource 是否初始化默认资源
     */
    protected abstract void doInit(boolean initDefaultResource);

    /**
     * 生成验证码方法
     *
     * @param param param
     * @return ImageCaptchaInfo
     */
    protected abstract ImageCaptchaInfo doGenerateCaptchaImage(GenerateParam param);

    @Override
    public ImageCaptchaResourceManager getImageResourceManager() {
        return imageCaptchaResourceManager;
    }

    @Override
    public void setImageResourceManager(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        this.imageCaptchaResourceManager = imageCaptchaResourceManager;
    }

    @Override
    public ImageTransform getImageTransform() {
        return imageTransform;
    }

    @Override
    public void setImageTransform(ImageTransform imageTransform) {
        this.imageTransform = imageTransform;
    }
}
