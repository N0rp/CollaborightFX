package eu.dowsing.collaborightfx.xmpp.app;

import eu.dowsing.collaborightfx.sketch.structure.Shape;

/**
 * Receives notification when xmpp remote updates are received.
 * 
 * @author richardg
 * 
 */
public interface RemoteConstructUpdateListener {

    /**
     * Notified when a remote shape has been received
     * 
     * @param shape
     */
    void onRemoteConstructUpdate(Shape shape);

}
