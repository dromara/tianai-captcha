## 可能是开源界最好用的行为验证码工具

> 重大更新，1.5.0版本， 与1.4.x的版本不兼容，请谨慎升级
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

## 扩展

### 生成带有混淆滑块的图片

```java
package example.readme;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;

public class Test3 {
    public static void main(String[] args) {
        // application 为 ImageCaptchaApplication对象
        // 生成 具有混淆的 滑块验证码 (目前只有滑块验证码支持混淆滑块， 旋转验证，滑动还原，点选验证 均不支持混淆功能)
        ImageCaptchaInfo imageCaptchaInfo = application.generateCaptcha(GenerateParam.builder()
                .type(CaptchaTypeConstant.SLIDER)
                // 是否添加混淆滑块
                .obfuscate(true)
                .build());
    }
}

```

### 添加自定义图片资源

- 自定义图片资源大小为 600*360 格式为jpg

```java
package example.readme;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import cloud.tianai.captcha.resource.impl.provider.URLResourceProvider;

public class Test5 {
    public static void main(String[] args) {
        // 在资源管理器中设置自定义图片资源
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        // 通过资源管理器或者资源存储器
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加classpath目录下的 aa.jpg 图片
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource(ClassPathResourceProvider.NAME, "/aa.jpg"));
        // 添加远程url图片资源
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource(URLResourceProvider.NAME, "http://www.xx.com/aa.jpg"));
        // 内置了通过url 和 classpath读取图片资源，如果想扩展可实现 ResourceProvider 接口，进行自定义扩展
    }
}

```

### 添加自定义模板资源

- 系统内置了2套模板，可以到QQ群:1021884609 文件中获取更多模板或者自己制作模板
- 模板图片格式
    - 滑块验证码
        - 滑块大小为 110*110 格式为png
        - 凹槽大小为 110*110 格式为png
    - 旋转验证码
        - 滑块大小为 200*200 格式为png
        - 凹槽大小为 200*200 格式为png

```java
package example.readme;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.common.constant.SliderCaptchaConstant;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;

public class Test6 {
    public static void main(String[] args) {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        // 通过资源管理器或者资源存储器
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加滑块验证码模板.模板图片由三张图片组成
        ResourceMap template1 = new ResourceMap("default", 4);
        template1.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, "/active.png"));
        template1.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, "/fixed.png"));
        resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template1);
        // 模板与两张图片组成 滑块、凹槽
        // 同样默认支持 classpath 和 url 两种获取图片资源， 如果想扩展可实现 ResourceProvider 接口，进行自定义扩展
    }
}
```

### 自定义 `ResourceProvider` 实现自定义文件读取策略， 比如 oss之类的

```java
package example.readme;

import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceProvider;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;

import java.io.InputStream;

public class Test7 {
    public static void main(String[] args) {
        // 自定义 ResourceProvider
        ResourceProvider resourceProvider = new ResourceProvider() {
            @Override
            public InputStream getResourceInputStream(Resource data) {
                return null;
            }

            @Override
            public boolean supported(String type) {
                return false;
            }

            @Override
            public String getName() {
                return null;
            }
        };
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager, imageTransform).init(false);
        // 注册
        imageCaptchaResourceManager.registerResourceProvider(resourceProvider);
    }
}

```

### 扩展，对`StandardImageCaptchaGenerator`增加了缓存模块

> 由于实时生成滑块图片可能会有一点性能影响，内部基于`StandardSliderCaptchaGenerator`进行了提前缓存生成好的图片，
> `CacheSliderCaptchaGenerator` 是一个装饰类，这只是基本的缓存逻辑，比较简单，用户可以定义一些更加有意思的扩展，用于突破性能瓶颈

```java
package example.readme;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.CacheImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;

public class Test8 {
    public static void main(String[] args) throws InterruptedException {
        // 使用 CacheSliderCaptchaGenerator 对滑块验证码进行缓存，使其提前生成滑块图片
        // 参数一: 真正实现 滑块的 SliderCaptchaGenerator
        // 参数二: 默认提前缓存多少个
        // 参数三: 出错后 等待xx时间再进行生成
        // 参数四: 检查时间间隔
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        ImageCaptchaGenerator imageCaptchaGenerator = new CacheImageCaptchaGenerator(new MultiImageCaptchaGenerator(imageCaptchaResourceManager, imageTransform), 10, 1000, 100);
        imageCaptchaGenerator.init(true);
    }
}
```
### 验证码拦截器

> 如有需要对验证码进行增强或拦截之类的功能，可以通过实现`ImageCaptchaInterceptor`接口，然后注册到`ImageCaptchaApplication`中
> 拦截器内具体执行顺序和功能请查看代码注释

# qq群: 305532064

# 微信群:

<img src="https://minio.tianai.cloud/public/qun2.jpg?t=20230825" width="270px" title="微信群" />

## 微信群加不上的话 加微信好友 微信号: youseeseeyou-1ttd 拉你入群
