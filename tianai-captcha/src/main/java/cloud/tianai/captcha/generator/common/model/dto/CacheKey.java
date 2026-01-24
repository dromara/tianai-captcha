package cloud.tianai.captcha.generator.common.model.dto;

import java.util.*;

/**
 * @Author: 天爱有情
 * @Description 缓存键包装类，支持忽略指定字段
 */
public class CacheKey {

    private final GenerateParam generateParam;
    private final Set<String> ignoredFields;

    /**
     * 构造函数
     *
     * @param generateParam 生成参数
     * @param ignoredFields 需要忽略的字段集合（不参与equals和hashCode计算）
     */
    public CacheKey(GenerateParam generateParam, Set<String> ignoredFields) {
        if (generateParam == null) {
            throw new IllegalArgumentException("generateParam 不能为 null");
        }
        this.generateParam = generateParam;
        this.ignoredFields = ignoredFields != null ? ignoredFields : Collections.emptySet();
    }

    /**
     * 获取原始的GenerateParam
     */
    public GenerateParam getGenerateParam() {
        return generateParam;
    }

    /**
     * 获取参与缓存计算的有效字段
     */
    private Map<String, Object> getEffectiveFields() {
        Map<String, Object> effectiveMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : generateParam.entrySet()) {
            if (!ignoredFields.contains(entry.getKey())) {
                effectiveMap.put(entry.getKey(), entry.getValue());
            }
        }
        return effectiveMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheKey cacheKey = (CacheKey) o;
        return Objects.equals(getEffectiveFields(), cacheKey.getEffectiveFields());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEffectiveFields());
    }

    @Override
    public String toString() {
        return "CacheKey{effectiveFields=" + getEffectiveFields() + "}";
    }
}
