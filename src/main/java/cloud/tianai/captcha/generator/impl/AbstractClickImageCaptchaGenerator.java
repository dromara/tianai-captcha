package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.CaptchaExchange;
import cloud.tianai.captcha.generator.common.model.dto.ClickImageCheckDefinition;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

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

        List<Resource> imgTips = randomGetClickImgTips(param);
        int allImages = imgTips.size();
        List<ClickImageCheckDefinition> clickImageCheckDefinitionList = new ArrayList<>(allImages);
        int avg = bgImage.getWidth() / allImages;
        if (allImages < imgTips.size()) {
            throw new IllegalStateException("随机生成点击图片小于请求数量， 请求生成数量=" + allImages + ",实际生成数量=" + imgTips.size());
        }
        for (int i = 0; i < allImages; i++) {
            // 随机获取点击图片
            ImgWrapper imgWrapper = getClickImg(imgTips.get(i),null);
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
            int randomY = randomInt(10, bgImage.getHeight() - clickImgHeight);
            // 通过随机x和y 进行覆盖图片
            CaptchaImageUtils.overlayImage(bgImage, image, randomX, randomY);
            ClickImageCheckDefinition clickImageCheckDefinition = new ClickImageCheckDefinition();
            clickImageCheckDefinition.setTip(imgWrapper.getTip());
            clickImageCheckDefinition.setX(randomX + clickImgWidth / 2);
            clickImageCheckDefinition.setY(randomY + clickImgHeight / 2);
            clickImageCheckDefinition.setWidth(clickImgWidth);
            clickImageCheckDefinition.setHeight(clickImgHeight);
            clickImageCheckDefinition.setImageColor(imgWrapper.getImageColor());
            clickImageCheckDefinitionList.add(clickImageCheckDefinition);
        }
        List<ClickImageCheckDefinition> checkClickImageCheckDefinitionList = filterAndSortClickImageCheckDefinition(captchaExchange,clickImageCheckDefinitionList);
        captchaExchange.setBackgroundImage(bgImage);
        captchaExchange.setTransferData(checkClickImageCheckDefinitionList);
        captchaExchange.setResourceImage(resourceImage);


//        // wrap
//        ImageCaptchaInfo imageCaptchaInfo = wrapClickImageCaptchaInfo(param, bgImage, checkClickImageCheckDefinitionList, resourceImage, data);
//        imageCaptchaInfo.setData(data);
//        return imageCaptchaInfo;

    }

    /**
     * 过滤并排序校验的图片点选顺序
     *
     * @param allCheckDefinitionList 总的点选图片
     * @return List<ClickImageCheckDefinition>
     */
    protected abstract List<ClickImageCheckDefinition> filterAndSortClickImageCheckDefinition(CaptchaExchange captchaExchange,List<ClickImageCheckDefinition> allCheckDefinitionList);

    /**
     * 随机获取一组数据用于生成随机图
     *
     * @return List<String>
     */
    protected abstract List<Resource> randomGetClickImgTips(GenerateParam param);

    /**
     * 随机获取点击的图片
     *
     * @param tip 提示数据,根据改数据生成图片
     * @return ImgWrapper
     */
    public abstract ImgWrapper getClickImg(Resource tip, Color randomColor);

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
        private Resource tip;
        /** 图片颜色. */
        private Color imageColor;
    }
}
