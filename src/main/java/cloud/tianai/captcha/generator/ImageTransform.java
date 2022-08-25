package cloud.tianai.captcha.generator;

import java.awt.image.BufferedImage;

/**
 * @Author: 天爱有情
 * @date 2022/8/25 10:21
 * @Description 图片转换为字符串， 扩展接口, 可以转换为文件地址等
 */
public interface ImageTransform {

    /**
     * 转换
     *
     * @param bufferedImage 图片
     * @param transformType 转换类型
     * @return String
     */
    String transform(BufferedImage bufferedImage, String transformType);
}
