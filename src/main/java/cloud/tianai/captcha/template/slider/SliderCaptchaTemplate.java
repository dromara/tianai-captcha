package cloud.tianai.captcha.template.slider;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:06
 * @Description 滑块验证码模板
 */
public class SliderCaptchaTemplate {

    /** 默认的resource资源文件路径.*/
    public static final String DEFAULT_SLIDER_IMAGE_RESOURCE_PATH = "META-INF/cut-image/resource";
    /** 默认的template资源文件路径.*/
    public static final String DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH = "META-INF/cut-image/template";

    public static final String ACTIVE_IMAGE_NAME = "active.png";
    public static final String CUT_IMAGE_NAME = "cut.png";
    public static final String FIXED_IMAGE_NAME = "fixed.png";
    public static final String MATRIX_IMAGE_NAME = "matrix.png";

    /** resource图片.*/
    private static   List<URL> resourceImageFiles = new ArrayList<>(20);
    /** 模板图片.*/
    private  static List<Map<String, URL>> templateImageFiles = new ArrayList<>(2);

    static {
        // 添加一些系统的资源文件
        addResource(getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg")));
        addResource(getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/2.jpg")));
        addResource(getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/3.jpg")));
        addResource(getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/4.jpg")));
        addResource(getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/5.jpg")));
        addResource(getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/6.jpg")));
        addResource(getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/7.jpg")));
        addResource(getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/8.jpg")));
        addResource(getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/9.jpg")));
        addResource(getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/10.jpg")));

        // 添加一些系统的 模板文件
        Map<String, URL> template1 = new HashMap<>(4);
        template1.put(ACTIVE_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/active.png")));
        template1.put(CUT_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/cut.png")));
        template1.put(FIXED_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/fixed.png")));
        template1.put(MATRIX_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/matrix.png")));
        addTemplate(template1);


        Map<String, URL> template2 = new HashMap<>(4);
        template2.put(ACTIVE_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/active.png")));
        template2.put(CUT_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/cut.png")));
        template2.put(FIXED_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/fixed.png")));
        template2.put(MATRIX_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/matrix.png")));
        addTemplate(template2);

        Map<String, URL> template3 = new HashMap<>(4);
        template3.put(ACTIVE_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/active.png")));
        template3.put(CUT_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/cut.png")));
        template3.put(FIXED_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/fixed.png")));
        template3.put(MATRIX_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/matrix.png")));
        addTemplate(template3);

    }



    private final AtomicBoolean loadResources = new AtomicBoolean(false);

    private String sliderImageResourcePath = DEFAULT_SLIDER_IMAGE_RESOURCE_PATH;
    private String sliderImageTemplatePath = DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH;

    public SliderCaptchaTemplate() {
        // 加载系统资源文件
    }


    public SliderCaptchaTemplate(String sliderImageResourcePath, String sliderImageTemplatePath) {
        this.sliderImageResourcePath = sliderImageResourcePath;
        this.sliderImageTemplatePath = sliderImageTemplatePath;
        // 加载系统资源文件
    }

    public SliderCaptchaTemplate(List<URL> r, List<Map<String, URL>> t) {
        resourceImageFiles = r;
        templateImageFiles = t;
    }

    public static void addResource(URL url) {
        resourceImageFiles.remove(url);
        resourceImageFiles.add(url);
    }

    public static void setResource(List<URL> resources) {
        resourceImageFiles = resources;
    }

    public static void setTemplates(List<Map<String, URL>> imageTemplates) {
        templateImageFiles = imageTemplates;
    }

    public static void deleteResource(URL resource) {
        resourceImageFiles.remove(resource);
    }

    public static void deleteTemplate(Map<String, URL> template) {
        templateImageFiles.remove(template);
    }

    public static void addTemplate(Map<String, URL> template) {
        templateImageFiles.remove(template);
        templateImageFiles.add(template);
    }

    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = SliderCaptchaTemplate.getClassLoader();
        }
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return classLoader;
    }

    public SliderCaptchaInfo getSlideImageInfo(){
        return getSlideImageInfo("jpg", "png");
    }

    public SliderCaptchaInfo getSlideImageInfoForWebp(){
        return getSlideImageInfo("webp", "webp");
    }


    @SneakyThrows
    public SliderCaptchaInfo getSlideImageInfo(String targetFormatName, String matrixFormatName) {
        URL resourceImage = getRandomResourceImage();
        Map<String, URL> templateImages = getRandomTemplateImages();

        BufferedImage cutBackground = warpFile2BufferedImage(resourceImage);
        BufferedImage targetBackground = warpFile2BufferedImage(resourceImage);

        BufferedImage fixedTemplate = warpFile2BufferedImage(getTemplateFile(templateImages, FIXED_IMAGE_NAME));
        BufferedImage activeTemplate = warpFile2BufferedImage(getTemplateFile(templateImages, ACTIVE_IMAGE_NAME));
        BufferedImage matrixTemplate = warpFile2BufferedImage(getTemplateFile(templateImages, MATRIX_IMAGE_NAME));
        BufferedImage cutTemplate = warpFile2BufferedImage(getTemplateFile(templateImages, CUT_IMAGE_NAME));

        // 获取随机的 x 和 y 轴
        Random random = new Random();
        int randomX = random.nextInt(targetBackground.getWidth() - fixedTemplate.getWidth() * 2) + fixedTemplate.getWidth();
        int randomY = random.nextInt(targetBackground.getHeight() - fixedTemplate.getHeight());

        coverImage(targetBackground, fixedTemplate, randomX, randomY);
        BufferedImage cutImage = cutImage(cutBackground, cutTemplate, randomX, randomY);
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
    public boolean percentageContrast(Float newPercentage, Float oriPercentage) {
        boolean falg = false;
        BigDecimal num = BigDecimal.valueOf(0.05d).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal newPercentageBig = new BigDecimal(newPercentage).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal oriPercentageBig = new BigDecimal(oriPercentage).setScale(2, BigDecimal.ROUND_HALF_UP);
        //最小百分比
        BigDecimal minOriPercentage = oriPercentageBig.subtract(num).setScale(2, BigDecimal.ROUND_HALF_UP);
        //最大百分比
        BigDecimal maxOriPercentage = oriPercentageBig.add(num).setScale(2, BigDecimal.ROUND_HALF_UP);
        if (newPercentageBig.compareTo(minOriPercentage) > 0 && maxOriPercentage.compareTo(newPercentageBig) > 0) {
            falg = true;
        }
        return falg;
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
        return "data:image/"+formatName+";base64,".concat(base64);
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
                    if (j == width) {
                        if (temp == 0) {
                            rec.add(new Area(new Rectangle(j, i, 1, 1)));
                        } else {
                            rec.add(new Area(new Rectangle(temp, i, j - temp, 1)));
                            temp = 0;
                        }
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

    private URL getTemplateFile(Map<String, URL> templateImages, String imageName) {
        URL url = templateImages.get(imageName);
        if (url == null) {
            throw new IllegalArgumentException("查找模板异常， 该模板下未找到 ");
        }
        return url;
    }

    private Map<String, URL> getRandomTemplateImages() {
        if (templateImageFiles.size() == 1) {
            return templateImageFiles.get(0);
        }
        int templateNo = new Random().nextInt(templateImageFiles.size());
        return templateImageFiles.get(templateNo);
    }

    @SneakyThrows
    private static BufferedImage warpFile2BufferedImage(URL resourceImage) {
        if (resourceImage == null) {
            throw new IllegalArgumentException("包装文件到 BufferedImage 失败， file不能为空");
        }
        return ImageIO.read(resourceImage);
    }

    private  URL getRandomResourceImage() {
        int targetNo = new Random().nextInt(resourceImageFiles.size());
        return resourceImageFiles.get(targetNo);
    }

    public static void main(String[] args) {
        SliderCaptchaTemplate sliderCaptchaTemplate = new SliderCaptchaTemplate();
        // 生成滑块图片
        SliderCaptchaInfo slideImageInfo = sliderCaptchaTemplate.getSlideImageInfo();
        // 获取背景图片的base64
        String backgroundImage = slideImageInfo.getBackgroundImage();
        // 获取滑块图片
        slideImageInfo.getSliderImage();
        // 获取滑块被背景图片的百分比， (校验图片使用)
        Float xPercent = slideImageInfo.getXPercent();
    }
}
