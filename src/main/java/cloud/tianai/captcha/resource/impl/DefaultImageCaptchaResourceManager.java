package cloud.tianai.captcha.resource.impl;

import cloud.tianai.captcha.resource.*;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import lombok.Getter;

import java.io.InputStream;
import java.util.List;

/**
 * @Author: 天爱有情
 * @date 2021/8/7 15:35
 * @Description 默认的滑块验证码资源管理
 */
public class DefaultImageCaptchaResourceManager implements ImageCaptchaResourceManager {

    /** 资源存储. */
    private ResourceStore resourceStore;
    /** 资源转换 转换为stream流. */
    @Getter
    private ResourceProviders resourceProviders;

    public DefaultImageCaptchaResourceManager() {
        init();
    }

    public DefaultImageCaptchaResourceManager(ResourceStore resourceStore, ResourceProviders resourceProviders) {
        this.resourceStore = resourceStore;
        this.resourceProviders = resourceProviders;
        init();
    }

    private void init() {
        if (this.resourceStore == null) {
            this.resourceStore = new LocalMemoryResourceStore();
        }
        // 在这里临时加上字体缓存器
        resourceStore.addListener(FontCache.getInstance());
        resourceStore.init(this);
    }

    @Override
    public ResourceMap randomGetTemplate(String type, String tag) {
        ResourceMap resourceMap = resourceStore.randomGetTemplateByTypeAndTag(type, tag);
        if (resourceMap == null) {
            throw new IllegalStateException("随机获取模板错误，store中模板为空, type:" + type);
        }
        return resourceMap;
    }

    @Override
    public Resource randomGetResource(String type, String tag) {
        Resource resource = resourceStore.randomGetResourceByTypeAndTag(type, tag);
        if (resource == null) {
            throw new IllegalStateException("随机获取资源错误，store中资源为空, type:" + type);
        }
        return resource;
    }

    @Override
    public InputStream getResourceInputStream(Resource resource) {
        return resourceProviders.getResourceInputStream(resource);
    }

    @Override
    public List<ResourceProvider> listResourceProviders() {
        return resourceProviders.listResourceProviders();
    }

    @Override
    public void registerResourceProvider(ResourceProvider resourceProvider) {
        resourceProviders.registerResourceProvider(resourceProvider);
    }

    @Override
    public boolean deleteResourceProviderByName(String name) {
        return resourceProviders.deleteResourceProviderByName(name);
    }

    @Override
    public void setResourceStore(ResourceStore resourceStore) {
        this.resourceStore = resourceStore;
    }

    @Override
    public ResourceStore getResourceStore() {
        return resourceStore;
    }


}
