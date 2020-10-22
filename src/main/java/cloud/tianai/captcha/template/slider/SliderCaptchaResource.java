package cloud.tianai.captcha.template.slider;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @Author: 天爱有情
 * @date 2020/10/19 18:38
 * @Description 滑块验证码资源
 */
public interface SliderCaptchaResource {
    /**
     * 添加资源
     *
     * @param url url
     */
    void addResource(URL url);

    /**
     * 设置资源
     *
     * @param resources resources
     */
    void setResource(List<URL> resources);

    /**
     * 删除资源
     *
     * @param resource resource
     */
    void deleteResource(URL resource);

    /**
     * 读取所有资源
     *
     * @return List<URL>
     */
    List<URL> listResources();

    /**
     * 清除所有资源
     */
    void clearResources();

    /**
     * 添加模板
     *
     * @param template template
     */
    void addTemplate(Map<String, URL> template);


    /**
     * 设置模板
     *
     * @param imageTemplates imageTemplates
     */
    void setTemplates(List<Map<String, URL>> imageTemplates);

    /**
     * 删除模板
     *
     * @param template template
     */
    void deleteTemplate(Map<String, URL> template);

    /**
     * 查询所有模板
     *
     * @return List<Map < String, URL>>
     */
    List<Map<String, URL>> listTemplates();

    /**
     * 清除所有模板
     */
    void clearTemplates();
}
