import "./captcha.scss"
import Slider from "./slider/slider"
import Rotate from "./rotate/rotate";
import Concat from "./concat/concat";
import Disable from "./disable/disable";
import WordImageClick from "./word_image_click/word_image_click";
import {CaptchaConfig, wrapConfig, wrapStyle} from "./config/config";
import {clearAllPreventDefault} from "./common/common";
const template =
    `
    <div id="tianai-captcha-parent">
        <div id="tianai-captcha-bg-img"></div>
        <div id="tianai-captcha-box">
            <div id="tianai-captcha-loading" class="loading"></div>
        </div>
        <!-- 底部 -->
        <div class="slider-bottom">
            <img class="logo" id="tianai-captcha-logo" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAMAAAAM7l6QAAAAMFBMVEVHcEz3tkX3tkX3tkX3tkX3tkX3tkX3tkX3tkX3tkX3tkX3tkX3tkX3tkX3tkX3tkVmTmjZAAAAD3RSTlMASbTm8wh12hOGoCNiyTV98jvOAAABB0lEQVR42nVT0aIFEQiMorD0/397Lc5a7J0n1UylgIniLRKyDcbBDudZH2DYCAabn3PmTrjeUX+7rJGWx0SqVpzReAfTtKU5fgVCNfxWjB69USUDGwoOiauHpZEpSr0tCx8ILb3Dm3WgBbAlifAJk6+Ww6wqEUmpmIorQVZ1JtqKnDMjkb7AgIpO/wMCaQbuBuEtsBUxhuD9daUaZnApiQB8NAKotMwirGGr6mbXpPnHLHDmy6oy3FgP+1j8IBdVklFc01xUJwv3NR0rIeXV5zpzdlruiijzNq/ufOeKWzZLP3160u5P8RjT1M+HHFtx+PwGyOZqT/D8ROOfjOInTLBIHjy/hvwHxkwPu5cCE1QAAAAASUVORK5CYII=" id="tianai-captcha-logo"></img>
            <div class="close-btn" id="tianai-captcha-slider-close-btn"></div>
            <div class="refresh-btn" id="tianai-captcha-slider-refresh-btn"></div>
        </div>
    </div>
    `;
function createCaptchaByType(type, tac) {
    const box = tac.config.domBindEl.find("#tianai-captcha-box");
    const styleConfig = tac.style;
    switch (type) {
        case "SLIDER":
            return new Slider(box, styleConfig);
        case "ROTATE":
            return new Rotate(box, styleConfig);
        case "CONCAT":
            return new Concat(box, styleConfig);
        case "WORD_IMAGE_CLICK":
            return new WordImageClick(box, styleConfig);
        case "DISABLED":
            return new Disable(box, styleConfig);
        default:
            return null;
    }
}
class TianAiCaptcha {
    constructor(config, style) {
        this.config = wrapConfig(config);
        if (this.config.btnRefreshFun) {
            this.btnRefreshFun = this.config.btnRefreshFun;
        }
        if (this.config.btnCloseFun) {
            this.btnCloseFun = this.config.btnCloseFun;
        }
        this.style = wrapStyle(style);
    }

    init() {
        this.destroyWindow();
        this.config.domBindEl.append(template);
        this.domTemplate = this.config.domBindEl.find("#tianai-captcha-parent");
        clearAllPreventDefault(this.domTemplate);
        this.loadStyle();
        // 绑定按钮事件
        this.config.domBindEl.find("#tianai-captcha-slider-refresh-btn").click((el) => {
            this.btnRefreshFun(el, this);
        });
        this.config.domBindEl.find("#tianai-captcha-slider-close-btn").click((el) => {
            this.btnCloseFun(el, this);
        });
        // 加载验证码
        this.reloadCaptcha();
        return this;
    }

    btnRefreshFun(el, tac) {
        tac.reloadCaptcha();
    }
    btnCloseFun(el, tac) {
        tac.destroyWindow();
    }
    reloadCaptcha() {
        this.showLoading();
        this.destroyCaptcha(() => {
            this.createCaptcha();
        })
    }
    showLoading() {
        this.config.domBindEl.find("#tianai-captcha-loading").css("display", "block");
    }

    closeLoading() {
        this.config.domBindEl.find("#tianai-captcha-loading").css("display", "none");
    }

    loadStyle() {
        // 设置样式
        const bgUrl = this.style.bgUrl;
        const logoUrl = this.style.logoUrl;
        if (bgUrl) {
            // 背景图片
            this.config.domBindEl.find("#tianai-captcha-bg-img").css("background-image", "url(" + bgUrl + ")");
        }
        if (logoUrl && logoUrl !== "") {
            // logo
            this.config.domBindEl.find("#tianai-captcha-logo").attr("src", logoUrl);
        } else if (logoUrl === null){
            // 删除logo
            this.config.domBindEl.find("#tianai-captcha-logo").css("display", "none");
        }
    }

    destroyWindow() {
        if (this.C) {
            this.C.destroy();
            this.C = undefined;
        }
        if (this.domTemplate) {
            this.domTemplate.remove();
        }
    }

    openCaptcha() {
        setTimeout(() => {

            this.C.el.css("transform", "translateX(0)")
        }, 10)
    }

    createCaptcha() {
        this.config.requestCaptchaData().then(data => {
            this.closeLoading();
            if (!data.code) {
                throw new Error("[TAC] 后台验证码接口数据错误!!!");
            }
            let captchaType = data.code === 200 ? data.data?.type : "DISABLED"
            const captcha = createCaptchaByType(captchaType, this);
            if (captcha == null) {
                throw new Error("[TAC] 未知的验证码类型[" + captchaType + "]");
            }
            captcha.init(data, (d, c) => {
                // 验证
                const currentCaptchaData = c.currentCaptchaData;
                const data = {
                    bgImageWidth: currentCaptchaData.bgImageWidth,
                    bgImageHeight: currentCaptchaData.bgImageHeight,
                    templateImageWidth: currentCaptchaData.templateImageWidth,
                    templateImageHeight: currentCaptchaData.templateImageHeight,
                    startTime: currentCaptchaData.startTime.getTime(),
                    stopTime: currentCaptchaData.stopTime.getTime(),
                    trackList: currentCaptchaData.trackList,
                };
                if (c.type === 'ROTATE_DEGREE' || c.type === 'ROTATE') {
                    data.bgImageWidth = c.currentCaptchaData.end;
                }
                if (currentCaptchaData.data) {
                    data.data = currentCaptchaData.data;
                }
                // 清空
                const id = c.currentCaptchaData.currentCaptchaId;
                c.currentCaptchaData = undefined;
                // 调用验证接口
                this.config.validCaptcha(id, data, c, this)
            })
            this.C = captcha;
            this.openCaptcha()
        });
    }

    destroyCaptcha(callback) {
        if (this.C) {
            this.C.el.css("transform", "translateX(300px)")
            setTimeout(() => {
                this.C.destroy();
                if (callback) {
                    callback();
                }
            }, 500)
        } else {
            callback();
        }
    }


}

export {TianAiCaptcha, CaptchaConfig}
