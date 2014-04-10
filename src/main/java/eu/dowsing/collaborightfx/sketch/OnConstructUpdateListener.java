package eu.dowsing.collaborightfx.sketch;

import eu.dowsing.collaborightfx.sketch.structure.Shape;

/**
 * Notifies when the sketch receives a new construct update
 * 
 * @author richardg
 * 
 */
public interface OnConstructUpdateListener {

    /**
     * Notified when construct receives an update.
     * 
     * @param shape
     * @param create
     *            <code>true</code> if the construct was just created locally
     */
    void onCosntructUpdate(Shape shape, boolean create);

}
