package cloud.tianai.captcha.resource.common.model.dto;

import cloud.tianai.captcha.resource.ResourceProvider;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: 天爱有情
 * @date 2021/8/7 15:15
 * @Description 资源对象
 */
@Data
public class Resource {
    /** 类型. */
    private String type;
    /** 数据,传输给 {@link ResourceProvider} 的参数 */
    public String data;
    /** 标签.*/
    private String tag;

    public Resource(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public Resource(String type, String data, String tag) {
        this.type = type;
        this.data = data;
        this.tag = tag;
    }
}
