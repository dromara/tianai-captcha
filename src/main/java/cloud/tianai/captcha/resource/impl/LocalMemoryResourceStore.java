package cloud.tianai.captcha.resource.impl;

import cloud.tianai.captcha.common.constant.CommonConstant;
import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.common.util.ObjectUtils;
import cloud.tianai.captcha.resource.AbstractResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: 天爱有情
 * @date 2021/8/7 15:43
 * @Description 默认的资源存储
 */
public class LocalMemoryResourceStore extends AbstractResourceStore {
    private static final String TYPE_TAG_SPLIT_FLAG = "|";

    /** 用于检索 type和tag. */
    private Map<String, List<ResourceMap>> templateResourceTagMap = new HashMap<>(2);
    private Map<String, List<Resource>> resourceTagMap = new HashMap<>(2);

    @Override
    public void doAddResource(String type, Resource resource) {
        if (ObjectUtils.isEmpty(resource.getTag())) {
            resource.setTag(CommonConstant.DEFAULT_TAG);
        }
        resourceTagMap.computeIfAbsent(mergeTypeAndTag(type, resource.getTag()), k -> new ArrayList<>(20)).add(resource);
    }

    @Override
    public void doAddTemplate(String type, ResourceMap template) {
        if (ObjectUtils.isEmpty(template.getTag())) {
            template.setTag(CommonConstant.DEFAULT_TAG);
        }
        templateResourceTagMap.computeIfAbsent(mergeTypeAndTag(type, template.getTag()), k -> new ArrayList<>(2)).add(template);
    }

    @Override
    public Resource doDeleteResource(String type, String id) {
        for (Map.Entry<String, List<Resource>> entry : resourceTagMap.entrySet()) {
            String k = entry.getKey();
            List<Resource> v = entry.getValue();
            String splitType = splitTypeTag(k)[0];
            if (splitType.equals(type)) {
                Iterator<Resource> iterator = v.iterator();
                while (iterator.hasNext()) {
                    Resource next = iterator.next();
                    if (next.getId().equals(id)) {
                        iterator.remove();
                        return next;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ResourceMap doDeleteTemplate(String type, String id) {
        for (Map.Entry<String, List<ResourceMap>> entry : templateResourceTagMap.entrySet()) {
            String k = entry.getKey();
            List<ResourceMap> v = entry.getValue();
            String splitType = splitTypeTag(k)[0];
            if (splitType.equals(type)) {
                Iterator<ResourceMap> iterator = v.iterator();
                while (iterator.hasNext()) {
                    ResourceMap next = iterator.next();
                    if (next.getId().equals(id)) {
                        iterator.remove();
                        return next;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> listResourcesByTypeAndTag(String type, String tag) {
        if (!ObjectUtils.isEmpty(tag)) {
            return resourceTagMap.get(mergeTypeAndTag(type, tag));
        }
        List<Resource> resourceList = new ArrayList<>();
        resourceTagMap.forEach((k, v) -> {
            String splitType = splitTypeTag(k)[0];
            if (splitType.equals(type)) {
                resourceList.addAll(v);
            }
        });
        return resourceList;
    }

    @Override
    public List<ResourceMap> listTemplatesByTypeAndTag(String type, String tag) {
        if (!ObjectUtils.isEmpty(tag)) {
            return templateResourceTagMap.get(mergeTypeAndTag(type, tag));
        }
        List<ResourceMap> resourceMapList = new ArrayList<>();
        templateResourceTagMap.forEach((k, v) -> {
            String splitType = splitTypeTag(k)[0];
            if (splitType.equals(type)) {
                resourceMapList.addAll(v);
            }
        });
        return resourceMapList;
    }

    @Override
    public Resource doRandomGetResourceByTypeAndTag(String type, String tag) {
        List<Resource> resources = resourceTagMap.get(mergeTypeAndTag(type, tag));
        if (CollectionUtils.isEmpty(resources)) {
            throw new IllegalStateException("随机获取资源错误，store中资源为空, type:" + type + ",tag:" + tag);
        }
        if (resources.size() == 1) {
            return resources.get(0);
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(resources.size());
        try {
            return resources.get(randomIndex);
        } catch (IndexOutOfBoundsException e) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException ex) {
                // ignore
            }
            return doRandomGetResourceByTypeAndTag(type, tag);
        }
    }

    @Override
    public ResourceMap doRandomGetTemplateByTypeAndTag(String type, String tag) {
        List<ResourceMap> templateList = templateResourceTagMap.get(mergeTypeAndTag(type, tag));
        if (CollectionUtils.isEmpty(templateList)) {
            throw new IllegalStateException("随机获取模板错误，store中模板为空, type:" + type + ",tag:" + tag);
        }
        if (templateList.size() == 1) {
            return templateList.get(0);
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(templateList.size());
        try {
            return templateList.get(randomIndex);
        } catch (IndexOutOfBoundsException e) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException ex) {
                // ignore
            }
            return doRandomGetTemplateByTypeAndTag(type, tag);
        }
    }

    public String mergeTypeAndTag(String type, String tag) {
        if (tag == null) {
            tag = CommonConstant.DEFAULT_TAG;
        }
        return type + TYPE_TAG_SPLIT_FLAG + tag;
    }

    public String[] splitTypeTag(String k) {
        return k.split("\\" + TYPE_TAG_SPLIT_FLAG);
    }


    public void clearResources(String type, String tag) {
        resourceTagMap.remove(mergeTypeAndTag(type, tag));
    }

    @Override
    public void doClearAllResources() {
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


    @Override
    public void doClearAllTemplates() {
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
