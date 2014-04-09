package eu.dowsing.collaborightfx.sketch.transaction;

import org.jivesoftware.smack.packet.PacketExtension;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public abstract class Transaction implements PacketExtension {

    public static final String NAME = "Transaction";
    public static final String NS = "eu.dowsing.collaboright";

    protected static Serializer serializer = new Persister();

    @Override
    public String getElementName() {
        return NAME;
    }

    @Override
    public String getNamespace() {
        return NS;
    }

}
