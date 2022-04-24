package cloud.tianai.captcha.template.slider.generator.impl;

import cloud.tianai.captcha.template.slider.common.util.NamedThreadFactory;
import cloud.tianai.captcha.template.slider.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: 天爱有情
 * @date 2020/10/20 9:23
 * @Description 滑块验证码缓冲器
 */
@Slf4j
public class CacheImageCaptchaGenerator implements ImageCaptchaGenerator {

    protected final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("slider-captcha-queue"));
    protected Map<GenerateParam, ConcurrentLinkedQueue<ImageCaptchaInfo>> queueMap = new ConcurrentHashMap<>(8);
    protected Map<GenerateParam, AtomicInteger> posMap = new ConcurrentHashMap<>(8);
    protected ImageCaptchaGenerator target;
    protected int size;
    /** 等待时间，一般报错或者拉取为空时会休眠一段时间再试. */
    protected int waitTime = 1000;
    /** 调度器检查缓存的间隔时间. */
    protected int period = 100;

    @Getter
    @Setter
    protected boolean requiredGetCaptcha = true;

    public CacheImageCaptchaGenerator(ImageCaptchaGenerator target,  int size) {
        this.target = target;
        this.size = size;
    }

    public CacheImageCaptchaGenerator(ImageCaptchaGenerator target, int size, int waitTime, int period) {
        this.target = target;
        this.size = size;
        this.waitTime = waitTime;
        this.period = period;
    }

    /**
     * 记的初始化调度器
     */
    public void initSchedule() {
        init(size);
    }

    private void init(int z) {
        this.size = z;
        // 初始化一个队列扫描
        scheduledExecutor.scheduleAtFixedRate(() -> {
            queueMap.forEach((k, queue) -> {
                try {
                    AtomicInteger pos = posMap.computeIfAbsent(k, k1 -> new AtomicInteger(0));
                    while (pos.get() < this.size) {
                        if (pos.get() >= size) {
                            return;
                        }
                        ImageCaptchaInfo slideImageInfo = target.generateCaptchaImage(k);
                        if (slideImageInfo != null) {
                            boolean addStatus = queue.offer(slideImageInfo);
                            if (addStatus) {
                                // 添加记录
                                pos.incrementAndGet();
                            }
                        } else {
                            sleep();
                        }
                    }
                } catch (Exception e) {
                    // cache所有
                    log.error("缓存队列扫描时出错， ex", e);
                    // 休眠
                    sleep();
                }
            });

        }, 0, period, TimeUnit.MILLISECONDS);
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(waitTime);
        } catch (InterruptedException ignored) {
        }
    }

    @SneakyThrows
    @Override
    public ImageCaptchaInfo generateCaptchaImage(String type) {
        GenerateParam generateParam = new GenerateParam();
        generateParam.setType(type);
        return generateCaptchaImage(generateParam, this.requiredGetCaptcha);
    }

    @SneakyThrows
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam generateParam, boolean requiredGetCaptcha) {
        ConcurrentLinkedQueue<ImageCaptchaInfo> queue = queueMap.computeIfAbsent(generateParam, g -> new ConcurrentLinkedQueue<>());
        ImageCaptchaInfo poll = queue.poll();
        if (poll == null && requiredGetCaptcha) {
            log.warn("滑块验证码缓存不足, genParam:{}", generateParam);
            // 如果池内没数据， 则直接生成
            return target.generateCaptchaImage(generateParam);
        }
        // 减1
        if (poll != null) {
            AtomicInteger pos = posMap.get(generateParam);
            if (pos != null) {
                pos.decrementAndGet();
            }
        }
        return poll;
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(String type, String targetFormatName, String matrixFormatName) {
        return generateCaptchaImage(GenerateParam.builder()
                .type(type)
                .backgroundFormatName(targetFormatName)
                .sliderFormatName(matrixFormatName)
                .build(), true);
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {
        return generateCaptchaImage(param, true);
    }

    @Override
    public ImageCaptchaResourceManager getImageResourceManager() {
        return target.getImageResourceManager();
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

}
