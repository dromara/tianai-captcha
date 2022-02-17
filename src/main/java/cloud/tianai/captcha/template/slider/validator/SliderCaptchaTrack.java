package cloud.tianai.captcha.template.slider.validator;

import cloud.tianai.captcha.template.slider.util.CollectionUtils;
import cloud.tianai.captcha.template.slider.util.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: 天爱有情
 * @date 2022/2/17 9:23
 * @Description 滑块验证码滑动轨迹
 */
@Data
public class SliderCaptchaTrack {

    /** 背景图片宽度. */
    private Integer bgImageWidth;
    /** 背景图片高度. */
    private Integer bgImageHeight;
    /** 滑块图片宽度. */
    private Integer sliderImageWidth;
    /** 滑块图片高度. */
    private Integer sliderImageHeight;
    /** 滑动开始时间. */
    private Date startSlidingTime;
    /** 滑动结束时间. */
    private Date entSlidingTime;
    /** 滑动的轨迹. */
    private List<Track> trackList;

    @Data
    @AllArgsConstructor
    public static class Track {
        private Integer x;
        private Integer y;
        private Integer t;
    }
}
