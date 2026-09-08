package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.exception.ImageCaptchaException;
import cloud.tianai.captcha.generator.AbstractImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.model.dto.CaptchaExchange;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.common.model.dto.ParamKeyEnum;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: 天爱有情
 * @date 2026/2/4 14:21
 * @Description 基于工作量证明的验证码
 */
public class PowCaptchaGenerator extends AbstractImageCaptchaGenerator {

    public PowCaptchaGenerator() {
    }

    /**
     * 构造函数，指定资源管理器、图片转换器和拦截器
     *
     * @param imageCaptchaResourceManager 图片验证码资源管理器
     * @param imageTransform              图片转换器
     * @param interceptor                 验证码拦截器
     */
    public PowCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, ImageTransform imageTransform, CaptchaInterceptor interceptor) {
        super(imageCaptchaResourceManager);
        setImageTransform(imageTransform);
        setInterceptor(interceptor);
    }
    @Override
    protected void doInit() {

    }

    @Override
    protected void doGenerateCaptchaImage(CaptchaExchange captchaExchange) {


    }

    @Override
    protected ImageCaptchaInfo doWrapImageCaptchaInfo(CaptchaExchange captchaExchange) {
        GenerateParam param = captchaExchange.getParam();
        List<Number> powParam = param.getParam(ParamKeyEnum.POW_PARAM, Arrays.asList(4, 16, 4));
        if (powParam.size() != 3) {
            throw new ImageCaptchaException("工作量证明验证码参数错误, 参数数量必须为3");
        }
//        int difficulty = powParam.get(0).intValue();
//        int saltLen = powParam.get(1).intValue();
//        int num = powParam.get(2).intValue();

        captchaExchange.getCustomData().putViewData("POW", powParam);
        captchaExchange.getCustomData().putData("POW", powParam);


        ImageCaptchaInfo imageCaptchaInfo = new ImageCaptchaInfo();
        imageCaptchaInfo.setType(CaptchaTypeConstant.POW);
        return imageCaptchaInfo;
    }
}
