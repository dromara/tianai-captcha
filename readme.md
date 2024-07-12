## 可能是开源界最好用的行为验证码工具

-----

## pc版在线体验 [在线体验](http://captcha.tianai.cloud)

## 在线文档 [在线文档](http://doc.captcha.tianai.cloud)

![](https://minio.tianai.cloud/public/1.png)
![](https://minio.tianai.cloud/public/4.png)
![](https://minio.tianai.cloud/public/6.png)
![](https://minio.tianai.cloud/public/7.png)
![](https://minio.tianai.cloud/public/9.png)
![](https://minio.tianai.cloud/public/10.png)
![](https://minio.tianai.cloud/public/11.png)
![](https://minio.tianai.cloud/public/12.png)

## 简单介绍

- tianai-captcha 目前支持的行为验证码类型
    - 滑块验证码
    - 旋转验证码
    - 滑动还原验证码
    - 文字点选验证码
    - 后面会陆续支持市面上更多好玩的验证码玩法... 敬请期待

## 快速上手

> 注意:  如果你项目是使用的**Springboot**，
>
>
请使用SpringBoot脚手架工具[tianai-captcha-springboot-starter](https://gitee.com/tianai/tianai-captcha-springboot-starter);
>
> 该工具对tianai-captcha验证码进行了封装，使其使用更加方便快捷


> **写好的验证码demo移步 [tianai-captcha-demo](https://gitee.com/tianai/tianai-captcha-demo)**

### 1. 导入xml

```xml
<!-- maven 导入 -->
<dependency>
    <groupId>cloud.tianai.captcha</groupId>
    <artifactId>tianai-captcha</artifactId>
    <version>1.5.0.beta</version>
</dependency>
```

### 2. 构建 `ImageCaptchaApplication`负责生成和校验验证码

```java
package example.readme;

import cloud.tianai.captcha.application.DefaultImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaProperties;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.cache.impl.LocalCacheStore;
import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.interceptor.CaptchaInterceptorGroup;
import cloud.tianai.captcha.interceptor.impl.BasicTrackCaptchaInterceptor;
import cloud.tianai.captcha.interceptor.impl.ParamCheckCaptchaInterceptor;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ApplicationTest {


    public static void main(String[] args) {
        ImageCaptchaApplication application = createImageCaptchaApplication();
        // 生成验证码数据， 可以将该数据直接返回给前端 ， 可配合 tianai-captcha-web-sdk 使用
        // 支持生成 滑动验证码(SLIDER)、旋转验证码(ROTATE)、滑动还原验证码(CONCAT)、文字点选验证码(WORD_IMAGE_CLICK)
        CaptchaResponse<ImageCaptchaVO> res = application.generateCaptcha("SLIDER");
        System.out.println(res);

        // 校验验证码， ImageCaptchaTrack 和 id 均为前端传开的参数， 可将 valid数据直接返回给 前端
        // 注意: 该项目只负责生成和校验验证码数据， 至于二次验证等需要自行扩展
        String id =res.getId();
        ImageCaptchaTrack imageCaptchaTrack = null;
        ApiResponse<?> valid = application.matching(id, imageCaptchaTrack);
        System.out.println(valid.isSuccess());


        // 扩展: 一个简单的二次验证
        CacheStore cacheStore = new LocalCacheStore();
        if (valid.isSuccess()) {
            // 如果验证成功，生成一个token并存储, 将该token返回给客户端，客户端下次请求数据时携带该token， 后台判断是否有效
            String token = UUID.randomUUID().toString();
            cacheStore.setCache(token, new AnyMap(), 5L, TimeUnit.MINUTES);
        }

    }

    public static ImageCaptchaApplication createImageCaptchaApplication() {
        // 验证码资源管理器 该类负责管理验证码背景图和模板图等数据
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        // 验证码生成器； 注意: 生成器必须调用init(...)初始化方法 true为加载默认资源，false为不加载，
        ImageCaptchaGenerator generator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager).init(true);
        // 验证码校验器
        ImageCaptchaValidator imageCaptchaValidator = new SimpleImageCaptchaValidator();
        // 缓存, 用于存放校验数据
        CacheStore cacheStore = new LocalCacheStore();
        // 验证码拦截器， 可以是单个，也可以是一组拦截器，可以嵌套， 这里演示加载参数校验拦截，和 滑动轨迹拦截
        CaptchaInterceptorGroup group = new CaptchaInterceptorGroup();
        group.addInterceptor(new ParamCheckCaptchaInterceptor());
        group.addInterceptor(new BasicTrackCaptchaInterceptor());

        ImageCaptchaProperties prop = new ImageCaptchaProperties();
        // application 验证码封装， prop为所需的一些扩展参数
        ImageCaptchaApplication application = new DefaultImageCaptchaApplication(generator, imageCaptchaValidator, cacheStore, prop, group);
        return application;
    }
}

```
# qq群: 305532064

# 微信群:

<img src="https://minio.tianai.cloud/public/qun2.jpg?t=20230825" width="270px" title="微信群" />

## 微信群加不上的话 加微信好友 微信号: youseeseeyou-1ttd 拉你入群
