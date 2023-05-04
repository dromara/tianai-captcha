package cloud.tianai.captcha.generator.common.util;

import lombok.SneakyThrows;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;
import java.awt.image.*;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: 天爱有情
 * @date 2022/2/16 9:46
 * @Description image Utils
 */
public class CaptchaImageUtils {

    public static final String TYPE_JPG = "jpg";
    public static final String TYPE_JPEG = "jpeg";
    public static final String TYPE_PNG = "png";

    @SneakyThrows
    public static BufferedImage wrapFile2BufferedImage(URL resourceImage) {
        if (resourceImage == null) {
            throw new IllegalArgumentException("包装文件到 BufferedImage 失败， file不能为空");
        }
        // 关闭磁盘缓存
        ImageIO.setUseCache(false);
        return ImageIO.read(resourceImage);
    }

    @SneakyThrows
    public static BufferedImage wrapFile2BufferedImage(InputStream resource) {
        if (resource == null) {
            throw new IllegalArgumentException("包装文件到 BufferedImage 失败， file不能为空");
        }
        // 关闭磁盘缓存
        ImageIO.setUseCache(false);
        return ImageIO.read(resource);
    }

    public static BufferedImage createTransparentImage(int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return bufferedImage;
    }


    /**
     * 图片覆盖（覆盖图压缩到width*height大小，覆盖到底图上）
     *
     * @param baseBufferedImage  底图
     * @param coverBufferedImage 覆盖图
     * @param x                  起始x轴
     * @param y                  起始y轴
     */
    public static void overlayImage(BufferedImage baseBufferedImage, BufferedImage coverBufferedImage,
                                    int x, int y) {
        // 创建Graphics2D对象，用在底图对象上绘图
        Graphics2D g2d = baseBufferedImage.createGraphics();
        // 绘制
        g2d.drawImage(coverBufferedImage, x, y, coverBufferedImage.getWidth(), coverBufferedImage.getHeight(), null);
        // 释放图形上下文使用的系统资源
        g2d.dispose();
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
     * 通过模板图片抠图（不透明部分）
     *
     * @param oriImage      源图片
     * @param templateImage 模板图片
     * @param xPos          坐标轴x
     * @param yPos          坐标轴y
     * @return BufferedImage
     */
    @SneakyThrows
    public static BufferedImage cutImage(BufferedImage oriImage, BufferedImage templateImage, int xPos, int yPos) {
        // 模板图像矩阵
        int bw = templateImage.getWidth(null);
        int bh = templateImage.getHeight(null);
        BufferedImage targetImage = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);
        // 透明色
        for (int y = 0; y < bh; y++) {
            for (int x = 0; x < bw; x++) {
                int rgb = templateImage.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                // 透明度大于100才处理，过滤一下边缘过于透明的像素点
                if (alpha > 100) {
                    int bgRgb = oriImage.getRGB(xPos + x, yPos + y);
                    targetImage.setRGB(x, y, bgRgb);
                }
            }

        }
        return targetImage;
    }


    @SneakyThrows
    public static BufferedImage cutImage_bak(BufferedImage origin, BufferedImage template, int x, int y) {
        int bw = template.getWidth(null);
        int bh = template.getHeight(null);
        int lw = origin.getWidth(null);
        int lh = origin.getHeight(null);
        //得到透明的区域(人物轮廓)
        Shape imageShape = getImageShape(template, false);
        long end = System.currentTimeMillis();
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
     * 旋转图片
     *
     * @param bufferedImage
     * @param degree
     * @return
     */
    public static BufferedImage rotateImage(final BufferedImage bufferedImage,
                                            final double degree) {
        // 得到图片宽度。
        int w = bufferedImage.getWidth();
        // 得到图片高度。
        int h = bufferedImage.getHeight();
        // 得到图片透明度。
        int type = bufferedImage.getColorModel().getTransparency();
        BufferedImage img;// 空的图片。
        Graphics2D graphics2d;// 空的画笔。
        (graphics2d = (img = new BufferedImage(w, h, type))
                .createGraphics()).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // 旋转，degree是整型，度数，比如垂直90度。
        graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
        // 从bufferedimagecopy图片至img，0,0是img的坐标。
        graphics2d.drawImage(bufferedImage, 0, 0, null);
        graphics2d.dispose();
        // 返回复制好的图片，原图片依然没有变，没有旋转，下次还可以使用。
        return img;
    }

    public static void centerOverlayAndRotateImage(BufferedImage baseBufferedImage, BufferedImage coverBufferedImage,
                                                   final double degree) {
        coverBufferedImage = rotateImage(coverBufferedImage, degree);
        int bw = baseBufferedImage.getWidth();
        int bh = baseBufferedImage.getHeight();
        int cw = coverBufferedImage.getWidth();
        int ch = coverBufferedImage.getHeight();
        overlayImage(baseBufferedImage, coverBufferedImage, bw / 2 - cw / 2, bh / 2 - ch / 2);
    }


    /**
     * 通过x和y轴截取图片
     *
     * @param x      x
     * @param y      y
     * @param width  宽度
     * @param height 高度
     * @param img    截取的图片
     * @return BufferedImage
     */
    public static BufferedImage subImage(int x, int y, int width, int height, BufferedImage img) {
        int[] simgRgb = new int[width * height];
        img.getRGB(x, y, width, height, simgRgb, 0, width);
        // 得到图片透明度。
        int type = img.getColorModel().getTransparency();
        BufferedImage newImage = new BufferedImage(width, height, type);
        newImage.setRGB(0, 0, width, height, simgRgb, 0, width);
        return newImage;
    }

    /**
     * 分隔图片
     *
     * @param pos       分隔点
     * @param direction true为水平方向， false为垂直方向
     * @param img       待分割的图片
     * @return BufferedImage[]
     */
    public static BufferedImage[] splitImage(int pos, boolean direction, BufferedImage img) {
        int startImageWidth;
        int startImageHeight;
        int endImageWidth;
        int endImageHeight;
        int endScanX;
        int endScanY;
        if (direction) {
            startImageHeight = img.getHeight() - pos;
            startImageWidth = img.getWidth();
            endImageWidth = img.getWidth();
            endImageHeight = pos;
            endScanX = 0;
            endScanY = startImageHeight;
        } else {
            startImageWidth = pos;
            startImageHeight = img.getHeight();
            endImageWidth = img.getWidth() - startImageWidth;
            endImageHeight = img.getHeight();
            endScanX = pos;
            endScanY = 0;
        }

        // start
        int[] rgbArr = new int[startImageWidth * startImageHeight];
        img.getRGB(0, 0, startImageWidth, startImageHeight, rgbArr, 0, startImageWidth);
        int type = img.getColorModel().getTransparency();
        BufferedImage startImg = new BufferedImage(startImageWidth, startImageHeight, type);
        startImg.setRGB(0, 0, startImageWidth, startImageHeight, rgbArr, 0, startImageWidth);
        // end
        rgbArr = new int[endImageWidth * endImageHeight];
        img.getRGB(endScanX, endScanY, endImageWidth, endImageHeight, rgbArr, 0, endImageWidth);
        BufferedImage endImg = new BufferedImage(endImageWidth, endImageHeight, type);
        endImg.setRGB(0, 0, endImageWidth, endImageHeight, rgbArr, 0, endImageWidth);

        BufferedImage[] splitImageArr = new BufferedImage[2];
        splitImageArr[0] = startImg;
        splitImageArr[1] = endImg;
        return splitImageArr;
    }


    /**
     * 拼接图片
     *
     * @param direction rue为水平方向， false为垂直方向
     * @param width     拼接后图片宽度
     * @param height    拼接后图片高度
     * @param imgArr    拼接的图片数组
     * @return BufferedImage
     */
    public static BufferedImage concatImage(boolean direction, int width, int height, BufferedImage... imgArr) {
        int pos = 0;
        BufferedImage newImage = new BufferedImage(width, height, imgArr[0].getColorModel().getTransparency());
        for (BufferedImage img : imgArr) {
            int[] rgbArr = new int[width * height];
            img.getRGB(0, 0, img.getWidth(), img.getHeight(), rgbArr, 0, img.getWidth());
            if (direction) {
                newImage.setRGB(pos, 0, img.getWidth(), img.getHeight(), rgbArr, 0, img.getWidth());
                pos += img.getWidth();
                // 水平方向
            } else {
                // 垂直方向
                newImage.setRGB(0, pos, img.getWidth(), img.getHeight(), rgbArr, 0, img.getWidth());
                pos += img.getHeight();
            }
        }
        return newImage;
    }

    public static char getRandomChar() {
        return (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
    }


    @SneakyThrows
    public static BufferedImage drawWordImg(Color fontColor,
                                            String word,
                                            Font font,
                                            float fontTopCoef,
                                            int imgWidth,
                                            int imgHeight,
                                            float deg) {
        BufferedImage fillRect = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = fillRect.createGraphics();
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, imgWidth, imgHeight);
        g.setColor(fontColor);
        g.setFont(font);
        float left = (imgWidth - font.getSize()) / 2f;
        float top = (imgHeight - font.getSize()) / 2f + font.getSize() - fontTopCoef;
        g.rotate(Math.toRadians(deg), imgWidth / 2f, imgHeight / 2f);
        g.drawString(word, left, top);
        g.dispose();
        return fillRect;
    }

    /**
     * 随机画干扰圆
     *
     * @param num   数量
     * @param color 颜色
     * @param g     Graphics2D
     */
    public static void drawOval(int num,
                                Color color,
                                Graphics2D g,
                                int width,
                                int height,
                                Random random) {
        for (int i = 0; i < num; i++) {
            g.setColor(color == null ? getRandomColor(random) : color);
            int w = 5 + random.nextInt(10);
            int x = random.nextInt(width - 25);
            int y = random.nextInt(height - 25);
            g.drawOval(x, y, w, w);
        }
    }


    /**
     * 随机画贝塞尔曲线
     *
     * @param num   数量
     * @param color 颜色
     * @param g     Graphics2D
     */
    public static void drawBesselLine(int num, Color color,
                                      Graphics2D g,
                                      int width,
                                      int height,
                                      ThreadLocalRandom random) {
        for (int i = 0; i < num; i++) {
            g.setColor(color == null ? getRandomColor(random) : color);
            int x1 = 5, y1 = random.nextInt(5, height / 2);
            int x2 = width - 5, y2 = random.nextInt(height / 2, height - 5);
            int ctrlx = random.nextInt(width / 4, width / 4 * 3);
            int ctrly = random.nextInt(5, height - 5);
            if (random.nextInt(2) == 0) {
                int ty = y1;
                y1 = y2;
                y2 = ty;
            }
            // 二阶贝塞尔曲线
            if (random.nextInt(2) == 0) {
                QuadCurve2D shape = new QuadCurve2D.Double();
                shape.setCurve(x1, y1, ctrlx, ctrly, x2, y2);
                g.draw(shape);
            } else {  // 三阶贝塞尔曲线
                int ctrlx1 = random.nextInt(width / 4, width / 4 * 3);
                int ctrly1 = random.nextInt(5, height - 5);
                CubicCurve2D shape = new CubicCurve2D.Double(x1, y1, ctrlx, ctrly, ctrlx1, ctrly1, x2, y2);
                g.draw(shape);
            }
        }
    }

    /**
     * 生成简单的验证码图片
     *
     * @param data                 验证码内容
     * @param font                 字体包
     * @param width                验证码宽度
     * @param height               验证码高度
     * @param startX               起始X
     * @param startY               起始Y
     * @param interferenceLineNum  干扰线数量
     * @param interferencePointNum 干扰点数量
     * @return BufferedImage
     */
    public static BufferedImage genSimpleImgCaptcha(String data,
                                                    Font font,
                                                    int width,
                                                    int height,
                                                    float startX,
                                                    float startY,
                                                    int interferenceLineNum,
                                                    int interferencePointNum) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        g.setFont(font);
        char[] chars = data.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            g.setColor(Color.gray);
            g.drawString(String.valueOf(chars[i]), startX + i * font.getSize(), startY);
        }
        // 干扰点
        if (interferencePointNum > 0) {
            drawOval(interferencePointNum, null, g, width, height, random);
        }
        if (interferencePointNum > 0) {
            g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            // 干扰线
            drawBesselLine(interferenceLineNum, null, g, width, height, random);
        }
        return bufferedImage;
    }


    public static void drawInterfere(Graphics2D g, int width,
                                     int height,
                                     int interferenceLineNum,
                                     int interferencePointNum) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // 干扰点
        if (interferencePointNum > 0) {
            drawOval(interferencePointNum, null, g, width, height, random);
        }
        if (interferencePointNum > 0) {
            g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            // 干扰线
            drawBesselLine(interferenceLineNum, null, g, width, height, random);
        }
    }


    /**
     * 随机获取颜色
     *
     * @return Color
     */
    public static Color getRandomColor(Random random) {
        return new Color(
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255));

    }

    public static RenderedImage toRenderedImage(Image img) {
        if (img instanceof RenderedImage) {
            return (RenderedImage) img;
        }
        return copyImage(img, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * 转换成指定类型的 BufferedImage
     *
     * @param image     image
     * @param imageType imageType
     * @return BufferedImage
     */
    public static BufferedImage toBufferedImage(Image image, String imageType) {
        final int type = TYPE_PNG.equalsIgnoreCase(imageType)
                ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB;
        return toBufferedImage(image, type);
    }

    /**
     * 转换成指定类型的 BufferedImage
     *
     * @param image     image
     * @param imageType imageType
     * @return BufferedImage
     */
    public static BufferedImage toBufferedImage(Image image, int imageType) {
        BufferedImage bufferedImage;
        if (image instanceof BufferedImage) {
            bufferedImage = (BufferedImage) image;
            if (imageType != bufferedImage.getType()) {
                bufferedImage = copyImage(image, imageType);
            }
        } else {
            bufferedImage = copyImage(image, imageType);
        }
        return bufferedImage;
    }

    /**
     * 拷贝图片
     *
     * @param img       img
     * @param imageType imageType
     * @return BufferedImage
     */
    public static BufferedImage copyImage(Image img, int imageType) {
        return copyImage(img, imageType, null);
    }

    /**
     * 拷贝图片
     *
     * @param img             img
     * @param imageType       imageType
     * @param backgroundColor backgroundColor
     * @return BufferedImage
     */
    public static BufferedImage copyImage(Image img, int imageType, Color backgroundColor) {
        final BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), imageType);
        final Graphics2D bGr = createGraphics(bimage, backgroundColor);
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    /**
     * 灰度处理,把原图传进去，传出来为修改后的图
     *
     * @param b b
     * @return BufferedImage
     */
    public static BufferedImage gray(BufferedImage b) {
        int width = b.getWidth();
        int height = b.getHeight();
        // 下面这个别忘了定义，不然会出错
        BufferedImage bufferedImageEnd = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        // 双层循环更改图片的RGB值，把得到的灰度值存到bufferedImage_end中，然后返回bufferedImage_end
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 获取到（x，y）此像素点的Colo，转化为灰度
                Color color = new Color(b.getRGB(x, y));
                int gray = (int) (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
                Color color_end = new Color(gray, gray, gray);
                bufferedImageEnd.setRGB(x, y, color_end.getRGB());
            }
        }
        return bufferedImageEnd;
    }

    /**
     * 创建画板
     *
     * @param image image
     * @param color color
     * @return Graphics2D
     */
    public static Graphics2D createGraphics(BufferedImage image, Color color) {
        final Graphics2D g = image.createGraphics();
        if (null != color) {
            // 填充背景
            g.setColor(color);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
        }
        return g;
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        // Return the buffered image
        return bimage;
    }


    /**
     * 后缀是否是jpg
     *
     * @param type type
     * @return boolean
     */
    public static boolean isJpeg(String type) {
        return TYPE_JPG.equalsIgnoreCase(type) || TYPE_JPEG.equalsIgnoreCase(type);
    }

    /**
     * 后缀是否是 png
     *
     * @param type type
     * @return boolean
     */
    public static boolean isPng(String type) {
        return TYPE_PNG.equalsIgnoreCase(type);
    }

}
