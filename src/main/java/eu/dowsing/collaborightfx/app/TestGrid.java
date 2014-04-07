package eu.dowsing.collaborightfx.app;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import org.jivesoftware.smack.XMPPException;

import eu.dowsing.collaborightfx.app.xmpp.OnChangeUpdateTextListener;
import eu.dowsing.collaborightfx.app.xmpp.XmppConnector;
import eu.dowsing.collaborightfx.app.xmpp.XmppConnector.ConnectStatus;
import eu.dowsing.collaborightfx.model.painting.Sketch;
import eu.dowsing.collaborightfx.view.painting.SketchView;
import eu.dowsing.collaborightfx.view.painting.ToggleButtonEventHandler;

public class TestGrid extends Application {

    private final XmppConnector jabber = new XmppConnector();

    private Text upperListLabel = new Text("Upper");
    private Text bottomListLabel = new Text("Lower");
    private ListView<String> upperList = new ListView<>();
    private ListView<String> bottomList = new ListView<>();
    private TextField messageField = new TextField();
    private Button messageSend = new Button("Send");

    /** Xmpp users. **/
    private ObservableList<String> contactData = FXCollections.observableArrayList("User1", "User2");
    /** Sketch partners. **/
    private ObservableList<String> partnersData = FXCollections.observableArrayList("Partner1", "Partner2");
    private ObservableList<String> contactMessageData = FXCollections.observableArrayList("Message1", "Message2");
    private ObservableList<String> partnerMessageData = FXCollections.observableArrayList("PartnerMessage1",
            "PartnerMessage2");
    /** All sketches that can be loaded. **/
    private ObservableList<String> sketchData = FXCollections.observableArrayList("P1", "P2");

    private ObservableList<Integer> lineWidthOptions = FXCollections.observableArrayList(1, 2, 5, 10);
    private ComboBox<Integer> lineWidthCombo = new ComboBox<>(lineWidthOptions);

    private SketchView sketch;

    private static final String lastPainting = "res/painting/current.xml";

    private ColorPicker strokePicker = new ColorPicker();;

    private Button bHideShow = new Button("Hide");

    /* ***
     * Xmpp User Details
     */
    private Button bUser = new Button("User");
    private Text lXConnected = new Text("...");
    private Text lXUser = new Text("User");
    private Text lXHost = new Text("Host");
    private Text lXPort = new Text("Port");
    private Text lXContacts = new Text("Contacts");

    private Color strokeColor;

    private ToggleButton tbDraw = new ToggleButton("Draw");
    private ToggleButton tbText = new ToggleButton("Text");
    private ToggleButton tbSelect = new ToggleButton("Select");
    private SegmentedButton toolButtons = new SegmentedButton(tbDraw, tbText, tbSelect);

    private ToggleButton btPartners = new ToggleButton("Current");
    private ToggleButton btContacts = new ToggleButton("Contacts");
    private ToggleButton btSketches = new ToggleButton("Paintings");
    private SegmentedButton listButtons = new SegmentedButton(btPartners, btContacts, btSketches);

    private NotificationPane notificationPane;

    @Override
    public void start(Stage primaryStage) {
        int width = 800;
        int height = 800;
        Control pane = createAndInitUI(width, height);
        initXmpp();

        final Scene scene = new Scene(pane, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();

        xmppConnectLogin();
    }

    private Control createAndInitUI(double maxWdith, double maxHeight) {
        /* **********************
         * Load Painting
         */
        Sketch painting = null;
        try {
            painting = Sketch.load(lastPainting);
            sketch = new SketchView(painting, maxWdith, 600);
            strokeColor = sketch.getFillColor();
        } catch (Exception e) {
            System.err.println("Could not load initial painting");
            e.printStackTrace();
        }

        /* **********************
         * Create layout
         */
        // main
        Pane main = new VBox();

        // control
        BorderPane controlBox = new BorderPane();
        Pane userDetails = new VBox();

        // canvas
        Pane sketchBox = new VBox();

        // details
        Pane detailBox = new VBox();
        Pane listBox = new VBox();
        Pane messageBox = new HBox();

        // content
        final MasterDetailPane contentBox = new MasterDetailPane();
        contentBox.setMasterNode(sketchBox);
        contentBox.setDetailNode(detailBox);
        contentBox.setDetailSide(Side.RIGHT);
        contentBox.setShowDetailNode(true);

        // finally
        main.getChildren().addAll(controlBox, contentBox);

        /* **********************
         * Fill layout
         */
        // control
        strokePicker.setValue(strokeColor);
        controlBox.setCenter(new HBox(toolButtons, strokePicker, lineWidthCombo));
        tbDraw.setSelected(true);
        controlBox.setRight(new HBox(bUser, bHideShow));
        ButtonBar.setType(bHideShow, ButtonType.RIGHT);

        userDetails.getChildren().addAll(lXConnected, lXHost, lXPort, lXUser, lXContacts);
        userDetails.setStyle("-fx-padding: 10 10 10 10;");
        final PopOver userPop = new PopOver(userDetails);
        userPop.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        userPop.setDetachable(false);

        // canvas
        sketchBox.getChildren().addAll(sketch);

        // details
        detailBox.getChildren().addAll(listButtons, listBox);
        listBox.getChildren().addAll(upperListLabel, upperList, bottomListLabel, bottomList, messageBox);
        messageBox.getChildren().addAll(messageField, messageSend);

        /* **********************
         * Create Control
         */
        btPartners.setOnAction(new ToggleButtonEventHandler<>(upperList, partnersData, upperListLabel, "Partners")
                .addListAndData(bottomList, partnerMessageData, bottomListLabel, "PartnerMessages")
                .setSelected(btPartners).addHide(messageBox));
        btContacts.setOnAction(new ToggleButtonEventHandler<>(upperList, contactData, upperListLabel, "Contacts")
                .addListAndData(bottomList, contactMessageData, bottomListLabel, "ContactMessages")
                .addHide(bottomList, bottomListLabel).addShow(messageBox));
        btSketches.setOnAction(new ToggleButtonEventHandler<>(upperList, sketchData, upperListLabel, "Sketches")
                .addHide(bottomList, bottomListLabel).addHide(messageBox));

        bUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (userPop.isShowing()) {
                    userPop.hide();
                } else {
                    Point2D l = bUser.localToScreen(0, 0);
                    userPop.show(bUser, l.getX() + bUser.getWidth() / 2, l.getY() + bUser.getHeight() * 2);
                }
            }
        });

        bHideShow.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                // toggle box visibility
                contentBox.showDetailNodeProperty().setValue(!contentBox.showDetailNodeProperty().getValue());
            }
        });
        contentBox.showDetailNodeProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observ, Boolean oldVal, Boolean isShown) {
                if (isShown) {
                    bHideShow.setText("Hide");
                } else {
                    bHideShow.setText("Show");
                }
            }

        });
        strokePicker.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                strokeColor = strokePicker.getValue();
                sketch.setStrokeColor(strokeColor);
            }
        });

        // lineWidth picker
        lineWidthCombo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sketch.setLineWidth(lineWidthCombo.getValue());
            }
        });

        notificationPane = new NotificationPane(main);
        return notificationPane;
    }

    private void initXmpp() {
        jabber.getXmppConnectStatus().addListener(new ChangeListener<ConnectStatus>() {

            @Override
            public void changed(ObservableValue<? extends ConnectStatus> observable, ConnectStatus oldValue,
                    ConnectStatus newValue) {
                System.out.println("Connection status was: " + oldValue + " and is now: " + newValue);
                if (newValue == ConnectStatus.LOGGED_IN) {
                    // testXmppMessage(jabber);
                    notificationPane.setText("Logged in");
                    notificationPane.show();
                }
            }
        });
        jabber.getXmppConnectStatus().addListener(
                new OnChangeUpdateTextListener<>("Connected:", "").setLables(lXConnected));
        jabber.getXmppHost().addListener(new OnChangeUpdateTextListener<>("Host:", "").setLables(lXHost));
        jabber.getXmppPort().addListener(new OnChangeUpdateTextListener<>("Port:", "").setLables(lXPort));
        jabber.getXmppUser().addListener(
                new OnChangeUpdateTextListener<>("User:", "").setButtons(bUser).setLables(lXUser));
        jabber.getXmppContactCount().addListener(
                new OnChangeUpdateTextListener<>("Contacts:", "").setLables(lXContacts));

    }

    private void xmppConnectLogin() {

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

        jabber.setConnectionData(host, port, user, pw);
        jabber.connectAndLoginAsync();
    }

    private void testXmppMessage(XmppConnector jabber) {
        System.out.println("Test jabber messaging");
        try {
            // jabber.connectAndLoginAsync();
            jabber.doStuff();
            System.out.println("Found " + jabber.getEntryCount() + " buddy entries");
            contactData.setAll(jabber.getOnlineUserNames());
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
