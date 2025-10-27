package cloud.tianai.captcha.validator.common.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 天爱有情
 * @date 2024/8/19 15:12
 * @Description 验证码匹配的对象
 */
@Data
@NoArgsConstructor
public class MatchParam {
    /** 轨迹信息. */
    private ImageCaptchaTrack track;
    /** 检测到的设备信息. */
    private Drives drives;
    /** 留一个扩展属性. */
    private Object extendData;


    public MatchParam(ImageCaptchaTrack track) {
        this.track = track;
    }

    public MatchParam(ImageCaptchaTrack track, Drives drives) {
        this.track = track;
        this.drives = drives;
    }

}
