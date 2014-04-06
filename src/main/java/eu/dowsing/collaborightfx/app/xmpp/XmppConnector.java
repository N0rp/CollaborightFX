package eu.dowsing.collaborightfx.app.xmpp;

import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;

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

/**
 * Connects to xmpp network.
 * 
 * @author richardg
 * 
 */
public class XmppConnector {

    public enum ConnectStatus {
        NOT_CONNECTED, LOGGED_IN, NOT_LOGGED_IN
    }

    private ChatManager chatManager;
    private MessageListener messageListener;

    private Connection conn;

    private String host;
    private int port;
    private String user;
    private String pw;

    private ObservableValue<ConnectStatus> xmppConnectStatus = new SimpleObjectProperty<ConnectStatus>(
            ConnectStatus.NOT_CONNECTED);
    private Task<Boolean> connectLoginTask = new Task<Boolean>() {
        @Override
        protected Boolean call() {
            ConnectStatus result = connectAndLoginSync();
            return result == ConnectStatus.LOGGED_IN;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            updateMessage("Done!");
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            updateMessage("Cancelled!");
        }

        @Override
        protected void failed() {
            super.failed();
            updateMessage("Failed!");
        }
    };

    /**
     * Connect and login asynchroniously. {@link XmppConnector#getXmppConnectListener()} will be notified with results.
     * 
     * <p/>
     * <b>Note</b> {@link XmppConnector#setConnectionData(String host, int port, String user, String password)} should
     * be called before this.
     */
    public void connectAndLoginAsync() {
        System.out.println("Login using user | password : " + user + " | " + pw);
        new Thread(connectLoginTask).start();
    }

    /**
     * Connect and login synchronously.
     * 
     * <p/>
     * <b>Note</b> {@link XmppConnector#setConnectionData(String host, int port, String user, String password)} should
     * be called before this.
     * 
     * @return the result of the login
     */
    public ConnectStatus connectAndLoginSync() {
        try {
            connect(host, port);
        } catch (XMPPException e) {
            System.err.println("Could not connect to " + host + "through port " + port);
            e.printStackTrace();
            return ConnectStatus.NOT_CONNECTED;
        }

        try {
            login(user, pw);
        } catch (XMPPException e) {
            System.err.println("Could not connect to " + host + "through port " + port + " as user " + user);
            e.printStackTrace();
            return ConnectStatus.NOT_LOGGED_IN;
        }

        return ConnectStatus.LOGGED_IN;
    }

    private void connect(String host, int port) throws XMPPException {
        ConnectionConfiguration config = new ConnectionConfiguration(host, 5222);
        config.setSASLAuthenticationEnabled(true);

        conn = new XMPPConnection(config);
        conn.connect();

        this.host = conn.getHost();

        chatManager = conn.getChatManager();
        chatManager.addChatListener(new ChatManagerListener() {

            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                System.out.println("Chat was created with: " + chat.getParticipant());
                try {
                    chat.sendMessage("pong");
                } catch (XMPPException e) {
                    System.err.println("Cannot send message to chat");
                    e.printStackTrace();
                }

                if (!createdLocally) {
                    // add message listener
                    chat.addMessageListener(new MyMessageListener());
                }
            }
        });
    }

    private void login(String user, String password) throws XMPPException {
        // You have to put this code before you login
        // SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        conn.login(user, password);
        this.user = conn.getUser();
    }

    public ObservableValue<ConnectStatus> getXmppConnectStatus() {
        return xmppConnectStatus;
    }

    /**
     * Set connection data for the xmpp connection
     * 
     * @param host
     * @param port
     * @param user
     * @param password
     */
    public void setConnectionData(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pw = password;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public void doStuff() {
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus("What's up everyone?");
        conn.sendPacket(presence);
    }

    public void sendMessage(String message, String buddyJID) throws XMPPException {
        System.out.println(String.format("Sending mesage '%1$s' to user %2$s", message, buddyJID));

        Chat chat = chatManager.createChat(buddyJID, messageListener);
        chat.sendMessage(message);
    }

    public int getEntryCount() {
        return conn.getRoster().getEntries().size();
    }

    public List<String> getOnlineUserNames() {
        List<String> names = new LinkedList<String>();
        Roster roster = conn.getRoster();
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
        Roster roster = conn.getRoster();

        roster.createEntry(user, name, null);
        // RosterEntry entry = roster.getEntry(user);
        // return entry.getStatus().toString();
    }

    public void disconnect() {
        if (conn != null) {
            conn.disconnect();
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
