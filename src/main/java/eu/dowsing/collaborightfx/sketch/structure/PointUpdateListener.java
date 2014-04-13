package eu.dowsing.collaborightfx.sketch.structure;

import eu.dowsing.collaborightfx.sketch.misc.Point;

/**
 * Listens for point updates in a shape.
 * 
 * @author richardg
 * 
 */
public interface PointUpdateListener {

    /**
     * Called when a shape receives a point update
     * 
     * @param shape
     *            the shape with a point update
     * @param point
     *            the point update
     */
    void onPointUpdate(Shape shape, Point point);

}
