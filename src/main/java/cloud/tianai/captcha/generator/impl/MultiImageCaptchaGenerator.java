package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.util.ObjectUtils;
import cloud.tianai.captcha.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageCaptchaGeneratorProvider;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.provider.StandardConcatImageCaptchaGeneratorProvider;
import cloud.tianai.captcha.generator.impl.provider.StandardRandomWordClickImageCaptchaGeneratorProvider;
import cloud.tianai.captcha.generator.impl.provider.StandardRotateImageCaptchaGeneratorProvider;
import cloud.tianai.captcha.generator.impl.provider.StandardSliderImageImageCaptchaGeneratorProvider;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: 天爱有情
 * @date 2022/4/24 9:27
 * @Description 根据type 匹配对应的验证码生成器
 */
public class MultiImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    protected Map<String, ImageCaptchaGenerator> imageCaptchaGeneratorMap = new ConcurrentHashMap<>(4);
    protected Map<String, ImageCaptchaGeneratorProvider> imageCaptchaGeneratorProviderMap = new HashMap<>(4);

    @Setter
    @Getter
    private String defaultCaptcha = CaptchaTypeConstant.SLIDER;

    protected boolean initDefaultResource = false;

    public MultiImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        super(imageCaptchaResourceManager);
    }

    public MultiImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, ImageTransform imageTransform) {
        super(imageCaptchaResourceManager);
        setImageTransform(imageTransform);
    }

    @Override
    protected void doInit(boolean initDefaultResource) {
        this.initDefaultResource = initDefaultResource;
        // 滑块验证码
        addImageCaptchaGeneratorProvider(new StandardSliderImageImageCaptchaGeneratorProvider());
        // 旋转验证码
        addImageCaptchaGeneratorProvider(new StandardRotateImageCaptchaGeneratorProvider());
        // 拼接验证码
        addImageCaptchaGeneratorProvider(new StandardConcatImageCaptchaGeneratorProvider());
        // 点选文字验证码
        addImageCaptchaGeneratorProvider(new StandardRandomWordClickImageCaptchaGeneratorProvider());
    }

    public void addImageCaptchaGeneratorProvider(ImageCaptchaGeneratorProvider provider) {
        imageCaptchaGeneratorProviderMap.put(provider.getType(), provider);
    }

    public ImageCaptchaGeneratorProvider removeImageCaptchaGeneratorProvider(String type) {
        return imageCaptchaGeneratorProviderMap.remove(type);
    }

    public ImageCaptchaGeneratorProvider getImageCaptchaGeneratorProvider(String type) {
        return imageCaptchaGeneratorProviderMap.get(type);
    }

    public void addImageCaptchaGenerator(String key, ImageCaptchaGenerator captchaGenerator) {
        imageCaptchaGeneratorMap.put(key, captchaGenerator);
    }

    public ImageCaptchaGenerator removeImageCaptchaGenerator(String key) {
        return imageCaptchaGeneratorMap.remove(key);
    }

    public ImageCaptchaGenerator getImageCaptchaGenerator(String key) {
        return imageCaptchaGeneratorMap.get(key);
    }

    @Override
    public ImageCaptchaInfo doGenerateCaptchaImage(GenerateParam param) {
        String type = param.getType();
        if (ObjectUtils.isEmpty(type)) {
            param.setType(defaultCaptcha);
            type = defaultCaptcha;
        }
        ImageCaptchaGenerator imageCaptchaGenerator = requireGetCaptchaGenerator(type);
        return imageCaptchaGenerator.generateCaptchaImage(param);
    }

    public ImageCaptchaGenerator requireGetCaptchaGenerator(String type) {
        ImageCaptchaGenerator imageCaptchaGenerator = imageCaptchaGeneratorMap.computeIfAbsent(type, t -> {
            ImageCaptchaGeneratorProvider provider = imageCaptchaGeneratorProviderMap.get(t);
            if (provider == null) {
                throw new IllegalArgumentException("生成验证码失败，错误的type类型:" + t);
            }
            return provider.get(getImageResourceManager(), getImageTransform()).init(initDefaultResource);
        });

        return imageCaptchaGenerator;
    }

    @Override
    public void setImageResourceManager(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        super.setImageResourceManager(imageCaptchaResourceManager);
        for (ImageCaptchaGenerator imageCaptchaGenerator : imageCaptchaGeneratorMap.values()) {
            imageCaptchaGenerator.setImageResourceManager(imageCaptchaResourceManager);
        }
    }

    @Override
    public void setImageTransform(ImageTransform imageTransform) {
        super.setImageTransform(imageTransform);
        for (ImageCaptchaGenerator imageCaptchaGenerator : imageCaptchaGeneratorMap.values()) {
            imageCaptchaGenerator.setImageTransform(imageTransform);
        }
    }
}
