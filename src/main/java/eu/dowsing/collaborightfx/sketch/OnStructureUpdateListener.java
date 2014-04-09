package eu.dowsing.collaborightfx.sketch;

import eu.dowsing.collaborightfx.sketch.structure.Shape;

/**
 * Listens for structure updates.
 * 
 * @author richardg
 * 
 */
public interface OnStructureUpdateListener {

    void onStructureUpdate(Shape shape, boolean create);

}
