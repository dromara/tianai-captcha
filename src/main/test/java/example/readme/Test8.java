package example.readme;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.CacheImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;

public class Test8 {
    public static void main(String[] args) throws InterruptedException {
        // 使用 CacheSliderCaptchaGenerator 对滑块验证码进行缓存，使其提前生成滑块图片
        // 参数一: 真正实现 滑块的 SliderCaptchaGenerator
        // 参数二: 默认提前缓存多少个
        // 参数三: 出错后 等待xx时间再进行生成
        // 参数四: 检查时间间隔
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageCaptchaGenerator imageCaptchaGenerator = new CacheImageCaptchaGenerator(new MultiImageCaptchaGenerator(imageCaptchaResourceManager,new Base64ImageTransform()), 10, 1000, 100);
        imageCaptchaGenerator.init();
        // 生成滑块图片
        ImageCaptchaInfo slideImageInfo = imageCaptchaGenerator.generateCaptchaImage(CaptchaTypeConstant.SLIDER);
        // 获取背景图片的base64
        String backgroundImage = slideImageInfo.getBackgroundImage();
        // 获取滑块图片
        String sliderImage = slideImageInfo.getTemplateImage();
        System.out.println(slideImageInfo);
    }
}
