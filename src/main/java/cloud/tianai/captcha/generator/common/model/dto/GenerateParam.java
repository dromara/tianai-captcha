package cloud.tianai.captcha.generator.common.model.dto;

import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import lombok.*;

/**
 * @Author: 天爱有情
 * @date 2022/2/11 9:44
 * @Description 生成参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// param作为扩展字段暂时将param从equals和toString中移除掉 以适应 CacheImageCaptchaGenerator
@EqualsAndHashCode(exclude = "param")
public class GenerateParam {


    /** 背景格式化类型. */
    private String backgroundFormatName = "jpeg";
    /** 模板图片格式化类型. */
    private String templateFormatName = "png";
    /** 是否混淆. */
    private Boolean obfuscate = false;
    /** 类型. */
    private String type = CaptchaTypeConstant.SLIDER;
    /** 背景图片标签, 用户二级过滤背景图片，或指定某背景图片. */
    private String backgroundImageTag;
    /** 滑动图片标签,用户二级过滤模板图片，或指定某模板图片.. */
    private String templateImageTag;
    /** 扩展参数. */
    private AnyMap param = new AnyMap();

    public void addParam(String key, Object value) {
        doGetOrCreateParam().put(key, value);
    }

    public Object getParam(String key) {
        return param == null ? null : param.get(key);
    }

    private AnyMap doGetOrCreateParam() {
        if (param == null) {
            param = new AnyMap();
        }
        return param;
    }

    public Object removeParam(String key) {
        if (param == null) {
            return null;
        }
        return param.remove(key);
    }

    public Object getOrDefault(String key, Object defaultValue) {
        if (param == null) {
            return defaultValue;
        }
        return param.getOrDefault(key, defaultValue);
    }


    public Object putIfAbsent(String key, Object value) {
        return doGetOrCreateParam().putIfAbsent(key, value);
    }


    public <T> void  addParam(ParamKey<T> paramKey, T value) {
        addParam(paramKey.getKey(), value);
    }

    public <T> T getParam(ParamKey<T> paramKey) {
        return (T) getParam(paramKey.getKey());
    }

    public <T> T getOrDefault(ParamKey<T> paramKey, T defaultValue) {
        return (T) getOrDefault(paramKey.getKey(), defaultValue);
    }


}
