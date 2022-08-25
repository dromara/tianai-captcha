package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.util.NamedThreadFactory;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
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
    protected Map<GenerateParam, Long> lastUpdateMap = new ConcurrentHashMap<>(8);
    protected ImageCaptchaGenerator target;
    protected int size;
    /** 等待时间，一般报错或者拉取为空时会休眠一段时间再试. */
    protected int waitTime = 1000;
    /** 调度器检查缓存的间隔时间. */
    protected int period = 5000;
    /** 10天内没有任何操作就删除已缓存的数据. */
    protected long expireTime = TimeUnit.DAYS.toMillis(10);
    @Getter
    @Setter
    protected boolean requiredGetCaptcha = true;

    private boolean init = false;

    public CacheImageCaptchaGenerator(ImageCaptchaGenerator target, int size) {
        this.target = target;
        this.size = size;
    }

    public CacheImageCaptchaGenerator(ImageCaptchaGenerator target, int size, int waitTime, int period) {
        this.target = target;
        this.size = size;
        this.waitTime = waitTime;
        this.period = period;
    }

    public CacheImageCaptchaGenerator(ImageCaptchaGenerator target, int size, int waitTime, int period, Long expireTime) {
        this.target = target;
        this.size = size;
        this.waitTime = waitTime;
        this.period = period;
        this.expireTime = expireTime;
    }

    /**
     * 记的初始化调度器
     */
    public void initSchedule() {
        init(size);
    }

    private void init(int z) {
        if (init) {
            return;
        }
        this.size = z;
        // 初始化一个队列扫描
        scheduledExecutor.scheduleAtFixedRate(() -> queueMap.forEach((k, queue) -> {
            try {
                AtomicInteger pos = posMap.computeIfAbsent(k, k1 -> new AtomicInteger(0));
                int addCount = 0;
                while (pos.get() < this.size) {
                    if (pos.get() >= size) {
                        return;
                    }
                    ImageCaptchaInfo slideImageInfo = target.generateCaptchaImage(k);
                    if (slideImageInfo != null) {
                        boolean addStatus = queue.offer(slideImageInfo);
                        addCount++;
                        if (addStatus) {
                            // 添加记录
                            pos.incrementAndGet();
                        }
                    } else {
                        sleep();
                    }
                }
                if (addCount == 0) {
                    // 没有添加，检测最新更新时间 如果时间过长，直接清除数据
                    Long lastUpdate = lastUpdateMap.get(k);
                    if (lastUpdate != null && System.currentTimeMillis() - lastUpdate > expireTime) {
                        queueMap.remove(k);
                        posMap.remove(k);
                        lastUpdateMap.remove(k);
                    }
                }
            } catch (Exception e) {
                // cache所有
                log.error("缓存队列扫描时出错， ex", e);
                // 删掉它
                queueMap.remove(k);
                posMap.remove(k);
                lastUpdateMap.remove(k);
                // 休眠
                sleep();
            }
        }), 0, period, TimeUnit.MILLISECONDS);
        init = true;
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(waitTime);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public ImageCaptchaGenerator init(boolean initDefaultResource) {
        ImageCaptchaGenerator captchaGenerator = target.init(initDefaultResource);
        // 初始化缓存
        init(size);;
        return captchaGenerator;
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
        ConcurrentLinkedQueue<ImageCaptchaInfo> queue = queueMap.get(generateParam);
        ImageCaptchaInfo captchaInfo = null;
        if (queue != null) {
            captchaInfo = queue.poll();
            if (captchaInfo == null) {
                log.warn("滑块验证码缓存不足, genParam:{}", generateParam);
            } else {
                AtomicInteger pos = posMap.get(generateParam);
                if (pos != null) {
                    pos.decrementAndGet();
                }
            }
        } else {
            queueMap.putIfAbsent(generateParam, new ConcurrentLinkedQueue<>());
            posMap.putIfAbsent(generateParam, new AtomicInteger(0));
        }
        if (captchaInfo == null && requiredGetCaptcha) {
            // 直接生成 不走缓存
            captchaInfo = target.generateCaptchaImage(generateParam);
        }
        if (captchaInfo != null) {
            // 记录最新时间
            lastUpdateMap.put(generateParam, System.currentTimeMillis());
        }
        return captchaInfo;
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(String type, String targetFormatName, String matrixFormatName) {
        return generateCaptchaImage(GenerateParam.builder().type(type).backgroundFormatName(targetFormatName).sliderFormatName(matrixFormatName).build(), true);
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {
        return generateCaptchaImage(param, true);
    }

    @Override
    public ImageCaptchaResourceManager getImageResourceManager() {
        return target.getImageResourceManager();
    }

    @Override
    public void setImageResourceManager(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        target.setImageResourceManager(imageCaptchaResourceManager);
    }

    @Override
    public ImageTransform getImageTransform() {
        return target.getImageTransform();
    }

    @Override
    public void setImageTransform(ImageTransform imageTransform) {
        target.setImageTransform(imageTransform);
    }
}
