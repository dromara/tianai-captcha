/** 是否打印日志 */
var isPrintLog = false;

function printLog(params) {
    if (isPrintLog) {
        console.log(JSON.stringify(params));
    }
}

/**
 * 清除默认事件
 * @param event event
 */
function clearPreventDefault(event) {
    if (event.preventDefault) {
        event.preventDefault();
    }
}

/**
 * 阻止某div默认事件
 * @param dom
 */
function clearAllPreventDefault(dom) {
    Dom(dom).each((el) => {
        // 手机端
        el.addEventListener('touchmove', clearPreventDefault, {passive: false});
        // pc端
        el.addEventListener('mousemove', clearPreventDefault, {passive: false});
    });
}

function reductionAllPreventDefault(dom) {
    Dom(dom).each(function (el) {
        el.removeEventListener('touchmove', clearPreventDefault);
        el.addEventListener('mousemove', clearPreventDefault);
    });
}

/**
 * 获取当前坐标
 * @param event 事件
 * @returns {{x: number, y: number}}
 */
function getCurrentCoordinate(event) {
    if (event.pageX !== null && event.pageX !== undefined) {
        return {
            x: Math.round(event.pageX),
            y: Math.round(event.pageY)
        }
    }
    let targetTouches;
    if (event.changedTouches) {
        // 抬起事件
        targetTouches = event.changedTouches;
    } else if (event.targetTouches) {
        // pc 按下事件
        targetTouches = event.targetTouches;
    } else if (event.originalEvent && event.originalEvent.targetTouches) {
        // 鼠标触摸事件
        targetTouches = event.originalEvent.targetTouches;
    }
    if (targetTouches[0].pageX !== null && targetTouches[0].pageX !== undefined) {
        return {
            x: Math.round(targetTouches[0].pageX),
            y: Math.round(targetTouches[0].pageY)
        }
    }
    return {
        x: Math.round(targetTouches[0].clientX),
        y: Math.round(targetTouches[0].clientY)
    }
}

function down(currentCaptcha, event) {
    // debugger
    const coordinate = getCurrentCoordinate(event);
    let startX = coordinate.x;
    let startY = coordinate.y;
    currentCaptcha.currentCaptchaData.startX = startX;
    currentCaptcha.currentCaptchaData.startY = startY;
    const trackList = currentCaptcha.currentCaptchaData.trackList;
    currentCaptcha.currentCaptchaData.startTime = new Date();
    const startTime = currentCaptcha.currentCaptchaData.startTime;

    trackList.push({
        x: coordinate.x,
        y: coordinate.y,
        type: "down",
        t: (new Date().getTime() - startTime.getTime())
    });
    printLog(["start", startX, startY])
    currentCaptcha.__m__ = move.bind(null, currentCaptcha);
    currentCaptcha.__u__ = up.bind(null, currentCaptcha);
    // pc
    window.addEventListener("mousemove", currentCaptcha.__m__);
    window.addEventListener("mouseup", currentCaptcha.__u__);
    // 手机端
    window.addEventListener("touchmove", currentCaptcha.__m__, false);
    window.addEventListener("touchend", currentCaptcha.__u__, false);
    if (currentCaptcha && currentCaptcha.doDown) {
        currentCaptcha.doDown(event, currentCaptcha)
    }
}

function move(currentCaptcha, event) {
    if (event.touches && event.touches.length > 0) {
        event = event.touches[0];
    }
    // debugger
    const coordinate = getCurrentCoordinate(event);
    let pageX = coordinate.x;
    let pageY = coordinate.y;
    const startX = currentCaptcha.currentCaptchaData.startX;
    const startY = currentCaptcha.currentCaptchaData.startY;
    const startTime = currentCaptcha.currentCaptchaData.startTime;
    const end = currentCaptcha.currentCaptchaData.end;
    const bgImageWidth = currentCaptcha.currentCaptchaData.bgImageWidth;
    const trackList = currentCaptcha.currentCaptchaData.trackList;
    let moveX = pageX - startX;
    let moveY = pageY - startY;
    const track = {
        x: coordinate.x,
        y: coordinate.y,
        type: "move",
        t: (new Date().getTime() - startTime.getTime())
    };
    trackList.push(track);
    if (moveX < 0) {
        moveX = 0;
    } else if (moveX > end) {
        moveX = end;
    }
    currentCaptcha.currentCaptchaData.moveX = moveX;
    currentCaptcha.currentCaptchaData.moveY = moveY;
    if (currentCaptcha.doMove) {
        currentCaptcha.doMove(event, currentCaptcha);
    }
    printLog(["move", track])
}
function destroyEvent(currentCaptcha) {
    if (currentCaptcha) {
        if (currentCaptcha.__m__) {
            window.removeEventListener("mousemove", currentCaptcha.__m__);
            window.removeEventListener("touchmove", currentCaptcha.__m__);
        }
        if (currentCaptcha.__u__) {
            window.removeEventListener("mouseup", currentCaptcha.__u__);
            window.removeEventListener("touchend", currentCaptcha.__u__);
        }
    }
}

function up(currentCaptcha, event) {
    destroyEvent(currentCaptcha);
    const coordinate = getCurrentCoordinate(event);
    currentCaptcha.currentCaptchaData.stopTime = new Date();
    const startTime = currentCaptcha.currentCaptchaData.startTime;
    const trackList = currentCaptcha.currentCaptchaData.trackList;

    const track = {
        x: coordinate.x,
        y: coordinate.y,
        type: "up",
        t: (new Date().getTime() - startTime.getTime())
    }

    trackList.push(track);
    printLog(["up", track])
    printLog(["tracks", trackList])
    if (currentCaptcha.doUp) {
        currentCaptcha.doUp(event, currentCaptcha)
    }
    currentCaptcha.endCallback(currentCaptcha.currentCaptchaData, currentCaptcha);
}

function initConfig(bgImageWidth, bgImageHeight, templateImageWidth, templateImageHeight, end) {
    // bugfix 图片宽高可能会有小数情况，强转一下整数
    const currentCaptchaConfig = {
        startTime: new Date(),
        trackList: [],
        movePercent: 0,
        clickCount: 0,
        bgImageWidth: Math.round(bgImageWidth),
        bgImageHeight: Math.round(bgImageHeight),
        templateImageWidth: Math.round(templateImageWidth),
        templateImageHeight: Math.round(templateImageHeight),
        end: end
    }
    printLog(["init", currentCaptchaConfig]);
    return currentCaptchaConfig;
}

function closeTips(el, callback) {
    const tipEl = Dom(el).find("#tianai-captcha-tips");
    tipEl.removeClass("tianai-captcha-tips-on")
    // tipEl.removeClass("tianai-captcha-tips-success")
    // tipEl.removeClass("tianai-captcha-tips-error")
    // 延时
    if (callback) {
        setTimeout(callback, .35);
    }
}

function showTips(el, msg, type, callback) {
    const tipEl = Dom(el).find("#tianai-captcha-tips");
    tipEl.text(msg);
    if (type === 1) {
        // 成功
        tipEl.removeClass("tianai-captcha-tips-error")
        tipEl.addClass("tianai-captcha-tips-success")
    } else {
        // 失败
        tipEl.removeClass("tianai-captcha-tips-success")
        tipEl.addClass("tianai-captcha-tips-error")
    }
    tipEl.addClass("tianai-captcha-tips-on");
    // 延时
    setTimeout(callback, 1000);
}

class CommonCaptcha {
    showTips(msg, type, callback) {
        showTips(this.el, msg, type, callback)
    }

    closeTips(msg, callback) {
        closeTips(this.el, msg, callback)
    }
}

function Dom(domStr, dom) {
    return new DomEl(domStr, dom);
}

class DomEl {
    constructor(domStr, dom) {
        if (dom && typeof dom === 'object' && typeof dom.nodeType !== 'undefined') {
            this.dom = dom;
            this.domStr = domStr;
            return;
        }
        if (domStr instanceof DomEl) {
            this.dom = domStr.dom;
            this.domStr = domStr.domStr;
        } else if (typeof domStr === "string") {
            this.dom = document.querySelector(domStr)
            this.domStr = domStr;
        } else if (typeof document === 'object' && typeof document.nodeType !== 'undefined') {
            this.dom = domStr;
            this.domStr = domStr.nodeName;
        } else {
            throw new Error("不支持的类型");
        }
    }

    each(callback) {
        this.getTarget().querySelectorAll("*").forEach(callback);
    }

    removeClass(className) {
        let element = this.getTarget();
        if (element.classList) {
            // 使用 classList API 移除类
            element.classList.remove(className);
        } else {
            // 兼容旧版本浏览器
            const currentClass = element.className;
            const regex = new RegExp('(?:^|\\s)' + className + '(?!\\S)', 'g');
            element.className = currentClass.replace(regex, '');
        }
        return this;
    }

    addClass(className) {
        const element = this.getTarget();
        if (element.classList) {
            // 使用 classList API 添加类
            element.classList.add(className);
        } else {
            // 兼容旧版本浏览器
            let currentClass = element.className;
            if (currentClass.indexOf(className) === -1) {
                element.className = currentClass + ' ' + className;
            }
        }
        return this;
    }

    find(str) {
        const el = this.getTarget().querySelector(str);
        if (el) {
            return new DomEl(str, el);
        }
        return null;
    }

    children(selector) {
        const childNodes = this.getTarget().childNodes;
        for (let i = 0; i < childNodes.length; i++) {
            if (childNodes[i].nodeType === 1 && childNodes[i].matches(selector)) {
                return new DomEl(selector, childNodes[i]);
            }
        }
        return null;
    }

    remove() {
        this.getTarget().remove();
        return null;
    }

    css(property, value) {
        if (typeof property === 'string' && typeof value === 'string') {
            // 设置单个属性
            this.getTarget().style[property] = value;
        } else if (typeof property === 'object') {
            // 设置多个属性
            for (var prop in property) {
                if (property.hasOwnProperty(prop)) {
                    this.getTarget().style[prop] = property[prop];
                }
            }
        } else if (typeof property === 'string' && typeof value === 'undefined') {
            // 获取单个属性
            return window.getComputedStyle(element)[property];
        }
    }

    attr(attributeName, value) {
        if (value === undefined) {
            // 如果未提供值，则返回属性的当前值
            return this.getTarget().getAttribute(attributeName);
        } else {
            // 如果提供了值，则设置属性的值
            this.getTarget().setAttribute(attributeName, value);
        }
        return this;
    }

    text(str) {
        this.getTarget().innerText = str;
        return this;
    }

    html(str) {
        this.getTarget().innerHtml = str;
        return this;
    }

    is(dom) {
        if (dom && typeof dom === 'object' && typeof dom.nodeType !== 'undefined') {
            return this.dom === dom;
        }
        if (dom instanceof DomEl) {
            return this.dom === dom.dom;
        }
    }

    append(content) {
        if (typeof content === 'string') {
            this.getTarget().insertAdjacentHTML("beforeend", content);
        } else if (content instanceof HTMLElement) {
            this.getTarget().appendChild(content);
        } else {
            throw new Error('Invalid content type');
        }
        return this;
    }

    click(fun) {
        this.on("click", fun);
        return this;
    }

    mousedown(fun) {
        this.on("mousedown", fun);
        return this;
    }

    touchstart(fun) {
        this.on("touchstart", fun);
        return this;
    }

    on(eventType, fun) {
        this.getTarget().addEventListener(eventType, fun, {passive: true});
        return this;
    }

    width() {
        return this.getTarget().offsetWidth;
    }

    height() {
        return this.getTarget().offsetHeight;
    }

    getTarget() {
        if (this.dom) {
            return this.dom;
        }
        throw new Error("dom不存在: [" + this.domStr + "]");
    }
}

function http(options) {
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();
        xhr.open(options.method || 'GET', options.url);
        // 设置请求头
        if (options.headers) {
            for (const header in options.headers) {
                if (options.headers.hasOwnProperty(header)) {
                    xhr.setRequestHeader(header, options.headers[header]);
                }
            }
        }
        xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status <= 500) {
                    const contentType = xhr.getResponseHeader('Content-Type');
                    if (contentType && contentType.indexOf('application/json') !== -1) {
                        resolve(JSON.parse(xhr.responseText));
                    } else {
                        resolve(xhr.responseText);
                    }
                } else {
                    reject(new Error('Request failed with status: ' + xhr.status));
                }
            }
        };
        xhr.onerror = function () {
            reject(new Error('Network Error'));
        };
        xhr.send(options.data);
    });
}

function isEmptyObject(obj) {
    for (var key in obj) {
        if (obj.hasOwnProperty(key)) {
            return false; // 对象不为空
        }
    }
    return true; // 对象为空
}


export {
    isEmptyObject,
    http,
    Dom,
    DomEl,
    CommonCaptcha,
    clearAllPreventDefault,
    down,
    move,
    up,
    initConfig,
    showTips,
    closeTips,
    destroyEvent
}
