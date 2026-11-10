package cloud.tianai.captcha.spring.plugins.secondary;

import cloud.tianai.captcha.application.FilterImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.spring.autoconfiguration.SecondaryVerificationProperties;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.MatchParam;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @Author: 天爱有情
 * @date 2022/3/2 14:16
 * @Description 二次验证
 */
public class SecondaryVerificationApplication extends FilterImageCaptchaApplication {

    /**
     * 二次验证缓存占位值，仅用于标记存在性，不承载业务数据。
     */
    private static final AnyMap EMPTY_CACHE = new AnyMap();

    private final SecondaryVerificationProperties prop;

    public SecondaryVerificationApplication(ImageCaptchaApplication target, SecondaryVerificationProperties prop) {
        super(target);
        this.prop = prop;
    }

    @Override
    public ApiResponse<?> matching(String id, MatchParam matchParam) {
        return doMatching(id, matchParam, () -> super.matching(id, matchParam));
    }

    @Override
    public ApiResponse<?> matching(String id, ImageCaptchaTrack imageCaptchaTrack) {
        return doMatching(id, new MatchParam(imageCaptchaTrack, null), () -> super.matching(id, imageCaptchaTrack));
    }

    /**
     * 统一执行匹配，并在匹配成功后添加二次验证记录
     *
     * @param id         验证码id
     * @param matchParam matchParam
     * @param matcher    实际的匹配动作
     * @return 匹配结果
     */
    private ApiResponse<?> doMatching(String id, MatchParam matchParam, Supplier<ApiResponse<?>> matcher) {
        ApiResponse<?> match = matcher.get();
        if (match.isSuccess()) {
            // 如果匹配成功， 添加二次验证记录
            addSecondaryVerification(id, matchParam);
        }
        return match;
    }


    /**
     * 二次缓存验证
     *
     * @param id id
     * @return boolean
     */
    public boolean secondaryVerification(String id) {
        AnyMap cache = target.getCacheStore().getAndRemoveCache(getKey(id));
        return cache != null;
    }

    /**
     * 添加二次缓存验证记录
     *
     * @param id         id
     * @param matchParam matchParam
     */
    protected void addSecondaryVerification(String id, MatchParam matchParam) {
        target.getCacheStore().setCache(getKey(id), EMPTY_CACHE, prop.getExpire(), TimeUnit.MILLISECONDS);
    }

    protected String getKey(String id) {
        return prop.getKeyPrefix().concat(":").concat(id);
    }
}
