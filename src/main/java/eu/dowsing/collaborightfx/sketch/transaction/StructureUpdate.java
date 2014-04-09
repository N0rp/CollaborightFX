package eu.dowsing.collaborightfx.sketch.transaction;

import java.io.StringWriter;
import java.io.Writer;

import org.jivesoftware.smack.packet.PacketExtension;
import org.simpleframework.xml.Element;

import eu.dowsing.collaborightfx.sketch.structure.Structure;

public class StructureUpdate extends Transaction {

    /** The id of the structure that receives an update. **/
    @Element
    private long structureId;

    /** The structure that receives an update. **/
    private Structure structure;

    public StructureUpdate() {

    }

    public StructureUpdate(long structureId) {
        this.structureId = structureId;
    }

    public long getStructureId() {
        return this.structureId;
    }

    /**
     * Update the data in this object
     * 
     * @throws Exception
     */
    public static StructureUpdate fromExtension(PacketExtension ext) throws Exception {
        StructureUpdate update = serializer.read(StructureUpdate.class, ext.toXML());
        return update;
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
