package eu.dowsing.collaborightfx.com.conversation;

import org.jivesoftware.smack.packet.Message;
import org.simpleframework.xml.Attribute;

/**
 * Message user to user.
 * 
 * @author richardg
 * 
 */
public class UserMessage extends PermanentMessage {

    @Attribute
    private String from;

    @Attribute
    private String to;

    @Attribute
    private String subject;

    @Attribute
    private String body;

    public UserMessage(@Attribute(name = "type") MessageType type, @Attribute(name = "time") long time,
            @Attribute(name = "from") String from, @Attribute(name = "to") String to,
            @Attribute(name = "subject") String subject, @Attribute(name = "body") String body) {
        super(type, time);
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public UserMessage(Message msg) {
        this(MessageType.USER, System.currentTimeMillis(), msg.getFrom(), msg.getTo(), msg.getSubject(), msg.getBody());
    }
}
