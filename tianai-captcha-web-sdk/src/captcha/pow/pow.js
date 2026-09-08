import "@/captcha/slider/slider.scss"
import "./pow.scss"
import {CommonCaptcha,  initConfig,showTips} from "../common/common.js"
import {SolverManager} from "./pow-core"
/**
 * 滑动验证码
 */

const TYPE = "POW"
function getTemplate(styleConfig) {
    return `
<div id="tianai-captcha" class="tianai-captcha-pow">
    <div class="slider-tip">
        <span id="tianai-captcha-slider-move-track-font" style="font-size: ${styleConfig.i18n.pow_title_size}">${styleConfig.i18n.pow_title}</span>
    </div>
    <div class="content">
         <div class="verify-loading">正在验证中...</div>
        <div class="tianai-captcha-tips" id="tianai-captcha-tips"></div>
    </div>
</div>
`;
}
class Pow extends CommonCaptcha{
    constructor(boxEl, styleConfig) {
        super();
        this.boxEl = boxEl;
        this.styleConfig = styleConfig;
        this.type = TYPE;
        this.currentCaptchaData = {}
    }
    init(captchaData, endCallback, loadSuccessCallback, tac) {
        // 重载样式
        this.destroy();
        this.boxEl.append(getTemplate(this.styleConfig));
        this.el = this.boxEl.find("#tianai-captcha");
        this.pow = new SolverManager();
        // window.currentCaptcha = this;
        // 载入验证码
        // tac.hideRefreshBtn();
        this.loadCaptchaForData(this, captchaData);
        this.endCallback = endCallback;
        if (loadSuccessCallback) {
            // 加载成功
            loadSuccessCallback(this);
        }

        return this;
    }

    destroy () {
        if (this.pow) {
            this.pow.cancel();
        }
        const existsCaptchaEl = this.boxEl.children("#tianai-captcha");
        if (existsCaptchaEl) {
            existsCaptchaEl.remove();
        }
    }
    showTips(msg, type,callback) {
        showTips(this.el, msg,type, callback)
    }
    closeTips(callback) {
        closeTips(this.el, callback)
    }

    loadCaptchaForData (that, data) {
        // 开始计算
        that.currentCaptchaData = initConfig(300, 180, 0, 0, 0);
        that.currentCaptchaData.currentCaptchaId = data.data.id;
        that.currentCaptchaData.startTime = new Date();
        // 先放个假的
        that.currentCaptchaData.trackList = [{x:0,y:0,t:0}]
        this.pow.start({token:data.data.id,c:data.data.data.POW[2], d:data.data.data.POW[0], s:data.data.data.POW[1]}).then(res => {
            const nonces = res.solutions;
            that.currentCaptchaData.customData =nonces.join("-");
            that.currentCaptchaData.stopTime = new Date();
            this.endCallback(that.currentCaptchaData,this);
        })
    }
}

export default Pow;
