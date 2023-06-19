## 可能是开源界最好用的行为验证码工具

> (作者用爱发电，可能修复bug和更新的速度不会很频繁，检测出bug或者建议欢迎广大同学提交lssues，同时也希望广大网友们加入进来一起维护该项目，打造出开源界最好用的行为验证码工具， 同时也希望广大企业联系作者进行私人定制来请作者喝杯咖啡o(*￣3￣)o)
---
## pc版在线体验 [在线体验](http://captcha.tianai.cloud)


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

>  注意:  如果你项目是使用的**Springboot**， 
>
> 请使用SpringBoot脚手架工具[tianai-captcha-springboot-starter](https://gitee.com/tianai/tianai-captcha-springboot-starter);
>
> 该工具对tianai-captcha验证码进行了封装，使其使用更加方便快捷



>  **写好的验证码demo移步 [tianai-captcha-demo](https://gitee.com/tianai/tianai-captcha-demo)**

### 1. 导入xml

```xml
<!-- maven 导入 -->
<dependency>
    <groupId>cloud.tianai.captcha</groupId>
    <artifactId>tianai-captcha</artifactId>
    <version>1.4.1</version>
</dependency>
```

### 2. 使用 `ImageCaptchaGenerator`生成器生成验证码

```java
package example.readme;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.impl.BasicCaptchaTrackValidator;

import java.util.Map;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager,imageTransform).init(true);
        /*
                生成滑块验证码图片, 可选项
                SLIDER (滑块验证码)
                ROTATE (旋转验证码)
                CONCAT (滑动还原验证码)
                WORD_IMAGE_CLICK (文字点选验证码)

                更多验证码支持 详见 cloud.tianai.captcha.common.constant.CaptchaTypeConstant
         */
        ImageCaptchaInfo imageCaptchaInfo = imageCaptchaGenerator.generateCaptchaImage(CaptchaTypeConstant.SLIDER);
        System.out.println(imageCaptchaInfo);

        // 负责计算一些数据存到缓存中，用于校验使用
        // ImageCaptchaValidator负责校验用户滑动滑块是否正确和生成滑块的一些校验数据; 比如滑块到凹槽的百分比值
        ImageCaptchaValidator imageCaptchaValidator = new BasicCaptchaTrackValidator();
        // 这个map数据应该存到缓存中，校验的时候需要用到该数据
        Map<String, Object> map = imageCaptchaValidator.generateImageCaptchaValidData(imageCaptchaInfo);
    }
}

```

### 3. 使用`ImageCaptchaValidator`校验器 验证

```java
package example.readme;

import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.impl.BasicCaptchaTrackValidator;

import java.util.Map;

public class Test2 {
    public static void main(String[] args) {
        BasicCaptchaTrackValidator sliderCaptchaValidator = new BasicCaptchaTrackValidator();

        ImageCaptchaTrack imageCaptchaTrack = null;
        Map<String, Object> map = null;
        Float percentage = null;
        // 用户传来的行为轨迹和进行校验
        // - imageCaptchaTrack为前端传来的滑动轨迹数据
        // - map 为生成验证码时缓存的map数据
        boolean check = sliderCaptchaValidator.valid(imageCaptchaTrack, map).isSuccess();
//        // 如果只想校验用户是否滑到指定凹槽即可，也可以使用
//        // - 参数1 用户传来的百分比数据
//        // - 参数2 生成滑块是真实的百分比数据
        check = sliderCaptchaValidator.checkPercentage(0.2f, percentage);
    }
}
```

## 整体架构设计

> tianai-captcha 验证码整体分为 生成器(`ImageCaptchaGenerator`)、校验器(`ImageCaptchaValidator`)、资源管理器(`ImageCaptchaResourceManager`)
> 其中生成器、校验器、资源管理器等都是基于接口模式实现 可插拔的，可以替换为自定义实现，灵活度高

- 生成器(`ImageCaptchaGenerator`)
    - 主要负责生成行为验证码所需的图片
- 校验器(`ImageCaptchaValidator`)
    - 主要负责校验用户滑动的行为轨迹是否合规
- 资源管理器(`ImageCaptchaResourceManager`)
    - 主要负责读取验证码背景图片和模板图片等
    - 资源管理器细分为 资源存储(`ResourceStore`)、资源提供者(`ResourceProvider`)
        - 资源存储(`ResourceStore`) 负责存储背景图和模板图
        - 资源提供者(`ResourceProvider`) 负责将资源存储器中对应的资源转换为文件流
            - 一般资源存储器中存储的是图片的url地址或者id之类， 资源提供者 就是负责将url或者别的id转换为真正的图片文件
- 图片转换器 (`ImageTransform`)
  - 主要负责将图片文件流转换成字符串类型，可以是base64格式/url 或其它加密格式，默认实现是bas64格式;

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
        // 资源管理器
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        // 标准验证码生成器
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager,imageTransform).init(true);
        // 生成 具有混淆的 滑块验证码 (目前只有滑块验证码支持混淆滑块， 旋转验证，滑动还原，点选验证 均不支持混淆功能)
        ImageCaptchaInfo imageCaptchaInfo = imageCaptchaGenerator.generateCaptchaImage(GenerateParam.builder()
                .type(CaptchaTypeConstant.SLIDER)
                .sliderFormatName("png")
                .backgroundFormatName("jpeg")
                // 是否添加混淆滑块
                .obfuscate(true)
                .build());
    }
}

```

### 生成webp格式的滑块图片

```java
package example.readme;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;

public class Test4 {
    public static void main(String[] args) {
        // 资源管理器
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        // 标准验证码生成器
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager,imageTransform).init(true);
        // 生成旋转验证码 图片类型为 webp
        // 注意 tianai-captcha-1.3.2 后面默认删除了生成webp格式图片需要用户自定义添加webp转换的工具，需要用户自定义添加和扩展
        // 参考 https://bitbucket.org/luciad/webp-imageio
        ImageCaptchaInfo slideImageInfo = imageCaptchaGenerator.generateCaptchaImage(GenerateParam.builder()
                .type(CaptchaTypeConstant.ROTATE)
                .sliderFormatName("webp")
                .backgroundFormatName("webp")
                .build());
        System.out.println(slideImageInfo);
    }
}

```

### 添加自定义图片资源

- 自定义图片资源大小为 590*360 格式为jpg

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
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        // 通过资源管理器或者资源存储器
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加classpath目录下的 aa.jpg 图片
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource(ClassPathResourceProvider.NAME, "/aa.jpg"));
        // 添加远程url图片资源
        resourceStore.addResource(CaptchaTypeConstant.SLIDER,new Resource(URLResourceProvider.NAME, "http://www.xx.com/aa.jpg"));
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
        // 模板与三张图片组成 滑块、凹槽、背景图
        // 同样默认支持 classpath 和 url 两种获取图片资源， 如果想扩展可实现 ResourceProvider 接口，进行自定义扩展
    }
}
```

- 清除内置的图片资源和模板资源

 ```java
package example.readme;

import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;

public class Test6 {
    public static void main(String[] args) {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        //为方便快速上手 系统本身自带了一张图片和两套滑块模板，如果不想用系统自带的可以不让它加载系统自带的
        // 第二个构造参数设置为false时将不加载默认的图片和模板
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager, imageTransform).init(false);
    }
}

 ```

### 自定义 `imageCaptchaValidator` 校验器

```java
// 该接口负责对用户滑动验证码后传回的数据进行校验，比如滑块是否滑到指定位置，滑块行为轨迹是否正常等等
// 该接口的默认实现有 
// SimpleImageCaptchaValidator 校验用户是否滑到了指定缺口处
// BasicCaptchaTrackValidator 是对 SimpleImageCaptchaValidator增强
// BasicCaptchaTrackValidator是对SimpleImageCaptchaValidator的增强 对滑动轨迹进行了简单的验证
// 友情提示 因为BasicCaptchaTrackValidator 里面校验滑动轨迹的算法已经开源，有强制要求的建议重写该接口的方法，避免被破解
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
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager,imageTransform).init(false);
        // 注册
        imageCaptchaResourceManager.registerResourceProvider(resourceProvider);
    }
}

```

### 扩展，对`StandardImageCaptchaGenerator`增加了缓存模块

> 由于实时生成滑块图片可能会有一点性能影响，内部基于`StandardSliderCaptchaGenerator`进行了提前缓存生成好的图片，`CacheSliderCaptchaGenerator` 这只是基本的缓存逻辑，比较简单，用户可以定义一些更加有意思的扩展，用于突破性能瓶颈

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
        // 生成滑块图片
        ImageCaptchaInfo slideImageInfo = imageCaptchaGenerator.generateCaptchaImage(CaptchaTypeConstant.SLIDER);
        // 获取背景图片的base64
        String backgroundImage = slideImageInfo.getBackgroundImage();
        // 获取滑块图片
        String sliderImage = slideImageInfo.getSliderImage();
        System.out.println(slideImageInfo);
    }
}
```
# qq群: 1021884609
# 微信群: 
![](https://minio.tianai.cloud/public/qun2.jpg?a=123123)

## 微信群加不上的话 加微信好友 微信号: youseeseeyou-1ttd 拉你入群
