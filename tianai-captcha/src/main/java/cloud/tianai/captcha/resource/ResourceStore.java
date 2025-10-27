package cloud.tianai.captcha.resource;

import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;

import java.util.List;

/**
 * @Author: 天爱有情
 * @date 2022/5/7 9:04
 * @Description 资源存储
 */
public interface ResourceStore {

    void init(ImageCaptchaResourceManager resourceManager);

    /**
     * 随机获取某个资源
     *
     * @param type type
     * @return Resource
     */
    List<Resource> randomGetResourceByTypeAndTag(String type, String tag, Integer quantity);

    /**
     * 随机获取某个模板通过type
     *
     * @param type type
     * @return Map<String, Resource>
     */
    List<ResourceMap> randomGetTemplateByTypeAndTag(String type, String tag,Integer quantity);

    default ResourceStore getTarget() {
        return this;
    }
}
