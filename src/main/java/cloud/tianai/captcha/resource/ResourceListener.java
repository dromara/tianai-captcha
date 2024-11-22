package cloud.tianai.captcha.resource;

import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;

/**
 * @Author: 天爱有情
 * @date 2024/11/19 9:26
 * @Description 此类负责对 ResourceStore 进行一下扩展增强, 对ResourceStore的相关方法添加一个hook回调
 */
public interface ResourceListener {

    default void onInit(ResourceStore resourceStore, ImageCaptchaResourceManager resourceManager) {
    }


    default void onAddResource(String type, Resource resource) {

    }

    default void onAddTemplate(String type, ResourceMap template) {

    }

    default void onDeleteResource(String type, Resource resource) {

    }

    default void onDeleteTemplate(String type, ResourceMap template) {

    }

    default void onClearAllResources() {

    }

    default void onClearAllTemplates() {

    }

    default void onRandomGetResourceByTypeAndTag(String type, String tag, Resource resource) {

    }

    default void onRandomGetTemplateByTypeAndTag(String type, String tag, ResourceMap template) {

    }


}
