package eu.dowsing.collaborightfx.com.conversation;

import org.simpleframework.xml.Attribute;

/**
 * Message when the state of a user changes (i.e. from available to offline).
 * 
 * @author richardg
 * 
 */
public class UserStateMessage extends PermanentMessage {

    @Attribute
    private String user;
    @Attribute
    private String state;

    public UserStateMessage(@Attribute(name = "type") MessageType type, @Attribute(name = "time") long time,
            @Attribute(name = "user") String user, @Attribute(name = "state") String state) {
        super(MessageType.STATE_CHANGE, time);
        this.user = user;
        this.state = state;
    }
}
