package eu.dowsing.collaborightfx.sketch;

/**
 * Can handle mouse viewpoint changes.
 * 
 * @author richardg
 * 
 */
public class AdvancedViewPoint extends ViewPoint {

    private double moveStartX;
    private double moveStartY;

    public void setMoveStart(double x, double y) {
        this.moveStartX = x;
        this.moveStartY = y;
    }

    public void setMoveCurrent(double x, double y) {
        setOffsetX(getOffsetX() + (x - moveStartX));
        setOffsetY(getOffsetY() + (y - moveStartY));
    }

    @Override
    public String toString() {
        return "MoveModel (OffsetX:" + getOffsetX() + " OffsetY:" + getOffsetY() + " Scale:" + getScale() + " StartX:"
                + moveStartX + " StartY:" + moveStartY + ")";
    }

}
