package eu.dowsing.collaborightfx.com.contacts.conversation;

import java.util.LinkedList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.jivesoftware.smack.packet.Message;

import eu.dowsing.collaborightfx.com.contacts.Person;

/**
 * Wraps around messages and chats.
 * 
 * @author richardg
 * 
 */
public class Conversation {

    public List<Person> people = new LinkedList<>();
    public ObservableList<ConversationEvent> events = FXCollections.observableArrayList();

    public void addMessage(Message message) {
        this.events.add(new MessageEvent());
    }
}
