package eu.dowsing.collaborightfx.com.conversation;

import java.util.LinkedList;
import java.util.List;

/**
 * Stores and retrieves message from permanent storage
 * 
 * @author richardg
 * 
 */
public class MessageStorage {

    private List<ConversationList> messageList = new LinkedList<ConversationList>();

    /**
     * Load the location of the conversation history.
     * 
     * @param conversationLocation
     */
    public MessageStorage(String historyFolder) {
        refreshHistory(historyFolder);
    }

    public void refreshHistory(String historyFolder) {

    }

}
