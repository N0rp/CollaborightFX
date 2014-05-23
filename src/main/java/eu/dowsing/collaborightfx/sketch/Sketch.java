package eu.dowsing.collaborightfx.sketch;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import eu.dowsing.collaborightfx.sketch.misc.Point;
import eu.dowsing.collaborightfx.sketch.misc.RgbaColor;
import eu.dowsing.collaborightfx.sketch.misc.User;
import eu.dowsing.collaborightfx.sketch.structure.ModificationFinishedListener;
import eu.dowsing.collaborightfx.sketch.structure.PointUpdateListener;
import eu.dowsing.collaborightfx.sketch.structure.Shape;

/**
 * Contains the data of the painting.
 * 
 * @author richardg
 * 
 */
public class Sketch {

    public static final RgbaColor DEFAULT_BACKGROUND = RgbaColor.WHITE;
    public static final RgbaColor DEFAULT_FILL = RgbaColor.BLUE;
    public static final RgbaColor DEFAULT_STROKE = RgbaColor.RED;

    public static final double DEFAULT_LINE_WIDTH = 5;

    // TODO update this when loading a new sketch
    @Element
    private long maxId = 0;

    private String defaultLocation = "res/painting/defaultPainting.xml";

    @Element
    private RgbaColor bg = DEFAULT_BACKGROUND;

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

    private RgbaColor fillColor = DEFAULT_FILL;
    private RgbaColor strokeColor = DEFAULT_STROKE;

    private double lineWidth = DEFAULT_LINE_WIDTH;

    private List<OnConstructUpdateListener> structureUpdateListener = new LinkedList<>();

    /**
     * 
     * @param filePath
     * @return
     * @throws Exception
     *             if simplexml cannot read the file a ValueRequiredException or an IOException when the file system
     *             made problems
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

    public Sketch() {
        this.shapes = new LinkedList<>();
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
        System.out.println("SKetch: Line Width now " + lineWidth);
        this.lineWidth = lineWidth;
    }

    /**
     * Create a new shape with the current painting parameters and add it to the list of shapes.
     * <hr/>
     * Do not forget that a shape needs to be set to modification done when user has finished drawing!!! This will be
     * done when you tell the sketch that the last point was added through
     * {@link Shape#addPoint(double, double, boolean, AdvancedViewPoint)}.
     * 
     * @param x
     * @param y
     * @param isModificationFinished
     *            <code>false</code> if the user is still drawing or changing the construct, else <code>true</code>
     * @param vp
     *            the viewpoint of the user, i.e. how, from where/what direction he looks at the sketch
     * @return
     */
    public Shape createConstruct(double x, double y, boolean isModificationFinished, AdvancedViewPoint vp) {
        Shape shape = new Shape(x, y, lineWidth, vp);
        shape.setFill(fillColor);
        shape.setStroke(strokeColor);
        shape.setModificationFinished(isModificationFinished);

        addConstruct(shape, false);
        return shape;
    }

    public void addRemoteConstruct(Shape shape) {
        System.out.println("Sketch: Adding Construct");
        addConstruct(shape, true);
    }

    public void addOnConstructUpdateListener(OnConstructUpdateListener listener) {
        this.structureUpdateListener.add(listener);
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
     * Add a new construct to the sketch.
     * 
     * @param shape
     * @param isRemote
     */
    private void addConstruct(Shape shape, final boolean isRemote) {
        this.shapes.add(shape);
        if (!isRemote && !shape.isModificationFinished()) {
            // make sure new notification is created once the construct is done
            shape.setOnModificationFinishedListener(new ModificationFinishedListener() {
                @Override
                public void onModificationFinished(Shape shape) {
                    notifyOnConstructUpdateListener(shape, isRemote, OnConstructUpdateListener.Type.UPDATE_DONE);
                }
            });
            shape.setPointUpdateListener(new PointUpdateListener() {

                @Override
                public void onPointUpdate(Shape shape, Point point) {
                    if (shape.isModificationFinished()) {
                        notifyOnConstructUpdateListener(shape, isRemote, OnConstructUpdateListener.Type.UPDATE_DONE);
                    } else {
                        notifyOnConstructUpdateListener(shape, isRemote,
                                OnConstructUpdateListener.Type.UPDATE_IN_PROGRESS);
                    }
                }
            });
        }
        notifyOnConstructUpdateListener(shape, isRemote, OnConstructUpdateListener.Type.CREATE_IN_PROGRESS);
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

    /**
     * Notify the construct update listeners.
     * 
     * @param shape
     * @param isRemote
     *            if <tt>true</tt> construct was updated remotely, <tt>false</tt> if updated locally
     * @param type
     *            the type of the update
     */
    private void notifyOnConstructUpdateListener(Shape shape, boolean isRemote, OnConstructUpdateListener.Type type) {
        updateModificationTime();
        for (OnConstructUpdateListener listener : structureUpdateListener) {
            listener.onConstructUpdate(shape, isRemote, type);
        }
    }

    // private Shape addShape(Shape shape) {
    // this.shapes.add(shape);
    // updateModificationTime();
    // notifyOnStructureUpdateListener(shape);
    // return shape;
    // }

}
