package cloud.tianai.captcha.spring.plugins;

import cloud.tianai.captcha.common.constant.CommonConstant;
import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.resource.CrudResourceStore;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: 天爱有情
 * @date 2023/8/23 10:52
 * @Description 基于redis的store
 */
@RequiredArgsConstructor
public class RedisResourceStore implements CrudResourceStore {


    private final StringRedisTemplate redisTemplate;

    @Getter
    @Setter
    private String resourcePrefix = "captcha:config:resource:";
    @Getter
    @Setter
    private String templatePrefix = "captcha:config:template:";
    private Gson gson = new Gson();

    public String joinResourceKey(String type, String tag) {
        if (tag == null) {
            tag = CommonConstant.DEFAULT_TAG;
        }
        type = type.toUpperCase();
        return resourcePrefix + tag + ":" + type;
    }

    public String joinTemplateKey(String type, String tag) {
        if (tag == null) {
            tag = CommonConstant.DEFAULT_TAG;
        }
        type = type.toUpperCase();
        return templatePrefix + tag + ":" + type;
    }

    public List<Resource> getResources(String type, String tag) {
        String key = joinResourceKey(type, tag);
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size < 1) {
            return Collections.emptyList();
        }
        List<String> range = redisTemplate.opsForList().range(key, 0, size);
        List<Resource> result = new ArrayList<>(range.size());
        for (String json : range) {
            result.add(gson.fromJson(json, Resource.class));
        }

        return result;
    }

    public List<ResourceMap> getTemplates(String type, String tag) {
        String key = joinTemplateKey(type, tag);
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size < 1) {
            return Collections.emptyList();
        }
        List<String> range = redisTemplate.opsForList().range(key, 0, size);
        List<ResourceMap> result = new ArrayList<>(range.size());
        for (String json : range) {
            result.add(gson.fromJson(json, ResourceMap.class));
        }
        return result;
    }


    public void setResources(String type, String tag, List<Resource> resources) {
        String key = joinResourceKey(type, tag);
        Long size = redisTemplate.opsForList().size(key);
        if (size != null && size > 0) {
            redisTemplate.delete(key);
        }
        for (Resource resource : resources) {
            addResource(type, resource);
        }
    }

    public void setTemplates(String type, String tag, List<ResourceMap> templates) {
        String key = joinTemplateKey(type, tag);
        Long size = redisTemplate.opsForList().size(key);
        if (size != null && size > 0) {
            redisTemplate.delete(key);
        }
        for (ResourceMap template : templates) {
            addTemplate(type, template);
        }
    }


    @Override
    public void addResource(String type, Resource resource) {
        // 添加tag标签字典
        redisTemplate.opsForList().rightPush(joinResourceKey(type, resource.getTag()), gson.toJson(resource));
    }

    @Override
    public void addTemplate(String type, ResourceMap template) {
        // 添加tag标签字典
        redisTemplate.opsForList().rightPush(joinTemplateKey(type, template.getTag()), gson.toJson(template));
    }

    @Override
    public Resource deleteResource(String type, String id) {
        Set<String> keys = redisTemplate.keys(joinResourceKey(type, "*"));
        if (!CollectionUtils.isEmpty(keys)) {
            for (String key : keys) {
                Long size = redisTemplate.opsForList().size(key);
                if (size == null || size < 1) {
                    continue;
                }
                List<String> range = redisTemplate.opsForList().range(key, 0, size);
                if (range != null) {
                    for (String json : range) {
                        Resource resource = gson.fromJson(json, Resource.class);
                        if (resource.getId().equals(id)) {
                            redisTemplate.opsForList().remove(key, 1, json);
                            return resource;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ResourceMap deleteTemplate(String type, String id) {
        Set<String> keys = redisTemplate.keys(joinTemplateKey(type, "*"));
        if (!CollectionUtils.isEmpty(keys)) {
            for (String key : keys) {
                Long size = redisTemplate.opsForList().size(key);
                if (size == null || size < 1) {
                    continue;
                }
                List<String> range = redisTemplate.opsForList().range(key, 0, size);
                if (range != null) {
                    for (String json : range) {
                        ResourceMap resourceMap = gson.fromJson(json, ResourceMap.class);
                        if (resourceMap.getId().equals(id)) {
                            redisTemplate.opsForList().remove(key, 1, json);
                            return resourceMap;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> listResourcesByTypeAndTag(String type, String tag) {
        if (StringUtils.isNotBlank(tag)) {
            return getResources(type, tag);
        }
        Set<String> keys = redisTemplate.keys(joinResourceKey(type, "*"));
        if (!CollectionUtils.isEmpty(keys)) {
            List<Resource> resources = new ArrayList<>();
            for (String key : keys) {
                Long size1 = redisTemplate.opsForList().size(key);
                if (size1 == null || size1 < 1) {
                    continue;
                }
                List<String> range = redisTemplate.opsForList().range(key, 0, size1);
                if (range != null) {
                    for (String json : range) {
                        Resource resource = gson.fromJson(json, Resource.class);
                        resources.add(resource);
                    }
                }
            }
            return resources;
        }
        return Collections.emptyList();
    }

    @Override
    public List<ResourceMap> listTemplatesByTypeAndTag(String type, String tag) {
        if (StringUtils.isNotBlank(tag)) {
            return getTemplates(type, tag);
        }
        Set<String> keys = redisTemplate.keys(joinTemplateKey(type, "*"));
        if (!CollectionUtils.isEmpty(keys)) {
            List<ResourceMap> templates = new ArrayList<>();
            for (String key : keys) {
                Long size1 = redisTemplate.opsForList().size(key);
                if (size1 == null || size1 < 1) {
                    continue;
                }
                List<String> range = redisTemplate.opsForList().range(key, 0, size1);
                if (range != null) {
                    for (String json : range) {
                        ResourceMap template = gson.fromJson(json, ResourceMap.class);
                        templates.add(template);
                    }
                }
            }
            return templates;
        }
        return Collections.emptyList();
    }

    @Override
    public void init(ImageCaptchaResourceManager resourceManager) {

    }

    @Override
    public List<Resource> randomGetResourceByTypeAndTag(String type, String tag, Integer quantity) {
        String key = joinResourceKey(type, tag);
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || quantity > size) {
            throw new IllegalArgumentException("请求的资源数量超过可用资源总数");
        }

        Set<Long> indexes = new HashSet<>(quantity);
        while (indexes.size() < quantity) {
            indexes.add(ThreadLocalRandom.current().nextLong(size));
        }
        List<Resource> result = new ArrayList<>(quantity);
        for (Long index : indexes) {
            String resourceJson = redisTemplate.opsForList().index(key, index);
            result.add(gson.fromJson(resourceJson, Resource.class));

        }
        return result;
    }

    @Override
    public List<ResourceMap> randomGetTemplateByTypeAndTag(String type, String tag, Integer quantity) {
        String key = joinTemplateKey(type, tag);
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size < 1) {
            throw new IllegalStateException("随机获取模板错误，store中模板为空, type:" + type);
        }
        if (quantity > size) {
            throw new IllegalArgumentException("请求的模板数量超过可用模板总数");
        }

        Set<Long> indexes = new HashSet<>(quantity);
        while (indexes.size() < quantity) {
            indexes.add(ThreadLocalRandom.current().nextLong(size));
        }

        List<ResourceMap> result = new ArrayList<>(quantity);

        for (Long index : indexes) {
            String resourceJson = redisTemplate.opsForList().index(key, index);
            result.add(gson.fromJson(resourceJson, ResourceMap.class));
        }
        return result;
    }

    @Override
    public void clearAllTemplates() {
        Set<String> keys = redisTemplate.keys(templatePrefix + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public void clearAllResources() {
        Set<String> keys = redisTemplate.keys(resourcePrefix + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

}
