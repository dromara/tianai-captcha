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
// param作为扩展字段暂时将param从equals和toString中移除掉 以适应 CacheImageCaptchaGenerator
@EqualsAndHashCode(exclude = "param")
public class GenerateParam {

    /**
     * 背景格式化类型.
     */
    private String backgroundFormatName = "jpeg";
    /**
     * 模板图片格式化类型.
     */
    private String templateFormatName = "png";
    /**
     * 是否混淆.
     */
    private Boolean obfuscate = false;
    /**
     * 类型.
     */
    private String type = CaptchaTypeConstant.SLIDER;
    /**
     * 背景图片标签, 用户二级过滤背景图片，或指定某背景图片.
     */
    private String backgroundImageTag;
    /**
     * 滑动图片标签,用户二级过滤模板图片，或指定某模板图片..
     */
    private String templateImageTag;
    /**
     * 扩展参数.
     */
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


    public <T> void addParam(ParamKey<T> paramKey, T value) {
        addParam(paramKey.getKey(), value);
    }

    public <T> T getParam(ParamKey<T> paramKey) {
        return (T) getParam(paramKey.getKey());
    }

    public <T> T getOrDefault(ParamKey<T> paramKey, T defaultValue) {
        return (T) getOrDefault(paramKey.getKey(), defaultValue);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String backgroundFormatName = "jpeg";
        private String templateFormatName = "png";
        private Boolean obfuscate = false;
        private String type = CaptchaTypeConstant.SLIDER;
        private String backgroundImageTag;
        private String templateImageTag;
        private AnyMap param = new AnyMap();

        private Builder() {
        }

        public Builder backgroundFormatName(String backgroundFormatName) {
            this.backgroundFormatName = backgroundFormatName;
            return this;
        }

        public Builder templateFormatName(String templateFormatName) {
            this.templateFormatName = templateFormatName;
            return this;
        }

        public Builder obfuscate(Boolean obfuscate) {
            this.obfuscate = obfuscate;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder backgroundImageTag(String backgroundImageTag) {
            this.backgroundImageTag = backgroundImageTag;
            return this;
        }

        public Builder templateImageTag(String templateImageTag) {
            this.templateImageTag = templateImageTag;
            return this;
        }

        public Builder param(AnyMap param) {
            this.param = param;
            return this;
        }

        public GenerateParam build() {
            GenerateParam generateParam = new GenerateParam();
            generateParam.backgroundFormatName = backgroundFormatName;
            generateParam.templateFormatName = templateFormatName;
            generateParam.obfuscate = obfuscate;
            generateParam.type = type;
            generateParam.backgroundImageTag = backgroundImageTag;
            generateParam.templateImageTag = templateImageTag;
            generateParam.param = param;
            return generateParam;
        }
    }


}
