package cloud.tianai.captcha.generator.common.model.dto;

import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.ParamKey;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: 天爱有情
 * @date 2022/2/11 9:44
 * @Description 生成参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GenerateParam extends AnyMap {

    public GenerateParam() {
        // 设置一些默认值
        setBackgroundFormatName("jpeg");
        setTemplateFormatName("png");
        setObfuscate(false);
        setType(CaptchaTypeConstant.SLIDER);
    }

    /**
     * 背景格式化类型.
     */
    private static final ParamKey<String> backgroundFormatName = () -> "backgroundFormatName";
    /**
     * 模板图片格式化类型.
     */
    private static final ParamKey<String> templateFormatName = () -> "templateFormatName";
    /**
     * 是否混淆.
     */
    private static final ParamKey<Boolean> obfuscate = () -> "obfuscate";
    /**
     * 类型.
     */
    private static final ParamKey<String> type = () -> "type";
    /**
     * 背景图片标签, 用户二级过滤背景图片，或指定某背景图片.
     */
    private static final ParamKey<String> backgroundImageTag = () -> "backgroundImageTag";
    /**
     * 滑动图片标签,用户二级过滤模板图片，或指定某模板图片..
     */
    private static final ParamKey<String> templateImageTag = () -> "templateImageTag";


    // =============== getter and setter ====================
    public void setBackgroundFormatName(String backgroundFormatName) {
        addParam(GenerateParam.backgroundFormatName, backgroundFormatName);
    }

    public void setTemplateFormatName(String templateFormatName) {
        addParam(GenerateParam.templateFormatName, templateFormatName);
    }

    public void setObfuscate(boolean obfuscate) {
        addParam(GenerateParam.obfuscate, obfuscate);
    }

    public void setType(String type) {
        addParam(GenerateParam.type, type);
    }

    public void setBackgroundImageTag(String backgroundImageTag) {
        addParam(GenerateParam.backgroundImageTag, backgroundImageTag);
    }

    public void setTemplateImageTag(String templateImageTag) {
        addParam(GenerateParam.templateImageTag, templateImageTag);
    }

    public String getBackgroundFormatName() {
        return getParam(GenerateParam.backgroundFormatName);
    }

    public String getTemplateFormatName() {
        return getParam(GenerateParam.templateFormatName);
    }

    public boolean getObfuscate() {
        return getParam(GenerateParam.obfuscate);
    }

    public String getType() {
        return getParam(GenerateParam.type);
    }

    public String getBackgroundImageTag() {
        return getParam(GenerateParam.backgroundImageTag);
    }

    public String getTemplateImageTag() {
        return getParam(GenerateParam.templateImageTag);
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


        public GenerateParam build() {
            GenerateParam generateParam = new GenerateParam();
            generateParam.setBackgroundFormatName(backgroundFormatName);
            generateParam.setTemplateFormatName(templateFormatName);
            generateParam.setObfuscate(obfuscate);
            generateParam.setType(type);
            generateParam.setBackgroundImageTag(backgroundImageTag);
            generateParam.setTemplateImageTag(templateImageTag);
            return generateParam;
        }
    }


}
