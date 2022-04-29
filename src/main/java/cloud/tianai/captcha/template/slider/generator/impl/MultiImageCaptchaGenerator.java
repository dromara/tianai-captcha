package cloud.tianai.captcha.template.slider.generator.impl;

import cloud.tianai.captcha.template.slider.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.template.slider.common.util.ObjectUtils;
import cloud.tianai.captcha.template.slider.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2022/4/24 9:27
 * @Description 根据type 匹配对应的验证码生成器
 */
public class MultiImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    private Map<String, ImageCaptchaGenerator> imageCaptchaGeneratorMap = new HashMap<>(4);

    private ImageCaptchaResourceManager imageCaptchaResourceManager;
    private boolean initDefaultResource;

    private String defaultCaptcha = CaptchaTypeConstant.SLIDER;

    public MultiImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, boolean initDefaultResource) {
        this.imageCaptchaResourceManager = imageCaptchaResourceManager;
        this.initDefaultResource = initDefaultResource;
        init();
    }

    protected void init() {
        // 滑块验证码
        addImageCaptchaGenerator(CaptchaTypeConstant.SLIDER, new StandardSliderImageCaptchaGenerator(imageCaptchaResourceManager, initDefaultResource));
        // 旋转验证码
        addImageCaptchaGenerator(CaptchaTypeConstant.ROTATE, new StandardRotateImageCaptchaGenerator(imageCaptchaResourceManager, initDefaultResource));
        // 拼接验证码
        addImageCaptchaGenerator(CaptchaTypeConstant.CONCAT, new StandardConcatImageCaptchaGenerator(imageCaptchaResourceManager, initDefaultResource));
        // 点选文字验证码
        addImageCaptchaGenerator(CaptchaTypeConstant.WORD_IMAGE_CLICK, new StandardRandomWordClickImageCaptchaGenerator(imageCaptchaResourceManager, initDefaultResource));
    }

    public void addImageCaptchaGenerator(String key, ImageCaptchaGenerator captchaGenerator) {
        imageCaptchaGeneratorMap.put(key, captchaGenerator);
    }

    public ImageCaptchaGenerator removeImageCaptchaGenerator(String key) {
        return imageCaptchaGeneratorMap.remove(key);
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
            throw new IllegalArgumentException("生成验证码失败，错误的type类型:" + type);
        }

        return imageCaptchaGenerator.generateCaptchaImage(param);
    }

    @Override
    public ImageCaptchaResourceManager getImageResourceManager() {
        return imageCaptchaResourceManager;
    }
}
