package cloud.tianai.captcha.resource.common.model.dto;

import cloud.tianai.captcha.resource.common.model.dto.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

/**
 * @Author: 天爱有情
 * @date 2022/12/30 9:23
 * @Description 存储一组Resource的Map, 增加tag标记
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceMap extends HashMap<String, Resource> {

    private String tag;

    public ResourceMap(String tag) {
        this.tag = tag;
    }

    public ResourceMap(String tag, int initialCapacity) {
        super(initialCapacity);
        this.tag = tag;
    }

    public ResourceMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ResourceMap() {
    }
}
