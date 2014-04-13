package eu.dowsing.collaborightfx.sketch.structure;


/**
 * 
 * @author richardg
 * 
 */
public interface ModificationFinishedListener {

    /**
     * Called when the shape is no longer being modified by the user.
     * 
     * @param shape
     */
    void onModificationFinished(Shape shape);

}
