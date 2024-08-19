package cloud.tianai.captcha.interceptor.impl;

import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.common.response.CodeDefinition;
import cloud.tianai.captcha.common.util.CaptchaTypeClassifier;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.interceptor.Context;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.MatchParam;

import java.util.List;

/**
 * @Author: 天爱有情
 * @date 2023/1/4 10:00
 * @Description BasicCaptchaTrackValidator
 */
public class BasicTrackCaptchaInterceptor implements CaptchaInterceptor {
    public static final CodeDefinition DEFINITION = new CodeDefinition(50001, "basic check fail");

    @Override
    public String getName() {
        return "basic_track_check";
    }

    @Override
    public ApiResponse<?> afterValid(Context context, String type, MatchParam matchData, AnyMap validData, ApiResponse<?> basicValid) {
        if (!basicValid.isSuccess()) {
            return context.getGroup().afterValid(context, type, matchData, validData, basicValid);
        }
        if (!CaptchaTypeClassifier.isSliderCaptcha(type)) {
            // 不是滑动验证码的话暂时跳过，点选验证码行为轨迹还没做
            return ApiResponse.ofSuccess();
        }
        ImageCaptchaTrack imageCaptchaTrack = matchData.getTrack();
        // 进行行为轨迹检测
        long startSlidingTime = imageCaptchaTrack.getStartTime().getTime();
        long endSlidingTime = imageCaptchaTrack.getStopTime().getTime();
        Integer bgImageWidth = imageCaptchaTrack.getBgImageWidth();
        List<ImageCaptchaTrack.Track> trackList = imageCaptchaTrack.getTrackList();
        // 这里只进行基本检测, 用一些简单算法进行校验，如有需要可扩展
        // 检测1: 滑动时间如果小于300毫秒 返回false
        // 检测2: 轨迹数据要是少于背10，或者大于背景宽度的五倍 返回false
        // 检测3: x轴和y轴应该是从0开始的，要是一开始x轴和y轴乱跑，返回false
        // 检测4: 如果y轴是相同的，必然是机器操作，直接返回false
        // 检测5： x轴或者y轴直接的区间跳跃过大的话返回 false
        // 检测6: x轴应该是由快到慢的， 要是速率一致，返回false
        // 检测7: 如果x轴超过图片宽度的频率过高，返回false

        // 检测1
        if (startSlidingTime + 300 > endSlidingTime) {
            context.end();
            return ApiResponse.ofMessage(DEFINITION);
        }
        // 检测2
        if (trackList.size() < 10 || trackList.size() > bgImageWidth * 5) {
            context.end();
            return ApiResponse.ofMessage(DEFINITION);
        }
        // 检测3
        ImageCaptchaTrack.Track firstTrack = trackList.get(0);
        if (firstTrack.getX() > 10 || firstTrack.getX() < -10 || firstTrack.getY() > 10 || firstTrack.getY() < -10) {
            context.end();
            return ApiResponse.ofMessage(DEFINITION);
        }
        int check4 = 0;
        int check7 = 0;
        for (int i = 1; i < trackList.size(); i++) {
            ImageCaptchaTrack.Track track = trackList.get(i);
            float x = track.getX();
            float y = track.getY();
            // check4
            if (firstTrack.getY() == y) {
                check4++;
            }
            // check7
            if (x >= bgImageWidth) {
                check7++;
            }
            // check5
            ImageCaptchaTrack.Track preTrack = trackList.get(i - 1);
            if ((track.getX() - preTrack.getX()) > 50 || (track.getY() - preTrack.getY()) > 50) {
                context.end();
                return ApiResponse.ofMessage(DEFINITION);
            }
        }
        if (check4 == trackList.size() || check7 > 200) {
            context.end();
            return ApiResponse.ofMessage(DEFINITION);
        }

        // check6
        int splitPos = (int) (trackList.size() * 0.7);
        ImageCaptchaTrack.Track splitPostTrack = trackList.get(splitPos - 1);
        ImageCaptchaTrack.Track lastTrack = trackList.get(trackList.size() - 1);
        // bugfix: wuhaochao
        ImageCaptchaTrack.Track stepOneFirstTrack = trackList.get(0);
        ImageCaptchaTrack.Track stepOneTwoTrack = trackList.get(splitPos);
        float posTime = splitPostTrack.getT() - stepOneFirstTrack.getT();
        double startAvgPosTime = posTime / (float) splitPos;
        double endAvgPosTime = (lastTrack.getT() - stepOneTwoTrack.getT()) / (float) (trackList.size() - splitPos);
        boolean check = endAvgPosTime > startAvgPosTime;
        if (check) {
            return ApiResponse.ofSuccess();
        }
        context.end();
        return ApiResponse.ofMessage(DEFINITION);
    }

}
