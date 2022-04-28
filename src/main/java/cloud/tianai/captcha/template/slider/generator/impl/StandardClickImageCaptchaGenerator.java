package cloud.tianai.captcha.template.slider.generator.impl;

import cloud.tianai.captcha.template.slider.common.util.CaptchaImageUtils;
import cloud.tianai.captcha.template.slider.common.util.FontUtils;
import cloud.tianai.captcha.template.slider.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.WordClickImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.resource.ResourceStore;
import cloud.tianai.captcha.template.slider.resource.common.model.dto.Resource;
import cloud.tianai.captcha.template.slider.resource.impl.provider.ClassPathResourceProvider;
import cloud.tianai.captcha.template.slider.resource.impl.provider.FileResourceProvider;
import lombok.Data;
import lombok.SneakyThrows;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static cloud.tianai.captcha.template.slider.common.util.CaptchaImageUtils.wrapFile2BufferedImage;
import static cloud.tianai.captcha.template.slider.generator.impl.StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_RESOURCE_PATH;

/**
 * @Author: 天爱有情
 * @date 2022/4/27 11:46
 * @Description 点选验证码
 */
@Data
public class StandardClickImageCaptchaGenerator extends AbstractImageCaptchaGenerator {


    protected ImageCaptchaResourceManager imageCaptchaResourceManager;

    protected Integer checkFontCount = 4;
    protected Integer interferenceCount = checkFontCount + 2;
    protected Font font;
    protected List<Color> randomColors = Arrays.asList(Color.PINK, Color.BLUE, Color.GREEN, Color.BLACK);

    @SneakyThrows
    public StandardClickImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, boolean initDefaultResource) {
        this.imageCaptchaResourceManager = imageCaptchaResourceManager;
        if (initDefaultResource) {
            initDefaultResource();
        }
        Resource fontResource = new Resource("", "META-INF/fonts/SIMSUN.TTC");
        InputStream inputStream = new ClassPathResourceProvider().doGetResourceInputStream(fontResource);
        this.font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        this.font = font.deriveFont(Font.BOLD, 50);
    }

    public StandardClickImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager,
                                              boolean initDefaultResource,
                                              Font font) {
        this.imageCaptchaResourceManager = imageCaptchaResourceManager;
        this.font = font;
        if (initDefaultResource) {
            initDefaultResource();
        }
    }


    public void initDefaultResource() {
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加一些系统的资源文件
        resourceStore.addResource(CaptchaTypeConstant.WORD_CLICK, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg")));
    }


    @Override
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {

        // 文字点选验证码不需要模板 只需要背景图
        Collection<InputStream> inputStreams = new LinkedList<>();
        try {
            Resource resourceImage = imageCaptchaResourceManager.randomGetResource(param.getType());
            InputStream resourceInputStream = imageCaptchaResourceManager.getResourceInputStream(resourceImage);
            inputStreams.add(resourceInputStream);
            BufferedImage bgImage = wrapFile2BufferedImage(resourceInputStream);
            Graphics2D graphics = (Graphics2D) bgImage.getGraphics();

            List<WordClickImageCaptchaInfo.WordDefinition> wordDefinitionList = new ArrayList<>(interferenceCount);
            for (int i = 0; i < interferenceCount; i++) {
                // 随机角度

                FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
                // 随机文字
                String randomWord = String.valueOf(FontUtils.getRandomChar());
                int wordWidth = metrics.stringWidth(randomWord);
                int wordHeight = metrics.getHeight();
                // 随机颜色
                Color randomColor = randomColor();
                // 随机x
                int randomX = ThreadLocalRandom.current().nextInt(10, bgImage.getWidth() - wordWidth);
                // 随机y
                int randomY = ThreadLocalRandom.current().nextInt(10, bgImage.getHeight() - wordHeight);
                int randomDeg = ThreadLocalRandom.current().nextInt(0, 85);
                AffineTransform affineTransform = new AffineTransform();
                affineTransform.rotate(Math.toRadians(randomDeg));
                Font rotatedFont = font.deriveFont(affineTransform);
                graphics.setFont(rotatedFont);
                graphics.setColor(randomColor);
                graphics.drawString(randomWord, randomX, randomY + metrics.getAscent());

                WordClickImageCaptchaInfo.WordDefinition wordDefinition = new WordClickImageCaptchaInfo.WordDefinition();
                wordDefinition.setWord(randomWord);
                wordDefinition.setX(randomX + wordWidth / 2);
                wordDefinition.setY(randomY + wordHeight / 2);
                wordDefinition.setDeg(randomDeg);
                wordDefinition.setWordWidth(wordWidth);
                wordDefinition.setWordHeight(wordHeight);
                wordDefinitionList.add(wordDefinition);
            }
            // 背景图转换为字符串
//            String bgImageStr = transform(bgImage, param.getBackgroundFormatName());
            try {
                ImageIO.write(bgImage, "jpeg", new FileOutputStream("C:\\Users\\Thinkpad\\Desktop\\123.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 打乱 word
            Collections.shuffle(wordDefinitionList);
            // 拿出要校验的文字
            List<WordClickImageCaptchaInfo.WordDefinition> checkWordDefinitionList = new ArrayList<>(checkFontCount);
            for (int i = 0; i < checkFontCount; i++) {
                checkWordDefinitionList.add(wordDefinitionList.get(i));
            }

            System.out.println(checkWordDefinitionList);
            return null;

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

    protected Color randomColor() {
        if (randomColors.size() == 1) {
            return randomColors.get(0);
        }
        return randomColors.get(ThreadLocalRandom.current().nextInt(0, randomColors.size() - 1));
    }

    @Override
    public ImageCaptchaResourceManager getImageResourceManager() {
        return null;
    }


    public static void main(String[] args) throws IOException, FontFormatException {
        Resource fontResource = new Resource("", "META-INF/fonts/SIMSUN.TTC");
        InputStream inputStream = new ClassPathResourceProvider().doGetResourceInputStream(fontResource);
        Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        font = font.deriveFont(Font.BOLD, 100);


        for (int i = 40; i < 60; i+=1) {
            FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);

            Resource imageResource = new Resource("", "C:\\Users\\Thinkpad\\Desktop\\111.jpg");
            inputStream = new FileResourceProvider().doGetResourceInputStream(imageResource);
            BufferedImage bufferedImage = CaptchaImageUtils.wrapFile2BufferedImage(inputStream);

            Graphics graphics = bufferedImage.getGraphics();
            graphics.setColor(Color.PINK);
            //设置角度
            AffineTransform affineTransform = new AffineTransform();
            affineTransform.rotate(Math.toRadians(180), i , -i);
            Font rotatedFont = font.deriveFont(affineTransform);
            graphics.setFont(rotatedFont);
            char ch = FontUtils.getRandomChar();
//        graphics.drawString(String.valueOf(ch), 10, 10+metrics.getAscent());
//        graphics.drawString(String.valueOf(ch), 50, 50);
            String randomWord = "张"/*String.valueOf(FontUtils.getRandomChar())*/;
            int wordWidth = metrics.stringWidth(randomWord);
            int wordHeight = metrics.getHeight();
//        //左边位置
            int left = (bufferedImage.getWidth()-wordWidth)/2;
//        //顶边位置+上升距离（原本字体基线位置对准画布的y坐标导致字体偏上ascent距离，加上ascent后下移刚好顶边吻合）
            int top = (bufferedImage.getHeight()-wordHeight)/2+metrics.getAscent();

            // 随机生成6个字的居中的 x 和 y
//        for (int i = 0; i < 6; i++) {


//        int randomX = ThreadLocalRandom.current().nextInt(10, bufferedImage.getWidth() - wordWidth);
//        int randomY = ThreadLocalRandom.current().nextInt(10, bufferedImage.getHeight() - wordHeight);

            graphics.drawString(randomWord, left, top);

            System.out.println(randomWord + "-->x:" + (left + wordWidth / 2) + ",y:" + (left + wordHeight / 2));
//        }

//        graphics.drawString("居中文字",0,metrics.getAscent()); //基线对齐改为顶边对齐
            ImageIO.write(bufferedImage, "jpeg", new FileOutputStream("C:\\Users\\Thinkpad\\Desktop\\111\\"+i+".jpg"));
        }
    }
}
