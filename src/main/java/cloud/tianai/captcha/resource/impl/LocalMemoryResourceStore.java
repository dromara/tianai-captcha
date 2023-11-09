package cloud.tianai.captcha.resource.impl;

import cloud.tianai.captcha.common.constant.CommonConstant;
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
public class LocalMemoryResourceStore implements ResourceStore {
    private static final String TYPE_TAG_SPLIT_FLAG = "|";

    /** 用于检索 type和tag. */
    private Map<String, List<ResourceMap>> templateResourceTagMap = new HashMap<>(2);
    private Map<String, List<Resource>> resourceTagMap = new HashMap<>(2);

    @Override
    public void addResource(String type, Resource resource) {
        if (ObjectUtils.isEmpty(resource.getTag())) {
            resource.setTag(CommonConstant.DEFAULT_TAG);
        }
        resourceTagMap.computeIfAbsent(mergeTypeAndTag(type, resource.getTag()), k -> new ArrayList<>(20)).add(resource);
    }

    @Override
    public void addTemplate(String type, ResourceMap template) {
        if (ObjectUtils.isEmpty(template.getTag())) {
            template.setTag(CommonConstant.DEFAULT_TAG);
        }
        templateResourceTagMap.computeIfAbsent(mergeTypeAndTag(type, template.getTag()), k -> new ArrayList<>(2)).add(template);
    }

    @Override
    public Resource randomGetResourceByTypeAndTag(String type, String tag) {
        List<Resource> resources = resourceTagMap.get(mergeTypeAndTag(type, tag));
        if (CollectionUtils.isEmpty(resources)) {
            throw new IllegalStateException("随机获取资源错误，store中资源为空, type:" + type + ",tag:" + tag);
        }
        if (resources.size() == 1) {
            return resources.get(0);
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(resources.size());
        return resources.get(randomIndex);
    }

    @Override
    public ResourceMap randomGetTemplateByTypeAndTag(String type, String tag) {
        List<ResourceMap> templateList = templateResourceTagMap.get(mergeTypeAndTag(type, tag));
        if (CollectionUtils.isEmpty(templateList)) {
            throw new IllegalStateException("随机获取模板错误，store中模板为空, type:" + type + ",tag:" + tag);
        }
        if (templateList.size() == 1) {
            return templateList.get(0);
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(templateList.size());
        return templateList.get(randomIndex);
    }

    public String mergeTypeAndTag(String type, String tag) {
        if (tag == null) {
            tag = CommonConstant.DEFAULT_TAG;
        }
        return type + TYPE_TAG_SPLIT_FLAG + tag;
    }


    public void clearResources(String type, String tag) {
        resourceTagMap.remove(mergeTypeAndTag(type, tag));
    }

    public void clearAllResources() {
        resourceTagMap.clear();
    }

    public Map<String, List<Resource>> listAllResources() {
        return resourceTagMap;
    }

    public List<Resource> listResourcesByType(String type, String tag) {
        return resourceTagMap.getOrDefault(mergeTypeAndTag(type, tag), Collections.emptyList());
    }

    public int getAllResourceCount() {
        int count = 0;
        for (List<Resource> value : resourceTagMap.values()) {
            count += value.size();
        }
        return count;
    }

    public int getResourceCount(String type, String tag) {
        return resourceTagMap.getOrDefault(mergeTypeAndTag(type, tag), Collections.emptyList()).size();
    }


    public void clearAllTemplates() {
        templateResourceTagMap.clear();
    }

    public void clearTemplates(String type, String tag) {
        templateResourceTagMap.remove(mergeTypeAndTag(type, tag));
    }

    public List<ResourceMap> listTemplatesByType(String type, String tag) {
        return templateResourceTagMap.getOrDefault(mergeTypeAndTag(type, tag), Collections.emptyList());
    }

    public Map<String, List<ResourceMap>> listAllTemplates() {
        return templateResourceTagMap;
    }


}
