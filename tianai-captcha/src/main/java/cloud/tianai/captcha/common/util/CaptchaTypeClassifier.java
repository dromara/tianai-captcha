package cloud.tianai.captcha.common.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: 天爱有情
 * @date 2023/11/2 9:22
 * @Description 验证码类型分类
 */
public class CaptchaTypeClassifier {

    /**
     * 每个类型集合的最大容量，防止内存泄漏
     */
    private static final int MAX_TYPE_SIZE = 100;

    private static final Set<String> SLIDER_CAPTCHA_TYPES = new HashSet<>();
    private static final Set<String> CLICK_CAPTCHA_TYPES = new HashSet<>();
    private static final Set<String> JIGSAW_CAPTCHA_TYPES = new HashSet<>();

    public static void addSliderCaptchaType(String type) {
        checkCapacity(SLIDER_CAPTCHA_TYPES, "SLIDER_CAPTCHA_TYPES");
        SLIDER_CAPTCHA_TYPES.add(type.toUpperCase());
    }

    public static void addClickCaptchaType(String type) {
        checkCapacity(CLICK_CAPTCHA_TYPES, "CLICK_CAPTCHA_TYPES");
        CLICK_CAPTCHA_TYPES.add(type.toUpperCase());
    }

    public static boolean isSliderCaptcha(String type) {
        return SLIDER_CAPTCHA_TYPES.contains(type.toUpperCase());
    }

    public static boolean isClickCaptcha(String type) {
        return CLICK_CAPTCHA_TYPES.contains(type.toUpperCase());
    }

    public static Set<String> getSliderCaptchaTypes() {
        return Collections.unmodifiableSet(SLIDER_CAPTCHA_TYPES);
    }

    public static Set<String> getClickCaptchaTypes() {
        return Collections.unmodifiableSet(CLICK_CAPTCHA_TYPES);
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
        checkCapacity(JIGSAW_CAPTCHA_TYPES, "JIGSAW_CAPTCHA_TYPES");
        JIGSAW_CAPTCHA_TYPES.add(type.toUpperCase());
    }

    public static void removeJigsawCaptchaType(String type) {
        JIGSAW_CAPTCHA_TYPES.remove(type.toUpperCase());
    }

    public static Set<String> getJigsawCaptchaTypes() {
        return Collections.unmodifiableSet(JIGSAW_CAPTCHA_TYPES);
    }

    /**
     * 检查集合容量，防止内存泄漏
     * @param set 要检查的集合
     * @param name 集合名称，用于日志
     */
    private static void checkCapacity(Set<String> set, String name) {
        if (set.size() >= MAX_TYPE_SIZE) {
            throw new IllegalStateException(
                String.format("验证码类型集合 %s 已达到最大容量 %d，无法继续添加", name, MAX_TYPE_SIZE)
            );
        }
    }
}
