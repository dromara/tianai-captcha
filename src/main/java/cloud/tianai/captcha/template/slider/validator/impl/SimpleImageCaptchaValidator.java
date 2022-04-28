package cloud.tianai.captcha.template.slider.validator.impl;

import cloud.tianai.captcha.template.slider.common.util.CollectionUtils;
import cloud.tianai.captcha.template.slider.common.util.ObjectUtils;
import cloud.tianai.captcha.template.slider.generator.common.constant.CaptchaTypeConstant;
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

    public static final String PERCENTAGE_KEY = "percentage";
    public static final String TOLERANT_KEY = "Tolerant";
    public static final String TYPE_KEY = "type";

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
        // 容错值
        Float tolerant = imageCaptchaInfo.getTolerant();
        if (tolerant != null && tolerant > 0) {
            map.put(TOLERANT_KEY, tolerant);
        }
        // 类型
        String type = imageCaptchaInfo.getType();
        if (ObjectUtils.isEmpty(type)) {
            type = CaptchaTypeConstant.SLIDER;
        }
        map.put(TYPE_KEY, type);
        return map;
    }

    @Override
    public boolean valid(SliderCaptchaTrack sliderCaptchaTrack, Map<String, Object> sliderCaptchaValidData) {
        Float oriPercentage = getFloatParam(PERCENTAGE_KEY, sliderCaptchaValidData);
        // 读容错值
        Float tolerantData = getFloatParam(TOLERANT_KEY, sliderCaptchaValidData, defaultTolerant);
        // 读验证码类型
        String type = getStringParam(TYPE_KEY, sliderCaptchaValidData, CaptchaTypeConstant.SLIDER);

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
        return doValid(sliderCaptchaTrack, sliderCaptchaValidData, oriPercentage, tolerantData, type);
    }

    public boolean doValid(SliderCaptchaTrack sliderCaptchaTrack,
                           Map<String, Object> sliderCaptchaValidData,
                           Float oriPercentage,
                           Float tolerant,
                           String type) {
        if (CaptchaTypeConstant.SLIDER.equals(type)
                || CaptchaTypeConstant.ROTATE.equals(type)
                || CaptchaTypeConstant.CONCAT.equals(type)) {
            if (oriPercentage == null) {
                // 没读取到百分比
                return false;
            }
            List<SliderCaptchaTrack.Track> trackList = sliderCaptchaTrack.getTrackList();
            // 取最后一个滑动轨迹
            SliderCaptchaTrack.Track lastTrack = trackList.get(trackList.size() - 1);
            // 计算百分比
            float calcPercentage = calcPercentage(lastTrack.getX(), sliderCaptchaTrack.getBgImageWidth());
            // 校验百分比
            return checkPercentage(calcPercentage, oriPercentage, tolerant);
        }
        // 不支持的类型
        log.warn("校验验证码警告， 不支持的验证码类型:{}, 请手动扩展 cloud.tianai.captcha.template.slider.validator.impl.SimpleImageCaptchaValidator.doValid 进行校验扩展", type);
        return false;
    }

    protected Float getFloatParam(String key, Map<String, Object> sliderCaptchaValidData) {
        return getFloatParam(key, sliderCaptchaValidData, null);
    }

    protected Float getFloatParam(String key, Map<String, Object> sliderCaptchaValidData, Float defaultData) {
        Object data = sliderCaptchaValidData.get(key);
        if (data != null) {
            if (data instanceof Number) {
                return ((Number) data).floatValue();
            }
            try {
                if (data instanceof String) {
                    return Float.parseFloat((String) data);
                }
            } catch (NumberFormatException e) {
                log.error("从 sliderCaptchaValidData 读取到的 " + key + "无法转换成float类型, [{}]", data);
                throw e;
            }
        }
        return defaultData;
    }

    protected String getStringParam(String key, Map<String, Object> sliderCaptchaValidData, String defaultData) {
        Object data = sliderCaptchaValidData.get(key);
        if (data != null) {
            if (data instanceof String) {
                return (String) data;
            }
            try {
                return String.valueOf(data);
            } catch (NumberFormatException e) {
                log.error("从 sliderCaptchaValidData 读取到的 " + key + "无法转换成String类型, [{}]", data);
                throw e;
            }
        }
        return defaultData;
    }

    protected void addPercentage(ImageCaptchaInfo imageCaptchaInfo, Map<String, Object> sliderCaptchaValidData) {
        float percentage = calcPercentage(imageCaptchaInfo.getRandomX(), imageCaptchaInfo.getBgImageWidth());
        sliderCaptchaValidData.put(PERCENTAGE_KEY, percentage);
    }
}
