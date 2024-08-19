package cloud.tianai.captcha.application;

import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.MatchParam;

/**
 * @Author: 天爱有情
 * @date 2022/3/2 14:22
 * @Description 用于SliderCaptchaApplication增加附属功能
 */
public class FilterImageCaptchaApplication implements ImageCaptchaApplication {


    protected ImageCaptchaApplication target;

    public FilterImageCaptchaApplication(ImageCaptchaApplication target) {
        this.target = target;
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha() {
        return target.generateCaptcha();
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(String type) {
        return target.generateCaptcha(type);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(CaptchaImageType captchaImageType) {
        return target.generateCaptcha(captchaImageType);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(String type, CaptchaImageType captchaImageType) {
        return target.generateCaptcha(type, captchaImageType);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(GenerateParam param) {
        return target.generateCaptcha(param);
    }

    @Override
    public ApiResponse<?> matching(String id, MatchParam matchParam) {
        return target.matching(id, matchParam);
    }

    @Override
    public ApiResponse<?> matching(String id, ImageCaptchaTrack track) {
        return target.matching(id, track);
    }

    @Override
    public boolean matching(String id, Float percentage) {
        return target.matching(id, percentage);
    }

    @Override
    public String getCaptchaTypeById(String id) {
        return target.getCaptchaTypeById(id);
    }

    @Override
    public ImageCaptchaResourceManager getImageCaptchaResourceManager() {
        return target.getImageCaptchaResourceManager();
    }

    @Override
    public void setImageCaptchaValidator(ImageCaptchaValidator sliderCaptchaValidator) {
        target.setImageCaptchaValidator(sliderCaptchaValidator);
    }

    @Override
    public void setImageCaptchaGenerator(ImageCaptchaGenerator imageCaptchaGenerator) {
        target.setImageCaptchaGenerator(imageCaptchaGenerator);
    }

    @Override
    public CaptchaInterceptor getCaptchaInterceptor() {
        return target.getCaptchaInterceptor();
    }

    @Override
    public void setCaptchaInterceptor(CaptchaInterceptor captchaInterceptor) {
        target.setCaptchaInterceptor(captchaInterceptor);
    }

    @Override
    public void setCacheStore(CacheStore cacheStore) {
        target.setCacheStore(cacheStore);
    }

    @Override
    public ImageCaptchaValidator getImageCaptchaValidator() {
        return target.getImageCaptchaValidator();
    }

    @Override
    public ImageCaptchaGenerator getImageCaptchaGenerator() {
        return target.getImageCaptchaGenerator();
    }

    @Override
    public CacheStore getCacheStore() {
        return target.getCacheStore();
    }
}
