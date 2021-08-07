package cloud.tianai.captcha.template.slider;

import java.util.List;
import java.util.Map;

public interface ResourceStore {

    /**
     * 添加资源
     *
     * @param resource 资源
     */
    void addResource(Resource resource);

    /**
     * 设置资源
     *
     * @param resources resources
     */
    void setResource(List<Resource> resources);

    /**
     * 删除资源
     *
     * @param resource resource
     */
    boolean deleteResource(Resource resource);

    /**
     * 清除所有资源
     */
    void clearResources();

    /**
     * 添加模板
     *
     * @param template template
     */
    void addTemplate(Map<String, Resource> template);


    /**
     * 设置模板
     *
     * @param templateResource templateResource
     */
    void setTemplates(List<Map<String, Resource>> templateResource);

    /**
     * 删除模板
     *
     * @param template template
     */
    void deleteTemplate(Map<String, Resource> template);

    /**
     * 清除所有模板
     */
    void clearTemplates();


    /**
     * 获取所有资源对象
     *
     * @return List<Resource>
     */
    List<Resource> listResources();

    /**
     * 获取所有模板
     *
     * @return List<Map < String, Resource>>
     */
    List<Map<String, Resource>> listTemplates();

    /**
     * 获取资源总数
     *
     * @return int
     */
    int getResourceCount();

    /**
     * 获取模板count
     *
     * @return int
     */
    int getTemplateCount();

    /**
     * 获取资源通过index
     *
     * @param index index
     * @return Resource
     */
    Resource getResourceByIndex(int index);

    /**
     * 获取模板通过indx
     *
     * @param index index
     * @return Map<String, Resource>
     */
    Map<String, Resource> getTemplateByIndex(int index);

}
