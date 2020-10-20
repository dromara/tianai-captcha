package cloud.tianai.captcha.template.slider;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: 天爱有情
 * @date 2020/10/20 9:23
 * @Description 滑块验证码缓冲器
 */
@Slf4j
public class CacheSliderCaptchaTemplate implements SliderCaptchaTemplate {

    private final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("slider-captcha-queue"));
    private ConcurrentLinkedQueue<SliderCaptchaInfo> queue;
    private AtomicInteger pos = new AtomicInteger(0);
    private SliderCaptchaTemplate target;
    private int size;

    public CacheSliderCaptchaTemplate(SliderCaptchaTemplate target, int size) {
        this.target = target;
        this.size = size;
    }

    /**
     * 记的初始化调度器
     */
    public void initSchedule() {
        init(size);
    }

    private void init(int z) {
        this.size = z;
        this.pos = new AtomicInteger(0);
        queue = new ConcurrentLinkedQueue<>();
        // 初始化一个队列扫描
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                while (pos.get() < this.size) {
                    if (pos.get() >= size) {
                        return;
                    }
                    SliderCaptchaInfo slideImageInfo = target.getSlideImageInfo();
                    if (slideImageInfo != null) {
                        boolean addStatus = queue.offer(slideImageInfo);
                        if (addStatus) {
                            // 添加记录
                            pos.incrementAndGet();
                        }
                    } else {
                        // 休眠500毫秒
                        try {
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (InterruptedException ignored) {
                        }
                    }

                }
            } catch (Exception e) {
                // cache所有
                log.error("缓存队列扫描时出错， ex", e);
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        log.info("缓存滑块验证码调度器初始化完成: size:{}", size);
    }

    @SneakyThrows
    @Override
    public SliderCaptchaInfo getSlideImageInfo() {
        SliderCaptchaInfo poll = queue.poll();
        if (poll == null) {
            log.warn("滑块验证码缓存不足");
            // 如果池内没数据， 则直接生成
            return target.getSlideImageInfo();
        }
        // 减1
        pos.decrementAndGet();
        return poll;
    }


//    public static void main(String[] args) throws InterruptedException {
//        SliderCaptchaTemplate captchaTemplate = new DefaultSliderCaptchaTemplate("jpeg", "png", true);
//
//        captchaTemplate = new CacheSliderCaptchaTemplate(captchaTemplate, 20);
//        TimeUnit.SECONDS.sleep(5);
//        for (int i = 0; i < 100; i++) {
//            long start = System.currentTimeMillis();
//            SliderCaptchaInfo info = captchaTemplate.getSlideImageInfo();
//            long end = System.currentTimeMillis();
//            System.out.println("耗时:" + (end - start));
//            TimeUnit.MILLISECONDS.sleep(10);
//        }
//    }

    @Override
    public void addResource(URL url) {
        target.addResource(url);
    }

    @Override
    public void addTemplate(Map<String, URL> template) {
        target.addTemplate(template);
    }

    @Override
    public void setResource(List<URL> resources) {
        target.setResource(resources);
    }

    @Override
    public void setTemplates(List<Map<String, URL>> imageTemplates) {
        target.setTemplates(imageTemplates);
    }

    @Override
    public void deleteResource(URL resource) {
        target.deleteResource(resource);
    }

    @Override
    public void deleteTemplate(Map<String, URL> template) {
        target.deleteTemplate(template);
    }

    @Override
    public boolean percentageContrast(Float newPercentage, Float oriPercentage) {
        return target.percentageContrast(newPercentage, oriPercentage);
    }
}
