package eu.dowsing.collaborightfx.view.sketch;

import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.paint.Color;
import eu.dowsing.collaborightfx.sketch.AdvancedViewPoint;
import eu.dowsing.collaborightfx.sketch.OnConstructUpdateListener;
import eu.dowsing.collaborightfx.sketch.Sketch;
import eu.dowsing.collaborightfx.sketch.misc.RgbaColor;
import eu.dowsing.collaborightfx.sketch.structure.Shape;
import eu.dowsing.collaborightfx.sketch.toolbox.ToolBarSettings;
import eu.dowsing.collaborightfx.sketch.toolbox.ToolBarSettings.ToolChoice;
import eu.dowsing.collaborightfx.view.sketch.shapes.ShapeView;

/**
 * The sketch, drawn on a canvas.
 * 
 * @author richardg
 * 
 */
public class SketchView extends Canvas implements OnConstructUpdateListener {

    private Canvas canvas = this;
    private GraphicsContext gc;

    private AdvancedViewPoint viewpoint = new AdvancedViewPoint();

    private List<ShapeView> shapes = new LinkedList<>();
    private ShapeView currentShape = null;

    private Sketch sketch;

    private ToolBarSettings toolBarSettings;

    public SketchView(ToolBarSettings toolBarSettings, Sketch sketch) {
        super();
        init(toolBarSettings, sketch);
    }

    public SketchView(ToolBarSettings toolBarSettings, Sketch painting, double width, double heigth) {
        super(width, heigth);
        init(toolBarSettings, painting);
    }

    public void setFillColor(Color color) {
        sketch.setFillColor(toModel(color));
    }

    public Color getFillColor() {
        return toFx(sketch.getFillColor());
    }

    public void setStrokeColor(Color color) {
        RgbaColor col = toModel(color);
        System.out.println("Setting stroke color from: " + sketch.getStrokeColor());
        System.out.println("Setting stroke color over: " + toString(color));
        System.out.println("Setting stroke color to: " + col);
        sketch.setStrokeColor(col);
    }

    public Color getStrokeColor() {
        return toFx(sketch.getStrokeColor());
    }

    public double getLineWidth() {
        return sketch.getLineWidth();
    }

    public void setLineWidth(double lineWidth) {
        this.sketch.setLineWidth(lineWidth);
    }

    private void init(ToolBarSettings toolBarSettings, Sketch sketch) {
        this.sketch = sketch;
        this.toolBarSettings = toolBarSettings;
        this.gc = canvas.getGraphicsContext2D();
        initControl();
        // drawSampleShapes(gc);
        // testSimpleXml();

        // fill painting values
        drawTestModel();
        this.shapes = toFx(sketch.getShapes());
        draw(gc);

        this.sketch.addOnConstructUpdateListener(this);
    }

    @Override
    public void onConstructUpdate(Shape shape, boolean isRemote, OnConstructUpdateListener.Type type) {

        if (type == OnConstructUpdateListener.Type.CREATE_IN_PROGRESS
                || type == OnConstructUpdateListener.Type.CREATE_DONE) {
            final ShapeView shapeView = new ShapeView(shape);
            addShapeView(shapeView, isRemote);
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    shapeView.draw(gc, viewpoint);
                }
            });
        }
    }

    private void testSimpleXml() {
        try {
            this.sketch.save("res/painting/testPainting.xml");
        } catch (Exception e) {
            System.err.println("Could not save painting");
            e.printStackTrace();
        }
    }

    private void drawTestModel() {
        // gc.setFill(Color.GREEN);
        // gc.setStroke(Color.BLUE);
        // gc.setLineWidth(5);
        // gc.strokeLine(40, 10, 10, 40);

        Shape s = Shape.fromTo(40, 10, 10, 40, RgbaColor.GREEN, RgbaColor.BLUE, 5);
        ShapeView v = new ShapeView(s);
        v.draw(gc, null);
    }

    private void initControl() {
        // begin a path when the user presses the mouse (primary button)
        // prepare to move the painting (secondary mouse button)
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    // System.out.println("Mouse pressed");
                    if (toolBarSettings.getTool() == ToolChoice.DRAW) {
                        // do not create a view here, the shape listener will take care of that
                        sketch.createConstruct(e.getX(), e.getY(), false, viewpoint);
                    } else if (toolBarSettings.getTool() == ToolChoice.TEXT) {

                    } else if (toolBarSettings.getTool() == ToolChoice.SELECT) {

                    }
                    // gc.moveTo(e.getX(), e.getY());
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    moveSketchView(viewpoint, e.getX(), e.getY(), true);
                }
            }
        });
        // draw a path when the user drags the mouse (primary)
        // move the painting (secondary)
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    // System.out.println("Draw view");
                    ShapeView shape = getCurrentShapeView();
                    shape.getShape().addPoint(e.getX(), e.getY(), false, viewpoint);
                    // Point2D prev = shape.getLastPoint(1);
                    // gc.fillRoundRect(e.getX(), e.getY(), 2, 2, 10, 10);
                    shape.draw(gc, viewpoint);
                    // gc.quadraticCurveTo(prev.getX(), prev.getY(), e.getX(), e.getY());
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    System.out.println("Move view");
                    moveSketchView(viewpoint, e.getX(), e.getY(), false);
                }
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.PRIMARY) {

                    ShapeView shape = getCurrentShapeView();
                    shape.getShape().addPoint(e.getX(), e.getY(), true, viewpoint);
                    try {
                        sketch.save();
                        System.out.println("Saved update");
                    } catch (Exception e1) {
                        System.err.println("Could not save painting after mouse released");
                        e1.printStackTrace();
                    }
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    System.out.println("Move view");
                    moveSketchView(viewpoint, e.getX(), e.getY(), false);
                }
            }

        });

        canvas.addEventHandler(ZoomEvent.ANY, new EventHandler<ZoomEvent>() {

            @Override
            public void handle(ZoomEvent e) {
                System.out.println("Zooming x:" + e.getSceneX() + " y:" + e.getSceneY() + " factor:"
                        + e.getZoomFactor() + " totalZommFactor:" + e.getTotalZoomFactor() + " isProgress:"
                        + (e.getEventType() == ZoomEvent.ZOOM_FINISHED));
                // rect.setScaleX(rect.getScaleX() * event.getZoomFactor());
                // rect.setScaleY(rect.getScaleY() * event.getZoomFactor());
                // log("Rectangle: Zoom event" +
                // ", inertia: " + e.isInertia() +
                // ", direct: " + e.isDirect());
                if (e.getEventType() != ZoomEvent.ZOOM_FINISHED) {
                    zoomSketchView(e.getX(), e.getY(), (e.getTotalZoomFactor() - 1.0) / 2);
                }
                // zoomSketchView(e.getX(), e.getY(), (e.getDeltaY() / 100));
            }

        });

        canvas.addEventHandler(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent e) {

                // Mouse wheel down is negative
                boolean isSingle = e.getEventType() == ScrollEvent.SCROLL;
                boolean isStart = e.getEventType() == ScrollEvent.SCROLL_STARTED;
                boolean isFinish = e.getEventType() == ScrollEvent.SCROLL_FINISHED;
                System.out.println("Scroll x:" + e.getSceneX() + " y:" + e.getSceneY() + " delx:" + e.getDeltaX()
                        + " dely:" + e.getDeltaY() + " touchpoints:" + e.getTouchCount() + " isSingle:" + isSingle
                        + " isStart:" + isStart + " isFinsihed:" + isFinish);
                if (e.getTouchCount() > 0) {
                    // scrolling using touch pad or touch screen
                    moveSketchViewByOffset(e.getDeltaX(), e.getDeltaY());
                } else {
                    // scrolling using mouse wheel
                    zoomSketchView(e.getX(), e.getY(), (e.getDeltaY() / 100));
                }

            }

        });
    }

    private void zoomSketchView(double zoomOnX, double zoomOnY, double scaleDelta) {
        viewpoint.setScale(viewpoint.getScale() + scaleDelta);
        reset(gc, getBackgroundColor());
        drawShapes(gc, shapes);
    }

    private void moveSketchViewByOffset(double offsetX, double offsetY) {
        viewpoint.setOffsetX(viewpoint.getOffsetX() + offsetX);
        viewpoint.setOffsetY(viewpoint.getOffsetY() + offsetY);
        reset(gc, getBackgroundColor());
        drawShapes(gc, shapes);
    }

    private void moveSketchView(AdvancedViewPoint viewpoint, double x, double y, boolean start) {
        if (start) {
            viewpoint.setMoveStart(x, y);
        } else {
            viewpoint.setMoveCurrent(x, y);
            reset(gc, getBackgroundColor());
            drawShapes(gc, shapes);
        }
    }

    public Color getBackgroundColor() {
        RgbaColor color = sketch.getBackground();
        return Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    private void reset(GraphicsContext gc, Color color) {
        gc.setFill(color);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void draw(GraphicsContext gc) {
        reset(gc, getBackgroundColor());
        drawShapes(gc, shapes);
    }

    private void drawShapes(GraphicsContext gc, List<ShapeView> shapes) {
        System.out.println("Drawing " + shapes.size() + " shapes");
        System.out.println("Move settings are: " + viewpoint);
        for (ShapeView view : shapes) {
            view.draw(gc, viewpoint);
        }
    }

    /**
     * Convenience method to get most recent shape, if there is one.
     * 
     * @return a shape or <code>null</code>.
     */
    private ShapeView getCurrentShapeView() {
        return currentShape;
    }

    private void addShapeView(ShapeView shapeView, boolean isRemote) {
        System.out.println("SketchView: add shape");

        int previousSize = this.shapes.size();
        this.shapes.add(shapeView);
        if (!isRemote) {
            this.currentShape = shapeView;
        }

        if (previousSize != shapes.size()) {
            System.out.println("SketchView: Shape size now: " + shapes.size());
        }
    }

    /**
     * Transform model to fx view.
     * 
     * @param shapes
     * @return
     */
    private List<ShapeView> toFx(List<Shape> shapes) {
        List<ShapeView> fxShapes = new LinkedList<>();
        for (Shape shape : shapes) {
            fxShapes.add(new ShapeView(shape));
        }
        return fxShapes;
    }

    /**
     * Transform model to fx view.
     * 
     * @param shapes
     * @return
     */
    public static RgbaColor toModel(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        float alpha = (float) color.getOpacity();

        RgbaColor c = new RgbaColor(red, green, blue, alpha);
        return c;
    }

    public static Color toFx(RgbaColor color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        double alpha = color.getAlpha();

        Color c = Color.rgb(red, green, blue, alpha);
        // String cStr = toString(c);
        return c;
    }

    public static String toString(Color color) {
        return "Color (Red:" + color.getRed() + " Green:" + color.getGreen() + " Blue:" + color.getBlue() + " Alpha:"
                + color.getOpacity() + ")";
    }
}
