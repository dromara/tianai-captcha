package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.util.FontUtils;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.model.dto.ClickImageCheckDefinition;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.common.model.dto.ImageTransformData;
import cloud.tianai.captcha.generator.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @Author: 天爱有情
 * @date 2022/4/27 11:46
 * @Description 点选验证码
 */
public class StandardRandomWordClickImageCaptchaGenerator extends AbstractClickImageCaptchaGenerator {

    /** 字体包. */
    @Getter
    @Setter
    protected Font font;
    @Getter
    @Setter
    protected Integer clickImgWidth = 80;
    @Getter
    @Setter
    protected Integer clickImgHeight = 80;
    @Getter
    @Setter
    protected int tipImageInterferenceLineNum = 2;
    @Getter
    @Setter
    protected int tipImageInterferencePointNum = 5;
    /** 参与校验的数量. */
    @Getter
    @Setter
    protected Integer checkClickCount = 4;
    /** 干扰数量. */
    @Getter
    @Setter
    protected Integer interferenceCount = 2;

    /**
     * 因为在画文字图形的时候 y 值不能准确通过 除法计算得出， 字体大小不一致中间的容错值算不准确
     * 方案: 通过 线性回归模型 计算出  intercept和coef 用于计算 容错值
     * 训练数据为 宋体 字体大小为 30~150 随机选择7组数据进行训练， 训练后r2结果为 0.9967106324620846
     */
    protected float intercept = 0.39583333f;
    protected float coef = 0.14645833f;

    protected float currentFontTopCoef = 0.0f;

    public StandardRandomWordClickImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        super(imageCaptchaResourceManager);
    }

    public StandardRandomWordClickImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, ImageTransform imageTransform) {
        super(imageCaptchaResourceManager);
        setImageTransform(imageTransform);
    }
    @Override
    protected List<Resource> randomGetClickImgTips(GenerateParam param) {
        int tipSize = interferenceCount + checkClickCount;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Resource> tipList = new ArrayList<>(tipSize);
        for (int i = 0; i < tipSize; i++) {
            String randomWord = FontUtils.getRandomChar(random);
            tipList.add(new Resource(null, randomWord));
        }
        // 随机文字
        return tipList;
    }

    @Override
    @SneakyThrows({IOException.class, FontFormatException.class})
    protected void doInit(boolean initDefaultResource) {
        if (this.font == null) {
            // 使用默认字体
            Resource fontResource = new Resource(null, "META-INF/fonts/SIMSUN.TTC");
            InputStream inputStream = new ClassPathResourceProvider().doGetResourceInputStream(fontResource);
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            this.font = font.deriveFont(Font.BOLD, 70);
        }
        // 计算容错
        currentFontTopCoef = coef * font.getSize() + intercept;
        if (initDefaultResource) {
            initDefaultResource();
        }
    }

    public StandardRandomWordClickImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, Font font) {
        super(imageCaptchaResourceManager);
        this.font = font;
    }

    public void initDefaultResource() {
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加一些系统的资源文件
        resourceStore.addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg")));
    }

    public ImgWrapper genTipImage(List<ClickImageCheckDefinition> imageCheckDefinitions) {
        String tips = imageCheckDefinitions.stream().map(c -> c.getTip().getData()).collect(Collectors.joining());
        // 生成随机颜色
        int fontWidth = tips.length() * font.getSize();
        int width = fontWidth + 6;
        int height = font.getSize() + 6;
        float left = (width - fontWidth) / 2f;
        float top = 6 / 2f + font.getSize() - currentFontTopCoef;
        BufferedImage bufferedImage = CaptchaImageUtils.genSimpleImgCaptcha(tips,
                font, width, height, left, top, tipImageInterferenceLineNum, tipImageInterferencePointNum);
        return new ImgWrapper(bufferedImage, new Resource(null, tips));
    }

    @Override
    public ImgWrapper getClickImg(Resource tip) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // 随机颜色
        Color randomColor = CaptchaImageUtils.getRandomColor(random);
        // 随机角度
        int randomDeg = randomInt(0, 85);
        BufferedImage fontImage = CaptchaImageUtils.drawWordImg(randomColor,
                tip.getData(),
                font,
                currentFontTopCoef,
                clickImgWidth,
                clickImgHeight,
                randomDeg);
        return new ImgWrapper(fontImage, tip);
    }

    @Override
    protected List<ClickImageCheckDefinition> filterAndSortClickImageCheckDefinition(List<ClickImageCheckDefinition> allCheckDefinitionList) {
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

    @Override
    public ImageCaptchaInfo wrapClickImageCaptchaInfo(GenerateParam param, BufferedImage bgImage,
                                                      List<ClickImageCheckDefinition> checkClickImageCheckDefinitionList, Resource resourceImage) {
        // 提示图片
        BufferedImage tipImage = genTipImage(checkClickImageCheckDefinitionList).getImage();
        ImageTransformData transform = getImageTransform().transform(param, bgImage, tipImage, resourceImage, checkClickImageCheckDefinitionList);
        ImageCaptchaInfo clickImageCaptchaInfo = new ImageCaptchaInfo();
        clickImageCaptchaInfo.setBackgroundImage(transform.getBackgroundImageUrl());
        clickImageCaptchaInfo.setBackgroundImageTag(resourceImage.getTag());
        clickImageCaptchaInfo.setTemplateImage(transform.getTemplateImageUrl());
        clickImageCaptchaInfo.setBackgroundImageWidth(bgImage.getWidth());
        clickImageCaptchaInfo.setBackgroundImageHeight(bgImage.getHeight());
        clickImageCaptchaInfo.setTemplateImageWidth(tipImage.getWidth());
        clickImageCaptchaInfo.setTemplateImageHeight(tipImage.getHeight());
        clickImageCaptchaInfo.setRandomX(null);
        clickImageCaptchaInfo.setTolerant(null);
        clickImageCaptchaInfo.setType(CaptchaTypeConstant.WORD_IMAGE_CLICK);
        clickImageCaptchaInfo.setExpand(checkClickImageCheckDefinitionList);
        return clickImageCaptchaInfo;
    }

}
