package eu.dowsing.collaborightfx.xmpp.app;

import java.util.Collection;
import java.util.LinkedList;

import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

/**
 * Connects to xmpp network.
 * 
 * @author richardg
 * 
 */
public class XmppConnector {

    /** You should probably not use this service name. Connection seems to be impossible then. **/
    // public static final String SERVICE_NAME = "Collabo";

    public enum ConnectStatus {
        NOT_CONNECTED, LOGGED_IN, NOT_LOGGED_IN
    }

    private XmppHistory history = new XmppHistory();

    private XmppReceiver receiver = new XmppReceiver();

    private XmppSender sender = new XmppSender(receiver, history);

    /** Xmpp Contacts that are online. **/
    private ObservableList<RosterEntry> xmppOnlineContacts = FXCollections.observableArrayList();

    private Connection conn;
    /**
     * Roster of a user.The roster lets you keep track of the availability ("presence") of other users. A roster also
     * allows you to organize users into groups such as "Friends" and "Co-workers". Other IM systems refer to the roster
     * as the buddy list, contact list, etc.
     */
    private Roster roster;

    private String host;
    private int port;
    private String user;
    private String pw;

    private ObservableValue<ConnectStatus> xmppConnectStatus = new SimpleObjectProperty<ConnectStatus>(
            ConnectStatus.NOT_CONNECTED);
    private StringPropertyBase xmppHost = new SimpleStringProperty("No Host");
    private StringPropertyBase xmppUser = new SimpleStringProperty("No User");
    private IntegerPropertyBase xmppPort = new SimpleIntegerProperty(0);

    public XmppReceiver getReceiver() {
        return this.receiver;
    }

    public XmppHistory getHistory() {
        return this.history;
    }

    public XmppSender getSender() {
        return this.sender;
    }

    private Task<Boolean> connectLoginTask = new Task<Boolean>() {
        @Override
        protected Boolean call() {
            System.out.println("Starting Asynchronous Login");
            ConnectStatus result = connectAndLoginSync();
            boolean success = result == ConnectStatus.LOGGED_IN;
            System.out.println("Asynchronous was a success " + success);
            if (success) {
                updateProgress(1, 1);
            } else {
                updateProgress(0, 1);
            }
            return success;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            System.out.println("Done");
            updateMessage("Done!");
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            System.out.println("Cancelled");
            updateMessage("Cancelled!");
        }

        @Override
        protected void failed() {
            super.failed();
            System.out.println("Failed");
            updateMessage("Failed!");
        }
    };

    public String getHost() {
        return this.host;
    }

    public StringPropertyBase propertyConnectedHost() {
        return xmppHost;
    }

    public ObservableValue<String> getXmppUser() {
        return xmppUser;
    }

    public ObservableValue<Number> getXmppPort() {
        return xmppPort;
    }

    public ObservableValue<ConnectStatus> getXmppConnectStatus() {
        return xmppConnectStatus;
    }

    public ObservableList<RosterEntry> getXmppOnlineContacts() {
        return xmppOnlineContacts;
    }

    /**
     * Connect and login asynchroniously. {@link XmppConnector#getXmppConnectListener()} will be notified with results.
     * 
     * <p/>
     * <b>Note</b> {@link XmppConnector#setConnectionData(String host, int port, String user, String password)} should
     * be called before this.
     */
    public void connectAndLoginAsync() {
        connectLoginTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                // This handler will be called if Task succesfully executed login code
                // disregarding result of login operation

                // and here we act according to result of login code
                if (connectLoginTask.getValue()) {
                    System.out.println("Successful login");
                    // xmppConnectStatus.setValue(ConnectStatus.LOGGED_IN);

                    xmppHost.setValue(conn.getHost());
                    xmppPort.setValue(conn.getPort());
                    xmppUser.setValue(conn.getUser());
                } else {
                    System.out.println("Invalid login");
                }

            }
        });
        connectLoginTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                // This handler will be called if exception occured during your task execution
                // E.g. network or db connection exceptions
                System.out.println("Connection error.");
            }
        });

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
            System.out.println("Connecting with host | port : " + host + " | " + port);
            connect(host, port);
        } catch (XMPPException e) {
            System.err.println("Could not connect to " + host + "through port " + port);
            e.printStackTrace();
            return ConnectStatus.NOT_CONNECTED;
        }

        try {
            System.out.println("Login using user : " + user);
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
        // config.setServiceName(SERVICE_NAME);

        conn = new XMPPConnection(config);
        conn.connect();

        receiver.setData(history, conn.getChatManager());

        // add listeners
        roster = conn.getRoster();
        roster.addRosterListener(new MyRoosterListener());
        // roster.setSubscriptionMode(SubscriptionMode.reject_all);
    }

    private void login(String user, String password) throws XMPPException {
        // You have to put this code before you login
        // SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        conn.login(user, password);
        xmppOnlineContacts.setAll(getOnlineEntries(roster, roster.getEntries()));
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

    // public void doStuff() {
    // Presence presence = new Presence(Presence.Type.available);
    // presence.setStatus("What's up everyone?");
    // conn.sendPacket(presence);
    // }

    public void createEntry(String user, String name) throws Exception {
        System.out.println(String.format("Creating entry for buddy '%1$s' with name %2$s", user, name));
        Roster roster = conn.getRoster();

        roster.createEntry(user, name, null);
        // RosterEntry entry = roster.getEntry(user);
        // return entry.getStatus().toString();

    }

    public void removeEntry(String user) throws XMPPException {
        RosterEntry entry = roster.getEntry(user);
        roster.removeEntry(entry);
    }

    public void disconnect() {
        if (conn != null) {
            System.out.println("Jabber: Disconnecting");
            conn.disconnect();
        }
    }

    private boolean isOnline(Presence presence) {
        return presence != null && presence.getType() == Presence.Type.available;
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

    private String getResource(String from) {
        if (from != null && from.lastIndexOf("/") > 0) {
            String resource = from.substring(from.lastIndexOf("/") + 1);

            return resource;
        }

        return null;
    }

    class MyRoosterListener implements RosterListener {
        @Override
        public void presenceChanged(Presence presence) {
            // presence of a user has changed
            if (isOnline(presence)) {
                // smack gives us not only the Jabber ID but also the resource
                String from = presence.getFrom();
                String jid = getJID(from);

                RosterEntry entry = roster.getEntry(jid);

                System.out.println("JID " + jid + " and User " + from + " is avaiable and has new status: "
                        + presence.getStatus() + " to " + presence.getTo() + " and entry " + entry);

                xmppOnlineContacts.add(entry);
            } else {
                System.out
                        .println("User " + presence.getFrom() + " is not available but instead " + presence.getType());
            }
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            // updates in roster for xmpp addresses
            System.out.println("Addresses " + addresses + " have new updates");
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            // xmpp addresses deleted from roster
            System.out.println("Addresses " + addresses + " deleted");
        }

        @Override
        public void entriesAdded(Collection<String> addresses) {
            // new xmpp addresses in roster
            System.out.println("Addresses " + addresses + " added");
        }
    }

    private Collection<RosterEntry> getOnlineEntries(Roster roster, Collection<RosterEntry> entries) {
        Collection<RosterEntry> notOffline = new LinkedList<RosterEntry>();
        for (RosterEntry entry : entries) {
            Presence presence = roster.getPresence(entry.getUser());
            if (isOnline(presence)) {
                notOffline.add(entry);
            }
        }
        return notOffline;
    }

    /**
     * Get the nick name of the user
     * 
     * @param jid
     * @return the nickname or <code>null</code>
     */
    public String getNick(String jid) {
        jid = getJID(jid);

        String jidSelf = getJID(conn.getUser());
        if (jid.equals(jidSelf)) {
            return "Self";
        }

        RosterEntry entry = roster.getEntry(jid);
        if (entry != null) {
            return entry.getName();
        } else {
            return null;
        }
    }
}
