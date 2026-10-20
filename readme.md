> **🌏 Looking for English?** Read the **[English README](./README_EN.md)** · Online docs: **[English Documentation](http://doc.captcha.tianai.cloud/en/)**  
> **中文文档** · [在线文档](http://doc.captcha.tianai.cloud)

<div align="center">

![][image-logo]

![star](https://gitcode.com/dromara/tianai-captcha/star/badge.svg)

### tianaiCAPTCHA - 天爱验证码(TAC)
#### 基于 JAVA实现的行为验证码

**Language / 语言:** [中文](./readme.md) · [English](./README_EN.md)

### **[在线体验 🚀][online-demo-link]**
### **[在线文档 🚀][doc-link]** · **[English Docs 🚀][doc-en-link]**
</div>


![](https://minio.tianai.cloud/public/%E6%A0%87%E9%A2%98%E5%9B%BE%E7%89%87.jpg)

## 简单介绍

- tianai-captcha 目前支持的行为验证码类型
    - 滑块验证码（`SLIDER`）
    - 旋转验证码（`ROTATE`）
    - 滑动还原验证码（`CONCAT`）
    - 文字点选验证码（`WORD_IMAGE_CLICK`）
    - 工作量证明验证码（`POW`，v1.5.6 新增，无图片、前端自动求解）
    - 后面会陆续支持市面上更多好玩的验证码玩法... 敬请期待

- 完整可运行示例：[tianai-captcha-demo](https://gitee.com/tianai/tianai-captcha-demo)
- 更详细的配置说明请看 [在线文档][doc-link]

## 快速上手(后端)

当前版本：**1.5.6**

### SpringBoot 项目

1. 导入依赖

   ```xml
   <dependency>
       <groupId>cloud.tianai.captcha</groupId>
       <artifactId>tianai-captcha-springboot-starter</artifactId>
       <version>1.5.6</version>
   </dependency>
   ```

2. 最小配置（`application.yaml`）

   ```yaml
   captcha:
     # 加载系统自带滑块/旋转等模板
     init-default-resource: true
     expire:
       default: 10000
       WORD_IMAGE_CLICK: 60000
       POW: 60000
   ```

3. 使用 `ImageCaptchaApplication` 生成和校验验证码

   ```java
   @RestController
   public class CaptchaController {

       @Autowired
       private ImageCaptchaApplication application;

       /** 生成验证码，type 可选：SLIDER / ROTATE / CONCAT / WORD_IMAGE_CLICK / POW */
       @RequestMapping("/gen")
       public ApiResponse<ImageCaptchaVO> gen(@RequestParam(value = "type", required = false) String type) {
           if (type == null || type.isEmpty()) {
               type = CaptchaTypeConstant.SLIDER;
           }
           return application.generateCaptcha(type);
       }

       /** 校验验证码，data 为前端回传的轨迹 / POW 结果 */
       @PostMapping("/check")
       public ApiResponse<?> check(@RequestBody CaptchaData data) {
           ApiResponse<?> match = application.matching(data.getId(), data.getData());
           if (match.isSuccess()) {
               // 成功后把 id 回给前端，可用于二次验证 / 业务绑定
               return ApiResponse.ofSuccess(Collections.singletonMap("id", data.getId()));
           }
           return match;
       }

       @lombok.Data
       public static class CaptchaData {
           private String id;
           private ImageCaptchaTrack data;
       }
   }
   ```

4. SpringBoot 配置文件说明（常用项）

   ```yaml
   # 详细请看 cloud.tianai.captcha.spring.autoconfiguration.SpringImageCaptchaProperties
   captcha:
     # Redis key 前缀，默认 captcha（引入 spring-boot-starter-data-redis 后会自动用 Redis 存校验数据）
     prefix: captcha
     # 过期时间，单位毫秒；未配 default 时内置默认约 20 秒
     expire:
       default: 10000
       WORD_IMAGE_CLICK: 60000
       POW: 60000
     # 是否加载系统自带模板，默认 false
     init-default-resource: true
     # 本地预生成缓存，默认 false
     local-cache-enabled: true
     local-cache-size: 20
     local-cache-wait-time: 1000
     local-cache-period: 5000
     # 点选验证码字体，可配置多个，支持 ?tag=xxx
     font-path:
       - classpath:font/simhei.ttf
     secondary:
       # 二次验证，默认 false
       enabled: false
       expire: 120000
       keyPrefix: "captcha:secondary"
   ```

5. 自定义背景图（推荐）

   使用系统模板时，背景图尺寸建议 **600x360**。`POW` 不需要背景图。

   ```java
   @Configuration
   public class CaptchaResourceConfiguration {
       @Bean
       public ResourceStore resourceStore() {
           LocalMemoryResourceStore store = new LocalMemoryResourceStore();
           store.addResource(CaptchaTypeConstant.SLIDER,
                   new Resource("classpath", "bgimages/a.jpg", "default"));
           store.addResource(CaptchaTypeConstant.ROTATE,
                   new Resource("classpath", "bgimages/48.jpg", "default"));
           store.addResource(CaptchaTypeConstant.CONCAT,
                   new Resource("classpath", "bgimages/48.jpg", "default"));
           store.addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK,
                   new Resource("classpath", "bgimages/c.jpg", "default"));
           return store;
       }
   }
   ```

### 非 Spring 项目

1. 导入依赖

```xml
<dependency>
    <groupId>cloud.tianai.captcha</groupId>
    <artifactId>tianai-captcha</artifactId>
    <version>1.5.6</version>
</dependency>
```

2. 构建 `ImageCaptchaApplication`

```java
ImageCaptchaApplication application = TACBuilder.builder()
        .addDefaultTemplate()
        // 背景图宽高建议 600x360；Resource 参数1为 classpath / file / URL
        .addResource("SLIDER", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
        .addResource("WORD_IMAGE_CLICK", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
        .addResource("ROTATE", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
        .addResource("CONCAT", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
        .expire("default", 10000L)
        .expire(CaptchaTypeConstant.WORD_IMAGE_CLICK, 60000L)
        .build();

// 支持 SLIDER / ROTATE / CONCAT / WORD_IMAGE_CLICK / POW
ApiResponse<ImageCaptchaVO> res = application.generateCaptcha(CaptchaTypeConstant.SLIDER);

// id 与 ImageCaptchaTrack 均由前端回传（此处 track 仅为占位）
ImageCaptchaTrack track = null; // 实际项目中由前端 POST 传入
ApiResponse<?> valid = application.matching(res.getData().getId(), new MatchParam(track));
```

## 快速上手(前端)

| 条目     |                                                              |
| -------- | ------------------------------------------------------------ |
| 兼容性   | Chrome、Firefox、Safari、Opera、主流手机浏览器、iOS 及 Android 内嵌 WebView |
| 框架支持 | H5、Angular、React、Vue2、Vue3                               |

请保证 **前端 SDK 与后端版本一致（1.5.6）**。

### 方式一：直接引入（更容易上手）

1. 下载 tac 包：[tianai-captcha-1.5.6 / tac-web.zip](https://gitee.com/dromara/tianai-captcha/releases/download/tianai-captcha-1.5.6/tac-web.zip)  
   放到可访问目录（如 SpringBoot `static/tac`，或 Vue/React 的 `public/tac`）

2. 页面引入并初始化

```html
<link rel="stylesheet" href="/tac/css/tac.css">
<script src="/tac/js/tac.min.js"></script>
<div id="captcha-box"></div>
<script>
  new TAC({
    requestCaptchaDataUrl: "/gen?type=SLIDER",
    validCaptchaUrl: "/check",
    bindEl: "#captcha-box",
    validSuccess: (res, c, tac) => {
      tac.destroyWindow();
      // 与后端 /check 成功返回一致，业务侧拿 id
      console.log("captcha id =", res.data.id);
    },
    validFail: (res, c, tac) => tac.reloadCaptcha()
  }, {
    // logoUrl: null  // 隐藏 logo
  }).init();
</script>
```

### 方式二：load.js 按需加载（上线推荐）

首屏只引入很小的 `load.js`，用户点击登录时再拉取 `tac.min.js` / `tac.css`。

1. 同样准备好 `tac` 目录  
2. 下载 [load.js](https://minio.tianai.cloud/public/static/tac-opensoure/load.js)（或 [load.min.js](https://minio.tianai.cloud/public/static/captcha/js/load.min.js)）

```html
<script src="/js/load.js"></script>
<button id="login-btn">登录</button>
<div id="captcha-box"></div>
<script>
  document.getElementById("login-btn").onclick = function () {
    const config = {
      requestCaptchaDataUrl: "/gen?type=SLIDER",
      validCaptchaUrl: "/check",
      bindEl: "#captcha-box",
      validSuccess: (res, c, tac) => {
        tac.destroyWindow();
        console.log("验证成功", res.data.id);
      },
      validFail: (res, c, tac) => tac.reloadCaptcha()
    };
    const style = {
      logoUrl: null, // 隐藏 logo；也可写成 logoUrl: "/xx/logo.png"
    };
    // 参数1：tac 目录（需包含 js/tac.min.js、css/tac.css）
    // window.initTAC 与 window.loadTAC 等价
    window.loadTAC("./tac", config, style)
      .then(tac => tac.init())
      .catch(e => console.log("初始化失败", e));
  };
</script>
```

### 自定义样式参考

```js
const style = {
  btnUrl: "https://minio.tianai.cloud/public/captcha-btn/btn3.png",
  bgUrl: "https://minio.tianai.cloud/public/captcha-btn/btn3-bg.jpg",
  logoUrl: "https://minio.tianai.cloud/public/static/captcha/images/logo.png",
  moveTrackMaskBgColor: "#f7b645",
  moveTrackMaskBorderColor: "#ef9c0d",
  i18n: {
    slider_title: "拖动滑块完成拼图",
    pow_title: "正在验证中:",
    pow_title_size: "15px"
  }
};
```

## 工作量证明（POW）简介

```java
// 默认难度参数 [4, 16, 4]
application.generateCaptcha(CaptchaTypeConstant.POW);

// 自定义：difficulty / saltLen / count
GenerateParam param = new GenerateParam();
param.setType(CaptchaTypeConstant.POW);
param.addParam(ParamKeyEnum.POW_PARAM, Arrays.asList(4, 16, 4));
application.generateCaptcha(param);
```

前端使用 1.5.6+ SDK，生成接口返回 `type: "POW"` 即可自动求解提交，无需拖动。

---

## 详细文档请点击 [在线文档][doc-link]

# qq群: 993402836

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
[doc-en-link]: http://doc.captcha.tianai.cloud/en/
[qrcode-link]: https://minio.tianai.cloud/public/qun4.png
