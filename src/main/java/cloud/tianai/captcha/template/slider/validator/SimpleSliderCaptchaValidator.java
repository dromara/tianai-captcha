package cloud.tianai.captcha.template.slider.validator;

import cloud.tianai.captcha.template.slider.util.CollectionUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SimpleSliderCaptchaValidator implements SliderCaptchaValidator {

    public static float DEFAULT_TOLERANT = 0.02f;
    /** 容错值. */
    @Getter
    @Setter
    public float defaultTolerant = DEFAULT_TOLERANT;

    public SimpleSliderCaptchaValidator() {
    }

    public SimpleSliderCaptchaValidator(float defaultTolerant) {
        this.defaultTolerant = defaultTolerant;
    }

    @Override
    public float calcPercentage(int x, int bgImageWidth) {
        return (float) x / bgImageWidth;
    }

    @Override
    public boolean checkPercentage(Float newPercentage, Float oriPercentage) {
        return checkPercentage(newPercentage, oriPercentage, defaultTolerant);
    }

    @Override
    public boolean checkPercentage(Float newPercentage, Float oriPercentage, float tolerant) {
        if (newPercentage == null || Float.isNaN(newPercentage) || Float.isInfinite(newPercentage)
                || oriPercentage == null || Float.isNaN(oriPercentage) || Float.isInfinite(oriPercentage)) {
            return false;
        }
        // 容错值
        float maxTolerant = oriPercentage + tolerant;
        float minTolerant = oriPercentage - tolerant;
        return newPercentage >= minTolerant && newPercentage <= maxTolerant;
    }

    @Override
    public boolean valid(SliderCaptchaTrack sliderCaptchaTrack, Float oriPercentage) {
        Integer bgImageWidth = sliderCaptchaTrack.getBgImageWidth();
        if (bgImageWidth == null || bgImageWidth < 1) {
            // 没有背景图片宽度
            return false;
        }
        List<SliderCaptchaTrack.Track> trackList = sliderCaptchaTrack.getTrackList();
        if (CollectionUtils.isEmpty(trackList)) {
            // 没有滑动轨迹
            return false;
        }
        // 取最后一个滑动轨迹
        SliderCaptchaTrack.Track lastTrack = trackList.get(trackList.size() - 1);
        // 计算百分比
        float calcPercentage = calcPercentage(lastTrack.getX(), bgImageWidth);
        // 校验百分比
        return checkPercentage(calcPercentage, oriPercentage);
    }
}
