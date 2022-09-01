package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.ClickImageCheckDefinition;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import lombok.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: 天爱有情
 * @date 2022/4/27 11:46
 * @Description 点选验证码 点选验证码分为点选文字和点选图标等
 */
public abstract class AbstractClickImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    /** 参与校验的数量. */
    @Getter
    @Setter
    protected Integer checkClickCount = 4;
    /** 干扰数量. */
    @Getter
    @Setter
    protected Integer interferenceCount = 2;

    public AbstractClickImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        super(imageCaptchaResourceManager);
    }

    public AbstractClickImageCaptchaGenerator() {
    }

    @SneakyThrows
    @Override
    public ImageCaptchaInfo doGenerateCaptchaImage(GenerateParam param) {
        // 文字点选验证码不需要模板 只需要背景图
        Collection<InputStream> inputStreams = new LinkedList<>();
        try {
            Resource resourceImage = requiredRandomGetResource(param.getType());
            InputStream resourceInputStream = getImageResourceManager().getResourceInputStream(resourceImage);
            inputStreams.add(resourceInputStream);
            BufferedImage bgImage = CaptchaImageUtils.wrapFile2BufferedImage(resourceInputStream);

            List<ClickImageCheckDefinition> clickImageCheckDefinitionList = new ArrayList<>(interferenceCount);
            int allImages = interferenceCount + checkClickCount;
            int avg = bgImage.getWidth() / allImages;
            List<String> imgTips = randomGetClickImgTips(allImages);
            if (allImages < imgTips.size()) {
                throw new IllegalStateException("随机生成点击图片小于请求数量， 请求生成数量=" + allImages + ",实际生成数量=" + imgTips.size());
            }
            for (int i = 0; i < allImages; i++) {
                // 随机获取点击图片
                ImgWrapper imgWrapper = getClickImg(imgTips.get(i));
                BufferedImage image = imgWrapper.getImage();
                int clickImgWidth = image.getWidth();
                int clickImgHeight = image.getHeight();
                // 随机x
                int randomX;
                if (i == 0) {
                    randomX = 1;
                } else {
                    randomX = avg * i;
                }
                // 随机y
                int randomY = ThreadLocalRandom.current().nextInt(10, bgImage.getHeight() - clickImgHeight);
                // 通过随机x和y 进行覆盖图片
                CaptchaImageUtils.overlayImage(bgImage, imgWrapper.getImage(), randomX, randomY);
                ClickImageCheckDefinition clickImageCheckDefinition = new ClickImageCheckDefinition();
                clickImageCheckDefinition.setTip(imgWrapper.getTip());
                clickImageCheckDefinition.setX(randomX + clickImgWidth / 2);
                clickImageCheckDefinition.setY(randomY + clickImgHeight / 2);
                clickImageCheckDefinition.setWidth(clickImgWidth);
                clickImageCheckDefinition.setHeight(clickImgHeight);
                clickImageCheckDefinitionList.add(clickImageCheckDefinition);
            }
            List<ClickImageCheckDefinition> checkClickImageCheckDefinitionList = getCheckClickImageCheckDefinitionList(clickImageCheckDefinitionList,
                    checkClickCount);
            // wrap
            return wrapClickImageCaptchaInfo(param, bgImage, checkClickImageCheckDefinitionList);

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
    }

    /**
     * 从总的图片中 去除要校验的图片数据 以及顺序
     *
     * @param allCheckDefinitionList 总的点选图片
     * @param checkClickCount        参与校验的数据
     * @return List<ClickImageCheckDefinition>
     */
    protected List<ClickImageCheckDefinition> getCheckClickImageCheckDefinitionList(List<ClickImageCheckDefinition> allCheckDefinitionList,
                                                                                    Integer checkClickCount) {
        // 打乱
        Collections.shuffle(allCheckDefinitionList);
        // 拿出参与校验的数据
        List<ClickImageCheckDefinition> checkClickImageCheckDefinitionList = new ArrayList<>(checkClickCount);
        for (int i = 0; i < checkClickCount; i++) {
            ClickImageCheckDefinition clickImageCheckDefinition = allCheckDefinitionList.get(i);
            checkClickImageCheckDefinitionList.add(clickImageCheckDefinition);
        }
        return checkClickImageCheckDefinitionList;
    }

    /**
     * 随机获取一组数据用于生成随机图
     *
     * @param tipSize tipSize
     * @return List<String>
     */
    protected abstract List<String> randomGetClickImgTips(int tipSize);

    /**
     * 随机获取点击的图片
     *
     * @param tip 提示数据,根据改数据生成图片
     * @return ImgWrapper
     */
    public abstract ImgWrapper getClickImg(String tip);

    /**
     * 包装 ImageCaptchaInfo
     *
     * @param param                              param
     * @param bgImage                            bgImage
     * @param checkClickImageCheckDefinitionList checkClickImageCheckDefinitionList
     * @return ImageCaptchaInfo
     */
    public abstract ImageCaptchaInfo wrapClickImageCaptchaInfo(GenerateParam param, BufferedImage bgImage,
                                                               List<ClickImageCheckDefinition> checkClickImageCheckDefinitionList);

    /**
     * @Author: 天爱有情
     * @date 2022/4/28 14:26
     * @Description 点击图片包装
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImgWrapper {
        /** 图片. */
        private BufferedImage image;
        /** 提示. */
        private String tip;
    }
}
