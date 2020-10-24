# 这是一个滑块验证码的实现
## 不说废话，直接上成品
![](image/1.png)
![](image/2.png)


- 该滑块验证码实现了 普通图片和 **webp**图片两种格式

- java获取滑块验证码例子


```xml
    <!-- maven 导入 -->
    <dependency>
        <groupId>cloud.tianai.captcha</groupId>
        <artifactId>tianai-captcha</artifactId>
        <version>1.1</version>
    </dependency>
```


```java
public static void main(String[] args) {
    SliderCaptchaTemplate sliderCaptchaTemplate = new SliderCaptchaTemplate();
    // 生成滑块图片
    SliderCaptchaInfo slideImageInfo = sliderCaptchaTemplate.getSlideImageInfo();
    // 获取背景图片的base64
    String backgroundImage = slideImageInfo.getBackgroundImage();
    // 获取滑块图片
    slideImageInfo.getSliderImage();
    // 获取滑块被背景图片的百分比， (校验图片使用)
    Float xPercent = slideImageInfo.getXPercent();
}
```
- 添加自定义背景图片例子
```java
addResource(getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg")));
```
- 添加自定义模板(滑块的颜色和形状)
```java
Map<String, URL> template1 = new HashMap<>(4);
template1.put(ACTIVE_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/active.png")));
template1.put(CUT_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/cut.png")));
template1.put(FIXED_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/fixed.png")));
template1.put(MATRIX_IMAGE_NAME, getClassLoader().getResource(DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/matrix.png")));
addTemplate(template1);
```
- qq群: 1021884609