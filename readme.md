 这是一个滑块验证码的实现
## [在线体验](https://www.tianai.cloud)
## 验证码demo移步 [tianai-captcha-demo](https://gitee.com/tianai/tianai-captcha-demo)


![](image/1.png)
![](image/2.png)
![](image/3.png)
![](image/4.png)
![](image/5.png)
![](image/6.png)

- 该滑块验证码实现了 普通图片和 **webp**图片两种格式
- java获取滑块验证码例子


##  快速上手
- 如果是SpringBoot开发者可直接使用SpringBoot快速启动器[tianai-captcha-springboot-starter](https://gitee.com/tianai/tianai-captcha-springboot-starter)

### 1. 导入xml

```xml
    <!-- maven 导入 -->
    <dependency>
        <groupId>cloud.tianai.captcha</groupId>
        <artifactId>tianai-captcha</artifactId>
        <version>1.2.7</version>
    </dependency>
```
### 2. 使用 `SliderCaptchaTemplate`获取滑块验证码

```java
public static void main(String[] args) throws InterruptedException {
    SliderCaptchaResourceManager sliderCaptchaResourceManager = new DefaultSliderCaptchaResourceManager();
    StandardSliderCaptchaTemplate sliderCaptchaTemplate = new StandardSliderCaptchaTemplate(sliderCaptchaResourceManager, true);
    // 生成滑块图片
    SliderCaptchaInfo slideImageInfo = sliderCaptchaTemplate.getSlideImageInfo();
    System.out.println(slideImageInfo);
    
    // 负责计算一些数据存到缓存中，用于校验使用
    // SliderCaptchaValidator负责校验用户滑动滑块是否正确和生成滑块的一些校验数据; 比如滑块到凹槽的百分比值
    SliderCaptchaValidator sliderCaptchaValidator = new BasicCaptchaTrackValidator();
    // 这个map数据应该存到缓存中，校验的时候需要用到该数据
    Map<String, Object> map = sliderCaptchaValidator.generateSliderCaptchaValidData(slideImageInfo);
}
```
### 3. 使用`SliderCaptchaValidator`校验

```java
SliderCaptchaValidator sliderCaptchaValidator = new BasicCaptchaTrackValidator();

// 用户传来的行为轨迹和进行校验 
// - sliderCaptchaTrack为前端传来的滑动轨迹数据
// - map 为生成验证码时缓存的map数据
boolean check = sliderCaptchaValidator.valid(sliderCaptchaTrack, map);
// 如果只想校验用户是否滑到指定凹槽即可，也可以使用
// - 参数1 用户传来的百分比数据
// - 参数2 生成滑块是真实的百分比数据
check =sliderCaptchaValidator.checkPercentage(0.2f, percentage);
```

# 常用接口

### 生成带有混淆滑块的图片 

```java
SliderCaptchaResourceManager sliderCaptchaResourceManager = new DefaultSliderCaptchaResourceManager();
StandardSliderCaptchaTemplate sliderCaptchaTemplate = new StandardSliderCaptchaTemplate(sliderCaptchaResourceManager, true);
// 生成滑块图片
SliderCaptchaInfo slideImageInfo = sliderCaptchaTemplate.getSlideImageInfo(GenerateParam.builder()
                                        .sliderFormatName("jpeg")
                                        .backgroundFormatName("png")
                                        // 是否添加混淆滑块
                                        .obfuscate(true)
                                        .build());
```

### 生成webp格式的滑块图片

```java
SliderCaptchaResourceManager sliderCaptchaResourceManager = new DefaultSliderCaptchaResourceManager();
StandardSliderCaptchaTemplate sliderCaptchaTemplate = new StandardSliderCaptchaTemplate(sliderCaptchaResourceManager, true);
// 生成滑块图片
SliderCaptchaInfo slideImageInfo = sliderCaptchaTemplate.getSlideImageInfo(GenerateParam.builder()
                                        .sliderFormatName("webp")
                                        .backgroundFormatName("webp")
                                        // 是否添加混淆滑块
                                        .obfuscate(false)
                                        .build());
```

### 添加自定义图片资源

- 自定义图片资源大小为 590*360 格式为jpg

```java
  ResourceStore resourceStore = sliderCaptchaResourceManager.getResourceStore();
  // 添加classpath目录下的 aa.jpg 图片      
  resourceStore.addResource(new Resource(ClassPathResourceProvider.NAME, "/aa.jpg"));
  // 添加远程url图片资源
  resourceStore.addResource(new Resource(URLResourceProvider.NAME, "http://www.xx.com/aa.jpg"));
  // 内置了通过url 和 classpath读取图片资源，如果想扩展可实现 ResourceProvider 接口，进行自定义扩展
```
### 添加自定义模板资源

- 系统内置了2套模板，可以到QQ群:1021884609 文件中获取更多模板或者自己制作模板
- 模板图片格式
  - 滑块大小为 110*110 格式为png
  - 凹槽大小为 110*110 格式为png
  - 模板大小为 110*360 格式为png，该图为固定格式，是一张纯透明图片

```java
  ResourceStore resourceStore = sliderCaptchaResourceManager.getResourceStore();=
  Map<String, Resource> template1 = new HashMap<>(4);
  template1.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME,"/active.png"));
  template1.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, "/fixed.png"));
  template1.put(SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, "/matrix.png"));
  resourceStore.addTemplate(template1);

  // 模板与三张图片组成 滑块、凹槽、背景图 
  // 同样默认支持 classpath 和 url 两种获取图片资源， 如果想扩展可实现 ResourceProvider 接口，进行自定义扩展
```
- 清除内置的图片资源和模板资源
 ```java
    //为方便快速上手 系统本身自带了一张图片和两套滑块模板，如果不想用系统自带的可以不让它加载系统自带的
    // 第二个构造参数设置为false时将不加载默认的图片和模板
    SliderCaptchaTemplate sliderCaptchaTemplate = new DefaultSliderCaptchaTemplate(sliderCaptchaResourceManager, false);
 ```

### 自定义 `SliderCaptchaValidator` 校验器

```java
// 该接口负责对用户滑动验证码后传回的数据进行校验，比如滑块是否滑到指定位置，滑块行为轨迹是否正常等等
// 该接口的默认实现有 
// SimpleSliderCaptchaValidator 校验用户是否滑到了指定缺口处
// BasicCaptchaTrackValidator 是对 SimpleSliderCaptchaValidator增强
// BasicCaptchaTrackValidator是对SimpleSliderCaptchaValidator的增强 对滑动轨迹进行了简单的验证
// 友情提示 因为BasicCaptchaTrackValidator 里面校验滑动轨迹的算法已经开源，有强制要求的建议重写该接口的方法，避免被破解
```

### 自定义 `ResourceProvider` 实现自定义文件读取策略， 比如 oss之类的

```java
  // 实现了 ResourceProvider 后
  SliderCaptchaResourceManager sliderCaptchaResourceManager = new DefaultSliderCaptchaResourceManager();
  StandardSliderCaptchaTemplate sliderCaptchaTemplate = new StandardSliderCaptchaTemplate(sliderCaptchaResourceManager, true);
  // 注册
  sliderCaptchaResourceManager.registerResourceProvider(new CustomResourceProvider());
```
### 扩展，对`StandardSliderCaptchaTemplate`增加了缓存模块

```java
public static void main(String[] args) throws InterruptedException {
    // 使用 CacheSliderCaptchaTemplate 对滑块验证码进行缓存，使其提前生成滑块图片
    // 参数一: 真正实现 滑块的 SliderCaptchaTemplate
    // 参数二: 默认提前缓存多少个
    // 参数三: 出错后 等待xx时间再进行生成
    // 参数四: 检查时间间隔    
    SliderCaptchaResourceManager sliderCaptchaResourceManager = new DefaultSliderCaptchaResourceManager();
    DefaultSliderCaptchaTemplate sliderCaptchaTemplate = new CacheSliderCaptchaTemplate(new StandardSliderCaptchaTemplate(sliderCaptchaResourceManager, true), 10, 1000, 100);
    // 生成滑块图片
    SliderCaptchaInfo slideImageInfo = sliderCaptchaTemplate.getSlideImageInfo();
    // 获取背景图片的base64
    String backgroundImage = slideImageInfo.getBackgroundImage();
    // 获取滑块图片
    slideImageInfo.getSliderImage();

    System.out.println(slideImageInfo);
}
```

# qq群: 1021884609