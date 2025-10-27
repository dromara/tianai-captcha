package cloud.tianai.captcha.resource.common.model.dto;

import cloud.tianai.captcha.common.util.UUIDUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @Author: 天爱有情
 * @date 2022/12/30 9:23
 * @Description 存储一组Resource的Map, 增加tag标记
 */
@Data
@EqualsAndHashCode
public class ResourceMap {
    /** 唯一ID. */
    private String id;
    private Map<String, Resource> resourceMap;
    private String tag;

    public ResourceMap(String tag) {
        this(tag, 10);
    }

    public ResourceMap(String tag, int initialCapacity) {
        this(UUIDUtils.getUUID(), tag, initialCapacity);
    }

    public ResourceMap(String id, String tag, int initialCapacity) {
        this.tag = tag;
        this.resourceMap = new HashMap<>(initialCapacity);
        this.id = id;
    }

    public ResourceMap(int initialCapacity) {
        this(null, initialCapacity);
    }

    public ResourceMap() {
        this(null);
    }

    private Map<String, Resource> getResourceMapOfCreate() {
        if (resourceMap == null) {
            resourceMap = new HashMap<>(2);
        }
        return resourceMap;
    }

    // ================== Map ==================

    public Resource put(String key, Resource value) {
        return getResourceMapOfCreate().put(key, value);
    }

    public Resource get(Object key) {
        return getResourceMapOfCreate().get(key);
    }

    public Resource remove(Object key) {
        return getResourceMapOfCreate().remove(key);
    }

    public Collection<Resource> values() {
        return getResourceMapOfCreate().values();
    }

    public void forEach(BiConsumer<String, Resource> action) {
        getResourceMapOfCreate().forEach(action);
    }
}
