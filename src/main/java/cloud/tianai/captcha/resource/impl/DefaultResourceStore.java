package cloud.tianai.captcha.resource.impl;

import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.common.util.ObjectUtils;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: 天爱有情
 * @date 2021/8/7 15:43
 * @Description 默认的资源存储
 */
public class DefaultResourceStore implements ResourceStore {
    private static final String TYPE_TAG_SPLIT_FLAG = "|";
    /**
     * 模板资源.
     */
    private Map<String, List<ResourceMap>> templateResourceMap = new HashMap<>(2);

    /**
     * resource.
     */
    private Map<String, List<Resource>> resourceMap = new HashMap<>(2);

    /** 用于检索 type和tag. */
    private Map<String, List<ResourceMap>> templateResourceTagMap = new HashMap<>(2);
    private Map<String, List<Resource>> resourceTagMap = new HashMap<>(2);

    @Override
    public void addResource(String type, Resource resource) {
        resourceMap.computeIfAbsent(type, k -> new ArrayList<>(20)).add(resource);
        // 添加tag标签字典
        if (!ObjectUtils.isEmpty(resource.getTag())) {
            resourceTagMap.computeIfAbsent(mergeTypeAndTag(type, resource.getTag()), k -> new ArrayList<>(20)).add(resource);
        }
    }

    @Override
    public void addTemplate(String type, ResourceMap template) {
        templateResourceMap.computeIfAbsent(type, k -> new ArrayList<>(2)).add(template);
        // 添加tag标签字典
        if (!ObjectUtils.isEmpty(template.getTag())) {
            templateResourceTagMap.computeIfAbsent(mergeTypeAndTag(type, template.getTag()), k -> new ArrayList<>(2)).add(template);
        }
    }

    @Override
    public Resource randomGetResourceByTypeAndTag(String type, String tag) {
        List<Resource> resources;
        if (ObjectUtils.isEmpty(tag)) {
            resources = resourceMap.get(type);
        } else {
            resources = resourceTagMap.get(mergeTypeAndTag(type, tag));
        }
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
    public ResourceMap randomGetTemplateByTypeAndTag(String type, String tag) {
        List<ResourceMap> templateList;
        if (ObjectUtils.isEmpty(tag)) {
            templateList = templateResourceMap.get(type);
        } else {
            templateList = templateResourceTagMap.get(mergeTypeAndTag(type, tag));
        }
        if (CollectionUtils.isEmpty(templateList)) {
            throw new IllegalStateException("随机获取模板错误，store中模板为空, type:" + type);
        }

        if (templateList.size() == 1) {
            return templateList.get(0);
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(templateList.size());
        return templateList.get(randomIndex);
    }

    public String mergeTypeAndTag(String type, String tag) {
        return type + TYPE_TAG_SPLIT_FLAG + tag;
    }


    public void clearResources(String type) {
        resourceMap.remove(type);
    }

    public void clearAllResources() {
        resourceMap.clear();
    }

    public Map<String, List<Resource>> listAllResources() {
        return resourceMap;
    }

    public List<Resource> listResourcesByType(String type) {
        return resourceMap.getOrDefault(type, Collections.emptyList());
    }

    public int getAllResourceCount() {
        int count = 0;
        for (List<Resource> value : resourceMap.values()) {
            count += value.size();
        }
        return count;
    }

    public int getResourceCount(String type) {
        return resourceMap.getOrDefault(type, Collections.emptyList()).size();
    }


    public void clearAllTemplates() {
        templateResourceMap.clear();
    }

    public void clearTemplates(String type) {
        templateResourceMap.remove(type);
    }

    public List<ResourceMap> listTemplatesByType(String type) {
        return templateResourceMap.getOrDefault(type, Collections.emptyList());
    }

    public  Map<String, List<ResourceMap>> listAllTemplates() {
        return templateResourceMap;
    }


}
