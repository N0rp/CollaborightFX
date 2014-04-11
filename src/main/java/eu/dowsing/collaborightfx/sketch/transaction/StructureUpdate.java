package eu.dowsing.collaborightfx.sketch.transaction;

import java.io.StringWriter;
import java.io.Writer;

import org.jivesoftware.smack.packet.PacketExtension;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import eu.dowsing.collaborightfx.sketch.structure.Shape;

@Namespace(reference = "http://www.dowsing.eu/collaboright")
public class StructureUpdate extends Transaction {

    /** The id of the structure that receives an update. **/
    @Element
    private long structureId;

    /** The structure that receives an update. **/
    @Element
    private Shape construct;

    public StructureUpdate() {

    }

    public static final String NAME = "structureUpdate";
    public static final String NS = "http://www.dowsing.eu/collaboright";

    protected static Serializer serializer = new Persister();

    @Override
    public String getElementName() {
        return NAME;
    }

    @Override
    public String getNamespace() {
        return NS;
    }

    public StructureUpdate(long structureId, Shape construct) {
        this.structureId = structureId;
        this.construct = construct;
    }

    public long getStructureId() {
        return this.structureId;
    }

    public Shape getConstruct() {
        return construct;
    }

    /**
     * Update the data in this object
     * 
     * @throws Exception
     */
    public static StructureUpdate fromExtension(PacketExtension ext) throws Exception {
        String asXml = ext.toXML();
        return fromXml(asXml);
    }

    public static StructureUpdate fromXml(String xml) throws Exception {
        StructureUpdate update = serializer.read(StructureUpdate.class, xml);
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

    @Override
    public String toString() {
        return toXML();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StructureUpdate) {
            StructureUpdate update = (StructureUpdate) obj;
            return this.construct.equals(update.construct) && this.structureId == update.structureId;
        } else {
            return super.equals(obj);
        }
    }

}
