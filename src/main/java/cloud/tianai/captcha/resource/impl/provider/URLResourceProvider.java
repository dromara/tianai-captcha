package cloud.tianai.captcha.resource.impl.provider;

import cloud.tianai.captcha.resource.AbstractResourceProvider;
import cloud.tianai.captcha.resource.AbstractResourceProvider;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URL;

/**
 * @Author: 天爱有情
 * @date 2021/8/7 16:05
 * @Description url
 */
public class URLResourceProvider extends AbstractResourceProvider {

    public static final String NAME = "URL";

    @SneakyThrows
    @Override
    public InputStream doGetResourceInputStream(Resource data) {
        URL url = new URL(data.getData());
        return url.openStream();
    }

    @Override
    public boolean supported(String type) {
        return NAME.equalsIgnoreCase(type);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
