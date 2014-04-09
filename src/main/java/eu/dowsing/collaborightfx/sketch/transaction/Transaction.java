package eu.dowsing.collaborightfx.sketch.transaction;

import org.jivesoftware.smack.packet.PacketExtension;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public abstract class Transaction implements PacketExtension {

    protected static Serializer serializer = new Persister();

    @Override
    public String getElementName() {
        return "Transaction";
    }

    @Override
    public String getNamespace() {
        return "eu.dowsing.collaboright";
    }

}
