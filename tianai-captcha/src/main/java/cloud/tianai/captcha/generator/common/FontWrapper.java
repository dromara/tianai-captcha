package cloud.tianai.captcha.generator.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FontWrapper {
    private Font font;
    private Float currentFontTopCoef;

    public FontWrapper(Font font) {
        this(font, 70);
    }

    public FontWrapper(Font font, int fontSize) {
        this.font = font;
        this.font = font.deriveFont(Font.BOLD, fontSize);
    }

    public float getCurrentFontTopCoef() {
        if (currentFontTopCoef != null) {
            return currentFontTopCoef;
        }
        currentFontTopCoef = 0.14645833f * font.getSize() + 0.39583333f;
        return currentFontTopCoef;
    }
}
