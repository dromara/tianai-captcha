package cloud.tianai.captcha.resource.impl;

import cloud.tianai.captcha.common.constant.CommonConstant;
import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.common.util.ObjectUtils;
import cloud.tianai.captcha.resource.CrudResourceStore;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: 天爱有情
 * @date 2021/8/7 15:43
 * @Description 默认的资源存储
 */
public class LocalMemoryResourceStore implements CrudResourceStore {
    /** 用于检索 type和tag. */
    private final Map<String, Map<String, List<ResourceMap>>> templateResourceTagMap = new HashMap<>(2);
    private final Map<String, Map<String, List<Resource>>> resourceTagMap = new HashMap<>(2);


    private void ensureTypeTagMapExists(Map<String, Map<String, List<Resource>>> map, String type, String tag) {
        map.computeIfAbsent(type, k -> new HashMap<>())
                .computeIfAbsent(tag, k -> new ArrayList<>(20));
    }

    private void ensureTypeTagMapExistsForTemplate(Map<String, Map<String, List<ResourceMap>>> map, String type, String tag) {
        map.computeIfAbsent(type, k -> new HashMap<>())
                .computeIfAbsent(tag, k -> new ArrayList<>(2));
    }

    @Override
    public void addResource(String type, Resource resource) {
        if (ObjectUtils.isEmpty(resource.getTag())) {
            resource.setTag(CommonConstant.DEFAULT_TAG);
        }
        ensureTypeTagMapExists(resourceTagMap, type, resource.getTag());
        resourceTagMap.get(type).get(resource.getTag()).add(resource);
    }

    @Override
    public void addTemplate(String type, ResourceMap template) {
        if (ObjectUtils.isEmpty(template.getTag())) {
            template.setTag(CommonConstant.DEFAULT_TAG);
        }
        ensureTypeTagMapExistsForTemplate(templateResourceTagMap, type, template.getTag());
        templateResourceTagMap.get(type).get(template.getTag()).add(template);
    }

    @Override
    public Resource deleteResource(String type, String id) {
        Map<String, List<Resource>> tagMap = resourceTagMap.get(type);
        if (tagMap == null) return null;

        for (List<Resource> resources : tagMap.values()) {
            Iterator<Resource> iterator = resources.iterator();
            while (iterator.hasNext()) {
                Resource res = iterator.next();
                if (res.getId().equals(id)) {
                    iterator.remove();
                    return res;
                }
            }
        }
        return null;
    }

    @Override
    public ResourceMap deleteTemplate(String type, String id) {
        Map<String, List<ResourceMap>> tagMap = templateResourceTagMap.get(type);
        if (tagMap == null) return null;

        for (List<ResourceMap> templates : tagMap.values()) {
            Iterator<ResourceMap> iterator = templates.iterator();
            while (iterator.hasNext()) {
                ResourceMap temp = iterator.next();
                if (temp.getId().equals(id)) {
                    iterator.remove();
                    return temp;
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> listResourcesByTypeAndTag(String type, String tag) {
        if (!ObjectUtils.isEmpty(tag)) {
            Map<String, List<Resource>> tagMap = resourceTagMap.get(type);
            return tagMap == null ? Collections.emptyList() : tagMap.getOrDefault(tag, Collections.emptyList());
        }
        List<Resource> result = new ArrayList<>();
        Map<String, List<Resource>> tagMap = resourceTagMap.get(type);
        if (tagMap != null) {
            for (List<Resource> list : tagMap.values()) {
                result.addAll(list);
            }
        }
        return result;
    }

    @Override
    public List<ResourceMap> listTemplatesByTypeAndTag(String type, String tag) {
        if (!ObjectUtils.isEmpty(tag)) {
            Map<String, List<ResourceMap>> tagMap = templateResourceTagMap.get(type);
            return tagMap == null ? Collections.emptyList() : tagMap.getOrDefault(tag, Collections.emptyList());
        }
        List<ResourceMap> result = new ArrayList<>();
        Map<String, List<ResourceMap>> tagMap = templateResourceTagMap.get(type);
        if (tagMap != null) {
            for (List<ResourceMap> list : tagMap.values()) {
                result.addAll(list);
            }
        }
        return result;
    }

    @Override
    public void init(ImageCaptchaResourceManager resourceManager) {

    }

    @Override
    public List<Resource> randomGetResourceByTypeAndTag(String type, String tag, Integer quantity) {
        List<Resource> resources = listResourcesByTypeAndTag(type, tag);
        if (CollectionUtils.isEmpty(resources)) {
            throw new IllegalStateException("随机获取资源错误，store中资源为空, type:" + type + ",tag:" + tag);
        }
        int size = resources.size();
        if (quantity > size) {
            throw new IllegalArgumentException("请求的资源数量超过可用资源总数");
        }

        Set<Integer> indexes = new HashSet<>(quantity);
        while (indexes.size() < quantity) {
            indexes.add(ThreadLocalRandom.current().nextInt(size));
        }

        List<Resource> result = new ArrayList<>(quantity);
        for (int index : indexes) {
            result.add(resources.get(index));
        }
        return result;
    }


    @Override
    public List<ResourceMap> randomGetTemplateByTypeAndTag(String type, String tag, Integer quantity) {
        List<ResourceMap> templates = listTemplatesByTypeAndTag(type, tag);
        if (CollectionUtils.isEmpty(templates)) {
            throw new IllegalStateException("随机获取模板错误，store中模板为空, type:" + type + ",tag:" + tag);
        }
        int size = templates.size();
        if (quantity > size) {
            throw new IllegalArgumentException("请求的模板数量超过可用模板总数");
        }

        Set<Integer> indexes = new HashSet<>(quantity);
        while (indexes.size() < quantity) {
            indexes.add(ThreadLocalRandom.current().nextInt(size));
        }

        List<ResourceMap> result = new ArrayList<>(quantity);
        for (int index : indexes) {
            result.add(templates.get(index));
        }
        return result;
    }

    @Override
    public void clearAllResources() {
        resourceTagMap.clear();
    }


    @Override
    public void clearAllTemplates() {
        templateResourceTagMap.clear();
    }


}
