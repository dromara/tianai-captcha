package example;

import cloud.tianai.captcha.template.slider.resource.common.model.dto.Resource;
import cloud.tianai.captcha.template.slider.resource.impl.provider.ClassPathResourceProvider;
import lombok.SneakyThrows;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 绘制验证码
 */
public class DrawCaptchaUtil {

    /**
     * 画布宽度
     */
    private static int canvasWidth = 300;

    /**
     * 画布高度
     */
    private static int canvasHeight = 150;


    /**
     * 图片宽度
     */
    private static int imgWidth = 300;

    /**
     * 图片高度
     */
    private static int imgHeight = 150;

    /**
     * 图中字体数量
     */
    private static int charNumber = 5;

    /**
     * 字体大小
     */
    private static int fontSize = 50;
    /**
     * 字体图片宽度
     */
    private static int charImgWidth = 60;
    /**
     * 字体图片高度
     */
    private static int charImgHeight = 60;


    /**
     * 绘制验证码图片
     * @param checkCode
     * @param bkDirPath
     * @return
     * @throws IOException
     */
    public static BufferedImage drawImg(LinkedHashMap<String, AnchorPoint> checkCode, String bkDirPath) throws IOException {

        BufferedImage imgCanvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_BGR);
        Graphics graphics = imgCanvas.getGraphics();


        //绘制图形
//        graphics.setColor(new Color(0,0,0,0));
        graphics.fillRect(0, 0, imgWidth, imgHeight);

        File bkPath = randomBackGroundImage(bkDirPath);

        BufferedImage read = ImageIO.read(Files.newInputStream(Paths.get(bkPath.getAbsolutePath())));
        graphics.drawImage(read, 0, 0, null, null);
        //绘制字体
//        graphics.setColor(getRandomColor());
        //随机在图片中生成5个绘制点每个绘制点半径能不能重叠
        Set<AnchorPoint> point = new HashSet<>();

        addPoint(point);
        Object[] pointArr = point.toArray();
        List<String> charList = new ArrayList<>();
        for (int i = 0; i < charNumber; i++) {
            AnchorPoint p = (AnchorPoint) pointArr[i];
            graphics.drawImage(getCharImg(getRandomColor(), charList), p.getX(), p.getY(), null, null);

        }
        //随机获取3个字符串作为校验对象
        Object[] charArr = charList.toArray();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int num = random.nextInt(pointArr.length - i);
            String key = (String) charArr[num];
            AnchorPoint value = (AnchorPoint) pointArr[num];
            checkCode.put(key, value);
            //置换位置
            Object a = pointArr[num];
            pointArr[num] = pointArr[pointArr.length - 1 - i];
            pointArr[pointArr.length - 1 - i] = a;

            a = charArr[num];
            charArr[num] = charArr[charArr.length - 1 - i];
            charArr[charArr.length - 1 - i] = a;
        }

        return imgCanvas;
    }

    /**
     * 在指定目录下随机获取背景图片
     * @param dir
     * @return
     */
    private static File randomBackGroundImage(String dir) {
        File file = new File(dir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            Random random = new Random();
            int i = random.nextInt(files.length);
            return files[i];
        } else {
            return file;
        }
    }

    /**
     * 添加坐标点
     * @param point
     */
    private static void addPoint(Set<AnchorPoint> point) {
        Random random = new Random();
        //避免因为坐标点过于特殊导致无法获取下一个坐标点进入死循环，到100置空重新获取
        int clearNumber = 0;
        //生成前2个点坐标
        while (point.size() < 2) {

            //添加X坐标点
            point.add(new AnchorPoint(random.nextInt(canvasWidth / 3), random.nextInt(canvasHeight - charImgWidth)));
            if (clearNumber == 100) {
                point.clear();
                clearNumber = 0;
            }
            clearNumber++;

        }
        clearNumber = 0;
        //生成后3个点坐标
        while (point.size() < charNumber) {
            //添加X坐标点
            point.add(new AnchorPoint(random.nextInt(canvasWidth - charImgWidth), random.nextInt(canvasHeight - charImgHeight)));
            if (clearNumber == 100) {
                point.clear();
                clearNumber = 0;
                addPoint(point);
                break;
            }
            clearNumber++;
        }


    }

    /**
     * 获取文字图片
     * @param fontColor
     * @param charList
     * @return
     * @throws UnsupportedEncodingException
     */
    @SneakyThrows
    private static BufferedImage getCharImg(Color fontColor, List<String> charList) throws IOException {
        BufferedImage fillRect = new BufferedImage(charImgWidth, charImgHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = fillRect.createGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, charImgWidth, charImgHeight);
        g.setColor(fontColor);
        Resource fontResource = new Resource("", "META-INF/fonts/SIMSUN.TTC");
        InputStream inputStream = new ClassPathResourceProvider().doGetResourceInputStream(fontResource);
        Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        font = font.deriveFont(Font.BOLD, fontSize);
//        Font font = new Font("微软雅黑", Font.BOLD, fontSize);
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
//        AffineTransform affineTransform = new AffineTransform();
//        affineTransform.rotate(Math.toRadians(85), 0, 0);
//        font.deriveFont(affineTransform);
        g.setFont(font);
        String charStr = getChineseCharacters();
        charList.add(charStr);

        float left = (60 - 50) / 2f;
//        //顶边位置+上升距离（原本字体基线位置对准画布的y坐标导致字体偏上ascent距离，加上ascent后下移刚好顶边吻合）
        float top = (60 - 50) / 2f + metrics.getAscent() - 10;

        g.rotate(Math.toRadians(ThreadLocalRandom.current().nextInt(0, 85)), 30, 30);
        g.drawString(charStr, left, top);
        g.dispose();
        return fillRect;
    }

    /**
     * 随机获取颜色
     * @return
     */
    private static Color getRandomColor() {
        Random random = new Random();
        return new Color(
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255));

    }

    /**
     * 随机获取中文字
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String getChineseCharacters() throws UnsupportedEncodingException {
        Integer hightPos, lowPos; // 定义高低位
        Random random = new Random();
        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));
        byte[] bytes = new byte[2];
        bytes[0] = hightPos.byteValue();
        bytes[1] = lowPos.byteValue();
        return new String(bytes, "GBK");

    }


    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 100; i++) {
            BufferedImage charImg = getCharImg(getRandomColor(), new ArrayList<>());
            ImageIO.write(charImg, "png", new FileOutputStream("C:\\Users\\tianai\\Desktop\\111\\" + i + ".png"));

        }
    }


}