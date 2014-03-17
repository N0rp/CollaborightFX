package eu.dowsing.collaborightfx.app;

import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

public class Jabber {

    private ChatManager chatManager;
    private MessageListener messageListener;

    private Connection conn2;

    public void connect() throws XMPPException {
        Preferences p = PreferenceTest.getPreferences();
        String host = p.get(PreferenceTest.JABBER_HOST, PreferenceTest.JABBER_HOST_DEFAULT);
        ConnectionConfiguration config = new ConnectionConfiguration(host, 5222);
        config.setSASLAuthenticationEnabled(true);

        conn2 = new XMPPConnection(config);
        conn2.connect();

        chatManager = conn2.getChatManager();
        messageListener = new MyMessageListener();
        chatManager.addChatListener(new ChatManagerListener() {

            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                System.out.println("Chat was created with: " + chat.getParticipant());
                try {
                    chat.sendMessage("pong");
                } catch (XMPPException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (!createdLocally) {
                    // add message listener
                    chat.addMessageListener(new MyMessageListener());
                }
            }
        });
    }

    public void login() throws XMPPException {
        Preferences p = PreferenceTest.getPreferences();
        String user = p.get(PreferenceTest.JABBER_USER, PreferenceTest.JABBER_USER_DEFAULT);
        String pw = p.get(PreferenceTest.JABBER_PASSWORD, PreferenceTest.JABBER_PASSWORD_DEFAULT);
        System.out.println("Login using user | password : " + user + " | " + pw);
        // You have to put this code before you login
        // SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        conn2.login(user, pw);
    }

    public void doStuff() {
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus("What's up everyone?");
        conn2.sendPacket(presence);
    }

    public void sendMessage(String message, String buddyJID) throws XMPPException {
        System.out.println(String.format("Sending mesage '%1$s' to user %2$s", message, buddyJID));
        Chat chat = chatManager.createChat(buddyJID, messageListener);
        chat.sendMessage(message);
    }

    public int getEntryCount() {
        return conn2.getRoster().getEntries().size();
    }

    public List<String> getOnlineUserNames() {
        List<String> names = new LinkedList<String>();
        Roster roster = conn2.getRoster();
        for (RosterEntry entry : roster.getEntries()) {
            System.out.println(String.format("Buddy:%1$s - Status:%2$s", entry.getName(), entry.getStatus()));
            if (entry.getStatus() != null) {
                names.add(entry.getUser());
            }
        }
        return names;
    }

    public void createEntry(String user, String name) throws Exception {
        System.out.println(String.format("Creating entry for buddy '%1$s' with name %2$s", user, name));
        Roster roster = conn2.getRoster();

        roster.createEntry(user, name, null);
        // RosterEntry entry = roster.getEntry(user);
        // return entry.getStatus().toString();
    }

    public void disconnect() {
        if (conn2 != null) {
            conn2.disconnect();
        }
    }

    class MyMessageListener implements MessageListener {

        @Override
        public void processMessage(Chat chat, Message message) {
            String from = message.getFrom();
            String body = message.getBody();
            System.out.println(String.format("Received message '%1$s' from %2$s", body, from));
        }
    }
}
