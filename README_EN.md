> [中文文档](./readme.md) | **English**

<div align="center">

![][image-logo]

![star](https://gitcode.com/dromara/tianai-captcha/star/badge.svg)

### tianaiCAPTCHA (TAC)
#### Behavioral CAPTCHA for Java
### **[Live Demo 🚀][online-demo-link]**
### **[Documentation 🚀][doc-link]**
</div>


![](https://minio.tianai.cloud/public/%E6%A0%87%E9%A2%98%E5%9B%BE%E7%89%87.jpg)

## Introduction

- Supported CAPTCHA types:
    - Slider puzzle (`SLIDER`)
    - Rotation (`ROTATE`)
    - Concat / swipe-to-restore (`CONCAT`)
    - Word image click (`WORD_IMAGE_CLICK`)
    - Proof of Work (`POW`, added in v1.5.6 — no image, solved automatically in the browser)
    - More types coming soon...

- Runnable demo: [tianai-captcha-demo](https://gitee.com/tianai/tianai-captcha-demo)
- Full configuration guide: [Online Docs][doc-link]

## Quick Start (Backend)

Current version: **1.5.6**

### Spring Boot

1. Add dependency

   ```xml
   <dependency>
       <groupId>cloud.tianai.captcha</groupId>
       <artifactId>tianai-captcha-springboot-starter</artifactId>
       <version>1.5.6</version>
   </dependency>
   ```

2. Minimal `application.yaml`

   ```yaml
   captcha:
     # Load built-in slider/rotate templates
     init-default-resource: true
     expire:
       default: 10000
       WORD_IMAGE_CLICK: 60000
       POW: 60000
   ```

3. Generate and validate with `ImageCaptchaApplication`

   ```java
   @RestController
   public class CaptchaController {

       @Autowired
       private ImageCaptchaApplication application;

       /** type: SLIDER / ROTATE / CONCAT / WORD_IMAGE_CLICK / POW */
       @RequestMapping("/gen")
       public ApiResponse<ImageCaptchaVO> gen(@RequestParam(value = "type", required = false) String type) {
           if (type == null || type.isEmpty()) {
               type = CaptchaTypeConstant.SLIDER;
           }
           return application.generateCaptcha(type);
       }

       /** `data` is the track / POW payload from the frontend */
       @PostMapping("/check")
       public ApiResponse<?> check(@RequestBody CaptchaData data) {
           ApiResponse<?> match = application.matching(data.getId(), data.getData());
           if (match.isSuccess()) {
               // Return id for secondary verification / business binding
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

4. Common Spring Boot options

   ```yaml
   # See cloud.tianai.captcha.spring.autoconfiguration.SpringImageCaptchaProperties
   captcha:
     # Redis key prefix (default: captcha). With spring-boot-starter-data-redis, Redis is used automatically
     prefix: captcha
     # TTL in milliseconds; built-in default is ~20s if expire.default is omitted
     expire:
       default: 10000
       WORD_IMAGE_CLICK: 60000
       POW: 60000
     # Load built-in templates (default: false)
     init-default-resource: true
     # Local pre-generate cache (default: false)
     local-cache-enabled: true
     local-cache-size: 20
     local-cache-wait-time: 1000
     local-cache-period: 5000
     # Fonts for word-click CAPTCHA; supports ?tag=xxx
     font-path:
       - classpath:font/simhei.ttf
     secondary:
       # Secondary verification (default: false)
       enabled: false
       expire: 120000
       keyPrefix: "captcha:secondary"
   ```

5. Custom background images (recommended)

   When using built-in templates, background size should be **600x360**. `POW` does not need backgrounds.

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

### Non-Spring projects

1. Dependency

```xml
<dependency>
    <groupId>cloud.tianai.captcha</groupId>
    <artifactId>tianai-captcha</artifactId>
    <version>1.5.6</version>
</dependency>
```

2. Build `ImageCaptchaApplication`

```java
ImageCaptchaApplication application = TACBuilder.builder()
        .addDefaultTemplate()
        // Preferred background size: 600x360; Resource arg1 = classpath / file / URL
        .addResource("SLIDER", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
        .addResource("WORD_IMAGE_CLICK", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
        .addResource("ROTATE", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
        .addResource("CONCAT", new Resource("classpath", "META-INF/cut-image/resource/1.jpg"))
        .expire("default", 10000L)
        .expire(CaptchaTypeConstant.WORD_IMAGE_CLICK, 60000L)
        .build();

// Supports SLIDER / ROTATE / CONCAT / WORD_IMAGE_CLICK / POW
ApiResponse<ImageCaptchaVO> res = application.generateCaptcha(CaptchaTypeConstant.SLIDER);

// Both id and ImageCaptchaTrack come from the frontend (track is a placeholder here)
ImageCaptchaTrack track = null; // In real apps, this is posted by the client
ApiResponse<?> valid = application.matching(res.getData().getId(), new MatchParam(track));
```

## Quick Start (Frontend)

| Item | |
| ---- | ---- |
| Compatibility | Chrome, Firefox, Safari, Opera, major mobile browsers, iOS / Android WebView |
| Frameworks | H5, Angular, React, Vue2, Vue3 |

Keep the **frontend SDK version aligned with the backend (1.5.6)**.

### Option 1: Direct include (easiest to start)

1. Download the tac package: [tianai-captcha-1.5.6 / tac-web.zip](https://gitee.com/dromara/tianai-captcha/releases/download/tianai-captcha-1.5.6/tac-web.zip)  
   Put it somewhere publicly accessible (e.g. Spring Boot `static/tac`, or Vue/React `public/tac`)

2. Include and init

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
      // Matches backend /check success payload — use id in your business flow
      console.log("captcha id =", res.data.id);
    },
    validFail: (res, c, tac) => tac.reloadCaptcha()
  }, {
    // logoUrl: null  // hide logo
  }).init();
</script>
```

### Option 2: On-demand loading with `load.js` (recommended for production)

Only load a tiny `load.js` on first paint; fetch `tac.min.js` / `tac.css` when the user clicks login.

1. Prepare the same `tac` directory  
2. Download [load.js](https://minio.tianai.cloud/public/static/tac-opensoure/load.js) (or [load.min.js](https://minio.tianai.cloud/public/static/captcha/js/load.min.js))

```html
<script src="/js/load.js"></script>
<button id="login-btn">Login</button>
<div id="captcha-box"></div>
<script>
  document.getElementById("login-btn").onclick = function () {
    const config = {
      requestCaptchaDataUrl: "/gen?type=SLIDER",
      validCaptchaUrl: "/check",
      bindEl: "#captcha-box",
      validSuccess: (res, c, tac) => {
        tac.destroyWindow();
        console.log("success", res.data.id);
      },
      validFail: (res, c, tac) => tac.reloadCaptcha()
    };
    const style = {
      logoUrl: null, // hide logo; or logoUrl: "/xx/logo.png"
    };
    // Arg1: tac directory (must contain js/tac.min.js and css/tac.css)
    // window.initTAC is an alias of window.loadTAC
    window.loadTAC("./tac", config, style)
      .then(tac => tac.init())
      .catch(e => console.log("init failed", e));
  };
</script>
```

### Style examples

```js
const style = {
  btnUrl: "https://minio.tianai.cloud/public/captcha-btn/btn3.png",
  bgUrl: "https://minio.tianai.cloud/public/captcha-btn/btn3-bg.jpg",
  logoUrl: "https://minio.tianai.cloud/public/static/captcha/images/logo.png",
  moveTrackMaskBgColor: "#f7b645",
  moveTrackMaskBorderColor: "#ef9c0d",
  i18n: {
    slider_title: "Drag the slider to complete the puzzle",
    pow_title: "Verifying...",
    pow_title_size: "15px"
  }
};
```

## Proof of Work (POW)

```java
// Default params [4, 16, 4]
application.generateCaptcha(CaptchaTypeConstant.POW);

// Custom: difficulty / saltLen / count
GenerateParam param = new GenerateParam();
param.setType(CaptchaTypeConstant.POW);
param.addParam(ParamKeyEnum.POW_PARAM, Arrays.asList(4, 16, 4));
application.generateCaptcha(param);
```

With frontend SDK 1.5.6+, returning `type: "POW"` is enough — the SDK solves and submits automatically (no dragging).

---

## Full docs: [Online Documentation][doc-link]

# QQ Group: 993402836

# WeChat Group:
![](https://minio.tianai.cloud/public/qun2.jpg)


## If you cannot join the WeChat group, add WeChat: youseeseeyou-1ttd



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
