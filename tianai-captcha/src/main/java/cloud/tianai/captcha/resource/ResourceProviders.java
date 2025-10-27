package cloud.tianai.captcha.resource;

import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import cloud.tianai.captcha.resource.impl.provider.FileResourceProvider;
import cloud.tianai.captcha.resource.impl.provider.URLResourceProvider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResourceProviders {

    private final List<ResourceProvider> resourceProviderList = new ArrayList<>(8);


    public ResourceProviders() {
        registerResourceProvider(new URLResourceProvider());
        registerResourceProvider(new ClassPathResourceProvider());
        registerResourceProvider(new FileResourceProvider());
    }

    public void registerResourceProvider(ResourceProvider resourceProvider) {
        deleteResourceProviderByName(resourceProvider.getName());
        resourceProviderList.add(resourceProvider);
    }

    public boolean deleteResourceProviderByName(String name) {
        return resourceProviderList.removeIf(r -> r.getName().equals(name));
    }

    public List<ResourceProvider> listResourceProviders() {
        return Collections.unmodifiableList(resourceProviderList);
    }


    public InputStream getResourceInputStream(Resource resource) {
        for (ResourceProvider resourceProvider : resourceProviderList) {
            if (resourceProvider.supported(resource)) {
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

}
