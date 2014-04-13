package eu.dowsing.collaborightfx.sketch;

import eu.dowsing.collaborightfx.sketch.structure.Shape;

/**
 * Notifies when the sketch receives a new construct update
 * 
 * @author richardg
 * 
 */
public interface OnConstructUpdateListener {

    public enum Type {
        CREATE_IN_PROGRESS, CREATE_DONE, UPDATE_IN_PROGRESS, UPDATE_DONE
    }

    /**
     * Notified when construct receives an update.
     * 
     * @param shape
     * @param isRemote
     *            <code>true</code> if the construct was just created remotely, <tt>false</tt> if it was created locally
     * @param type
     *            the type of the update
     */
    void onConstructUpdate(Shape shape, boolean isRemote, Type type);

}
