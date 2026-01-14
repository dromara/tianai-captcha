package cloud.tianai.captcha.spring.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class URL {

    public static final String PARAM_TAG_KEY = "tag";

    private String protocol;
    private String path;
    private Map<String, String> params;

    public String getParam(String key, String defaultValue) {
        return params.getOrDefault(key, defaultValue);
    }

    public static URL valueOf(String input) {
        // 分割协议和剩余部分
        String[] parts = input.split(":", 2);
        String protocol = parts[0];
        String remaining = parts[1];

        // 分割路径和查询参数
        String path;
        String query = null;

        if (remaining.contains("?")) {
            String[] pathQuerySplit = remaining.split("\\?", 2);
            path = pathQuerySplit[0];
            query = pathQuerySplit[1];
        } else {
            path = remaining;
        }
        if (path.startsWith("//")) {
            path = path.substring(2);
        }
        // 处理查询参数，提取键值对
        Map<String, String> queryParams = new HashMap<>();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=", 2);
                String key = keyValue[0];
                String value = keyValue.length > 1 ? keyValue[1] : "";
                queryParams.put(key, value);
            }
        }

        return new URL(protocol, path, queryParams);

    }


}
