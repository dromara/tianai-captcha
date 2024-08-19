package cloud.tianai.captcha.interceptor.impl;

import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.common.util.ObjectUtils;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.interceptor.Context;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.MatchParam;

/**
 * @Author: 天爱有情
 * @date 2023/1/4 10:10
 * @Description 轨迹参数校验， 如果轨迹参数为空抛异常
 */
public class ParamCheckCaptchaInterceptor implements CaptchaInterceptor {
    @Override
    public ApiResponse<?> beforeValid(Context context, String type, MatchParam matchParam, AnyMap validData) {
        checkParam(matchParam.getTrack());
        return ApiResponse.ofSuccess();
    }

    @Override
    public String getName() {
        return "param_check";
    }

    public void checkParam(ImageCaptchaTrack imageCaptchaTrack) {
        if (ObjectUtils.isEmpty(imageCaptchaTrack.getBgImageWidth())) {
            throw new IllegalArgumentException("bgImageWidth must not be null");
        }
        if (ObjectUtils.isEmpty(imageCaptchaTrack.getBgImageHeight())) {
            throw new IllegalArgumentException("bgImageHeight must not be null");
        }
        if (ObjectUtils.isEmpty(imageCaptchaTrack.getStartTime())) {
            throw new IllegalArgumentException("startTime must not be null");
        }
        if (ObjectUtils.isEmpty(imageCaptchaTrack.getStopTime())) {
            throw new IllegalArgumentException("stopTime must not be null");
        }
        if (CollectionUtils.isEmpty(imageCaptchaTrack.getTrackList())) {
            throw new IllegalArgumentException("trackList must not be null");
        }
        for (ImageCaptchaTrack.Track track : imageCaptchaTrack.getTrackList()) {
            Float x = track.getX();
            Float y = track.getY();
            Float t = track.getT();
            String type = track.getType();
            if (x == null || y == null || t == null || ObjectUtils.isEmpty(type)) {
                throw new IllegalArgumentException("track[x,y,t,type] must not be null");
            }
        }
    }
}
