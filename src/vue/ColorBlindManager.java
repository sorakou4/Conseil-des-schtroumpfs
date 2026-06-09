package vue;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Region;


public class ColorBlindManager {

    public enum ColorBlindType {
        NONE          ("Aucun",         0.0,   0.0,   0.0,  0.0),
        DEUTERANOPIA  ("Deutéranopie",  -0.2, -0.3,   0.0,  0.0),
        PROTANOPIA    ("Protanopie",     0.15, -0.25,  0.0,  0.0),
        TRITANOPIA    ("Tritanopie",     0.35, -0.2,   0.0,  0.0),
        ACHROMATOPSIA ("Achromatopsie",  0.0,  -1.0,   0.0,  0.0);

        public final String label;
        public final double hue;
        public final double saturation;
        public final double brightness;
        public final double contrast;

        ColorBlindType(String label, double hue, double saturation,
                       double brightness, double contrast) {
            this.label      = label;
            this.hue        = hue;
            this.saturation = saturation;
            this.brightness = brightness;
            this.contrast   = contrast;
        }
    }

    private static ColorBlindType currentType = ColorBlindType.NONE;

    public static void apply(Region root, ColorBlindType type) {
        currentType = type;
        if (type == ColorBlindType.NONE) {
            root.setEffect(null);
        } else {
            ColorAdjust adjust = new ColorAdjust();
            adjust.setHue(type.hue);
            adjust.setSaturation(type.saturation);
            adjust.setBrightness(type.brightness);
            adjust.setContrast(type.contrast);
            root.setEffect(adjust);
        }
        System.setProperty("schtroumpfs.colorblind", type.name());
    }

    public static ColorBlindType getCurrentType() {
        return currentType;
    }

    /** Restores preference saved in system properties. */
    public static ColorBlindType loadPreference() {
        String saved = System.getProperty("schtroumpfs.colorblind");
        if (saved == null) return ColorBlindType.NONE;
        try { return ColorBlindType.valueOf(saved); }
        catch (IllegalArgumentException e) { return ColorBlindType.NONE; }
    }
}
