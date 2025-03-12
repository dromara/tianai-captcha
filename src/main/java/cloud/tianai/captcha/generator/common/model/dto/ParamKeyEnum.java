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

    private String key;

}
