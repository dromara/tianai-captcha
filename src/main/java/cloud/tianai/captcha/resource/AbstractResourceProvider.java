package cloud.tianai.captcha.resource;

import cloud.tianai.captcha.resource.common.model.dto.Resource;

import java.io.InputStream;

/**
 * @Author: 天爱有情
 * @date 2021/12/16 16:52
 * @Description 抽象的ResourceProvider
 */
public abstract class AbstractResourceProvider implements ResourceProvider {
    @Override
    public InputStream getResourceInputStream(Resource data) {
        InputStream resourceInputStream = doGetResourceInputStream(data);
        if (resourceInputStream == null) {
            throw new IllegalArgumentException("滑块验证码无法读到指定的资源[" + getName() + "]" + data);
        }
        return resourceInputStream;
    }

    /**
     * 通过 Resource 获取  InputStream
     *
     * @param data data
     * @return InputStream
     */
    public abstract InputStream doGetResourceInputStream(Resource data);
}
