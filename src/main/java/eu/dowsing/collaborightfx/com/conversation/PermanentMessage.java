package eu.dowsing.collaborightfx.com.conversation;

import org.simpleframework.xml.Attribute;

public class PermanentMessage {

    public enum MessageType {
        USER, STATE_CHANGE
    }

    @Attribute
    private MessageType type;

    @Attribute
    private long time;

    public PermanentMessage(@Attribute(name = "type") MessageType type, @Attribute(name = "time") long time) {
        this.type = type;
        this.time = time;
    }

    public MessageType getType() {
        return this.type;
    }

    public long getTime() {
        return time;
    }
}
