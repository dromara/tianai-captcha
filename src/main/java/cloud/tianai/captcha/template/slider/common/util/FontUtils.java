package cloud.tianai.captcha.template.slider.common.util;

import lombok.SneakyThrows;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: 天爱有情
 * @date 2022/4/27 11:34
 * @Description 字体工具包
 */
public class FontUtils {

    @SneakyThrows
    public static String getRandomChar(Random random) {
        Integer hightPos, lowPos; // 定义高低位
        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));
        byte[] bytes = new byte[2];
        bytes[0] = hightPos.byteValue();
        bytes[1] = lowPos.byteValue();
        return new String(bytes, "GBK");
    }



}
