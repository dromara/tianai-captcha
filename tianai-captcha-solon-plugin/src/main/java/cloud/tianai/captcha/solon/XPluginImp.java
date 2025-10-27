package cloud.tianai.captcha.solon;

import cloud.tianai.captcha.solon.config.ImageCaptchaAutoConfiguration;
import cloud.tianai.captcha.solon.properties.CaptchaProperties;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * 插件启动类
 * @Author XT
 * @Date 2024.09.03
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) {
        context.beanMake(CaptchaProperties.class);
        context.beanMake(ImageCaptchaAutoConfiguration.class);
    }

}
