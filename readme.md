<div align="center">

![][image-logo]

![star](https://gitcode.com/dromara/tianai-captcha/star/badge.svg)

### tianaiCAPTCHA - 天爱验证码(TAC)
#### 基于 JAVA实现的行为验证码
### **[在线体验 🚀][online-demo-link]**
### **[在线文档 🚀][doc-link]**
</div>


![](https://minio.tianai.cloud/public/%E6%A0%87%E9%A2%98%E5%9B%BE%E7%89%87.jpg)

## 简单介绍

- tianai-captcha 目前支持的行为验证码类型
    - 滑块验证码
    - 旋转验证码
    - 滑动还原验证码
    - 文字点选验证码
    - 后面会陆续支持市面上更多好玩的验证码玩法... 敬请期待

## 快速上手(后端)

### springboot项目

1. 导入依赖

   ```xml
   <dependency>
       <groupId>cloud.tianai.captcha</groupId>
       <artifactId>tianai-captcha-springboot-starter</artifactId>
       <version>1.5.2</version>
   </dependency>
   ```

2. 使用`ImageCaptchaApplication`生成和校验验证码

   ```java
   public class Test2 {
       @Autowired
       private ImageCaptchaApplication application;
   
       // 生成验证码 
       public void gen() {
           ApiResponse<ImageCaptchaVO> res1 = application.generateCaptcha(CaptchaTypeConstant.SLIDER);
   
           // 匹配验证码是否正确
           // 该参数包含了滑动轨迹滑动时间等数据，用于校验滑块验证码。 由前端传入
           ImageCaptchaTrack sliderCaptchaTrack = new ImageCaptchaTrack();
           ApiResponse<?> match = application.matching(res1.getId(), sliderCaptchaTrack);
       }
       
       // 校验验证码 
       public boolean valid(@RequestBody ImageCaptchaTrack captchaTrack) {
           ApiResponse<?> matching = captchaApplication.matching(data.getId(), sliderCaptchaTrack);
           return matching.isSuccess();
       }
   
   }
   ```

3. springboot配置文件说明

   ```yaml
   # 滑块验证码配置， 详细请看 cloud.tianai.captcha.autoconfiguration.ImageCaptchaProperties 类
   captcha:
     # 如果项目中使用到了redis，滑块验证码会自动把验证码数据存到redis中， 这里配置redis的key的前缀,默认是captcha:slider
     prefix: captcha
     # 验证码过期时间，默认是2分钟,单位毫秒， 可以根据自身业务进行调整
     expire:
       # 默认缓存时间 2分钟
       default: 10000
       # 针对 点选验证码 过期时间设置为 2分钟， 因为点选验证码验证比较慢，把过期时间调整大一些
       WORD_IMAGE_CLICK: 20000
     # 使用加载系统自带的资源， 默认是 false
     init-default-resource: false
     # 缓存控制， 默认为false不开启
     local-cache-enabled: true
     # 验证码会提前缓存一些生成好的验证数据， 默认是20
     local-cache-size: 20
     # 缓存拉取失败后等待时间 默认是 5秒钟
     local-cache-wait-time: 5000
     # 缓存检查间隔 默认是2秒钟
     local-cache-period: 2000
     # 配置字体库，文字点选验证码的字体库，可以配置多个
     font-path:
       - classpath:font/simhei.ttf
     secondary:
       # 二次验证， 默认false 不开启
       enabled: false
       # 二次验证过期时间， 默认 2分钟
       expire: 120000
       # 二次验证缓存key前缀，默认是 captcha:secondary
       keyPrefix: "captcha:secondary"
   ```

   


### 非spring项目

1. 导入xml

```xml
<!-- maven 导入 -->
<dependency>
    <groupId>cloud.tianai.captcha</groupId>
    <artifactId>tianai-captcha</artifactId>
    <version>1.5.2</version>
</dependency>
```

2. 构建 `ImageCaptchaApplication`负责生成和校验验证码

```java
import cloud.tianai.captcha.validator.common.model.dto.MatchParam;

public class ApplicationTest {

    public static void main(String[] args) {
        ImageCaptchaApplication application = TACBuilder.builder()
                .addDefaultTemplate() // 添加默认模板
                // 给滑块验证码 添加背景图片，宽高为600*360, Resource 参数1为 classpath/file/url , 参数2 为具体url 
                .addResource("SLIDER", new Resource("classpath", "META-INF/cut-image/resource/1.jpg")) // 滑块验证的背景图
                .addResource("WORD_IMAGE_CLICK", new Resource("classpath", "META-INF/cut-image/resource/1.jpg")) // 文字点选的背景图
                .addResource("ROTATE", new Resource("classpath", "META-INF/cut-image/resource/1.jpg")) // 旋转验证的背景图
                .build();
        // 生成验证码数据， 可以将该数据直接返回给前端 ， 可配合 tianai-captcha-web-sdk 使用
        // 支持生成 滑动验证码(SLIDER)、旋转验证码(ROTATE)、滑动还原验证码(CONCAT)、文字点选验证码(WORD_IMAGE_CLICK)
        CaptchaResponse<ImageCaptchaVO> res = application.generateCaptcha("SLIDER");
        System.out.println(res);

        // 校验验证码， ImageCaptchaTrack 和 id 均为前端传开的参数， 可将 valid数据直接返回给 前端
        // 注意: 该项目只负责生成和校验验证码数据， 至于二次验证等需要自行扩展
        String id = res.getId();
        ImageCaptchaTrack imageCaptchaTrack = null;
        ApiResponse<?> valid = application.matching(id, new MatchParam(imageCaptchaTrack));
        System.out.println(valid.isSuccess());


        // 扩展: 一个简单的二次验证
        CacheStore cacheStore = new LocalCacheStore();
        if (valid.isSuccess()) {
            // 如果验证成功，生成一个token并存储, 将该token返回给客户端，客户端下次请求数据时携带该token， 后台判断是否有效
            String token = UUID.randomUUID().toString();
            cacheStore.setCache(token, new AnyMap(), 5L, TimeUnit.MINUTES);
        }

    }
}

```


## 快速上手(前端)


| 条目     |                                                              |
| -------- | ------------------------------------------------------------ |
| 兼容性   | Chrome、Firefox、Safari、Opera、主流手机浏览器、iOS 及 Android上的内嵌Webview |
| 框架支持 | H5、Angular、React、Vue2、Vue3                               |



### 安装

1. 将打包好的`tac`目录放到自己项目中,如果是vue、react等框架，将tac目录放到public目录中、或者放到某个可以访问到地方，比如oss之类的可以被浏览器访问到的地方 （tac下载地址 [https://gitee.com/tianai/tianai-captcha-web-sdk/releases/tag/1.2](https://gitee.com/tianai/tianai-captcha-web-sdk/releases/tag/1.2)）

2. 引入初始化函数 (load.js下载地址 [https://minio.tianai.cloud/public/static/captcha/js/load.min.js](https://minio.tianai.cloud/public/static/captcha/js/load.min.js)) 可自己将load.js下载到本地

   ```html
   <script src="load.min.js"></script>
   ```

   **注:  如果是web框架，将该引入代码放到 `public/index.html`**

### 使用方法

2. 创建一个div块用于渲染验证码， 该div用于装载验证码

   ```html
    <div id="captcha-box"></div>
   ```

3. 在需要调用验证码的时候执行加载验证码方法

   ```js
   function login() {
       // config 对象为TAC验证码的一些配置和验证的回调
       const config = {
           // 生成接口 (必选项,必须配置, 要符合tianai-captcha默认验证码生成接口规范)
           requestCaptchaDataUrl: "/gen",
           // 验证接口 (必选项,必须配置, 要符合tianai-captcha默认验证码校验接口规范)
           validCaptchaUrl: "/check",
           // 验证码绑定的div块 (必选项,必须配置)
           bindEl: "#captcha-box",
           // 验证成功回调函数(必选项,必须配置)
           validSuccess: (res, c, tac) => {
                // 销毁验证码服务
               tac.destroyWindow();
               console.log("验证成功，后端返回的数据为", res);
   			// 调用具体的login方法
               login(res.data.token)
           },
           // 验证失败的回调函数(可忽略，如果不自定义 validFail 方法时，会使用默认的)
           validFail: (res, c, tac) => {
               console.log("验证码验证失败回调...")
               // 验证失败后重新拉取验证码
               tac.reloadCaptcha();
           },
           // 刷新按钮回调事件
           btnRefreshFun: (el, tac) => {
               console.log("刷新按钮触发事件...")
               tac.reloadCaptcha();
           },
           // 关闭按钮回调事件
           btnCloseFun: (el, tac) => {
               console.log("关闭按钮触发事件...")
               tac.destroyWindow();
           }
       }
       // 一些样式配置， 可不传
       let style = {
           logoUrl: null;// 去除logo    
           // logoUrl: "/xx/xx/xxx.png" // 替换成自定义的logo   
       }
       // 参数1 为 tac文件是目录地址， 目录里包含 tac的js和css等文件
       // 参数2 为 tac验证码相关配置
       // 参数3 为 tac窗口一些样式配置
       window.initTAC("./tac", config, style).then(tac => {
           tac.init(); // 调用init则显示验证码
       }).catch(e => {
           console.log("初始化tac失败", e);
       })
   }
   ```

### 对滑块的按钮和背景设置为自定义的一些样式

```js
// 这里分享一些作者自己调的样式供参考
const style =    {
    	// 按钮样式
        btnUrl: "https://minio.tianai.cloud/public/captcha-btn/btn3.png",
    	// 背景样式
        bgUrl: "https://minio.tianai.cloud/public/captcha-btn/btn3-bg.jpg",
    	// logo地址
        logoUrl: "https://minio.tianai.cloud/public/static/captcha/images/logo.png",
 		// 滑动边框样式
    	moveTrackMaskBgColor: "#f7b645",
        moveTrackMaskBorderColor: "#ef9c0d"
    }
 window.initTAC("./tac", config, style).then(tac => {
     tac.init(); // 调用init则显示验证码
 }).catch(e => {
     console.log("初始化tac失败", e);
 })
```



## 详细文档请点击 [在线文档](http://doc.captcha.tianai.cloud)

# qq群: 197340494

# 微信群:
![](https://minio.tianai.cloud/public/qun2.jpg)


## 微信群加不上的话 加微信好友 微信号: youseeseeyou-1ttd 拉你入群



[image-logo]: https://minio.tianai.cloud/public/captcha/logo/logo-519x100.png
[github-release-shield]: https://img.shields.io/github/v/release/tianaiyouqing/tianai-captcha-go?color=369eff&labelColor=black&logo=github&style=flat-square
[github-release-link]: https://github.com/tianaiyouqing/tianai-captcha-go/releases
[github-license-link]: https://github.com/tianaiyouqing/tianai-captcha-go/blob/master/LICENSE
[github-license-shield]: https://img.shields.io/badge/MulanPSL-2.0-white?labelColor=black&style=flat-square
[tianai-captcha-java-link]: https://github.com/dromara/tianai-captcha
[captcha-go-demo-link]: https://gitee.com/tianai/captcha-go-demo
[tianai-captcha-web-sdk-link]: https://github.com/tianaiyouqing/captcha-web-sdk
[online-demo-link]: http://captcha.tianai.cloud
[doc-link]: http://doc.captcha.tianai.cloud
[qrcode-link]: https://minio.tianai.cloud/public/qun4.png
