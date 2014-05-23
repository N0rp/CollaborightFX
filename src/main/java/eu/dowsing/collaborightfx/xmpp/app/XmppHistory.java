package eu.dowsing.collaborightfx.xmpp.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

public class XmppHistory {

    /** Messages for the **/
    private ObservableList<Message> xmppSelectedContactChat = FXCollections.observableArrayList();

    /** Xmpp Contacts that are online. **/
    private ObservableList<RosterEntry> xmppSketchPartners = FXCollections.observableArrayList();
    /** Messages for the **/
    private ObservableList<Message> xmppSelectedPartnerChat = FXCollections.observableArrayList();

    /** List of messages exchanged for each user. **/
    private Map<String, List<Message>> user2Messages = new HashMap<>();

    public void setSelectedContact(String cont) {
        String contact = getJID(cont);
        System.out.println("Selected Contact is " + cont);

        if (user2Messages.containsKey(contact)) {
            System.out.println(". Previous message count with user is " + user2Messages.get(contact).size());
            xmppSelectedContactChat.setAll(user2Messages.get(contact));
        } else {
            xmppSelectedContactChat.clear();
        }
    }

    public ObservableList<Message> getXmppSelectedContactChat() {
        return xmppSelectedContactChat;
    }

    public ObservableList<RosterEntry> getXmppSktechPartnerContacts() {
        return xmppSketchPartners;
    }

    public ObservableList<Message> getXmppSelectedPartnerChat() {
        return xmppSelectedPartnerChat;
    }

    private String getJID(String from) {
        // TODO verify that this is better
        String name = StringUtils.parseName(from);

        if (from != null && from.indexOf("/") > 0) {
            String jid = from.substring(0, from.indexOf("/"));

            return jid;
        }

        return from;
    }

    public void addMessage2ContactHistory(String jid, Message msg) {
        if (!user2Messages.containsKey(jid)) {
            System.out.println("Creating new chat history for contact " + jid);
            ObservableList<Message> nouveau = FXCollections.observableArrayList();
            user2Messages.put(jid, nouveau);
        }
        xmppSelectedContactChat.add(msg);
        user2Messages.get(jid).add(msg);
    }

}
