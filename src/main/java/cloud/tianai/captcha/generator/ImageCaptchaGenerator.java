package cloud.tianai.captcha.generator;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;

/**
 * @Author: 天爱有情
 * @date 2020/10/19 18:37
 * @Description 图片验证码生成器
 */
public interface ImageCaptchaGenerator {


    /**
     * 初始化
     *
     * @return ImageCaptchaGenerator
     */
    ImageCaptchaGenerator init();

    /**
     * 生成验证码图片
     *
     * @param type 类型 {@link CaptchaTypeConstant}
     * @return SliderCaptchaInfo
     */
    ImageCaptchaInfo generateCaptchaImage(String type);


    /**
     * 生成滑块验证码
     *
     * @param type             type {@link CaptchaTypeConstant}
     * @param targetFormatName jpeg或者webp格式
     * @param matrixFormatName png或者webp格式
     * @return SliderCaptchaInfo
     */
    ImageCaptchaInfo generateCaptchaImage(String type, String targetFormatName, String matrixFormatName);

    /**
     * 生成验证码
     *
     * @param param 生成参数
     * @return SliderCaptchaInfo
     */
    ImageCaptchaInfo generateCaptchaImage(GenerateParam param);


    /**
     * 获取滑块验证码资源管理器
     *
     * @return SliderCaptchaResourceManager
     */
    ImageCaptchaResourceManager getImageResourceManager();

    /**
     * 设置滑块验证码资源管理器
     *
     * @param imageCaptchaResourceManager
     */
    void setImageResourceManager(ImageCaptchaResourceManager imageCaptchaResourceManager);

    /**
     * 获取图片转换器
     *
     * @return ImageTransform
     */
    ImageTransform getImageTransform();

    /**
     * 设置图片转换器
     *
     * @param imageTransform imageTransform
     * @return ImageTransform
     */
    void setImageTransform(ImageTransform imageTransform);


    CaptchaInterceptor getInterceptor();

    void setInterceptor(CaptchaInterceptor interceptor);

}
