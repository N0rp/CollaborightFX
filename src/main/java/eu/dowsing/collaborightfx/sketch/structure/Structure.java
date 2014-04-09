package eu.dowsing.collaborightfx.sketch.structure;

import org.simpleframework.xml.Element;

import eu.dowsing.collaborightfx.sketch.Sketch;

/**
 * Represents the top level visible thing in a sketch. Can be anything, from text to a small line to a rectangle to
 * class.
 * 
 * @author richardg
 * 
 */
public class Structure {

    /** Id on the client side. */
    @Element
    private final long clientId;

    /** The shape of this structure. **/
    private final Shape shape;

    public Structure(Sketch sketch, Shape shape) {
        this.clientId = sketch.generateStructureId();
        this.shape = shape;
    }

    public long getClientId() {
        return this.clientId;
    }

}
