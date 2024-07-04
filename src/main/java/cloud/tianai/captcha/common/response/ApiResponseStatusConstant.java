package cloud.tianai.captcha.common.response;


/**
 * @Author: 天爱有情
 * @Date 2020/5/26 17:58
 * @Description 统一返回错误码， 详见 阿里巴巴开发规范 错误码列表
 * <p>
 * 该枚举定义了一些公共的code码，自定义code码数据需在自己业务中编写
 */
public interface ApiResponseStatusConstant {

    /**
     * 成功.
     */
    CodeDefinition SUCCESS = new CodeDefinition(200, "OK");

    /**
     * 无效参数
     */
    CodeDefinition NOT_VALID_PARAM = new CodeDefinition(403, "无效参数");

    /**
     * 未知的内部错误
     */
    CodeDefinition INTERNAL_SERVER_ERROR = new CodeDefinition(500, "未知的内部错误");

    /**
     * 已失效
     */
    CodeDefinition EXPIRED = new CodeDefinition(4000, "已失效");

    /**
     * 基础校验失败
     */
    CodeDefinition BASIC_CHECK_FAIL = new CodeDefinition(4001, "基础校验失败");



}
