package cloud.tianai.captcha.validator.impl;

import cloud.tianai.captcha.common.util.CaptchaUtils;
import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.common.util.ObjectUtils;
import cloud.tianai.captcha.common.util.CaptchaUtils;
import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.common.util.ObjectUtils;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;

import java.util.List;
import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2022/2/17 11:01
 * @Description 基本的行为轨迹校验
 */
public class BasicCaptchaTrackValidator extends SimpleImageCaptchaValidator {

    public BasicCaptchaTrackValidator() {
    }

    public BasicCaptchaTrackValidator(float defaultTolerant) {
        super(defaultTolerant);
    }

    @Override
    public boolean beforeValid(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> sliderCaptchaValidData, Float tolerant, String type) {
        // 校验参数
        checkParam(imageCaptchaTrack);
        return true;
    }

    @Override
    public boolean afterValid(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> sliderCaptchaValidData, Float tolerant, String type) {
        if (!CaptchaUtils.isSliderCaptcha(type)){
            // 不是滑动验证码的话暂时跳过，点选验证码行为轨迹还没做
            return true;
        }
        // 进行行为轨迹检测
        long startSlidingTime = imageCaptchaTrack.getStartSlidingTime().getTime();
        long endSlidingTime = imageCaptchaTrack.getEndSlidingTime().getTime();
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
            return false;
        }
        // 检测2
        if (trackList.size() < 10 || trackList.size() > bgImageWidth * 5) {
            return false;
        }
        // 检测3
        ImageCaptchaTrack.Track firstTrack = trackList.get(0);
        if (firstTrack.getX() > 10 || firstTrack.getX() < -10 || firstTrack.getY() > 10 || firstTrack.getY() < -10) {
            return false;
        }
        int check4 = 0;
        int check7 = 0;
        for (int i = 1; i < trackList.size(); i++) {
            ImageCaptchaTrack.Track track = trackList.get(i);
            int x = track.getX();
            int y = track.getY();
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
                return false;
            }
        }
        if (check4 == trackList.size() || check7 > 200) {
            return false;
        }

        // check6
        int splitPos = (int) (trackList.size() * 0.7);
        ImageCaptchaTrack.Track splitPostTrack = trackList.get(splitPos - 1);
        int posTime = splitPostTrack.getT();
        float startAvgPosTime = posTime / (float) splitPos;

        ImageCaptchaTrack.Track lastTrack = trackList.get(trackList.size() - 1);
        float endAvgPosTime = lastTrack.getT() / (float) (trackList.size() - splitPos);

        return endAvgPosTime > startAvgPosTime;
    }

    public void checkParam(ImageCaptchaTrack imageCaptchaTrack) {
        if (ObjectUtils.isEmpty(imageCaptchaTrack.getBgImageWidth())) {
            throw new IllegalArgumentException("bgImageWidth must not be null");
        }
        if (ObjectUtils.isEmpty(imageCaptchaTrack.getBgImageHeight())) {
            throw new IllegalArgumentException("bgImageHeight must not be null");
        }
        if (ObjectUtils.isEmpty(imageCaptchaTrack.getStartSlidingTime())) {
            throw new IllegalArgumentException("startSlidingTime must not be null");
        }
        if (ObjectUtils.isEmpty(imageCaptchaTrack.getEndSlidingTime())) {
            throw new IllegalArgumentException("endSlidingTime must not be null");
        }
        if (CollectionUtils.isEmpty(imageCaptchaTrack.getTrackList())) {
            throw new IllegalArgumentException("trackList must not be null");
        }
        for (ImageCaptchaTrack.Track track : imageCaptchaTrack.getTrackList()) {
            Integer x = track.getX();
            Integer y = track.getY();
            Integer t = track.getT();
            String type = track.getType();
            if (x == null || y == null || t == null || ObjectUtils.isEmpty(type)) {
                throw new IllegalArgumentException("track[x,y,t,type] must not be null");
            }
        }
    }
}
