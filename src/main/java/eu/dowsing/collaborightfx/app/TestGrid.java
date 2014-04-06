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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.jivesoftware.smack.XMPPException;

import eu.dowsing.collaborightfx.app.xmpp.Jabber;
import eu.dowsing.collaborightfx.app.xmpp.Jabber.ConnectStatus;
import eu.dowsing.collaborightfx.model.painting.Painting;
import eu.dowsing.collaborightfx.view.painting.PaintingView;

public class TestGrid extends Application {

    private ListView<String> usersList;
    private ObservableList<String> userData = FXCollections.observableArrayList("Richard", "Rogers");

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

        testJabber();
    }

    private Pane createAndInitUI() {
        BorderPane border = new BorderPane();

        // create control
        HBox control = new HBox();
        accountUser = new Text("UserAccount");
        control.getChildren().add(accountUser);

        // create user list
        usersList = new ListView<>();
        usersList.setItems(userData);

        // create drawing canvas
        Painting painting;
        try {
            painting = Painting.load(lastPainting);
            canvas = new PaintingView(painting, 500, 600);
            Pane canvasPane = new Pane();
            canvasPane.getChildren().add(canvas);
            border.setCenter(canvasPane);
        } catch (Exception e) {
            System.err.println("Could not load initial painting");
            e.printStackTrace();
        }

        border.setTop(control);
        border.setLeft(usersList);

        return border;
    }

    public static void main(String[] args) {
        System.out.println("Loading Preference");
        try {
            PreferenceTest.load(true);
        } catch (IOException | InvalidPreferencesFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // PreferenceTest.printPreference();

        try {
            PreferenceTest.save();
        } catch (IOException | BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Launching App");
        Application.launch(TestGrid.class);
    }

    private void updateUser(final String text) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                accountUser.setText(text);
            }
        });
    }

    private void testJabberMessage(Jabber jabber) {
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

        final Jabber jabber = new Jabber();
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
}
