import "@/captcha/slider/slider.scss"
import "./rotate.scss"
import {Dom,CommonCaptcha, down, initConfig, destroyEvent} from "../common/common.js"

/**
 * 滑动验证码
 */

const TYPE = "ROTATE"
function getTemplate(styleConfig) {
    return `
<div id="tianai-captcha" class="tianai-captcha-slider tianai-captcha-rotate">
    <div class="slider-tip">
        <span id="tianai-captcha-slider-move-track-font" style="font-size: ${styleConfig.i18n.rotate_title_size}">${styleConfig.i18n.rotate_title}</span>
    </div>
    <div class="content">
        <div class="bg-img-div">
            <img id="tianai-captcha-slider-bg-img" src="" alt/>
            <canvas id="tianai-captcha-slider-bg-canvas"></canvas>
        </div>
        <div class="rotate-img-div" id="tianai-captcha-slider-img-div">
            <img id="tianai-captcha-slider-move-img" src="" alt/>
        </div>
         <div class="tianai-captcha-tips" id="tianai-captcha-tips"></div>
    </div>
    <div class="slider-move">
        <div class="slider-move-track">
            <div id="tianai-captcha-slider-move-track-mask"></div>
            <div class="slider-move-shadow"></div>
        </div>
        <div class="slider-move-btn" id="tianai-captcha-slider-move-btn">
        </div>
    </div>
</div>
`;
}
class Rotate extends CommonCaptcha{
    constructor(boxEl, styleConfig) {
        super();
        this.boxEl = boxEl;
        this.styleConfig = styleConfig;
        this.type = TYPE;
        this.currentCaptchaData = {}
    }
    init(captchaData, endCallback, loadSuccessCallback) {
        // 重载样式
        this.destroy();
        this.boxEl.append(getTemplate(this.styleConfig));
        this.el = this.boxEl.find("#tianai-captcha");
        this.loadStyle();
        // 按钮绑定事件
        this.el.find("#tianai-captcha-slider-move-btn").mousedown(down.bind(null,this));
        this.el.find("#tianai-captcha-slider-move-btn").touchstart(down.bind(null,this));
        // 绑定全局
        // window.currentCaptcha = this;
        // 载入验证码
        this.loadCaptchaForData(this, captchaData);
        this.endCallback = endCallback;
        if (loadSuccessCallback) {
            // 加载成功
            loadSuccessCallback(this);
        }
        return this;
    }

    destroy () {
        const existsCaptchaEl = this.boxEl.children("#tianai-captcha");
        if (existsCaptchaEl) {
            existsCaptchaEl.remove();
        }
        destroyEvent();
    }
    doMove() {
        const moveX = this.currentCaptchaData.moveX;
        this.el.find("#tianai-captcha-slider-move-btn").css("transform", "translate(" + moveX + "px, 0px)")
        this.el.find("#tianai-captcha-slider-move-img").css("transform", "rotate(" + (moveX / (this.currentCaptchaData.end / 360)) + "deg)")
        this.el.find("#tianai-captcha-slider-move-track-mask").css("width", moveX + "px")
    }
    loadStyle () {
        let sliderImg = "";
        let moveTrackMaskBorderColor = "#00f4ab";
        let moveTrackMaskBgColor = "#a9ffe5";
        const styleConfig = this.styleConfig;
        if (styleConfig) {
            sliderImg = styleConfig.btnUrl;
            moveTrackMaskBgColor = styleConfig.moveTrackMaskBgColor;
            moveTrackMaskBorderColor = styleConfig.moveTrackMaskBorderColor;
        }
        this.el.find(".slider-move .slider-move-btn").css("background-image", "url(" + sliderImg + ")");
        // this.el.find("#tianai-captcha-slider-move-track-font").text(title);
        this.el.find("#tianai-captcha-slider-move-track-mask").css("border-color", moveTrackMaskBorderColor);
        this.el.find("#tianai-captcha-slider-move-track-mask").css("background-color", moveTrackMaskBgColor);
    }
    loadCaptchaForData (that, data) {
        const bgImg = that.el.find("#tianai-captcha-slider-bg-img");
        const sliderImg = that.el.find("#tianai-captcha-slider-move-img");
        bgImg.attr("src", data.data.backgroundImage);
        sliderImg.attr("src", data.data.templateImage);
        bgImg.on("load",() => {
            that.currentCaptchaData = initConfig(bgImg.width(), bgImg.height(), sliderImg.width(), sliderImg.height(), 300 - 63 + 5);
            that.currentCaptchaData.currentCaptchaId = data.data.id;
        });
    }
}

export default Rotate;
