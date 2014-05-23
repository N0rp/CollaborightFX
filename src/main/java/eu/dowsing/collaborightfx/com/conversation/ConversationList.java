package eu.dowsing.collaborightfx.com.conversation;

import java.util.List;

import org.simpleframework.xml.ElementList;

public class ConversationList {

    @ElementList
    private List<String> users;

    @ElementList
    private List<PermanentMessage> messages;

    private String subject;

    public ConversationList(@ElementList List<String> users, @ElementList List<PermanentMessage> messages) {
        this.users = users;
        this.messages = messages;
    }
}
