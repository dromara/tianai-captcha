package cloud.tianai.captcha.validator.common.model.dto;

import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.ParamKey;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 天爱有情
 * @date 2024/8/19 15:12
 * @Description 验证码匹配的对象
 */
@Data
@NoArgsConstructor
public class MatchParam extends AnyMap {
    private static final ParamKey<ImageCaptchaTrack> track = () -> "track";

    private static final ParamKey<Drives> drives = () -> "drives";


    public ImageCaptchaTrack getTrack() {
        return getParam(MatchParam.track);
    }

    public Drives getDrives() {
        return getParam(MatchParam.drives);
    }

    public void setTrack(ImageCaptchaTrack track) {
        addParam(MatchParam.track, track);
    }

    public void setDrives(Drives drives) {
        addParam(MatchParam.drives, drives);
    }

    public MatchParam(ImageCaptchaTrack track) {
        this.setTrack(track);
    }

    public MatchParam(ImageCaptchaTrack track, Drives drives) {
        this.setTrack(track);
        this.setDrives(drives);
    }

}
