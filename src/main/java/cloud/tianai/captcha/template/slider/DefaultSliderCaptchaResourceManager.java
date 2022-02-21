package cloud.tianai.captcha.template.slider;

import cloud.tianai.captcha.template.slider.provider.ClassPathResourceProvider;
import cloud.tianai.captcha.template.slider.provider.FileResourceProvider;
import cloud.tianai.captcha.template.slider.provider.URLResourceProvider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: 天爱有情
 * @date 2021/8/7 15:35
 * @Description 默认的滑块验证码资源管理
 */
public class DefaultSliderCaptchaResourceManager implements SliderCaptchaResourceManager {

    private ResourceStore resourceStore;

    private List<ResourceProvider> resourceProviderList = new ArrayList<>(8);


    public DefaultSliderCaptchaResourceManager() {
        init();
    }

    public DefaultSliderCaptchaResourceManager(ResourceStore resourceStore) {
        this.resourceStore = resourceStore;
        init();
    }

    private void init() {
        if (this.resourceStore == null) {
            this.resourceStore = new DefaultResourceStore();
        }
        // 注入一些默认的提供者
        registerResourceProvider(new URLResourceProvider());
        registerResourceProvider(new ClassPathResourceProvider());
        registerResourceProvider(new FileResourceProvider());
    }

    @Override
    public Map<String, Resource> randomGetTemplate() {
        int count = resourceStore.getTemplateCount();
        if (count < 1) {
            throw new IllegalStateException("随机获取模板错误，store中模板为空");
        }
        if (count == 1) {
            return resourceStore.getTemplateByIndex(0);
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(count);
        return resourceStore.getTemplateByIndex(randomIndex);
    }

    @Override
    public Resource randomGetResource() {
        int count = resourceStore.getResourceCount();
        if (count < 1) {
            throw new IllegalStateException("随机获取资源错误，store中资源为空");
        }
        if (count == 1) {
            return resourceStore.getResourceByIndex(0);
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(count);
        return resourceStore.getResourceByIndex(randomIndex);
    }

    @Override
    public InputStream getResourceInputStream(Resource resource) {
        for (ResourceProvider resourceProvider : resourceProviderList) {
            if (resourceProvider.supported(resource.getType())) {
                InputStream resourceInputStream = resourceProvider.getResourceInputStream(resource);
                if (resourceInputStream == null) {
                    throw new IllegalArgumentException("滑块验证码 ResourceProvider 读到的图片资源为空,providerName=["
                            + resourceProvider.getName() + "], resource=[" + resource + "]");
                }
                return resourceInputStream;
            }
        }
        throw new IllegalStateException("没有找到Resource [" + resource.getType() + "]对应的资源提供者");
    }

    @Override
    public List<ResourceProvider> listResourceProviders() {
        return Collections.unmodifiableList(resourceProviderList);
    }

    @Override
    public void registerResourceProvider(ResourceProvider resourceProvider) {
        deleteResourceProviderByName(resourceProvider.getName());
        resourceProviderList.add(resourceProvider);
    }

    @Override
    public boolean deleteResourceProviderByName(String name) {
        return resourceProviderList.removeIf(r -> r.getName().equals(name));
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
