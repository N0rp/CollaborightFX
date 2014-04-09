package eu.dowsing.collaborightfx.sketch;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import eu.dowsing.collaborightfx.sketch.misc.RgbaColor;
import eu.dowsing.collaborightfx.sketch.misc.User;
import eu.dowsing.collaborightfx.sketch.structure.Shape;

/**
 * Contains the data of the painting.
 * 
 * @author richardg
 * 
 */
public class Sketch {
    // TODO update this when loading a new sketch
    @Element
    private long maxId = 0;

    private String defaultLocation = "res/painting/defaultPainting.xml";

    @Element
    private RgbaColor bg = new RgbaColor();

    @Element
    private User owner = User.DEFAULT;

    @Element
    /** The time when this painting was created. **/
    private long creationTime = System.currentTimeMillis();

    @Element
    /** The time when this painting was last modified. **/
    private long modificationTime = System.currentTimeMillis();

    @ElementList(type = Shape.class)
    private List<Shape> shapes;

    private RgbaColor fillColor = RgbaColor.BLUE;
    private RgbaColor strokeColor = RgbaColor.GREEN;

    private double lineWidth = 5;

    public Sketch() {
        this.shapes = new LinkedList<>();
    }

    /**
     * Generate a new unique structure id.
     * 
     * @return the structure id
     */
    public long generateStructureId() {
        return maxId++;
    }

    public void setFillColor(RgbaColor fillColor) {
        this.fillColor = fillColor;
    }

    public RgbaColor getFillColor() {
        return this.fillColor;
    }

    public void setStrokeColor(RgbaColor strokeColor) {
        this.strokeColor = strokeColor;
    }

    public RgbaColor getStrokeColor() {
        return this.strokeColor;
    }

    public double getLineWidth() {
        return this.lineWidth;
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * Create a new shape with the current painting parameters and add it to the list of shapes.
     * 
     * @param x
     * @param y
     * @param mover
     * @return
     */
    public Shape createShape(double x, double y, PaintingMover mover) {
        Shape shape = new Shape(x, y, lineWidth, mover);
        shape.setFill(fillColor);
        shape.setStroke(strokeColor);
        notifyOnStructureUpdateListener(shape, true);
        return shape;
    }

    /**
     * Create a new painting with an initial list of shapes.
     * 
     * @param shapes
     */
    public Sketch(List<Shape> shapes) {
        this.shapes = shapes;
    }

    /**
     * Create a new painting with an initial list of shapes.
     * 
     * @param shapes
     */
    public Sketch(List<Shape> shapes, RgbaColor background, User owner, long creationTime, long modificationTime) {
        this.shapes = shapes;
        this.bg = background;
        // this.owner = owner;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
    }

    /**
     * Save the painting to its default location.
     * 
     * @throws Exception
     */
    public void save() throws Exception {
        save(defaultLocation);
    }

    /**
     * Save this painting to persistent file.
     * 
     * @param filePath
     * @throws Exception
     */
    public void save(String filePath) throws Exception {
        System.out.println("Sketch: Saving to " + filePath);
        Serializer serializer = new Persister();
        File result = new File(filePath);

        // version existing file
        if (result.exists()) {
            versionFile(result);
        }

        // delete file and recreate to have an empty file
        result.delete();
        result.createNewFile();

        serializer.write(this, result);
    }

    /**
     * Create a versioned copy of an existing file.
     * 
     * @param file
     */
    private void versionFile(File file) {
        // TODO
    }

    private void updateModificationTime() {
        this.modificationTime = System.currentTimeMillis();
    }

    private List<OnStructureUpdateListener> structureUpdateListener = new LinkedList<>();

    public void addOnStructureUpdateListener(OnStructureUpdateListener listener) {
        this.structureUpdateListener.add(listener);
    }

    private void notifyOnStructureUpdateListener(Shape shape, boolean create) {
        for (OnStructureUpdateListener listener : structureUpdateListener) {
            listener.onStructureUpdate(shape, create);
        }
    }

    // private Shape addShape(Shape shape) {
    // this.shapes.add(shape);
    // updateModificationTime();
    // notifyOnStructureUpdateListener(shape);
    // return shape;
    // }

    /**
     * Get the shapes in the painting.
     * 
     * @return
     */
    public List<Shape> getShapes() {
        return this.shapes;
    }

    public User getOwner() {
        return this.owner;
    }

    /**
     * Get background color.
     * 
     * @return
     */
    public RgbaColor getBackground() {
        return bg;
    }

    public void setBackground(RgbaColor bg) {
        this.bg = bg;
        updateModificationTime();
    }

    /**
     * 
     * @param filePath
     * @return
     * @throws Exception
     */
    public static Sketch load(String filePath) throws Exception {
        Serializer serializer = new Persister();
        File source = new File(filePath);
        if (source.exists()) {
            System.out.println("Sketch: load: File " + filePath + " exists");
            Sketch painting = serializer.read(Sketch.class, source);
            painting.defaultLocation = filePath;
            return painting;
        } else {
            System.out.println("Sketch: load: File " + filePath + " does not exist, creating");
            source.createNewFile();
            Sketch painting = new Sketch();
            painting.defaultLocation = filePath;
            painting.save();
            return painting;
        }
    }
}
