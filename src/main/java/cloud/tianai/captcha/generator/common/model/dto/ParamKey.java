package cloud.tianai.captcha.generator.common.model.dto;

/**
 * @Author: 天爱有情
 * @date 2024/11/20 11:34
 * @Description 此接口的作用是在给 {@link GenerateParam} 添加/获取参数时做一个类型限制和转换
 */
public interface ParamKey<T> {

    String getKey();

}
