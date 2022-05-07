package cloud.tianai.captcha.common.util;

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

    /**
     * 获取随机文字
     *
     * @param random 随机数生成器
     * @return String
     */
    @SneakyThrows
    public static String getRandomChar(Random random) {
        Integer heightPos, lowPos; // 定义高低位
        heightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));
        byte[] bytes = new byte[2];
        bytes[0] = heightPos.byteValue();
        bytes[1] = lowPos.byteValue();
        return new String(bytes, "GBK");
    }


}
