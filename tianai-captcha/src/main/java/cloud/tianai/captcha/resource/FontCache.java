package cloud.tianai.captcha.resource;

import cloud.tianai.captcha.generator.common.FontWrapper;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: 天爱有情
 * @date 2024/11/19 11:25
 * @Description 一个用于统一缓存字体文件的对象
 */
@Slf4j
public class FontCache implements ResourceStore {


    public static final String FONT_TYPE = "font";
    /**
     * 字体缓存最大数量，防止内存泄露
     */
    private static final int MAX_FONT_CACHE_SIZE = 100;
    private final Map<String, FontWrapper> fontMap = new ConcurrentHashMap<>();

    private ResourceStore resourceStore;
    private ImageCaptchaResourceManager resourceManager;

    public FontCache(ResourceStore resourceStore) {
        this.resourceStore = resourceStore;
    }

    @Override
    public void init(ImageCaptchaResourceManager resourceManager) {
        resourceStore.init(resourceManager);
        this.resourceManager = resourceManager;
    }


    public FontWrapper getFont(Resource resource) {
        try (InputStream stream = resourceManager.getResourceInputStream(resource)) {
            Font font = Font.createFont(0, stream);
            return new FontWrapper(font);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String calcId(Resource resource) {
        // 缓存id， 避免重复加载。 多个验证码可能使用同一个字体， 这里不使用资源ID作为缓存ID， 而是使用type+data作为缓存ID。
        return resource.getType() + "_" + resource.getData();
    }

    /**
     * 检查缓存大小，如果超过限制则清理
     */
    private void checkCacheSize() {
        if (fontMap.size() > MAX_FONT_CACHE_SIZE) {
            log.warn("字体缓存超过限制大小: {}，执行清理", fontMap.size());
            // 简单清理策略：清空缓存
            // 如果需要更精细的 LRU 策略，可以使用 LinkedHashMap 或第三方缓存库
            fontMap.clear();
        }
    }

    @Override
    public List<Resource> randomGetResourceByTypeAndTag(String type, String tag, Integer quantity) {
        List<Resource> resources = resourceStore.randomGetResourceByTypeAndTag(type, tag, quantity);
        // 字体增强
        if (FONT_TYPE.equalsIgnoreCase(type)) {
            // 在添加新字体前检查缓存大小
            checkCacheSize();
            for (Resource resource : resources) {
                FontWrapper fontWrapper = fontMap.computeIfAbsent(calcId(resource), v -> getFont(resource));
                resource.setExtra(fontWrapper);
            }
        }
        return resources;
    }

    @Override
    public List<ResourceMap> randomGetTemplateByTypeAndTag(String type, String tag, Integer quantity) {
        return resourceStore.randomGetTemplateByTypeAndTag(type, tag, quantity);
    }

    @Override
    public ResourceStore getTarget() {
        return resourceStore;
    }
}
