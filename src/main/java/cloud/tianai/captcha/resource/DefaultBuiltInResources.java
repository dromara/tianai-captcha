package cloud.tianai.captcha.resource;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static cloud.tianai.captcha.common.constant.CommonConstant.DEFAULT_TAG;
import static cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator.TEMPLATE_ACTIVE_IMAGE_NAME;
import static cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator.TEMPLATE_FIXED_IMAGE_NAME;


/**
 * @Author: 天爱有情
 * @date 2024/7/15 9:10
 * @Description 默认资源配置
 * 注意： 不推荐使用该类，应该将资源模板自己设置，而不是使用默认的，这里编写的目的只是为了演示方便
 */
public class DefaultBuiltInResources {

    public static final String PATH_PREFIX = "classpath:META-INF/cut-image/template";

    private static Map<String, Consumer<ResourceStore>> defaultTemplateResource = new HashMap<>(8);


    public DefaultBuiltInResources(String defaultPathPrefix) {
        init(defaultPathPrefix);
    }

    private void init(String defaultPathPrefix) {
        String[] split = defaultPathPrefix.split(":");
        String type;
        String pathPrefix;
        if (split.length < 1) {
            type = "file";
            pathPrefix = defaultPathPrefix;
        } else {
            type = split[0];
            pathPrefix = split[1];
        }
        if (pathPrefix.endsWith("/")) {
            pathPrefix = pathPrefix.substring(0, pathPrefix.length() - 1);
        }
        // 滑动验证
        String finalPathPrefix = pathPrefix;
        defaultTemplateResource.put(CaptchaTypeConstant.SLIDER, resourceStore -> {
            ResourceMap template1 = new ResourceMap(DEFAULT_TAG, 4);
            template1.put(TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(type, finalPathPrefix.concat("/slider_1/active.png")));
            template1.put(TEMPLATE_FIXED_IMAGE_NAME, new Resource(type, finalPathPrefix.concat("/slider_1/fixed.png")));
            resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template1);

            ResourceMap template2 = new ResourceMap(DEFAULT_TAG, 4);
            template2.put(TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(type, finalPathPrefix.concat("/slider_2/active.png")));
            template2.put(TEMPLATE_FIXED_IMAGE_NAME, new Resource(type, finalPathPrefix.concat("/slider_2/fixed.png")));
            resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template2);
        });

        // 旋转验证
        defaultTemplateResource.put(CaptchaTypeConstant.ROTATE, resourceStore -> {
            // 添加一些系统的 模板文件
            ResourceMap template1 = new ResourceMap(DEFAULT_TAG, 4);
            template1.put(TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(type, finalPathPrefix.concat("/rotate_1/active.png")));
            template1.put(TEMPLATE_FIXED_IMAGE_NAME, new Resource(type, finalPathPrefix.concat("/rotate_1/fixed.png")));
            resourceStore.addTemplate(CaptchaTypeConstant.ROTATE, template1);
        });

        // 字体包
        defaultTemplateResource.put(FontCache.FONT_TYPE, resourceStore -> {
            resourceStore.addResource(FontCache.FONT_TYPE,new Resource(type,  finalPathPrefix.concat("/fontS/SIMSUN.TTC")));
        });
    }


    public void addDefaultTemplate(String type, ResourceStore resourceStore) {
        Consumer<ResourceStore> resourceStoreConsumer = defaultTemplateResource.get(type);
        if (resourceStoreConsumer == null) {
            return;
        }
        resourceStoreConsumer.accept(resourceStore);
    }

    public void addDefaultTemplate(ResourceStore resourceStore) {
        defaultTemplateResource.forEach((type, consumer) -> {
            consumer.accept(resourceStore);
        });
    }

}
