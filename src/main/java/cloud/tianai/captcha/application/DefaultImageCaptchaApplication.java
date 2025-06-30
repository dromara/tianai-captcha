package cloud.tianai.captcha.application;

import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.exception.ImageCaptchaException;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.common.response.ApiResponseStatusConstant;
import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.CacheImageCaptchaGenerator;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.interceptor.EmptyCaptchaInterceptor;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.MatchParam;
import cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:52
 * @Description 默认的 图片验证码应用程序
 */
@Slf4j
public class DefaultImageCaptchaApplication implements ImageCaptchaApplication {
    private CaptchaInterceptor captchaInterceptor;
    /** 图片验证码生成器. */
    private ImageCaptchaGenerator captchaGenerator;
    /** 图片验证码校验器. */
    private ImageCaptchaValidator imageCaptchaValidator;
    /** 缓冲存储. */
    private CacheStore cacheStore;
    /** 验证码配置属性. */
    private final ImageCaptchaProperties prop;
    /** 默认的过期时间. */
    private long defaultExpire = 20000L;

    public static final String ID_SPLIT = "_";

    public DefaultImageCaptchaApplication(ImageCaptchaGenerator captchaGenerator,
                                          ImageCaptchaValidator imageCaptchaValidator,
                                          CacheStore cacheStore,
                                          ImageCaptchaProperties prop,
                                          CaptchaInterceptor captchaInterceptor) {
        this.prop = prop;

        setImageCaptchaValidator(imageCaptchaValidator);
        setCacheStore(cacheStore);
        // 默认过期时间
        Long defaultExpire = prop.getExpire().get("default");
        if (defaultExpire != null && defaultExpire > 0) {
            this.defaultExpire = defaultExpire;
        }
        if (captchaInterceptor == null) {
            this.captchaInterceptor = EmptyCaptchaInterceptor.INSTANCE;
        } else {
            this.captchaInterceptor = captchaInterceptor;
        }
        captchaGenerator.setInterceptor(this.captchaInterceptor);
        if (prop.isLocalCacheEnabled()) {
            captchaGenerator = new CacheImageCaptchaGenerator(captchaGenerator,
                    prop.getLocalCacheSize(), prop.getLocalCacheWaitTime(),
                    prop.getLocalCachePeriod(), prop.getLocalCacheExpireTime());
        }
        // 初始化生成器
        captchaGenerator.init();
        setImageCaptchaGenerator(captchaGenerator);
    }

    @Override
    public ApiResponse<ImageCaptchaVO> generateCaptcha() {
        // 生成滑块验证码
        return generateCaptcha(CaptchaTypeConstant.SLIDER);
    }

    @Override
    public ApiResponse<ImageCaptchaVO> generateCaptcha(String type) {
        GenerateParam generateParam = new GenerateParam();
        generateParam.setType(type);
        return generateCaptcha(generateParam);
    }

    @Override
    public ApiResponse<ImageCaptchaVO> generateCaptcha(GenerateParam param) {
        ApiResponse<ImageCaptchaVO> captchaResponse = beforeGenerateCaptcha(param);
        if (captchaResponse != null) {
            return captchaResponse;
        }
        ImageCaptchaInfo imageCaptchaInfo = getImageCaptchaGenerator().generateCaptchaImage(param);
        captchaResponse = convertToCaptchaResponse(imageCaptchaInfo);
        afterGenerateCaptcha(imageCaptchaInfo, captchaResponse);
        return captchaResponse;
    }

    @Override
    public ApiResponse<ImageCaptchaVO> generateCaptcha(CaptchaImageType captchaImageType) {
        return generateCaptcha(CaptchaTypeConstant.SLIDER, captchaImageType);
    }

    @Override
    public ApiResponse<ImageCaptchaVO> generateCaptcha(String type, CaptchaImageType captchaImageType) {
        GenerateParam param = new GenerateParam();
        if (CaptchaImageType.WEBP.equals(captchaImageType)) {
            param.setBackgroundFormatName("webp");
            param.setTemplateFormatName("webp");
        } else {
            param.setBackgroundFormatName("jpeg");
            param.setTemplateFormatName("png");
        }
        param.setType(type);
        return generateCaptcha(param);
    }


    public ApiResponse<ImageCaptchaVO> convertToCaptchaResponse(ImageCaptchaInfo imageCaptchaInfo) {
        if (imageCaptchaInfo == null) {
            // 要是生成失败
            throw new ImageCaptchaException("生成验证码失败，验证码生成为空");
        }
        // 生成ID
        String id = generatorId(imageCaptchaInfo);
        ApiResponse<ImageCaptchaVO> response = beforeGenerateImageCaptchaValidData(imageCaptchaInfo);
        if (response != null) {
            return response;
        }
        // 生成校验数据
        AnyMap validData = getImageCaptchaValidator().generateImageCaptchaValidData(imageCaptchaInfo);
        afterGenerateImageCaptchaValidData(imageCaptchaInfo, validData);
        if (!CollectionUtils.isEmpty(validData)) {
            // 存到缓存里
            cacheVerification(id, imageCaptchaInfo.getType(), validData);
        }
        ImageCaptchaVO verificationVO = new ImageCaptchaVO();
        verificationVO.setType(imageCaptchaInfo.getType());
        verificationVO.setBackgroundImage(imageCaptchaInfo.getBackgroundImage());
        verificationVO.setTemplateImage(imageCaptchaInfo.getTemplateImage());
        verificationVO.setBackgroundImageTag(imageCaptchaInfo.getBackgroundImageTag());
        verificationVO.setTemplateImageTag(imageCaptchaInfo.getTemplateImageTag());
        verificationVO.setBackgroundImageWidth(imageCaptchaInfo.getBackgroundImageWidth());
        verificationVO.setBackgroundImageHeight(imageCaptchaInfo.getBackgroundImageHeight());
        verificationVO.setTemplateImageWidth(imageCaptchaInfo.getTemplateImageWidth());
        verificationVO.setTemplateImageHeight(imageCaptchaInfo.getTemplateImageHeight());
        verificationVO.setData(imageCaptchaInfo.getData() == null ? null : imageCaptchaInfo.getData().getViewData());
        verificationVO.setId(id);
        return ApiResponse.ofSuccess(verificationVO);
    }


    @Override
    public ApiResponse<?> matching(String id, MatchParam matchParam) {
        AnyMap validData = getVerification(id);
        if (validData == null) {
            return ApiResponse.ofMessage(ApiResponseStatusConstant.EXPIRED);
        }
        ApiResponse<?> response = beforeValid(id, matchParam, validData);
        if (!response.isSuccess()) {
            return response;
        }
        ApiResponse<?> basicValid = getImageCaptchaValidator().valid(matchParam.getTrack(), validData);
        response = afterValid(id, matchParam, validData, basicValid);
        if (!response.isSuccess()) {
            return response;
        }
        return basicValid;
    }

    @Override
    public ApiResponse<?> matching(String id, ImageCaptchaTrack track) {
        return matching(id, new MatchParam(track, null));
    }


    @Override
    public boolean matching(String id, Float percentage) {
        AnyMap cachePercentage = getVerification(id);
        if (cachePercentage == null) {
            return false;
        }
        ImageCaptchaValidator imageCaptchaValidator = getImageCaptchaValidator();
        if (!(imageCaptchaValidator instanceof SimpleImageCaptchaValidator)) {
            return false;
        }
        SimpleImageCaptchaValidator simpleImageCaptchaValidator = (SimpleImageCaptchaValidator) imageCaptchaValidator;
        Float oriPercentage = cachePercentage.getFloat(SimpleImageCaptchaValidator.PERCENTAGE_KEY);
        // 读容错值
        Float tolerant = cachePercentage.getFloat(SimpleImageCaptchaValidator.TOLERANT_KEY, simpleImageCaptchaValidator.getDefaultTolerant());
        return simpleImageCaptchaValidator.checkPercentage(percentage, oriPercentage, tolerant);
    }

    @Override
    public String getCaptchaTypeById(String id) {
        String[] split = id.split(ID_SPLIT);
        if (split.length >= 2) {
            return split[0];
        }
        return null;
    }

    protected String generatorId(ImageCaptchaInfo imageCaptchaInfo) {
        return imageCaptchaInfo.getType() + ID_SPLIT + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 通过缓存获取百分比
     *
     * @param id 验证码ID
     * @return AnyMap
     */
    protected AnyMap getVerification(String id) {
        return getCacheStore().getAndRemoveCache(getKey(id));
    }

    /**
     * 缓存验证码
     *
     * @param id        id
     * @param type
     * @param validData validData
     */
    protected void cacheVerification(String id, String type, AnyMap validData) {
        Long expire = prop.getExpire().getOrDefault(type, defaultExpire);
        if (!getCacheStore().setCache(getKey(id), validData, expire, TimeUnit.MILLISECONDS)) {
            log.error("缓存验证码数据失败， id={}, validData={}", id, validData);
            throw new ImageCaptchaException("缓存验证码数据失败" + type);
        }
    }

    protected String getKey(String id) {
        return prop.getPrefix().concat(":").concat(id);
    }

    @Override
    public ImageCaptchaResourceManager getImageCaptchaResourceManager() {
        return getImageCaptchaGenerator().getImageResourceManager();
    }

    @Override
    public void setImageCaptchaValidator(ImageCaptchaValidator imageCaptchaValidator) {
        this.imageCaptchaValidator = imageCaptchaValidator;
    }

    @Override
    public void setImageCaptchaGenerator(ImageCaptchaGenerator imageCaptchaGenerator) {
        this.captchaGenerator = imageCaptchaGenerator;
    }

    @Override
    public CaptchaInterceptor getCaptchaInterceptor() {
        return this.captchaInterceptor;
    }

    @Override
    public void setCaptchaInterceptor(CaptchaInterceptor captchaInterceptor) {
        this.captchaInterceptor = captchaInterceptor;
        this.captchaGenerator.setInterceptor(captchaInterceptor);
    }

    @Override
    public void setCacheStore(CacheStore cacheStore) {
        this.cacheStore = cacheStore;
    }

    @Override
    public ImageCaptchaValidator getImageCaptchaValidator() {
        return this.imageCaptchaValidator;
    }

    @Override
    public ImageCaptchaGenerator getImageCaptchaGenerator() {
        return this.captchaGenerator;
    }

    @Override
    public CacheStore getCacheStore() {
        return this.cacheStore;
    }

    // ============== 一些模板方法 ================

    private void afterGenerateCaptcha(ImageCaptchaInfo imageCaptchaInfo, ApiResponse<ImageCaptchaVO> captchaResponse) {
        captchaInterceptor.afterGenerateCaptcha(captchaInterceptor.createContext(), imageCaptchaInfo.getType(), imageCaptchaInfo, captchaResponse);
    }

    private ApiResponse<ImageCaptchaVO> beforeGenerateCaptcha(GenerateParam param) {
        return captchaInterceptor.beforeGenerateCaptcha(captchaInterceptor.createContext(), param.getType(), param);
    }

    private ApiResponse<ImageCaptchaVO> beforeGenerateImageCaptchaValidData(ImageCaptchaInfo imageCaptchaInfo) {
        return captchaInterceptor.beforeGenerateImageCaptchaValidData(captchaInterceptor.createContext(), imageCaptchaInfo.getType(), imageCaptchaInfo);
    }

    private void afterGenerateImageCaptchaValidData(ImageCaptchaInfo imageCaptchaInfo, AnyMap validData) {
        captchaInterceptor.afterGenerateImageCaptchaValidData(captchaInterceptor.createContext(), imageCaptchaInfo.getType(), imageCaptchaInfo, validData);
    }

    private ApiResponse<?> beforeValid(String id, MatchParam matchParam, AnyMap validData) {
        return captchaInterceptor.beforeValid(captchaInterceptor.createContext(), getCaptchaTypeById(id), matchParam, validData);
    }

    private ApiResponse<?> afterValid(String id, MatchParam matchParam, AnyMap validData, ApiResponse<?> basicValid) {
        return captchaInterceptor.afterValid(captchaInterceptor.createContext(), getCaptchaTypeById(id), matchParam, validData, basicValid);
    }

}
