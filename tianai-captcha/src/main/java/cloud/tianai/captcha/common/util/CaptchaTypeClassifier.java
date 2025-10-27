package cloud.tianai.captcha.common.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: 天爱有情
 * @date 2023/11/2 9:22
 * @Description 验证码类型分类
 */
public class CaptchaTypeClassifier {

    private static final Set<String> SLIDER_CAPTCHA_TYPES = new HashSet<>();
    private static final Set<String> CLICK_CAPTCHA_TYPES = new HashSet<>();
    private static final Set<String> JIGSAW_CAPTCHA_TYPES = new HashSet<>();

    public static void addSliderCaptchaType(String type) {
        SLIDER_CAPTCHA_TYPES.add(type.toUpperCase());
    }

    public static void addClickCaptchaType(String type) {
        CLICK_CAPTCHA_TYPES.add(type.toUpperCase());
    }

    public static boolean isSliderCaptcha(String type) {
        return SLIDER_CAPTCHA_TYPES.contains(type.toUpperCase());
    }

    public static boolean isClickCaptcha(String type) {
        return CLICK_CAPTCHA_TYPES.contains(type.toUpperCase());
    }

    public static Set<String> getSliderCaptchaTypes() {
        return SLIDER_CAPTCHA_TYPES;
    }

    public static Set<String> getClickCaptchaTypes() {
        return CLICK_CAPTCHA_TYPES;
    }

    public static void removeSliderCaptchaType(String type) {
        SLIDER_CAPTCHA_TYPES.remove(type.toUpperCase());
    }

    public static void removeClickCaptchaType(String type) {
        CLICK_CAPTCHA_TYPES.remove(type.toUpperCase());
    }

    public static boolean isJigsawCaptcha(String type) {
        return JIGSAW_CAPTCHA_TYPES.contains(type.toUpperCase());
    }

    public static void addJigsawCaptchaType(String type) {
        JIGSAW_CAPTCHA_TYPES.add(type.toUpperCase());
    }

    public static void removeJigsawCaptchaType(String type) {
        JIGSAW_CAPTCHA_TYPES.remove(type.toUpperCase());
    }

    public static Set<String> getJigsawCaptchaTypes() {
        return JIGSAW_CAPTCHA_TYPES;
    }
}
