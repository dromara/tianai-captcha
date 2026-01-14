package cloud.tianai.captcha.generator.common.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParamKeyEnum<T> implements ParamKey<T> {


    /** 点选验证码参与校验的数量. 值为Integer */
    public static final ParamKey<Integer> CLICK_CHECK_CLICK_COUNT = new ParamKeyEnum<>("checkClickCount");
    /** 点选验证码干扰数量. 值为Integer */
    public static final ParamKey<Integer> CLICK_INTERFERENCE_COUNT = new ParamKeyEnum<>("interferenceCount");
    /** 读取字体时，可指定字体TAG，可用于给不同的验证码指定不同的字体包.*/
    public static final ParamKey<String> FONT_TAG = new ParamKeyEnum<>("fontTag");

    /** 验证码ID，内部使用.*/
    public static final ParamKey<String> ID = new ParamKeyEnum<>("_id");

    private String key;

}
