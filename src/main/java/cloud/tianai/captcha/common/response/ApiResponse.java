package cloud.tianai.captcha.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 天爱有情
 * @date 2023/4/20 9:53
 * @Description API统一返回格式类
 */
@Data
@SuppressWarnings({"unchecked", "rawtypes"})
public class ApiResponse<T> implements Serializable {

    public static final ApiResponse<?> SUCCESS;

    static {
        CodeDefinition definition = ApiResponseStatusConstant.SUCCESS;
        SUCCESS = new ApiResponse(definition.getCode(), definition.getMessage(), null);
    }

    /**
     * code码.
     */
    private Integer code;
    /**
     * 信息.
     */
    private String msg;
    /**
     * 成功时返回的数据.
     */
    private T data;

    public ApiResponse(Integer code, String errMsg, T data) {
        this.code = code;
        this.msg = errMsg;
        this.data = data;
    }

    public ApiResponse(CodeDefinition definition, T data) {
        this.code = definition.getCode();
        this.msg = definition.getMessage();
        this.data = data;
    }

    public ApiResponse() {
        CodeDefinition definition = ApiResponseStatusConstant.SUCCESS;
        this.code = definition.getCode();
        this.msg = definition.getMessage();
    }

    public <R> ApiResponse<R> convert() {
        ApiResponse<R> result = new ApiResponse<>();
        result.setCode(this.getCode());
        result.setMsg(this.getMsg());
        return result;
    }


    public boolean isSuccess() {
        return ApiResponseStatusConstant.SUCCESS.getCode().equals(getCode());
    }

    public static <T> ApiResponse<T> of(Integer code, String msg, T data) {
        return new ApiResponse(code, msg, data);
    }

    public static <T> ApiResponse<T> of(CodeDefinition definition, T data) {
        return new ApiResponse(definition.getCode(), definition.getMessage(), data);
    }

    public static <T> ApiResponse<T> ofMessage(CodeDefinition definition) {
        return new ApiResponse(definition.getCode(), definition.getMessage(), null);
    }

    public static <T> ApiResponse<T> ofError(String message) {
        return new ApiResponse(ApiResponseStatusConstant.INTERNAL_SERVER_ERROR.getCode(), message, null);
    }

    public static <T> ApiResponse<T> ofError(String message, Object obj) {
        return new ApiResponse(ApiResponseStatusConstant.INTERNAL_SERVER_ERROR.getCode(), message, obj);
    }

    public static <T> ApiResponse<T> ofCheckError(String message) {
        return new ApiResponse(ApiResponseStatusConstant.NOT_VALID_PARAM.getCode(), message, null);
    }

    public static <T> ApiResponse<T> ofSuccess(T data) {
        CodeDefinition definition = ApiResponseStatusConstant.SUCCESS;
        return new ApiResponse(definition.getCode(), definition.getMessage(), data);
    }

    public static <T> ApiResponse<T> ofSuccess() {
        return (ApiResponse<T>) SUCCESS;
    }

}
