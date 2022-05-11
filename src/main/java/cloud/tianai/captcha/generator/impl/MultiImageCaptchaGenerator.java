package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.util.ObjectUtils;
import cloud.tianai.captcha.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @Author: 天爱有情
 * @date 2022/4/24 9:27
 * @Description 根据type 匹配对应的验证码生成器
 */
public class MultiImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    private Map<String, ImageCaptchaGenerator> imageCaptchaGeneratorMap = new HashMap<>(4);
    private Map<String, BiFunction<String, MultiImageCaptchaGenerator, ImageCaptchaGenerator>> imageCaptchaGeneratorProviderMap = new HashMap<>(4);

    @Setter
    @Getter
    private String defaultCaptcha = CaptchaTypeConstant.SLIDER;

    public MultiImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, boolean initDefaultResource) {
        super(imageCaptchaResourceManager, initDefaultResource);
    }

    @Override
    protected void doInit() {
        // 滑块验证码
        addImageCaptchaGeneratorProvider(CaptchaTypeConstant.SLIDER, (type, context) ->
                new StandardSliderImageCaptchaGenerator(imageCaptchaResourceManager, initDefaultResource).init());
        // 旋转验证码
        addImageCaptchaGeneratorProvider(CaptchaTypeConstant.ROTATE, (type, context) ->
                new StandardRotateImageCaptchaGenerator(imageCaptchaResourceManager, initDefaultResource).init());
        // 拼接验证码
        addImageCaptchaGeneratorProvider(CaptchaTypeConstant.CONCAT, (type, context) ->
                new StandardConcatImageCaptchaGenerator(imageCaptchaResourceManager, initDefaultResource).init());
        // 点选文字验证码
        addImageCaptchaGeneratorProvider(CaptchaTypeConstant.WORD_IMAGE_CLICK, (type, context) ->
                new StandardRandomWordClickImageCaptchaGenerator(imageCaptchaResourceManager, initDefaultResource).init());
    }

    public void addImageCaptchaGeneratorProvider(String key, BiFunction<String, MultiImageCaptchaGenerator, ImageCaptchaGenerator> provider) {
        imageCaptchaGeneratorProviderMap.put(key, provider);
    }

    public BiFunction<String, MultiImageCaptchaGenerator, ImageCaptchaGenerator> removeImageCaptchaGeneratorProvider(String key) {
        return imageCaptchaGeneratorProviderMap.remove(key);
    }

    public BiFunction<String, MultiImageCaptchaGenerator, ImageCaptchaGenerator> getImageCaptchaGeneratorProvider(String key) {
        return imageCaptchaGeneratorProviderMap.get(key);
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
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {
        String type = param.getType();
        if (ObjectUtils.isEmpty(type)) {
            param.setType(defaultCaptcha);
            type = defaultCaptcha;
        }
        ImageCaptchaGenerator imageCaptchaGenerator = imageCaptchaGeneratorMap.get(type);
        if (imageCaptchaGenerator == null) {
            BiFunction<String, MultiImageCaptchaGenerator, ImageCaptchaGenerator> provider = imageCaptchaGeneratorProviderMap.get(type);
            if (provider == null) {
                throw new IllegalArgumentException("生成验证码失败，错误的type类型:" + type);
            }
            imageCaptchaGenerator = imageCaptchaGeneratorMap.computeIfAbsent(type, k -> provider.apply(k, this));
        }

        return imageCaptchaGenerator.generateCaptchaImage(param);
    }
}
