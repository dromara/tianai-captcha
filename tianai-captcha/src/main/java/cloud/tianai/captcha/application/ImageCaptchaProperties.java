package cloud.tianai.captcha.application;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: 天爱有情
 * @date 2020/10/19 18:41
 * @Description 滑块验证码属性
 */
@Data
public class ImageCaptchaProperties {
    /** 过期key prefix. */
    private String prefix = "captcha";
    /** 过期时间. */
    private Map<String, Long> expire = new HashMap<>();

    // 本地提前缓存
    private boolean localCacheEnabled = false;
    private int localCacheSize = 10;
    private int localCacheWaitTime = 1000;
    private int localCachePeriod = 5000;
    private Long localCacheExpireTime;
    private Set<String> localCacheIgnoredCacheFields;
}
