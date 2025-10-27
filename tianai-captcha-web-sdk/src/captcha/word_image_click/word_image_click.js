import ImageClick from "../image_click/image_click"
/**
 * 滑动验证码
 */
const TYPE = "WORD_IMAGE_CLICK"
class WordImageClick extends ImageClick {
    constructor(divId, styleConfig) {
        super(divId, styleConfig);
        this.type = TYPE;
    }
}

export default WordImageClick;
