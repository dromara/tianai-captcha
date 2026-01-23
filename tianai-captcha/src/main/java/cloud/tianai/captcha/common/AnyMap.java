package cloud.tianai.captcha.common;

import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@EqualsAndHashCode
public class AnyMap implements Map<String, Object> {

    private final Map<String, Object> target;

    public AnyMap() {
        target = new LinkedHashMap<>();
    }

    public AnyMap(Map<String, Object> map) {
        this.target = map;
    }

    public Float getFloat(String key) {
        return getFloat(key, null);
    }

    public Float getFloat(String key, Float defaultData) {
        Object data = get(key);
        if (data != null) {
            if (data instanceof Number) {
                return ((Number) data).floatValue();
            }
            try {
                if (data instanceof String) {
                    return Float.parseFloat((String) data);
                }
            } catch (NumberFormatException e) {
                throw e;
            }
        }
        return defaultData;
    }

    public Integer getInt(String key, Integer defaultData) {
        Object data = get(key);
        if (data != null) {
            if (data instanceof Number) {
                return ((Number) data).intValue();
            }
            try {
                if (data instanceof String) {
                    return Integer.parseInt((String) data);
                }
            } catch (NumberFormatException e) {
                throw e;
            }
        }
        return defaultData;
    }

    public String getString(String key, String defaultData) {
        Object data = get(key);
        if (data != null) {
            if (data instanceof String) {
                return (String) data;
            }
            return String.valueOf(data);
        }
        return defaultData;
    }


    public static AnyMap of(Map<String, Object> map) {
        return new AnyMap(map);
    }


    public void addParam(String key, Object value) {
        put(key, value);
    }

    public Object getParam(String key) {
        return get(key);
    }

    public Object removeParam(String key) {
        return remove(key);
    }

    public <T> Object removeParam(ParamKey<T> paramKey) {
        return removeParam(paramKey.getKey());
    }

    public <T> void addParam(ParamKey<T> paramKey, T value) {
        addParam(paramKey.getKey(), value);
    }

    public <T> T getParam(ParamKey<T> paramKey) {
        return getParam(paramKey, null);
    }

    public <T> T getParam(ParamKey<T> paramKey, T defaultValue) {
        return (T) getParam(paramKey.getKey());

    }

    public <T> T getOrDefault(ParamKey<T> paramKey, T defaultValue) {
        return (T) getOrDefault(paramKey.getKey(), defaultValue);
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
