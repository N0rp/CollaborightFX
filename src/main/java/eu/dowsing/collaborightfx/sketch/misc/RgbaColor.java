package eu.dowsing.collaborightfx.sketch.misc;

import org.simpleframework.xml.Attribute;

public class RgbaColor {

    public final static RgbaColor RED = new RgbaColor(255, 0, 0, 1.0f);
    public final static RgbaColor GREEN = new RgbaColor(0, 255, 0, 1.0f);
    public final static RgbaColor BLUE = new RgbaColor(0, 0, 255, 1.0f);
    public final static RgbaColor WHITE = new RgbaColor(255, 255, 255, 1.0f);

    @Attribute
    /** Red value. Between 0 and 255. **/
    private int r;

    @Attribute
    /** Background Green value. Between 0 and 255. **/
    private int g;

    @Attribute
    /** Background Blue value. Between 0 and 255. **/
    private int b;

    @Attribute
    /** Background Alpha value. Between 0.0f and 1.0f. **/
    private float a;

    /**
     * Create a new color, with default value white.
     */
    public RgbaColor() {
        this(255, 255, 255, 1.0f);
    }

    /**
     * Create a new color, not transparent.
     */
    public RgbaColor(int r, int g, int b) {
        this(r, g, b, 1.0f);
    }

    /**
     * Create a new color, not transparent.
     */
    public RgbaColor(int r, int g, int b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public int getRed() {
        return r;
    }

    public int getBlue() {
        return b;
    }

    public int getGreen() {
        return g;
    }

    public float getAlpha() {
        return a;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RgbaColor) {
            RgbaColor col = (RgbaColor) obj;
            return this.a == col.a && this.b == col.b && this.g == col.g && this.r == col.r;
        } else {
            return super.equals(obj);
        }
    }

}
