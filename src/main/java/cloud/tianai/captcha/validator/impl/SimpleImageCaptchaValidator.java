package cloud.tianai.captcha.validator.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.util.CaptchaUtils;
import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.common.util.ObjectUtils;
import cloud.tianai.captcha.generator.common.model.dto.ClickImageCheckDefinition;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.common.constant.TrackTypeConstant;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: 天爱有情
 * @date 2022/2/17 11:01
 * @Description 基本的滑块验证校验 ， 值进行基本校验， 目前只校验用户是否滑动到缺口处，不校验行为轨迹
 */
@Slf4j
public class SimpleImageCaptchaValidator implements ImageCaptchaValidator {

    /** 默认的容错值. */
    public static float DEFAULT_TOLERANT = 0.02f;
    /** 验证数据 key. */
    public static final String PERCENTAGE_KEY = "percentage";
    /** 容错值key. */
    public static final String TOLERANT_KEY = "tolerant";
    /** 类型 key， 标识是哪张类型的验证码. */
    public static final String TYPE_KEY = "type";

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
    public float calcPercentage(Number pos, Number maxPos) {
        return pos.floatValue() / maxPos.floatValue();
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
    public Map<String, Object> generateImageCaptchaValidData(ImageCaptchaInfo imageCaptchaInfo) {
        Map<String, Object> map = new HashMap<>(8);
        if (beforeGenerateImageCaptchaValidData(imageCaptchaInfo, map)) {
            doGenerateImageCaptchaValidData(map, imageCaptchaInfo);
        }
        afterGenerateImageCaptchaValidData(imageCaptchaInfo, map);
        return map;
    }

    public boolean beforeGenerateImageCaptchaValidData(ImageCaptchaInfo imageCaptchaInfo, Map<String, Object> map) {
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
        return true;
    }

    public void afterGenerateImageCaptchaValidData(ImageCaptchaInfo imageCaptchaInfo, Map<String, Object> map) {

    }

    public void doGenerateImageCaptchaValidData(Map<String, Object> map,
                                                ImageCaptchaInfo imageCaptchaInfo) {
        // type
        String type = (String) map.getOrDefault(TYPE_KEY, CaptchaTypeConstant.SLIDER);
        Object expand = imageCaptchaInfo.getData() == null ? null : imageCaptchaInfo.getData().getExpand();
        if (CaptchaUtils.isSliderCaptcha(type)) {
            // 滑动验证码
            addPercentage(imageCaptchaInfo, map);
        } else if (CaptchaUtils.isClickCaptcha(type)) {
            // 图片点选验证码
            if (expand == null) {
                throw new IllegalArgumentException("点选验证码扩展数据转换为 List<ClickImageCheckDefinition> 失败， info=" + imageCaptchaInfo);
            }
            List<ClickImageCheckDefinition> clickImageCheckDefinitionList;
            try {
                clickImageCheckDefinitionList = (List<ClickImageCheckDefinition>) expand;
            } catch (Exception e) {
                throw new IllegalArgumentException("点选验证码扩展数据转换为 List<ClickImageCheckDefinition> 失败， info=" + imageCaptchaInfo);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < clickImageCheckDefinitionList.size(); i++) {
                ClickImageCheckDefinition definition = clickImageCheckDefinitionList.get(i);
                Integer x = definition.getX();
                Integer y = definition.getY();
                Integer width = imageCaptchaInfo.getBackgroundImageWidth();
                Integer height = imageCaptchaInfo.getBackgroundImageHeight();
                float vx = calcPercentage(x, width);
                float vy = calcPercentage(y, height);
                sb.append(vx).append(",").append(vy).append(";");
                if (i == 0 && !map.containsKey(TOLERANT_KEY)) {
                    // 重新计算容错值
                    float minLeft = calcPercentage(x - definition.getWidth() / 2f, width);
                    float tolerant = vx - minLeft;
                    map.put(TOLERANT_KEY, tolerant);
                }
            }
            // 添加点选验证数据
            map.put(PERCENTAGE_KEY, sb.toString());
        }
    }

    @Override
    public boolean valid(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> sliderCaptchaValidData) {
        // 读容错值
        Float tolerant = getFloatParam(TOLERANT_KEY, sliderCaptchaValidData, defaultTolerant);
        // 读验证码类型
        String type = getStringParam(TYPE_KEY, sliderCaptchaValidData, CaptchaTypeConstant.SLIDER);
        // 验证前
        // 在验证前必须读取 容错值 和验证码类型
        if (!beforeValid(imageCaptchaTrack, sliderCaptchaValidData, tolerant, type)) {
            return false;
        }
        Integer bgImageWidth = imageCaptchaTrack.getBgImageWidth();
        if (bgImageWidth == null || bgImageWidth < 1) {
            // 没有背景图片宽度
            return false;
        }
        List<ImageCaptchaTrack.Track> trackList = imageCaptchaTrack.getTrackList();
        if (CollectionUtils.isEmpty(trackList)) {
            // 没有滑动轨迹
            return false;
        }
        // 验证
        boolean valid = doValid(imageCaptchaTrack, sliderCaptchaValidData, tolerant, type);
        if (valid) {
            // 验证后
            valid = afterValid(imageCaptchaTrack, sliderCaptchaValidData, tolerant, type);
        }
        return valid;
    }

    /**
     * 验证前
     *
     * @param imageCaptchaTrack      sliderCaptchaTrack
     * @param sliderCaptchaValidData sliderCaptchaValidData
     * @param tolerant               tolerant
     * @param type                   type
     * @return boolean
     */
    public boolean beforeValid(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> sliderCaptchaValidData, Float tolerant, String type) {
        return true;
    }

    /**
     * 验证后
     *
     * @param imageCaptchaTrack      sliderCaptchaTrack
     * @param sliderCaptchaValidData sliderCaptchaValidData
     * @param tolerant               tolerant
     * @param type                   type
     * @return boolean
     */
    public boolean afterValid(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> sliderCaptchaValidData, Float tolerant, String type) {
        return true;
    }

    public boolean doValid(ImageCaptchaTrack imageCaptchaTrack,
                           Map<String, Object> sliderCaptchaValidData,
                           Float tolerant,
                           String type) {
        if (CaptchaUtils.isSliderCaptcha(type)) {
            // 滑动类型验证码
            return doValidSliderCaptcha(imageCaptchaTrack, sliderCaptchaValidData, tolerant, type);
        } else if (CaptchaUtils.isClickCaptcha(type)) {
            // 点选类型验证码
            return doValidClickCaptcha(imageCaptchaTrack, sliderCaptchaValidData, tolerant, type);
        }
        // 不支持的类型
        log.warn("校验验证码警告， 不支持的验证码类型:{}, 请手动扩展 cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator.doValid 进行校验扩展", type);
        return false;
    }

    /**
     * 校验点选验证码
     *
     * @param imageCaptchaTrack      sliderCaptchaTrack
     * @param sliderCaptchaValidData sliderCaptchaValidData
     * @param tolerant               tolerant
     * @param type                   type
     * @return boolean
     */
    public boolean doValidClickCaptcha(ImageCaptchaTrack imageCaptchaTrack,
                                       Map<String, Object> sliderCaptchaValidData,
                                       Float tolerant,
                                       String type) {
        String validStr = getStringParam(PERCENTAGE_KEY, sliderCaptchaValidData, null);
        if (ObjectUtils.isEmpty(validStr)) {
            return false;
        }
        String[] splitArr = validStr.split(";");
        List<ImageCaptchaTrack.Track> trackList = imageCaptchaTrack.getTrackList();
        if (trackList.size() < splitArr.length) {
            return false;
        }
        // 取出点击事件的轨迹数据
        List<ImageCaptchaTrack.Track> clickTrackList = trackList
                .stream()
                .filter(t -> TrackTypeConstant.CLICK.equalsIgnoreCase(t.getType()))
                .collect(Collectors.toList());
        if (clickTrackList.size() != splitArr.length) {
            return false;
        }
        for (int i = 0; i < splitArr.length; i++) {
            ImageCaptchaTrack.Track track = clickTrackList.get(i);
            String posStr = splitArr[i];
            String[] posArr = posStr.split(",");
            float xPercentage = Float.parseFloat(posArr[0]);
            float yPercentage = Float.parseFloat(posArr[1]);

            float calcXPercentage = calcPercentage(track.getX(), imageCaptchaTrack.getBgImageWidth());
            float calcYPercentage = calcPercentage(track.getY(), imageCaptchaTrack.getBgImageHeight());

            if (!checkPercentage(calcXPercentage, xPercentage, tolerant)
                    || !checkPercentage(calcYPercentage, yPercentage, tolerant)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验滑动验证码
     *
     * @param imageCaptchaTrack      sliderCaptchaTrack
     * @param sliderCaptchaValidData sliderCaptchaValidData
     * @param tolerant               tolerant
     * @param type                   type
     * @return boolean
     */
    public boolean doValidSliderCaptcha(ImageCaptchaTrack imageCaptchaTrack,
                                        Map<String, Object> sliderCaptchaValidData,
                                        Float tolerant,
                                        String type) {
        Float oriPercentage = getFloatParam(PERCENTAGE_KEY, sliderCaptchaValidData);
        if (oriPercentage == null) {
            // 没读取到百分比
            return false;
        }
        List<ImageCaptchaTrack.Track> trackList = imageCaptchaTrack.getTrackList();
        // 取最后一个滑动轨迹
        ImageCaptchaTrack.Track lastTrack = trackList.get(trackList.size() - 1);
        // 计算百分比
        float calcPercentage = calcPercentage(lastTrack.getX(), imageCaptchaTrack.getBgImageWidth());
        // 校验百分比
        return checkPercentage(calcPercentage, oriPercentage, tolerant);
    }

    public Float getFloatParam(String key, Map<String, Object> sliderCaptchaValidData) {
        return getFloatParam(key, sliderCaptchaValidData, null);
    }

    public Float getFloatParam(String key, Map<String, Object> sliderCaptchaValidData, Float defaultData) {
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

    public String getStringParam(String key, Map<String, Object> sliderCaptchaValidData, String defaultData) {
        if (CollectionUtils.isEmpty(sliderCaptchaValidData)) {
            return defaultData;
        }
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

    protected void addPercentage(ImageCaptchaInfo imageCaptchaInfo, Map<String, Object> imageCaptchaValidData) {
        float percentage = calcPercentage(imageCaptchaInfo.getRandomX(), imageCaptchaInfo.getBackgroundImageWidth());
        imageCaptchaValidData.put(PERCENTAGE_KEY, percentage);
    }
}
