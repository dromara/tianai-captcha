package cloud.tianai.captcha.resource.impl;

import cloud.tianai.captcha.common.util.CollectionUtils;
import cloud.tianai.captcha.resource.*;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import lombok.Getter;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        resourceStore = new FontCache(resourceStore);
        resourceStore.init(this);
    }

    @Override
    public ResourceMap randomGetTemplate(String type, String tag) {
        return randomGetTemplate(type, tag, 1).get(0);
    }

    @Override
    public Resource randomGetResource(String type, String tag) {
        return randomGetResource(type, tag, 1).get(0);
    }

    @Override
    public List<ResourceMap> randomGetTemplate(String type, String tag, Integer quantity) {
        List<ResourceMap> resourceMaps = resourceStore.randomGetTemplateByTypeAndTag(type, tag, quantity);
        if (CollectionUtils.isEmpty(resourceMaps) || resourceMaps.size() != quantity) {
            throw new IllegalStateException("随机获取**模板**错误，获取到的数量和指定数量不一致，" +
                    " 指定获取数量[" + quantity + "],获取到的数据:[" + Optional.ofNullable(resourceMaps).orElse(Collections.emptyList()).size() + "], " +
                    "[type:" + type + ",tag:" + tag + "]");
        }
        return resourceMaps;
    }

    @Override
    public List<Resource> randomGetResource(String type, String tag, Integer quantity) {
        List<Resource> resources = resourceStore.randomGetResourceByTypeAndTag(type, tag, quantity);
        if (CollectionUtils.isEmpty(resources) || resources.size() != quantity) {
            throw new IllegalStateException("随机获取**资源**错误，获取到的数量和指定数量不一致，" +
                    " 指定获取数量[" + quantity + "],获取到的数据:[" + Optional.ofNullable(resources).orElse(Collections.emptyList()).size() + "], " +
                    "[type:" + type + ",tag:" + tag + "]");
        }
        return resources;
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
