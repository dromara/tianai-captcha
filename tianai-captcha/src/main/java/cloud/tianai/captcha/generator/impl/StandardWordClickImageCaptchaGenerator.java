package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.constant.CommonConstant;
import cloud.tianai.captcha.common.exception.ImageCaptchaException;
import cloud.tianai.captcha.common.util.FontUtils;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.FontWrapper;
import cloud.tianai.captcha.generator.common.model.dto.*;
import cloud.tianai.captcha.generator.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.resource.FontCache;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.image.BufferedImage;
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
public class StandardWordClickImageCaptchaGenerator extends AbstractClickImageCaptchaGenerator {

    /** 字体包. */
//    @Getter
//    @Setter
//    protected List<FontWrapper> fonts = new ArrayList<>();
    protected Integer clickImgWidth = 100;
    protected Integer clickImgHeight = 100;
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
//    protected float intercept = 0.39583333f;
//    protected float coef = 0.14645833f;
//
//    protected float currentFontTopCoef = 0.0f;
    public StandardWordClickImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        this(imageCaptchaResourceManager, null, null);
    }


    public StandardWordClickImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, ImageTransform imageTransform, CaptchaInterceptor interceptor) {
        super(imageCaptchaResourceManager);
        setImageTransform(imageTransform);
        setInterceptor(interceptor);
    }


    @Override
    protected List<ResourceMap> randomGetClickImgTips(GenerateParam param) {
        Integer checkClickCount = param.getOrDefault(ParamKeyEnum.CLICK_CHECK_CLICK_COUNT, getCheckClickCount());
        Integer interferenceCount = param.getOrDefault(ParamKeyEnum.CLICK_INTERFERENCE_COUNT, getInterferenceCount());
        int tipSize = interferenceCount + checkClickCount;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<ResourceMap> tipList = new ArrayList<>(tipSize);
        for (int i = 0; i < tipSize; i++) {
            String randomWord = FontUtils.getRandomChar(random);
            ResourceMap resourceMap = new ResourceMap(param.getTemplateImageTag());
            resourceMap.put(CommonConstant.IMAGE_TIP_ICON, new Resource(null, randomWord));
            resourceMap.put(CommonConstant.IMAGE_CLICK_ICON, new Resource(null, randomWord));
            tipList.add(resourceMap);
        }
        // 随机文字
        return tipList;
    }

    @Override
    public ClickImageCheckDefinition.ImgWrapper getClickImg(GenerateParam param, Resource tip, Color randomColor) {
        if (randomColor == null) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            randomColor = CaptchaImageUtils.getRandomColor(random);
        }
        // 随机角度
        int randomDeg = randomInt(0, 85);
        // 缩放
        double factor = 600d;

        FontWrapper fontWrapper = randomFont(param);
        Font font = fontWrapper.getFont((float) (FontWrapper.DEFAULT_FONT_SIZE * factor));
        float currentFontTopCoef = fontWrapper.getFontTopCoef(font);
        // 图片点击宽度
        int clickImgWidth = (int) (font.getSize() * 1.428571428571429);

        BufferedImage fontImage = CaptchaImageUtils.drawWordImg(randomColor,
                tip.getData(),
                font,
                currentFontTopCoef,
                clickImgWidth,
                clickImgWidth,
                randomDeg);
        return new ClickImageCheckDefinition.ImgWrapper(fontImage, tip, randomColor);
    }

    @Override
    protected void doInit() {
//        if (CollectionUtils.isEmpty(fonts)) {
//            throw new ImageCaptchaException("初始化文字点选验证码失败，请设置字体包后再调用init()");
//        }
//        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
//        // 添加一些系统的资源文件
//        resourceStore.addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg"), DEFAULT_TAG));
    }

    public FontWrapper randomFont(GenerateParam param) {
        String fontTag = param.getOrDefault(ParamKeyEnum.FONT_TAG, CommonConstant.DEFAULT_TAG);
        Resource resource = requiredRandomGetResource(FontCache.FONT_TYPE, fontTag);
        Object extra = resource.getExtra();
        if (extra instanceof FontWrapper) {
            return (FontWrapper) extra;
        }
        throw new ImageCaptchaException("随机获取字体失败， resource中没有读到字体包, resource=" + resource);
    }



    public ClickImageCheckDefinition.ImgWrapper genTipImage(List<ClickImageCheckDefinition> imageCheckDefinitions, GenerateParam param) {
        FontWrapper fontWrapper = randomFont(param);
        Font font = fontWrapper.getFont();
        float currentFontTopCoef = fontWrapper.getFontTopCoef(font);
        String tips = imageCheckDefinitions.stream().map(c -> c.getTip().getData()).collect(Collectors.joining());
        // 生成随机颜色
        int fontWidth = tips.length() * font.getSize();
        int width = fontWidth + 6;
        int height = font.getSize() + 6;
        float left = (width - fontWidth) / 2f;
        float top = 6 / 2f + font.getSize() - currentFontTopCoef;
        BufferedImage bufferedImage = CaptchaImageUtils.genSimpleImgCaptcha(tips,
                font, width, height, left, top, tipImageInterferenceLineNum, tipImageInterferencePointNum);
        return new ClickImageCheckDefinition.ImgWrapper(bufferedImage, new Resource(null, tips), null);
    }

//    @Override
//    public ImgWrapper getClickImg(Resource tip) {
//        ThreadLocalRandom random = ThreadLocalRandom.current();
//        // 随机颜色
//        Color randomColor = CaptchaImageUtils.getRandomColor(random);
//        return getClickImg(tip, randomColor);
//    }


    @Override
    protected List<ClickImageCheckDefinition> filterAndSortClickImageCheckDefinition(CaptchaExchange captchaExchange, List<ClickImageCheckDefinition> allCheckDefinitionList) {
        GenerateParam param = captchaExchange.getParam();
        Integer checkClickCount = param.getOrDefault(ParamKeyEnum.CLICK_CHECK_CLICK_COUNT, getCheckClickCount());
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
    public ImageCaptchaInfo doWrapImageCaptchaInfo(CaptchaExchange captchaExchange) {
        List<ClickImageCheckDefinition> checkClickImageCheckDefinitionList = (List<ClickImageCheckDefinition>) captchaExchange.getTransferData();
        BufferedImage bgImage = captchaExchange.getBackgroundImage();
        GenerateParam param = captchaExchange.getParam();
        Resource resourceImage = captchaExchange.getResourceImage();
        CustomData data = captchaExchange.getCustomData();
        // 提示图片
        BufferedImage tipImage = genTipImage(checkClickImageCheckDefinitionList, param).getImage();
        ImageTransformData transform = getImageTransform().transform(param, bgImage, tipImage, resourceImage, checkClickImageCheckDefinitionList, data);
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
        data.setExpand(checkClickImageCheckDefinitionList);
        return clickImageCaptchaInfo;
    }


}
