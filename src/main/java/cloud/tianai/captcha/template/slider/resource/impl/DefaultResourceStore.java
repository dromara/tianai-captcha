package cloud.tianai.captcha.template.slider.resource.impl;

import cloud.tianai.captcha.template.slider.resource.ResourceStore;
import cloud.tianai.captcha.template.slider.resource.common.model.dto.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2021/8/7 15:43
 * @Description 默认的资源存储
 */
public class DefaultResourceStore implements ResourceStore {
    /**
     * 模板资源.
     */
    private List<Map<String, Resource>> templateResourceList = new ArrayList<>(2);

    /**
     * resource.
     */
    private List<Resource> resourceList = new ArrayList<>(20);

    @Override
    public void addResource(Resource resource) {
        resourceList.add(resource);
    }

    @Override
    public void setResource(List<Resource> resources) {
        resourceList = new ArrayList<>(resources);
    }

    @Override
    public boolean deleteResource(Resource resource) {
        return resourceList.remove(resource);
    }

    @Override
    public void clearResources() {
        resourceList.clear();
    }

    @Override
    public void addTemplate(Map<String, Resource> template) {
        templateResourceList.add(template);
    }

    @Override
    public void setTemplates(List<Map<String, Resource>> templateResource) {
        templateResourceList = new ArrayList<>(templateResource);
    }

    @Override
    public void deleteTemplate(Map<String, Resource> template) {
        templateResourceList.remove(template);
    }

    @Override
    public void clearTemplates() {
        templateResourceList.clear();
    }

    @Override
    public List<Resource> listResources() {
        return Collections.unmodifiableList(resourceList);
    }

    @Override
    public List<Map<String, Resource>> listTemplates() {
        return Collections.unmodifiableList(templateResourceList);
    }

    @Override
    public int getResourceCount() {
        return resourceList.size();
    }

    @Override
    public int getTemplateCount() {
        return templateResourceList.size();
    }

    @Override
    public Resource getResourceByIndex(int index) {
        if (index < 0 || index > resourceList.size() - 1) {
            throw new IllegalArgumentException("错误的index");
        }
        return resourceList.get(index);
    }

    @Override
    public Map<String, Resource> getTemplateByIndex(int index) {
        if (index < 0 || index > templateResourceList.size() - 1) {
            throw new IllegalArgumentException("错误的index");
        }
        return templateResourceList.get(index);
    }
}
