package eu.dowsing.collaborightfx.model.painting;

/**
 * Contains data how the painting is transformed.
 * 
 * @author richardg
 * 
 */
public class PaintingTransform {

    private double offsetX = 0.0;
    private double offsetY = 0.0;
    private double scale = 1.0;

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

}
