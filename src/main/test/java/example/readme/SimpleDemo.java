package example.readme;

import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.impl.BasicCaptchaTrackValidator;

import java.util.Map;

/**
* 基础 SimpleDemo
*/
public class SimpleDemo {

    public static void main(String[] args) throws InterruptedException {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager,imageTransform).init(true);
        BasicCaptchaTrackValidator imageCaptchaValidator = new BasicCaptchaTrackValidator();
        // 注意:  上面这个四个对象都是单例的， 整个项目创建一次即可

        // 这里生成一个滑块验证码数据， 里面包括背景图、滑块图等等，按需传给前端进行展示
        ImageCaptchaInfo imageCaptchaInfo = imageCaptchaGenerator.generateCaptchaImage(CaptchaTypeConstant.SLIDER);

        // 这个数据是根据当前生成的这条验证码数据生成对应的验证数据， 该数据要存到缓存中
        AnyMap map = imageCaptchaValidator.generateImageCaptchaValidData(imageCaptchaInfo);



        // 这是用户移动滑块后的校验接口
        // imageCaptchaTrack 对象为前端传来的滑动轨迹数据， 这里进行验证滑块， 返回 true 说明校验通过
        ImageCaptchaTrack imageCaptchaTrack = null;
        boolean check = imageCaptchaValidator.valid(imageCaptchaTrack, map).isSuccess();
    }

}
