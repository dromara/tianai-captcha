package cloud.tianai.captcha.resource;

import cloud.tianai.captcha.generator.common.FontWrapper;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
public class FontCache implements ResourceListener {


    public static final String FONT_TYPE = "font";
    private final Map<String, FontWrapper> fontMap = new ConcurrentHashMap<>();

    private ResourceStore resourceStore;
    private ImageCaptchaResourceManager resourceManager;
    @Setter
    @Getter
    private int fontSize = 70;
    public static FontCache getInstance() {
        return INSTANCE.INSTANCE;
    }

    public FontCache() {
    }

    @Override
    public void onInit(ResourceStore resourceStore, ImageCaptchaResourceManager resourceManager) {
        this.resourceStore = resourceStore;
        this.resourceManager = resourceManager;
    }

    public FontWrapper getFont(Resource resource) {
        try (InputStream stream = resourceManager.getResourceInputStream(resource)) {
            Font font = Font.createFont(0, stream);
            return new FontWrapper(font, fontSize);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAddResource(String type, Resource resource) {
        if (FONT_TYPE.equalsIgnoreCase(type)) {
            fontMap.computeIfAbsent(resource.getId(), v -> getFont(resource));
        }
    }

    @Override
    public void onDeleteResource(String type, Resource resource) {
        if (FONT_TYPE.equalsIgnoreCase(type)) {
            fontMap.remove(resource.getId());
        }
    }

    @Override
    public void onClearAllResources() {
        fontMap.clear();
    }

    @Override
    public void onRandomGetResourceByTypeAndTag(String type, String tag, Resource resource) {
        if (FONT_TYPE.equalsIgnoreCase(type)) {
            FontWrapper fontWrapper = fontMap.computeIfAbsent(resource.getId(), v -> getFont(resource));

            resource.setExtra(fontWrapper);
        }
    }

    public void loadAllFonts() {
        List<Resource> resources = resourceStore.listResourcesByTypeAndTag(FONT_TYPE, null);
        for (Resource resource : resources) {
            fontMap.computeIfAbsent(resource.getId(), v -> getFont(resource));
        }
    }


    private static class INSTANCE {
        private static final FontCache INSTANCE = new FontCache();
    }
}
