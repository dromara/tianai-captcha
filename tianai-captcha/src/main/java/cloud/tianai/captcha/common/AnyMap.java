package cloud.tianai.captcha.common;

import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @Author: 天爱有情
 * @Description 通用Map包装类，提供类型安全的get/set方法
 */
@EqualsAndHashCode
public class AnyMap implements Map<String, Object> {

    private final Map<String, Object> target;

    public AnyMap() {
        target = new LinkedHashMap<>();
    }

    /**
     * 构造函数 - 防御性拷贝
     * @param map 源Map（会被复制，不会共享引用）
     */
    public AnyMap(Map<String, Object> map) {
        this.target = map != null ? new LinkedHashMap<>(map) : new LinkedHashMap<>();
    }

    // ==================  类型转换方法 =======================

    /**
     * 获取Float值
     */
    public Float getFloat(String key) {
        return getFloat(key, null);
    }

    /**
     * 获取Float值，支持默认值
     */
    public Float getFloat(String key, Float defaultValue) {
        return convertToNumber(key, defaultValue, Number::floatValue, Float::parseFloat);
    }

    /**
     * 获取Integer值
     */
    public Integer getInt(String key) {
        return getInt(key, null);
    }

    /**
     * 获取Integer值，支持默认值
     */
    public Integer getInt(String key, Integer defaultValue) {
        return convertToNumber(key, defaultValue, Number::intValue, Integer::parseInt);
    }

    /**
     * 获取Long值
     */
    public Long getLong(String key) {
        return getLong(key, null);
    }

    /**
     * 获取Long值，支持默认值
     */
    public Long getLong(String key, Long defaultValue) {
        return convertToNumber(key, defaultValue, Number::longValue, Long::parseLong);
    }

    /**
     * 获取Double值
     */
    public Double getDouble(String key) {
        return getDouble(key, null);
    }

    /**
     * 获取Double值，支持默认值
     */
    public Double getDouble(String key, Double defaultValue) {
        return convertToNumber(key, defaultValue, Number::doubleValue, Double::parseDouble);
    }

    /**
     * 获取Boolean值
     */
    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    /**
     * 获取Boolean值，支持默认值
     */
    public Boolean getBoolean(String key, Boolean defaultValue) {
        Object data = get(key);
        if (data == null) {
            return defaultValue;
        }
        if (data instanceof Boolean) {
            return (Boolean) data;
        }
        if (data instanceof String) {
            return Boolean.parseBoolean((String) data);
        }
        if (data instanceof Number) {
            return ((Number) data).intValue() != 0;
        }
        return defaultValue;
    }

    /**
     * 获取String值
     */
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * 获取String值，支持默认值
     */
    public String getString(String key, String defaultValue) {
        Object data = get(key);
        if (data == null) {
            return defaultValue;
        }
        if (data instanceof String) {
            return (String) data;
        }
        return String.valueOf(data);
    }

    /**
     * 通用数字类型转换方法（减少重复代码）
     */
    private <T extends Number> T convertToNumber(
            String key,
            T defaultValue,
            Function<Number, T> numberConverter,
            Function<String, T> stringParser) {
        Object data = get(key);
        if (data == null) {
            return defaultValue;
        }
        if (data instanceof Number) {
            return numberConverter.apply((Number) data);
        }
        if (data instanceof String) {
            try {
                return stringParser.apply((String) data);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    // ==================  ParamKey 相关方法 =======================

    /**
     * 添加参数（使用ParamKey）
     */
    public <T> void addParam(ParamKey<T> paramKey, T value) {
        put(paramKey.getKey(), value);
    }

    /**
     * 获取参数（使用ParamKey）
     */
    public <T> T getParam(ParamKey<T> paramKey) {
        return getParam(paramKey, null);
    }

    /**
     * 获取参数（使用ParamKey），支持默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getParam(ParamKey<T> paramKey, T defaultValue) {
        Object value = get(paramKey.getKey());
        return value != null ? (T) value : defaultValue;
    }

    /**
     * 移除参数（使用ParamKey）
     */
    public <T> Object removeParam(ParamKey<T> paramKey) {
        return remove(paramKey.getKey());
    }

    /**
     * 获取参数或默认值（使用ParamKey）
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(ParamKey<T> paramKey, T defaultValue) {
        return (T) getOrDefault(paramKey.getKey(), defaultValue);
    }

    // ==================  便捷方法 =======================

    /**
     * 添加参数（String key）
     * 注意：这个方法等价于put()，保留是为了向后兼容
     */
    public void addParam(String key, Object value) {
        put(key, value);
    }

    /**
     * 获取参数（String key）
     * 注意：这个方法等价于get()，保留是为了向后兼容
     */
    public Object getParam(String key) {
        return get(key);
    }

    /**
     * 移除参数（String key）
     * 注意：这个方法等价于remove()，保留是为了向后兼容
     */
    public Object removeParam(String key) {
        return remove(key);
    }

    /**
     * 链式调用 - 设置值并返回this
     * @param key 键
     * @param value 值
     * @return this，支持链式调用
     */
    public AnyMap set(String key, Object value) {
        put(key, value);
        return this;
    }

    /**
     * 链式调用 - 使用ParamKey设置值
     */
    public <T> AnyMap set(ParamKey<T> paramKey, T value) {
        put(paramKey.getKey(), value);
        return this;
    }

    /**
     * 静态工厂方法
     */
    public static AnyMap of(Map<String, Object> map) {
        return new AnyMap(map);
    }

    /**
     * 静态工厂方法 - 创建空Map
     */
    public static AnyMap create() {
        return new AnyMap();
    }

    // ==================  implement Map =======================

    @Override
    public int size() {
        return target.size();
    }

    @Override
    public boolean isEmpty() {
        return target.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return target.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return target.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return target.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return target.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return target.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        target.putAll(m);
    }

    @Override
    public void clear() {
        target.clear();
    }

    @Override
    public Set<String> keySet() {
        return target.keySet();
    }

    @Override
    public Collection<Object> values() {
        return target.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return target.entrySet();
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        return target.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Object> action) {
        target.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
        target.replaceAll(function);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        return target.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return target.remove(key, value);
    }

    @Override
    public boolean replace(String key, Object oldValue, Object newValue) {
        return target.replace(key, oldValue, newValue);
    }

    @Override
    public Object replace(String key, Object value) {
        return target.replace(key, value);
    }

    @Override
    public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
        return target.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return target.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return target.compute(key, remappingFunction);
    }

    @Override
    public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return target.merge(key, value, remappingFunction);
    }
}
