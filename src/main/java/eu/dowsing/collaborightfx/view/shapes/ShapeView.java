package eu.dowsing.collaborightfx.view.shapes;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import eu.dowsing.collaborightfx.model.Point;
import eu.dowsing.collaborightfx.model.RgbaColor;
import eu.dowsing.collaborightfx.model.painting.PaintingTransform;
import eu.dowsing.collaborightfx.model.shapes.Shape;

/**
 * View of a shape.
 * 
 * @author richardg
 * 
 */
public class ShapeView {

    private final Shape shape;
    private Color fill;
    private Color stroke;

    /**
     * Create a new view for the shape.
     * 
     * @param shape
     */
    public ShapeView(Shape shape) {
        this.shape = shape;
        this.fill = toFx(shape.getFill());
        this.stroke = toFx(shape.getStroke());
    }

    public Shape getShape() {
        return this.shape;
    }

    public Color getFill() {
        return fill;
    }

    public Color getStroke() {
        return stroke;
    }

    public double getLineWidth() {
        return shape.getLineWidth();
    }

    /**
     * Set the graphics context draw values like fill and stroke with those from the shape.
     * 
     * @param gc
     */
    public void setDrawGraphicsContext(GraphicsContext gc) {
        gc.setFill(getFill());
        gc.setStroke(getStroke());
        gc.setLineWidth(getLineWidth());
    }

    public void draw(GraphicsContext gc, PaintingTransform transform) {
        gc.setFill(fill);
        gc.setStroke(stroke);
        gc.setLineWidth(shape.getLineWidth());

        List<Point> points = shape.getUntransformedPoints(transform);
        boolean first = true;
        gc.beginPath();
        // System.out.print("Draw");
        for (Point p : points) {
            if (first) {
                // System.out.print(" from:" + p);
                gc.moveTo(p.getX(), p.getY());
                first = false;
            } else {
                // System.out.print(" through:" + p);
                gc.lineTo(p.getX(), p.getY());
                gc.stroke();
            }
        }
        gc.closePath();
        System.out.println();
    }

    /**
     * From model to fx view.
     * 
     * @param color
     * @return fx color
     */
    public static Color toFx(RgbaColor color) {
        return Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

}
