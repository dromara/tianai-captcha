package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.constant.CommonConstant;
import cloud.tianai.captcha.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.CaptchaExchange;
import cloud.tianai.captcha.generator.common.model.dto.ClickImageCheckDefinition;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: 天爱有情
 * @date 2022/4/27 11:46
 * @Description 点选验证码 点选验证码分为点选文字和点选图标等
 */
public abstract class AbstractClickImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    public static final String CLICK_IMAGE_DISTORT_KEY = "clickImageDistort";

    public AbstractClickImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        super(imageCaptchaResourceManager);
    }

    public AbstractClickImageCaptchaGenerator() {
    }

    @SneakyThrows
    @Override
    public void doGenerateCaptchaImage(CaptchaExchange captchaExchange) {
        GenerateParam param = captchaExchange.getParam();
        // 文字点选验证码不需要模板 只需要背景图
        Resource resourceImage = requiredRandomGetResource(param.getType(), param.getBackgroundImageTag());

        BufferedImage bgImage = getResourceImage(resourceImage);

        List<ResourceMap> imgTips = randomGetClickImgTips(param);
        int allImages = imgTips.size();
        List<ClickImageCheckDefinition> clickImageCheckDefinitionList = new ArrayList<>(allImages);
        int avg = bgImage.getWidth() / allImages;
        if (allImages < imgTips.size()) {
            throw new IllegalStateException("随机生成点击图片小于请求数量， 请求生成数量=" + allImages + ",实际生成数量=" + imgTips.size());
        }
        List<Block> blocks = new ArrayList<>();
        for (int i = 0; i < allImages; i++) {
            ResourceMap resourceMap = imgTips.get(i);
            Resource tipResource = resourceMap.get(CommonConstant.IMAGE_TIP_ICON);
            Resource clickResource = resourceMap.get(CommonConstant.IMAGE_CLICK_ICON);
            if (clickResource == null) {
                throw new IllegalStateException("随机生成点击图片失败，资源中必须包含[" + CommonConstant.IMAGE_CLICK_ICON + "]" + resourceMap);
            }
            if (tipResource == null) {
                tipResource = clickResource;
            }

            // 随机获取点击图片
            ClickImageCheckDefinition.ImgWrapper imgWrapper = getClickImg(param, clickResource, null, bgImage);
            BufferedImage image = imgWrapper.getImage();
            // 增加功能，是否需要扭曲图片
            image = obfuscateImage(image, param);
            int clickImgWidth = image.getWidth();
            int clickImgHeight = image.getHeight();
            if (i == 0) {
                // 假设每个icon的大小都是一样的, 按照宽高进行分块
                int w = clickImgWidth + clickImgWidth / 2;
                int h = clickImgHeight + clickImgHeight / 2;
                int xNum = (int) Math.floor((double) bgImage.getWidth() / w);
                int yNum = (int) Math.floor((double) bgImage.getHeight() / h);
                for (int x = 0; x < xNum; x++) {
                    for (int y = 0; y < yNum; y++) {
                        blocks.add(new Block(x * w + clickImgWidth / 2, clickImgWidth, y * h + clickImgHeight / 2, clickImgHeight));
                    }
                }
            }
            Block block = blocks.remove(ThreadLocalRandom.current().nextInt(0, blocks.size()));
//            // 随机x
//            int randomX;
//            if (i == 0) {
//                randomX = 1;
//            } else {
//                randomX = avg * i;
//            }
//            // 随机y
//            int randomY = randomInt(10, bgImage.getHeight() - clickImgHeight);
            // 通过随机x和y 进行覆盖图片7
            CaptchaImageUtils.overlayImage(bgImage, image, block.startX, block.startY);
            ClickImageCheckDefinition clickImageCheckDefinition = new ClickImageCheckDefinition();
            clickImageCheckDefinition.setTip(tipResource);
            clickImageCheckDefinition.setTipImage(imgWrapper);
            clickImageCheckDefinition.setX(block.startX + clickImgWidth / 2);
            clickImageCheckDefinition.setY(block.startY + clickImgHeight / 2);
            clickImageCheckDefinition.setWidth(clickImgWidth);
            clickImageCheckDefinition.setHeight(clickImgHeight);
            clickImageCheckDefinition.setImageColor(imgWrapper.getImageColor());
            clickImageCheckDefinitionList.add(clickImageCheckDefinition);
        }
        List<ClickImageCheckDefinition> checkClickImageCheckDefinitionList = filterAndSortClickImageCheckDefinition(captchaExchange, clickImageCheckDefinitionList);
        captchaExchange.setBackgroundImage(bgImage);
        captchaExchange.setTransferData(checkClickImageCheckDefinitionList);
        captchaExchange.setResourceImage(resourceImage);


//        // wrap
//        ImageCaptchaInfo imageCaptchaInfo = wrapClickImageCaptchaInfo(param, bgImage, checkClickImageCheckDefinitionList, resourceImage, data);
//        imageCaptchaInfo.setData(data);
//        return imageCaptchaInfo;

    }

    private BufferedImage obfuscateImage(BufferedImage image, GenerateParam param) {
        return image;
    }

    /**
     * 过滤并排序校验的图片点选顺序
     *
     * @param allCheckDefinitionList 总的点选图片
     * @return List<ClickImageCheckDefinition>
     */
    protected abstract List<ClickImageCheckDefinition> filterAndSortClickImageCheckDefinition(CaptchaExchange captchaExchange, List<ClickImageCheckDefinition> allCheckDefinitionList);

    /**
     * 随机获取一组数据用于生成随机图
     *
     * @return List<String>
     */
    protected abstract List<ResourceMap> randomGetClickImgTips(GenerateParam param);

    /**
     * 随机获取点击的图片
     *
     * @param tip 提示数据,根据改数据生成图片
     * @return ImgWrapper
     */
    public abstract ClickImageCheckDefinition.ImgWrapper getClickImg(GenerateParam param, Resource tip, Color randomColor, BufferedImage bgImage);


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Block {
        private int startX;
        private int width;
        private int startY;
        private int height;
    }
}
