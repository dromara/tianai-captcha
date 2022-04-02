package cloud.tianai.captcha.template.slider.resource;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2021/8/7 15:26
 * @Description 滑块验证码图片资源管理器
 */
public interface SliderCaptchaResourceManager {

    /**
     * 随机获取某个模板
     *
     * @return Map<String, Resource>
     */
    Map<String, Resource> randomGetTemplate();

    /**
     * 随机获取某个资源对象
     *
     * @return Resource
     */
    Resource randomGetResource();

    /**
     * 获取真正的资源流通过资源对象
     *
     * @param resource resource
     * @return InputStream
     */
    InputStream getResourceInputStream(Resource resource);

    /**
     * 获取所有资源提供者
     *
     * @return List<ResourceProvider>
     */
    List<ResourceProvider> listResourceProviders();

    /**
     * 注册资源提供者
     *
     * @param resourceProvider 资源提供者
     */
    void registerResourceProvider(ResourceProvider resourceProvider);

    /**
     * 删除资源提供者
     *
     * @param name 资源提供者名称
     * @return ResourceProvider
     */
    boolean deleteResourceProviderByName(String name);

    /**
     * 设置资源存储
     *
     * @param resourceStore resourceStore
     */
    void setResourceStore(ResourceStore resourceStore);

    /**
     * 获取资源存储
     *
     * @return ResourceStore
     */
    ResourceStore getResourceStore();
}
