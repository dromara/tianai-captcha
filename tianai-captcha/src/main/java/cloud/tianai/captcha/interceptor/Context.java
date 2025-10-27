package cloud.tianai.captcha.interceptor;

import cloud.tianai.captcha.common.AnyMap;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: 天爱有情
 * @date 2024/7/11 16:22
 * @Description 拦截器的上下文参数
 */
@Getter
public class Context {
    /** 名称. */
    private String name;
    /** 父容器. */
    @Setter
    private Context parent;
    /** 当前拦截器数量. */
    private Integer current;
    /** 拦截器总数. */
    private Integer count;
    /** 拦截器组. */
    private CaptchaInterceptor group;
    /** The previous interceptor returns data. */
    @Setter
    private Object preReturnData;
    /** 传输数据. */
    private AnyMap data = new AnyMap();

    public Context(String name, Context parent, Integer current, Integer count, CaptchaInterceptor group) {
        this.name = name;
        this.parent = parent;
        this.current = current;
        this.count = count;
        this.group = group;
    }

    public Object getPreReturnData() {
        Object returnData = preReturnData;
        if (returnData == null && parent != null) {
            returnData = parent.getPreReturnData();
        }
        return returnData;
    }

    public void putCurrentData(String key, Object value) {
        data.put(key, value);
    }

    public <T> T getCurrentData(String key, Class<T> type) {
        return convert(data.get(key), type);
    }

    public void putData(String key, Object value) {
        putCurrentData(key, value);
        if (parent != null) {
            parent.putData(key, value);
        }
    }

    public <T> T getData(String key, Class<T> type) {
        T result = getCurrentData(key, type);
        if (result == null && parent != null) {
            result = parent.getData(key, type);
        }
        return result;
    }


    private <T> T convert(Object data, Class<T> clazz) {
        if (data == null || clazz == null) {
            return null;
        }
        // 判断转换的类型是否是number类型
        return (T) data;
    }

    public Integer next() {
        current++;
        return current;
    }

    public Integer end() {
        current = count;
        return count;
    }

    public Boolean isEnd() {
        return current >= count;
    }

    public Boolean isStart() {
        return current < 0;
    }

    public void allEnd() {
        Context context = parent;
        if (context != null) {
            context.allEnd();
        }
        // 结束自身
        end();
    }
}
