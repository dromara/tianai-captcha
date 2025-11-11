package cloud.tianai.captcha.cache;


/**
 * 验证码缓存Key前缀处理
 *
 * @author Alay
 * @since 2025-11-11 13:44
 */
public interface StoreCacheKeyPrefix {
    /**
     * 缓存Key 计算处理
     *
     * @param captchaId 原始验证码Id
     * @return 处理后的验证码缓存Key
     */
    String compute(String captchaId);

    static StoreCacheKeyPrefix prefixed(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("prefix must not be null or empty");
        }
        return captchaId -> prefix.concat(":").concat(captchaId);
    }

}
