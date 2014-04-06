package eu.dowsing.collaborightfx.app;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.jivesoftware.smack.XMPPException;

import eu.dowsing.collaborightfx.app.xmpp.XmppConnector;
import eu.dowsing.collaborightfx.app.xmpp.XmppConnector.ConnectStatus;
import eu.dowsing.collaborightfx.model.painting.Painting;
import eu.dowsing.collaborightfx.view.painting.PaintingView;

public class TestGrid extends Application {

    private ListView<String> usersList;
    private ListView<String> messageList;
    private ObservableList<String> userData = FXCollections.observableArrayList("Richard", "Rogers");
    private ObservableList<String> messageData = FXCollections.observableArrayList("Hello", "You");

    private Text accountUser;
    private PaintingView canvas;

    private static final String lastPainting = "res/painting/current.xml";

    @Override
    public void start(Stage primaryStage) {
        Pane pane = createAndInitUI();

        final Scene scene = new Scene(pane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // TODO implement graphs with JUNG, JFreeChart or JGraphX?

        // testJabber();
    }

    private Pane createAndInitUI() {
        /* **********************
         * Create content
         */
        // create drawing canvas
        Painting painting = null;
        try {
            painting = Painting.load(lastPainting);
            canvas = new PaintingView(painting, 500, 600);
        } catch (Exception e) {
            System.err.println("Could not load initial painting");
            e.printStackTrace();
        }

        // create control
        HBox control = new HBox();
        accountUser = new Text("UserAccount");
        control.getChildren().add(accountUser);

        // create user list
        usersList = new ListView<>();
        usersList.setItems(userData);

        // create message list
        messageList = new ListView<>();
        messageList.setItems(messageData);

        /* **********************
         * Create layout
         */
        Pane main = new VBox();
        Pane headerBox = new VBox();
        Pane messageBox = new VBox();
        Pane middleBox = new HBox();
        main.getChildren().addAll(headerBox, middleBox, messageBox);

        Pane canvasBox = new Pane();
        Pane userBox = new VBox();
        middleBox.getChildren().addAll(canvasBox, userBox);

        /* **********************
         * Add content to layout
         */
        headerBox.getChildren().add(new Text("Header"));
        userBox.getChildren().addAll(control, usersList);
        canvasBox.getChildren().add(canvas);
        messageBox.getChildren().add(messageList);

        return main;
    }

    private void updateUser(final String text) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                accountUser.setText(text);
            }
        });
    }

    private void testJabberMessage(XmppConnector jabber) {
        System.out.println("Test jabber messaging");
        try {
            updateUser("Connecting...");
            jabber.connectAndLoginAsync();
            updateUser("Logged in as " + jabber.getUser() + " on " + jabber.getHost());
            jabber.doStuff();
            System.out.println("Found " + jabber.getEntryCount() + " buddy entries");
            userData.setAll(jabber.getOnlineUserNames());
            // jabber.createEntry("RichardG@chat.maibornwolff.de", "Richard");
            jabber.sendMessage("Ping", "fyinconvenience@xabber.de");
            System.out.println("Launching GUI");
        } catch (XMPPException e) {
            System.err.println("Jabber did not want to connect");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Jabber did not want to add user");
            e.printStackTrace();
        } finally {
            System.out.println("Disconnecting jabber");
            jabber.disconnect();
        }
    }

    private void testJabber() {

        Preferences p = PreferenceTest.getPreferences();
        String host = p.get(PreferenceTest.JABBER_HOST, PreferenceTest.JABBER_HOST_DEFAULT);
        int port = p.getInt(PreferenceTest.JABBER_PORT, PreferenceTest.JABBER_PORT_DEFAULT);
        String user = p.get(PreferenceTest.JABBER_USER, PreferenceTest.JABBER_USER_DEFAULT);
        String pw = p.get(PreferenceTest.JABBER_PASSWORD, PreferenceTest.JABBER_PASSWORD_DEFAULT);

        if (TestGrid.xmppArguments.isValid()) {
            host = TestGrid.xmppArguments.host;
            port = TestGrid.xmppArguments.port;
            user = TestGrid.xmppArguments.user;
            pw = TestGrid.xmppArguments.pw;
        }

        final XmppConnector jabber = new XmppConnector();
        jabber.setConnectionData(host, port, user, pw);
        jabber.getXmppConnectStatus().addListener(new ChangeListener<ConnectStatus>() {

            @Override
            public void changed(ObservableValue<? extends ConnectStatus> observable, ConnectStatus oldValue,
                    ConnectStatus newValue) {
                System.out.println("Connection status was: " + oldValue + " and is now: " + newValue);
                if (newValue == ConnectStatus.LOGGED_IN) {
                    testJabberMessage(jabber);
                }
            }
        });
        jabber.connectAndLoginAsync();
    }

    public static void main(String[] args) {
        System.out.println("Loading Preference");
        try {
            PreferenceTest.load(true);
        } catch (IOException | InvalidPreferencesFormatException e) {
            System.err.println("Could not load preferences");
            e.printStackTrace();
        }
        // PreferenceTest.printPreference();

        try {
            PreferenceTest.save();
        } catch (IOException | BackingStoreException e) {
            System.err.println("Could not save preferences");
            e.printStackTrace();
        }

        if (args.length > 0) {
            if (args.length != 4) {
                System.err.println("Arguments must be exactly 4: Xmpp host, port, user and password");
            } else {
                TestGrid.xmppArguments.setArguments(args);
            }
        }

        System.out.println("Launching App");
        Application.launch(TestGrid.class);
    }

    public static XmppArguments xmppArguments = new TestGrid.XmppArguments();

    public static class XmppArguments {
        private String host;
        private int port;
        private String user;
        private String pw;

        public boolean isValid() {
            return host != null && port > 0 && user != null && pw != null;
        }

        public void setArguments(String[] args) {
            this.host = args[0];
            this.port = Integer.parseInt(args[1]);
            this.user = args[2];
            this.pw = args[3];

        }
    }

}
