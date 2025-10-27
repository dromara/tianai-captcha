package cloud.tianai.captcha.generator.common.model.dto;

import cloud.tianai.captcha.common.AnyMap;
import lombok.Data;

/**
 * @Author: 天爱有情
 * @date 2023/4/24 10:27
 * @Description 自定义扩展数据
 */
@Data
public class CustomData {

    /** 透传字段，用于传给前端. */
    private AnyMap viewData;
    /** 内部使用的字段数据. */
    private AnyMap data;
    /**
     * 扩展字段
     */
    public Object expand;

    public void putViewData(String key, Object data) {
        if (this.viewData == null) {
            this.viewData = new AnyMap();
        }
        this.viewData.put(key, data);
    }

    public void putData(String key, Object data) {
        if (this.data == null) {
            this.data = new AnyMap();
        }
        this.data.put(key, data);
    }
}
