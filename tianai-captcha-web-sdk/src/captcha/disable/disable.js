const TYPE = "DISABLE";
import "./disable.scss"
import {Dom} from "../common/common";

function getTemplate(styleConfig) {
   return `
    <div id="tianai-captcha" class="tianai-captcha-disable">
        <div class="slider-tip">
            <span id="tianai-captcha-slider-move-track-font" style="font-size: ${styleConfig.i18n.disable_title_size}">${styleConfig.i18n.disable_title}</span>
        </div>
        <div class="content">
           <div class="bg-img-div">
<!--                <svg width="100" height="100" viewBox="0 0 100 100">-->
<!--                  <polygon points="50,10 90,90 10,90" fill="none" stroke="#FF9900" stroke-width="4"/>-->
<!--                  <path d="M50 35V65 M50 75V75" stroke="#FF9900" stroke-width="4" stroke-linecap="round"/>-->
<!--                </svg>-->
                <span id="content-span"></span>
            </div>
        </div>
    </div>
    `;
}
class Disable {
    constructor(boxEl, styleConfig) {
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
        const existsCaptchaEl = this.boxEl.find("#tianai-captcha");
        if (existsCaptchaEl) {
            existsCaptchaEl.remove();
        }
    }
    loadCaptchaForData (that, data) {
        const msg = data.msg || data.message || "接口异常";
        that.el.find("#content-span").text(msg);
    }
}

export default Disable;
