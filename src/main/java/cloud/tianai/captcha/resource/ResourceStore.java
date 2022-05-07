package cloud.tianai.captcha.resource;

import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.Resource;

import java.util.List;
import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2022/5/7 9:04
 * @Description 资源存储
 */
public interface ResourceStore {

    /**
     * 添加资源
     *
     * @param type     验证码类型
     * @param resource 资源
     */
    void addResource(String type, Resource resource);

    /**
     * 清除某个类型下的所有资源
     *
     * @param type type
     */
    void clearResources(String type);

    /**
     * 清除所有资源
     */
    void clearAllResources();

    /**
     * 获取所有资源对象
     *
     * @return List<Resource>
     */
    Map<String, List<Resource>> listAllResources();

    /**
     * 获取某个type下的所有资源对象
     *
     * @param type type
     * @return List<Resource>
     */
    List<Resource> listResourcesByType(String type);

    /**
     * 随机获取某个资源
     *
     * @param type type
     * @return Resource
     */
    Resource randomGetResource(String type);

    /**
     * 获取资源总数
     *
     * @return int
     */
    int getAllResourceCount();

    /**
     * 获取某个type下的资源总数
     *
     * @param type type
     * @return int
     */
    int getResourceCount(String type);

    /**
     * 添加模板
     *
     * @param type     验证码类型
     * @param template 模板
     */
    void addTemplate(String type, Map<String, Resource> template);

    /**
     * 清除所有模板
     */
    void clearAllTemplates();

    /**
     * 清除某个type下的所有模板
     *
     * @param type type
     */
    void clearTemplates(String type);

    /**
     * 获取所有模板通过type
     *
     * @param type 验证码类型
     * @return List<Map < String, Resource>>
     */
    List<Map<String, Resource>> listTemplatesByType(String type);

    /**
     * 获取所有模板
     *
     * @return Map<String, List < Map < String, Resource>>>
     */
    Map<String, List<Map<String, Resource>>> listAllTemplates();

    /**
     * 随机获取某个模板通过type
     *
     * @param type type
     * @return Map<String, Resource>
     */
    Map<String, Resource> randomGetTemplateByType(String type);

}
