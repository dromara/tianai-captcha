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
     * 给ResourceStore添加hook，用于一些扩展
     *
     * @param hook
     */
    void addListener(ResourceListener hook);

    /**
     * 添加资源
     *
     * @param type     验证码类型
     * @param resource 资源
     */
    void addResource(String type, Resource resource);


    /**
     * 添加模板
     *
     * @param type     验证码类型
     * @param template 模板
     */
    void addTemplate(String type, ResourceMap template);

    /**
     * 删除资源
     *
     * @param type 验证码类型
     * @param id   资源ID
     * @return Resource
     */
    Resource deleteResource(String type, String id);

    /**
     * 删除模板
     *
     * @param type 验证码类型
     * @param id   资源ID
     * @return ResourceMap
     */
    ResourceMap deleteTemplate(String type, String id);

    /**
     * 获取某个资源列表
     *
     * @param type 验证码类型
     * @param tag  资源标签(可为空)
     * @return List<Resource>
     */
    List<Resource> listResourcesByTypeAndTag(String type, String tag);

    /**
     * 获取某个模板列表
     *
     * @param type 验证码类型
     * @param tag  资源标签(可为空)
     * @return List<ResourceMap>
     */
    List<ResourceMap> listTemplatesByTypeAndTag(String type, String tag);

    /**
     * 随机获取某个资源
     *
     * @param type type
     * @return Resource
     */
    Resource randomGetResourceByTypeAndTag(String type, String tag);

    /**
     * 随机获取某个模板通过type
     *
     * @param type type
     * @return Map<String, Resource>
     */
    ResourceMap randomGetTemplateByTypeAndTag(String type, String tag);

    /**
     * 清除所有内置模板
     */
    void clearAllTemplates();

    /**
     * 清除所有内置资源
     */
    void clearAllResources();

}
