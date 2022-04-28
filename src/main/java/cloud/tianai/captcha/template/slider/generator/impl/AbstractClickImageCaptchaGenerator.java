package cloud.tianai.captcha.template.slider.generator.impl;

import cloud.tianai.captcha.template.slider.generator.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.template.slider.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ClickImageCheckDefinition;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.resource.common.model.dto.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static cloud.tianai.captcha.template.slider.generator.common.util.CaptchaImageUtils.wrapFile2BufferedImage;

/**
 * @Author: 天爱有情
 * @date 2022/4/27 11:46
 * @Description 点选验证码 点选验证码分为点选文字和点选图标等
 */
@Data
public abstract class AbstractClickImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    /** 参与校验的数量.*/
    protected Integer checkClickCount = 4;
    /** 干扰数量.*/
    protected Integer interferenceCount = 2;

    @SneakyThrows
    @Override
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {
        // 文字点选验证码不需要模板 只需要背景图
        Collection<InputStream> inputStreams = new LinkedList<>();
        try {
            Resource resourceImage = getImageResourceManager().randomGetResource(param.getType());
            InputStream resourceInputStream = getImageResourceManager().getResourceInputStream(resourceImage);
            inputStreams.add(resourceInputStream);
            BufferedImage bgImage = wrapFile2BufferedImage(resourceInputStream);

            List<ClickImageCheckDefinition> clickImageCheckDefinitionList = new ArrayList<>(interferenceCount);
            int allImages = interferenceCount + checkClickCount;
            for (int i = 0; i < allImages; i++) {
                // 随机获取点击图片
                ImgWrapper imgWrapper = randomGetClickImg();
                BufferedImage image = imgWrapper.getImage();
                int clickImgWidth = image.getWidth();
                int clickImgHeight = image.getHeight();
                // 随机x
                int randomX = ThreadLocalRandom.current().nextInt(10, bgImage.getWidth() - clickImgWidth);
                // 随机y
                int randomY = ThreadLocalRandom.current().nextInt(10, bgImage.getHeight() - clickImgHeight);
                // 通过随机x和y 进行覆盖图片
                CaptchaImageUtils.overlayImage(bgImage, imgWrapper.getImage(), randomX, randomY);
                ImageIO.write(imgWrapper.getImage(), "png", new FileOutputStream("C:\\Users\\tianai\\Desktop\\111\\" + i + ".png"));
                ClickImageCheckDefinition clickImageCheckDefinition = new ClickImageCheckDefinition();
                clickImageCheckDefinition.setTip(imgWrapper.getTip());
                clickImageCheckDefinition.setX(randomX + clickImgWidth / 2);
                clickImageCheckDefinition.setY(randomY + clickImgHeight / 2);
                clickImageCheckDefinition.setWidth(clickImgWidth);
                clickImageCheckDefinition.setHeight(clickImgHeight);
                clickImageCheckDefinitionList.add(clickImageCheckDefinition);
            }
            // 背景图转换为字符串
//            String bgImageStr = transform(bgImage, param.getBackgroundFormatName());
            try {
                ImageIO.write(bgImage, "jpeg", new FileOutputStream("C:\\Users\\tianai\\Desktop\\123.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 打乱
            Collections.shuffle(clickImageCheckDefinitionList);
            // 拿出参与校验的数据
            List<ClickImageCheckDefinition> checkClickImageCheckDefinitionList = new ArrayList<>(checkClickCount);
            for (int i = 0; i < checkClickCount; i++) {
                ClickImageCheckDefinition clickImageCheckDefinition = clickImageCheckDefinitionList.get(i);
                checkClickImageCheckDefinitionList.add(clickImageCheckDefinition);
            }
            // 将校验的文字生成提示图片
            ImgWrapper tipImage = genTipImage(checkClickImageCheckDefinitionList);

            try {
                ImageIO.write(tipImage.getImage(), "png", new FileOutputStream("C:\\Users\\tianai\\Desktop\\456.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return wrapClickImageCaptchaInfo(param, bgImage, tipImage.getImage(), checkClickImageCheckDefinitionList);

        } finally {
            // 使用完后关闭流
            for (InputStream inputStream : inputStreams) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

//        return null;
    }

    protected ImageCaptchaInfo wrapClickImageCaptchaInfo(GenerateParam param, BufferedImage bgImage,
                                                         BufferedImage tipImage,
                                                         List<ClickImageCheckDefinition> checkClickImageCheckDefinitionList) {
        ImageCaptchaInfo clickImageCaptchaInfo = new ImageCaptchaInfo();
        clickImageCaptchaInfo.setBackgroundImage(transform(bgImage, param.getBackgroundFormatName()));
        clickImageCaptchaInfo.setSliderImage(transform(bgImage, param.getSliderFormatName()));
        clickImageCaptchaInfo.setBgImageWidth(bgImage.getWidth());
        clickImageCaptchaInfo.setBgImageHeight(bgImage.getHeight());
        clickImageCaptchaInfo.setSliderImageWidth(tipImage.getWidth());
        clickImageCaptchaInfo.setSliderImageHeight(tipImage.getHeight());
        clickImageCaptchaInfo.setRandomX(null);
        clickImageCaptchaInfo.setTolerant(null);
        clickImageCaptchaInfo.setType(CaptchaTypeConstant.IMAGE_CLICK);
        clickImageCaptchaInfo.setExpand(checkClickImageCheckDefinitionList);
        return clickImageCaptchaInfo;
    }

    protected abstract ImgWrapper genTipImage(List<ClickImageCheckDefinition> imageCheckDefinitions);

    protected abstract ImgWrapper randomGetClickImg();

    /**
     * @Author: 天爱有情
     * @date 2022/4/28 14:26
     * @Description 点击图片包装
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImgWrapper {
        /** 图片.*/
        private BufferedImage image;
        /** 提示.*/
        private String tip;
    }
}
