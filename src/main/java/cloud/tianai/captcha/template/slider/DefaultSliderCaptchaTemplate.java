package cloud.tianai.captcha.template.slider;

import cloud.tianai.captcha.template.slider.provider.ClassPathResourceProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:06
 * @Description 滑块验证码模板
 */
@Slf4j
public class DefaultSliderCaptchaTemplate implements SliderCaptchaTemplate {

    /**
     * 默认的resource资源文件路径.
     */
    public static final String DEFAULT_SLIDER_IMAGE_RESOURCE_PATH = "META-INF/cut-image/resource";
    /**
     * 默认的template资源文件路径.
     */
    public static final String DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH = "META-INF/cut-image/template";


    private final SliderCaptchaResourceManager sliderCaptchaResourceManager;


    public void initDefaultResource() {
        ResourceStore resourceStore = sliderCaptchaResourceManager.getResourceStore();
        // 添加一些系统的资源文件
        resourceStore.addResource(new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg")));

        // 添加一些系统的 模板文件
        Map<String, Resource> template1 = new HashMap<>(4);
        template1.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/active.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/fixed.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/matrix.png")));
        resourceStore.addTemplate(template1);


        Map<String, Resource> template2 = new HashMap<>(4);
        template2.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/active.png")));
        template2.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/fixed.png")));
        template2.put(SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/matrix.png")));
        resourceStore.addTemplate(template2);

    }

    public DefaultSliderCaptchaTemplate(SliderCaptchaResourceManager sliderCaptchaResourceManager, boolean initDefaultResource) {
        this.sliderCaptchaResourceManager = sliderCaptchaResourceManager;
        if (initDefaultResource) {
            initDefaultResource();
        }
    }


    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = DefaultSliderCaptchaTemplate.getClassLoader();
        }
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return classLoader;
    }

    @Override
    public SliderCaptchaInfo getSlideImageInfo() {
        return getSlideImageInfo("jpeg", "png");
    }


    @SneakyThrows
    @Override
    public SliderCaptchaInfo getSlideImageInfo(String targetFormatName, String matrixFormatName) {


        Map<String, Resource> templateImages = sliderCaptchaResourceManager.randomGetTemplate();
        Resource resourceImage = sliderCaptchaResourceManager.randomGetResource();


        BufferedImage cutBackground = warpFile2BufferedImage(sliderCaptchaResourceManager.getResourceInputStream(resourceImage));
        // 拷贝一份图片
        BufferedImage targetBackground = deepCopyBufferedImage(cutBackground);

        BufferedImage fixedTemplate = warpFile2BufferedImage(getTemplateFile(templateImages, SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME));
        BufferedImage activeTemplate = warpFile2BufferedImage(getTemplateFile(templateImages, SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME));
        BufferedImage matrixTemplate = warpFile2BufferedImage(getTemplateFile(templateImages, SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME));
//        BufferedImage cutTemplate = warpFile2BufferedImage(getTemplateFile(templateImages, CUT_IMAGE_NAME));

        // 获取随机的 x 和 y 轴
        int randomX = ThreadLocalRandom.current().nextInt(targetBackground.getWidth() - fixedTemplate.getWidth() * 2) + fixedTemplate.getWidth();
        int randomY = ThreadLocalRandom.current().nextInt(targetBackground.getHeight() - fixedTemplate.getHeight());

        coverImage(targetBackground, fixedTemplate, randomX, randomY);
        BufferedImage cutImage = cutImage(cutBackground, fixedTemplate, randomX, randomY);
        coverImage(cutImage, activeTemplate, 0, 0);
        coverImage(matrixTemplate, cutImage, 0, randomY);
        // 计算滑块百分比
        Float xPercent = (float) randomX / targetBackground.getWidth();

        String backGroundImageBase64 = transformBase64(targetBackground, targetFormatName);
        String sliderImageBase64 = transformBase64(matrixTemplate, matrixFormatName);

        return SliderCaptchaInfo.of(randomX, xPercent, randomY, backGroundImageBase64, sliderImageBase64);
    }

    /**
     * 百分比对比
     *
     * @param newPercentage 用户百分比
     * @param oriPercentage 原百分比
     * @return true 成功 false 失败
     */
    @Override
    public boolean percentageContrast(Float newPercentage, Float oriPercentage) {
        if (newPercentage == null || Float.isNaN(newPercentage) || Float.isInfinite(newPercentage)
                || oriPercentage == null || Float.isNaN(oriPercentage) || Float.isInfinite(oriPercentage)) {
            return false;
        }
        // 容错值
        float tolerant = 0.02f;
        float maxTolerant = oriPercentage + tolerant;
        float minTolerant = oriPercentage - tolerant;
        return newPercentage >= minTolerant && newPercentage <= maxTolerant;
    }

    @Override
    public SliderCaptchaResourceManager getSlideImageResourceManager() {
        return sliderCaptchaResourceManager;
    }


    private String transformBase64(BufferedImage bufferedImage, String formatName) {
        byte[] data = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, formatName, byteArrayOutputStream);
            //转换成字节码
            data = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        String base64 = Base64.getEncoder().encodeToString(data);
        return "data:image/" + formatName + ";base64,".concat(base64);
    }


    /**
     * 通过模板图片抠图（不透明部分）
     *
     * @param origin   源图片
     * @param template 模板图片
     * @param x        坐标轴x
     * @param y        坐标轴y
     * @return BufferedImage
     */
    @SneakyThrows
    private static BufferedImage cutImage(BufferedImage origin, BufferedImage template, int x, int y) {
        int bw = template.getWidth(null);
        int bh = template.getHeight(null);
        int lw = origin.getWidth(null);
        int lh = origin.getHeight(null);
        //得到透明的区域(人物轮廓)
        Shape imageShape = getImageShape(template, false);
        //合成后的图片
        BufferedImage image = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        //设置画布为透明
        image = graphics.getDeviceConfiguration().createCompatibleImage(bw, bh, Transparency.TRANSLUCENT);
        graphics.dispose();
        Graphics2D graphics2 = image.createGraphics();
        //取交集(限制可以画的范围为shape的范围)
        graphics2.clip(imageShape);
        //抗锯齿
        graphics2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        graphics2.drawImage(origin, -x, -y, lw, lh, null);
        graphics2.dispose();
        return image;
    }


    /**
     * 深度拷贝图片
     *
     * @param bi 原图片
     * @return BufferedImage
     */
    public static BufferedImage deepCopyBufferedImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /**
     * 将Image图像中的透明/不透明部分转换为Shape图形
     *
     * @param img         图片信息
     * @param transparent 是否透明
     * @return Shape
     * @throws InterruptedException 异常
     */
    public static Shape getImageShape(Image img, boolean transparent) throws InterruptedException {
        ArrayList<Integer> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        // 首先获取图像所有的像素信息
        PixelGrabber pgr = new PixelGrabber(img, 0, 0, -1, -1, true);
        pgr.grabPixels();
        int[] pixels = (int[]) pgr.getPixels();

        // 循环像素
        for (int i = 0; i < pixels.length; i++) {
            // 筛选，将不透明的像素的坐标加入到坐标ArrayList x和y中
            int alpha = (pixels[i] >> 24) & 0xff;
            if (alpha != 0) {
                x.add(i % width > 0 ? i % width - 1 : 0);
                y.add(i % width == 0 ? (i == 0 ? 0 : i / width - 1) : i / width);
            }
        }

        // 建立图像矩阵并初始化(0为透明,1为不透明)
        int[][] matrix = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                matrix[i][j] = 0;
            }
        }

        // 导入坐标ArrayList中的不透明坐标信息
        for (int c = 0; c < x.size(); c++) {
            matrix[y.get(c)][x.get(c)] = 1;
        }

        /*
         * 逐一水平"扫描"图像矩阵的每一行，将透明（这里也可以取不透明的）的像素生成为Rectangle，
         * 再将每一行的Rectangle通过Area类的rec对象进行合并， 最后形成一个完整的Shape图形
         */
        Area rec = new Area();
        int temp = 0;
        //生成Shape时是1取透明区域还是取非透明区域的flag
        int flag = transparent ? 0 : 1;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (matrix[i][j] == flag) {
                    if (temp == 0) {
                        temp = j;
                    }
                } else {
                    if (temp != 0) {
                        rec.add(new Area(new Rectangle(temp, i, j - temp, 1)));
                        temp = 0;
                    }
                }
            }
            temp = 0;
        }
        return rec;
    }

    /**
     * 图片覆盖（覆盖图压缩到width*height大小，覆盖到底图上）
     *
     * @param baseBufferedImage  底图
     * @param coverBufferedImage 覆盖图
     * @param x                  起始x轴
     * @param y                  起始y轴
     */
    private static void coverImage(BufferedImage baseBufferedImage, BufferedImage coverBufferedImage,
                                   int x, int y) {
        // 创建Graphics2D对象，用在底图对象上绘图
        Graphics2D g2d = baseBufferedImage.createGraphics();
        // 绘制
        g2d.drawImage(coverBufferedImage, x, y, coverBufferedImage.getWidth(), coverBufferedImage.getHeight(), null);
        // 释放图形上下文使用的系统资源
        g2d.dispose();
    }

    private InputStream getTemplateFile(Map<String, Resource> templateImages, String imageName) {
        Resource resource = templateImages.get(imageName);
        if (resource == null) {
            throw new IllegalArgumentException("查找模板异常， 该模板下未找到 ".concat(imageName));
        }
        return sliderCaptchaResourceManager.getResourceInputStream(resource);
    }


    @SneakyThrows
    private static BufferedImage warpFile2BufferedImage(URL resourceImage) {
        if (resourceImage == null) {
            throw new IllegalArgumentException("包装文件到 BufferedImage 失败， file不能为空");
        }
        return ImageIO.read(resourceImage);
    }

    @SneakyThrows
    private static BufferedImage warpFile2BufferedImage(InputStream resource) {
        if (resource == null) {
            throw new IllegalArgumentException("包装文件到 BufferedImage 失败， file不能为空");
        }
        return ImageIO.read(resource);
    }


    public static void main(String[] args) throws InterruptedException {
        SliderCaptchaResourceManager sliderCaptchaResourceManager = new DefaultSliderCaptchaResourceManager();
        DefaultSliderCaptchaTemplate sliderCaptchaTemplate = new DefaultSliderCaptchaTemplate(sliderCaptchaResourceManager, true);
        // 生成滑块图片
        SliderCaptchaInfo slideImageInfo = sliderCaptchaTemplate.getSlideImageInfo();
        // 获取背景图片的base64
        String backgroundImage = slideImageInfo.getBackgroundImage();
        // 获取滑块图片
        slideImageInfo.getSliderImage();
        // 获取滑块被背景图片的百分比， (校验图片使用)
        Float xPercent = slideImageInfo.getXPercent();

        System.out.println(slideImageInfo);
    }
}
