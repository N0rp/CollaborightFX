package eu.dowsing.collaborightfx.sketch.structure;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import eu.dowsing.collaborightfx.sketch.PaintingMover;
import eu.dowsing.collaborightfx.sketch.PaintingTransform;
import eu.dowsing.collaborightfx.sketch.misc.Point;
import eu.dowsing.collaborightfx.sketch.misc.RgbaColor;

public class Shape {

    @Element
    private RgbaColor fillColor = RgbaColor.BLUE;

    @Element
    private RgbaColor strokeColor = RgbaColor.RED;

    private double lineWidth;

    @ElementList(type = Point.class)
    private List<Point> path = new LinkedList<>();

    /** If <code>true</code> shape is done and user does not want to change it any more. **/
    private boolean userIsDone = false;

    public Shape(@ElementList(name = "path") List<Point> path, @Element(name = "fillColor") RgbaColor fillColor,
            @Element(name = "strokeColor") RgbaColor strokeColor) {
        this.path = path;
        // this.lineWidth = lineWidth;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
    }

    public Shape(double startX, double startY, double lineWidth, PaintingMover mover) {
        this.lineWidth = lineWidth;

        addPoint(startX, startY, false, mover);
    }

    public Point getLastPoint(int index) {
        return path.get(path.size() - 1 - index);
    }

    /**
     * Add a point to the shape.
     * 
     * @param x
     * @param y
     * @param userIsDone
     *            if <code>true</code> user is done and the shape won't be modified any more.
     */
    public void addPoint(double x, double y, boolean userIsDone, PaintingMover mover) {
        Point transformed = transform(x, y, mover);
        path.add(transformed);
        this.userIsDone = userIsDone;
    }

    public double getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }

    public RgbaColor getFill() {
        return fillColor;
    }

    public void setFill(RgbaColor color) {
        this.fillColor = color;
    }

    public RgbaColor getStroke() {
        return strokeColor;
    }

    public void setStroke(RgbaColor color) {
        this.strokeColor = color;
    }

    /**
     * Transform the point into the absolute coordinate system
     * 
     * @param x
     * @param y
     * @param mover
     *            the data for visual displacement, can be null
     * @return the transformed coordinates in the order [x, y]
     */
    private Point transform(double x, double y, PaintingMover mover) {
        if (mover != null) {
            x += mover.getOffsetX();
            y += mover.getOffsetY();
            // TODO scale
        }
        return new Point(x, y);
    }

    private Point untransform(Point p, PaintingTransform transform) {
        if (transform == null) {
            return p;
        }
        double x = p.getX() - transform.getOffsetX();
        double y = p.getY() - transform.getOffsetY();
        // TODO scale
        return new Point(x, y);
    }

    /**
     * Get all points of the shape transformed to the current position and scale.
     * 
     * @param offsetX
     * @param offsetY
     * @param scale
     * @return
     */
    public List<Point> getUntransformedPoints(PaintingTransform transform) {
        List<Point> untransformed = new LinkedList<>();
        for (Point p : path) {
            Point un = untransform(p, transform);
            untransformed.add(un);
        }
        return untransformed;
    }

    /**
     * Get a line from a point to another.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param fill
     *            fill of the line
     * @param stroke
     *            stroke of the line
     * @param width
     *            width of the lone
     * @return
     */
    public static Shape fromTo(double x1, double y1, double x2, double y2, RgbaColor fill, RgbaColor stroke,
            double width) {
        Shape s = new Shape(x1, y1, width, null);
        s.addPoint(x2, y2, true, null);
        return s;
    }

}
