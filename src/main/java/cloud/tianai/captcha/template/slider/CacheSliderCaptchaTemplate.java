package cloud.tianai.captcha.template.slider;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

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

    protected final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("slider-captcha-queue"));
    protected ConcurrentLinkedQueue<SliderCaptchaInfo> queue;
    protected AtomicInteger pos = new AtomicInteger(0);
    protected SliderCaptchaTemplate target;
    protected int size;
    /** 等待时间，一般报错或者拉取为空时会休眠一段时间再试. */
    protected int waitTime = 1000;
    /** 调度器检查缓存的间隔时间. */
    protected int period = 100;
    protected GenerateParam generateParam;

    @Getter
    @Setter
    protected boolean requiredGetCaptcha = true;

    public CacheSliderCaptchaTemplate(SliderCaptchaTemplate target, GenerateParam generateParam, int size) {
        this.target = target;
        this.generateParam = generateParam;
        this.size = size;
    }

    public CacheSliderCaptchaTemplate(SliderCaptchaTemplate target, GenerateParam generateParam, int size, int waitTime, int period) {
        this.target = target;
        this.generateParam = generateParam;
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
        this.pos = new AtomicInteger(0);
        queue = new ConcurrentLinkedQueue<>();
        // 初始化一个队列扫描
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                while (pos.get() < this.size) {
                    if (pos.get() >= size) {
                        return;
                    }
                    SliderCaptchaInfo slideImageInfo = target.getSlideImageInfo(generateParam);
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
        }, 0, period, TimeUnit.MILLISECONDS);
        log.info("缓存滑块验证码调度器初始化完成: size:{}, genParam:{}", size, generateParam);
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(waitTime);
        } catch (InterruptedException ignored) {
        }
    }

    @SneakyThrows
    @Override
    public SliderCaptchaInfo getSlideImageInfo() {
        return getSlideImageInfo(this.requiredGetCaptcha);
    }

    @SneakyThrows
    public SliderCaptchaInfo getSlideImageInfo(boolean requiredGetCaptcha) {
        SliderCaptchaInfo poll = queue.poll();
        if (poll == null && requiredGetCaptcha) {
            log.warn("滑块验证码缓存不足, genParam:{}", generateParam);
            // 如果池内没数据， 则直接生成
            return target.getSlideImageInfo(generateParam);
        }
        // 减1
        pos.decrementAndGet();
        return poll;
    }

    @Override
    public SliderCaptchaInfo getSlideImageInfo(String targetFormatName, String matrixFormatName) {
        return target.getSlideImageInfo(targetFormatName, matrixFormatName);
    }

    @Override
    public SliderCaptchaInfo getSlideImageInfo(GenerateParam param) {
        return target.getSlideImageInfo(param);
    }

    @Override
    public boolean percentageContrast(Float newPercentage, Float oriPercentage) {
        return target.percentageContrast(newPercentage, oriPercentage);
    }

    @Override
    public SliderCaptchaResourceManager getSlideImageResourceManager() {
        return target.getSlideImageResourceManager();
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
