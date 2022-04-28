package cloud.tianai.captcha.template.slider.common.util;

/**
 * @Author: 天爱有情
 * @date 2022/4/27 11:34
 * @Description 字体工具包
 */
public class FontUtils {

    public static char getRandomChar() {
        return (char)(0x4e00 + (int)(Math.random()*(0x9fa5 - 0x4e00 + 1)));
    }




}
