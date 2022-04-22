package cloud.tianai.captcha.template.slider.resource.impl;

import cloud.tianai.captcha.template.slider.common.util.CollectionUtils;
import cloud.tianai.captcha.template.slider.resource.ResourceStore;
import cloud.tianai.captcha.template.slider.resource.common.model.dto.Resource;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: 天爱有情
 * @date 2021/8/7 15:43
 * @Description 默认的资源存储
 */
public class DefaultResourceStore implements ResourceStore {
    /**
     * 模板资源.
     */
    private Map<String, List<Map<String, Resource>>> templateResourceMap = new HashMap<>(2);

    /**
     * resource.
     */
    private Map<String, List<Resource>> resourceMap = new HashMap<>(2);

    @Override
    public void addResource(String type, Resource resource) {
        resourceMap.computeIfAbsent(type, k -> new ArrayList<>(20)).add(resource);
    }

    @Override
    public void clearResources(String type) {
        resourceMap.remove(type);
    }

    @Override
    public void clearAllResources() {
        resourceMap.clear();
    }

    @Override
    public Map<String, List<Resource>> listAllResources() {
        return resourceMap;
    }

    @Override
    public List<Resource> listResourcesByType(String type) {
        return resourceMap.getOrDefault(type, Collections.emptyList());
    }

    @Override
    public Resource randomGetResource(String type) {
        List<Resource> resources = resourceMap.get(type);
        if (CollectionUtils.isEmpty(resources)) {
            throw new IllegalStateException("随机获取资源错误，store中资源为空, type:" + type);
        }
        if (resources.size() == 1) {
            return resources.get(0);
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(resources.size());
        return resources.get(randomIndex);
    }

    @Override
    public int getAllResourceCount() {
        int count = 0;
        for (List<Resource> value : resourceMap.values()) {
            count += value.size();
        }
        return count;
    }

    @Override
    public int getResourceCount(String type) {
        return resourceMap.getOrDefault(type, Collections.emptyList()).size();
    }


    @Override
    public void addTemplate(String type, Map<String, Resource> template) {
        templateResourceMap.computeIfAbsent(type, k -> new ArrayList<>(2)).add(template);
    }

    @Override
    public void clearAllTemplates() {
        templateResourceMap.clear();
    }

    @Override
    public void clearTemplates(String type) {
        templateResourceMap.remove(type);
    }

    @Override
    public List<Map<String, Resource>> listTemplatesByType(String type) {
        return templateResourceMap.getOrDefault(type, Collections.emptyList());
    }

    @Override
    public Map<String, List<Map<String, Resource>>> listAllTemplates() {
        return templateResourceMap;
    }

    @Override
    public Map<String, Resource> randomGetTemplateByType(String type) {
        List<Map<String, Resource>> templateList = templateResourceMap.get(type);
        if (CollectionUtils.isEmpty(templateList)) {
            throw new IllegalStateException("随机获取模板错误，store中模板为空, type:" + type);
        }

        if (templateList.size() == 1) {
            return templateList.get(0);
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(templateList.size());
        return templateList.get(randomIndex);
    }

}
