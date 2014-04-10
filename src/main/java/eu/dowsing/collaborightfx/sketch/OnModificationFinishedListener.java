package eu.dowsing.collaborightfx.sketch;

import eu.dowsing.collaborightfx.sketch.structure.Shape;

/**
 * 
 * @author richardg
 * 
 */
public interface OnModificationFinishedListener {

    /**
     * Called when the shape is no longer being modified by the user.
     * 
     * @param shape
     */
    void onModificationFinished(Shape shape);

}
