package cloud.tianai.captcha.template.slider.validator.impl;

import cloud.tianai.captcha.template.slider.common.util.CollectionUtils;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.template.slider.validator.common.model.dto.SliderCaptchaTrack;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2022/2/17 11:01
 * @Description 基本的滑块验证校验 ， 值进行基本校验， 目前只校验用户是否滑动到缺口处，不校验行为轨迹
 */
@Slf4j
public class SimpleImageCaptchaValidator implements ImageCaptchaValidator {

    public static float DEFAULT_TOLERANT = 0.02f;
    /** 容错值. */
    @Getter
    @Setter
    public float defaultTolerant = DEFAULT_TOLERANT;

    public SimpleImageCaptchaValidator() {
    }

    public SimpleImageCaptchaValidator(float defaultTolerant) {
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
    public Map<String, Object> generateSliderCaptchaValidData(ImageCaptchaInfo imageCaptchaInfo) {
        Map<String, Object> map = new HashMap<>(8);
        addPercentage(imageCaptchaInfo, map);
        return map;
    }

    @Override
    public boolean valid(SliderCaptchaTrack sliderCaptchaTrack, Map<String, Object> sliderCaptchaValidData) {
        Float oriPercentage = getPercentage(sliderCaptchaTrack, sliderCaptchaValidData);
        if (oriPercentage == null) {
            // 没读取到百分比
            return false;
        }
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

    protected Float getPercentage(SliderCaptchaTrack sliderCaptchaTrack, Map<String, Object> sliderCaptchaValidData) {
        Object percentage = sliderCaptchaValidData.get("percentage");
        if (percentage != null) {
            if (percentage instanceof Number) {
                return ((Number) percentage).floatValue();
            }
            try {
                if (percentage instanceof String) {
                    return Float.parseFloat((String) percentage);
                }
            } catch (NumberFormatException e) {
                log.error("从 sliderCaptchaValidData 读取到的 percentage无法转换成float类型, [{}]", percentage);
                throw e;
            }
        }
        log.warn("无法从 sliderCaptchaValidData 获取到 percentage");
        return null;
    }

    protected void addPercentage(ImageCaptchaInfo imageCaptchaInfo, Map<String, Object> sliderCaptchaValidData) {
        float percentage = calcPercentage(imageCaptchaInfo.getRandomX(), imageCaptchaInfo.getBgImageWidth());
        sliderCaptchaValidData.put("percentage", percentage);
    }
}
