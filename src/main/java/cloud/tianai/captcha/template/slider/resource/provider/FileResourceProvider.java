package cloud.tianai.captcha.template.slider.resource.provider;

import cloud.tianai.captcha.template.slider.resource.Resource;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Author: 天爱有情
 * @date 2022/2/21 14:43
 * @Description file
 */
public class FileResourceProvider extends AbstractResourceProvider {

    public static final String NAME = "file";

    @SneakyThrows
    @Override
    public InputStream doGetResourceInputStream(Resource data) {
        FileInputStream fileInputStream = new FileInputStream(data.getData());
        return fileInputStream;
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
