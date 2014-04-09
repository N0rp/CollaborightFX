package eu.dowsing.collaborightfx.sketch;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Sketch loader can find sketches on persistent storage and load them into memory.
 * 
 * @author richardg
 * 
 */
public class SketchLoader {

    private static final String SKETCH_EXT = ".skml";

    private ObservableList<String> sketchFileNames = FXCollections.observableArrayList();

    private String sketchRoot;

    /**
     * Create a new SketchLoader
     * 
     * @param root
     *            the root path that will be used to find sketches. Should have a / at the end. Example: res/sketch/
     */
    public SketchLoader(String rootPath) {
        refreshSketchNames(rootPath);
    }

    /**
     * Refresh the list of available sketch names. When no path is provided the last submitted path will be used.
     */
    public void refreshSketchNames() {
        refreshSketchNames(sketchRoot);
    }

    /**
     * Refresh sketch names and remember the supplied name.
     * 
     * @param rootPath
     *            the root path were sketches fill be looked for.
     */
    public void refreshSketchNames(String rootPath) {
        this.sketchRoot = rootPath;
        this.sketchFileNames.setAll(getSketchNames(rootPath));
    }

    /**
     * Get all sketch name files in the root path.
     * 
     * @param rootPath
     * @return a list of names that match the sketches.
     */
    private List<String> getSketchNames(String rootPath) {
        List<String> sketches = new LinkedList<String>();
        File root = new File(rootPath);
        if (!root.exists()) {
            throw new RuntimeException("Root sketch folder does not exist at " + rootPath);
        } else if (root.isDirectory()) {
            for (String p : root.list()) {
                File f = new File(p);
                if (!f.isDirectory()) {
                    String name = f.getName();
                    int length = name.length();
                    if (length > SKETCH_EXT.length()
                            && name.substring(length - SKETCH_EXT.length(), length).equals(SKETCH_EXT)) {
                        sketches.add(p);
                    }
                }
            }
        } else {
            throw new RuntimeException("Root sketch folder is not a directory at " + rootPath);
        }
        return sketches;
    }

    /**
     * Load a sketch from persistent storage
     * 
     * @param fileName
     *            the name of the file
     * @param useSketchFolder
     *            if <code>true</code> root sketch path will be added to the fileName automatically.
     * @return
     * @throws Exception
     */
    public Sketch loadSketch(String filePath, boolean useSketchFolder) throws Exception {
        if (useSketchFolder) {
            System.out.println("SketchLoader: Loading using sketch folder root: " + sketchRoot);
            return Sketch.load(sketchRoot + filePath);
        } else {
            System.out.println("SketchLoader: Loading file directly with path: " + filePath);
            return Sketch.load(filePath);
        }
    }

    /**
     * Get the available sketches
     * 
     * @return
     */
    public ObservableList<String> getSketchFileNames() {
        return this.sketchFileNames;
    }

}
