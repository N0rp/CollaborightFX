package eu.dowsing.collaborightfx.sketch.structure;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import eu.dowsing.collaborightfx.sketch.AdvancedViewPoint;
import eu.dowsing.collaborightfx.sketch.ViewPoint;
import eu.dowsing.collaborightfx.sketch.misc.Point;
import eu.dowsing.collaborightfx.sketch.misc.RgbaColor;

public class Shape {

    public static final RgbaColor DEFAULT_FILL = RgbaColor.BLUE;
    public static final RgbaColor DEFAULT_STROKE = RgbaColor.RED;
    public static final double DEFAULT_LINE_WIDTH = 2;

    @Element
    private RgbaColor fillColor = DEFAULT_FILL;

    @Element
    private RgbaColor strokeColor = DEFAULT_STROKE;

    @Element
    private double lineWidth = DEFAULT_LINE_WIDTH;

    @ElementList(type = Point.class)
    private List<Point> path = new LinkedList<>();

    /** If <code>true</code> shape is done and user does not want to change it any more. **/
    @Attribute
    private boolean isModificationFinished = false;

    private ModificationFinishedListener modificationFinishedListener;
    private PointUpdateListener pointUpdateListener;

    public Shape(@ElementList(name = "path") List<Point> path, @Element(name = "fillColor") RgbaColor fillColor,
            @Element(name = "strokeColor") RgbaColor strokeColor, @Element(name = "lineWidth") double lineWidth,
            @Attribute(name = "isModificationFinished") boolean isModificationFinished) {
        this.path = path;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.lineWidth = lineWidth;
        this.isModificationFinished = isModificationFinished;
    }

    /**
     * Create a new shape.
     * <hr/>
     * The modification of this shape is not considered finished.
     * 
     * @param startX
     *            the x coordinate in the local coordinate system
     * @param startY
     *            the y coordinate in the local coordinate system
     * @param lineWidth
     * @param mover
     *            Determines by how much the point needs to be moved/scaled. Can be <code>null</code> if no
     *            moving/scaling is necessary
     */
    public Shape(double startX, double startY, double lineWidth, AdvancedViewPoint mover) {
        if (mover != null) {
            this.lineWidth = lineWidth / mover.getScale();
        } else {
            this.lineWidth = lineWidth;
        }

        addPoint(startX, startY, false, mover);
    }

    public void setOnModificationFinishedListener(ModificationFinishedListener listener) {
        this.modificationFinishedListener = listener;
    }

    private void notifyModificationFinishedListener() {
        if (this.modificationFinishedListener != null) {
            this.modificationFinishedListener.onModificationFinished(this);
        }
    }

    public void setPointUpdateListener(PointUpdateListener listener) {
        this.pointUpdateListener = listener;
    }

    private void notifyPointUpdateListener(Shape shape, Point point) {
        if (this.pointUpdateListener != null) {
            this.pointUpdateListener.onPointUpdate(shape, point);
        }
    }

    public void setModificationFinished(boolean isFinished) {
        this.isModificationFinished = isFinished;
        if (this.isModificationFinished) {
            notifyModificationFinishedListener();
        }
    }

    /**
     * Returns if all user modifications to the shape are finished.
     * 
     * @return
     */
    public boolean isModificationFinished() {
        return this.isModificationFinished;
    }

    /**
     * Get the last point in the path. Will definately throw an exception when the path does not have at least one
     * point.
     * 
     * @param offset
     *            the offset from the last element in the path. Offset 0 returns the last element. Offset 1 returns the
     *            next to last element.
     * @return the last point minues the offset.
     */
    public Point getLastPoint(int offset) {
        return path.get(path.size() - 1 - offset);
    }

    /**
     * Add a point to the shape.
     * 
     * @param x
     * @param y
     * @param isModificationFinished
     *            if <code>true</code> user is done and the shape won't be modified any more.
     * @param mover
     *            determines by how much the point needs to be moved/scaled. Can be <code>null</code> if no
     *            moving/scaling is necessary
     */
    public void addPoint(double x, double y, boolean isModificationFinished, AdvancedViewPoint mover) {
        Point transformed = transform(x, y, mover);
        path.add(transformed);

        notifyPointUpdateListener(this, transformed);
        setModificationFinished(isModificationFinished);
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
    private Point transform(double x, double y, AdvancedViewPoint mover) {
        if (mover != null) {
            x = (x + mover.getOffsetX()) / mover.getScale();
            y = (y + mover.getOffsetY()) / mover.getScale();
            // TODO scale
        }
        return new Point(x, y);
    }

    private Point untransform(Point p, ViewPoint vp) {
        if (vp == null) {
            return p;
        }
        double x = (p.getX() - vp.getOffsetX()) * vp.getScale();
        double y = (p.getY() - vp.getOffsetY()) * vp.getScale();

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
    public List<Point> getUntransformedPoints(ViewPoint transform) {
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

    public boolean equalsPath(List<Point> path) {
        if (this.path.size() != path.size()) {
            return false;
        }

        for (int i = 0; i < path.size(); i++) {
            if (!this.path.get(i).equals(path.get(i))) {
                return false;
            }
        }

        return true;
    }

    public boolean equals(Shape shape) {
        return this.fillColor.equals(shape.fillColor) && this.strokeColor.equals(shape.strokeColor)
                && this.lineWidth == shape.lineWidth && equalsPath(shape.path);
    }

}
