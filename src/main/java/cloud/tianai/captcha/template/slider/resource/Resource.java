package cloud.tianai.captcha.template.slider.resource;

import cloud.tianai.captcha.template.slider.resource.ResourceProvider;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: 天爱有情
 * @date 2021/8/7 15:15
 * @Description 资源对象
 */
@Data
@AllArgsConstructor
public class Resource {
    /** 类型. */
    private String type;
    /** 数据,传输给 {@link ResourceProvider} 的参数 */
    public String data;
}
