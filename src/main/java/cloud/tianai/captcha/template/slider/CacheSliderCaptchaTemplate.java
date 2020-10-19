package cloud.tianai.captcha.template.slider;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CacheSliderCaptchaTemplate implements SliderCaptchaTemplate {

    private final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("slider-captcha-queue"));
    private Queue<SliderCaptchaInfo> queue;
    private AtomicInteger pos = new AtomicInteger(0);
    private SliderCaptchaTemplate target;
    private int size;


    public CacheSliderCaptchaTemplate(SliderCaptchaTemplate target, int size) {
        this.target = target;
        init(size);
    }

    private void init(int z) {
        this.size = z;
        this.pos = new AtomicInteger(0);
        queue = new LinkedList<>();
        // 初始化一个队列扫描
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                while (pos.get() < this.size) {
                    if (pos.get() >= size) {
                        return;
                    }
                    SliderCaptchaInfo slideImageInfo = target.getSlideImageInfo();
                    if (slideImageInfo != null) {
                        queue.add(slideImageInfo);
                        // 添加记录
                        pos.incrementAndGet();
                    }else {
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
    }

    @SneakyThrows
    @Override
    public SliderCaptchaInfo getSlideImageInfo() {
        while (true) {
            int i = pos.get();
            if (i > 0) {
                if (pos.compareAndSet(i, i - 1)) {
                    SliderCaptchaInfo poll = queue.poll();
                    if (poll != null) {
                        return poll;
                    }
                }
            }
            // 休眠100毫秒
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }


    public static void main(String[] args) throws InterruptedException {
        SliderCaptchaTemplate captchaTemplate = new DefaultSliderCaptchaTemplate("webp", "webp", true);

        captchaTemplate = new CacheSliderCaptchaTemplate(captchaTemplate, 20);
        TimeUnit.SECONDS.sleep(5);
        for (int i = 0; i < 100; i++) {
            long start = System.currentTimeMillis();
            SliderCaptchaInfo info = captchaTemplate.getSlideImageInfo();
            long end = System.currentTimeMillis();
            System.out.println("耗时:" + (end - start));
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

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
