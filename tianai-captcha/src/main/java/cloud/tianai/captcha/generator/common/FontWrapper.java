package cloud.tianai.captcha.generator.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FontWrapper {
    //    private Font font;
//    private Float currentFontTopCoef;

    private Map<Float, Font> fontCache = new ConcurrentHashMap<>(4);
    private Font baseFont;
    public static final int DEFAULT_FONT_SIZE = 70;


    public Font getFont() {
        return getFont(DEFAULT_FONT_SIZE);
    }

    public Font getFont(float size) {
        return fontCache.computeIfAbsent(size, k -> baseFont.deriveFont(Font.BOLD, size));
    }

    public FontWrapper(Font font) {
        this.baseFont = font;
    }

    public float getFontTopCoef(Font font) {
        return 0.14645833f * font.getSize() + 0.39583333f;
    }
}
