package cloud.tianai.captcha.generator.common.constant;

/**
 * @Author: 天爱有情
 * @date 2021/8/7 17:14
 * @Description 滑块验证码常量
 */
public interface SliderCaptchaConstant {

    /** 模板滑块固定名称. */
    String TEMPLATE_ACTIVE_IMAGE_NAME = "active.png";
    /** 模板凹槽固定名称. */
    String TEMPLATE_FIXED_IMAGE_NAME = "fixed.png";
    /** 模板蒙版. */
    String TEMPLATE_MASK_IMAGE_NAME = "mask.png";
    /** 混淆的凹槽.*/
    String OBFUSCATE_TEMPLATE_FIXED_IMAGE_NAME = "obfuscate_" + TEMPLATE_FIXED_IMAGE_NAME;
}
