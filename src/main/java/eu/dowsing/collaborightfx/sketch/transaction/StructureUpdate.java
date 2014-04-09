package eu.dowsing.collaborightfx.sketch.transaction;

import java.io.StringWriter;
import java.io.Writer;

import org.simpleframework.xml.Element;

import eu.dowsing.collaborightfx.sketch.structure.Structure;

public class StructureUpdate extends Transaction {

    /** The id of the structure that receives an update. **/
    @Element
    private int structureId;

    /** The structure that receives an update. **/
    private Structure structure;

    public StructureUpdate() throws Exception {
        StructureUpdate update = serializer.read(StructureUpdate.class, toXML());
    }

    public StructureUpdate(int structureId) {

    }

    @Override
    public String toXML() {
        Writer w = new StringWriter();
        try {
            serializer.write(this, w);
        } catch (Exception e) {
            System.out.println("Could not write " + this + " as xml");
            e.printStackTrace();
            return null;
        }

        return w.toString();
    }

}
