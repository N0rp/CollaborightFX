package eu.dowsing.collaborightfx.view.painting;

import java.util.LinkedList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import eu.dowsing.collaborightfx.model.RgbaColor;
import eu.dowsing.collaborightfx.model.painting.Painting;
import eu.dowsing.collaborightfx.model.painting.PaintingMover;
import eu.dowsing.collaborightfx.model.shapes.Shape;
import eu.dowsing.collaborightfx.view.shapes.ShapeView;

/**
 * The painting, drawn on a canvas.
 * 
 * @author richardg
 * 
 */
public class PaintingView extends Canvas {

    private Canvas canvas = this;
    private GraphicsContext gc;

    private PaintingMover mover = new PaintingMover();

    private List<ShapeView> shapes = new LinkedList<>();

    private Painting painting;

    public PaintingView(Painting painting) {
        super();
        init(painting);
    }

    public PaintingView(Painting painting, double width, double heigth) {
        super(width, heigth);
        init(painting);
    }

    private void init(Painting painting) {
        this.painting = painting;
        this.gc = canvas.getGraphicsContext2D();
        initControl();
        // drawSampleShapes(gc);
        // testSimpleXml();

        // fill painting values
        drawTestModel();
        this.shapes = toFx(painting.getShapes());
        draw(gc);
    }

    private void testSimpleXml() {
        try {
            this.painting.save("res/painting/testPainting.xml");
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
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);

        // begin a path when the user presses the mouse (primary button)
        // prepare to move the painting (secondary mouse button)
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    // System.out.println("Mouse pressed");
                    Shape shape = new Shape(e.getX(), e.getY(), 5, mover);

                    shapes.add(new ShapeView(painting.addShape(shape)));

                    gc.beginPath();
                    gc.moveTo(e.getX(), e.getY());
                    System.out.println("Shape size now: " + shapes.size());
                    // gc.moveTo(e.getX(), e.getY());
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    mover.setMoveStart(e.getX(), e.getY());
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
                    shape.getShape().addPoint(e.getX(), e.getY(), false, mover);
                    // Point2D prev = shape.getLastPoint(1);
                    // gc.fillRoundRect(e.getX(), e.getY(), 2, 2, 10, 10);
                    gc.lineTo(e.getX(), e.getY());
                    gc.stroke();

                    // gc.quadraticCurveTo(prev.getX(), prev.getY(), e.getX(), e.getY());
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    System.out.println("Move view");
                    mover.setMoveCurrent(e.getX(), e.getY());
                    reset(gc, getBackgroundColor());
                    drawShapes(gc, shapes);
                }
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.PRIMARY) {

                    ShapeView shape = getCurrentShapeView();
                    shape.getShape().addPoint(e.getX(), e.getY(), true, mover);
                    try {
                        painting.save();
                        System.out.println("Saved update");
                    } catch (Exception e1) {
                        System.err.println("Could not save painting after mouse released");
                        e1.printStackTrace();
                    }
                }
            }

        });

    }

    // @Override
    // public void paint(Graphics g) {
    // super.paint(g);
    // }

    public Color getBackgroundColor() {
        RgbaColor color = painting.getBackground();
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
        System.out.println("Move settings are: " + mover);
        for (ShapeView view : shapes) {
            view.draw(gc, mover);
        }
    }

    /**
     * Draws a couple of sample shapes to show canvas capabilities.
     * 
     * @param gc
     */
    private void drawSampleShapes(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[] { 10, 40, 10, 40 }, new double[] { 210, 210, 240, 240 }, 4);
        gc.strokePolygon(new double[] { 60, 90, 60, 90 }, new double[] { 210, 210, 240, 240 }, 4);
        gc.strokePolyline(new double[] { 110, 140, 110, 140 }, new double[] { 210, 210, 240, 240 }, 4);
    }

    /**
     * Convenience method to get most recent shape, if there is one.
     * 
     * @return a shape or <code>null</code>.
     */
    private ShapeView getCurrentShapeView() {
        if (shapes.size() > 0) {
            return shapes.get(shapes.size() - 1);
        } else {
            return null;
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
}
