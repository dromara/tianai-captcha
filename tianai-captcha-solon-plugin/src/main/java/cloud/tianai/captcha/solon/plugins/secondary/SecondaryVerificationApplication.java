package cloud.tianai.captcha.solon.plugins.secondary;

import cloud.tianai.captcha.application.FilterImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.exception.ImageCaptchaException;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.solon.properties.CaptchaLimit;
import cloud.tianai.captcha.solon.properties.CaptchaProperties;
import cloud.tianai.captcha.solon.properties.CaptchaSecondary;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import org.noear.solon.core.handle.Context;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author XT
 * @Date 2024.09.03
 */
public class SecondaryVerificationApplication extends FilterImageCaptchaApplication {

    private final CaptchaSecondary prop;
    private final CaptchaProperties captchaProperties;
    private final CacheStore redisCacheService;

    public SecondaryVerificationApplication(ImageCaptchaApplication target, CaptchaProperties captchaProperties, CacheStore redisCacheService) {
        super(target);
        this.captchaProperties = captchaProperties;
        this.prop = captchaProperties.getSecondary();
        this.redisCacheService = redisCacheService;
    }

    @Override
    public ApiResponse<ImageCaptchaVO> generateCaptcha(String type) {
        // 检查是否每分钟超过限制
        CaptchaLimit limit = captchaProperties.getLimit();
        if (null != limit && limit.getEnable()) {
            Context current = Context.current();
            String errLimitKey = getLimitKey(current, "error");
            Long errLimit = redisCacheService.getLong(errLimitKey);
            if (null != errLimit && errLimit >= limit.getErrorLimit()) {
                throw new ImageCaptchaException("验证次数过多，请稍后再试");
            }
            String reqLimitKey = getLimitKey(current, "req");
            Long reqLimit = redisCacheService.getLong(reqLimitKey);
            if (null != reqLimit && reqLimit >= limit.getReqLimit()) {
                throw new ImageCaptchaException("获取验证码频繁，请稍后再试");
            }
            redisCacheService.incr(reqLimitKey, 1, 60L, TimeUnit.SECONDS);
        }
        return super.generateCaptcha(type);
    }

    @Override
    public ApiResponse<?> matching(String id, ImageCaptchaTrack imageCaptchaTrack) {
        ApiResponse<?> match = super.matching(id, imageCaptchaTrack);
        if (match.isSuccess()) {
            // 如果匹配成功， 添加二次验证记录
            addSecondaryVerification(id + getRemoteId(Context.current()), imageCaptchaTrack);
        } else {
            CaptchaLimit limit = captchaProperties.getLimit();
            if (null != limit && limit.getEnable()) {
                Context current = Context.current();
                String limitKey = getLimitKey(current, "error");
                redisCacheService.incr(limitKey, 1, 60L, TimeUnit.SECONDS);
            }
        }
        return match;
    }

    /**
     * 二次缓存验证
     * @param id id
     * @return boolean
     */
    public boolean secondaryVerification(String id) {
        Map<String, Object> cache = target.getCacheStore().getAndRemoveCache(getKey(id + getRemoteId(Context.current())));
        return cache != null;
    }

    /**
     * 添加二次缓存验证记录
     * @param id id
     * @param imageCaptchaTrack sliderCaptchaTrack
     */
    protected void addSecondaryVerification(String id, ImageCaptchaTrack imageCaptchaTrack) {
        target.getCacheStore().setCache(getKey(id), new AnyMap(), prop.getExpire(), TimeUnit.MILLISECONDS);
    }

    protected String getKey(String id) {
        return prop.getKeyPrefix().concat(":").concat(id);
    }

    protected String getLimitKey(Context ctx, String type) {
        return prop.getKeyPrefix().concat(":limit:")
                .concat(type)
                .concat(":")
                .concat(getRemoteId(ctx));
    }


    public static String getRemoteId(Context ctx) {
        return ctx.realIp() + ctx.userAgent();
    }

}
