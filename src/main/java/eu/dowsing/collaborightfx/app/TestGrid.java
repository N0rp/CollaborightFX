package eu.dowsing.collaborightfx.app;

import java.util.prefs.BackingStoreException;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import eu.dowsing.collaborightfx.app.xmpp.OnChangeUpdateTextListener;
import eu.dowsing.collaborightfx.app.xmpp.XmppConnector;
import eu.dowsing.collaborightfx.app.xmpp.XmppConnector.ConnectStatus;
import eu.dowsing.collaborightfx.model.painting.Sketch;
import eu.dowsing.collaborightfx.model.painting.SketchLoader;
import eu.dowsing.collaborightfx.preferences.PreferenceLoader;
import eu.dowsing.collaborightfx.preferences.PreferenceWrapper;
import eu.dowsing.collaborightfx.view.painting.SketchView;
import eu.dowsing.collaborightfx.view.painting.ToggleButtonEventHandler;

public class TestGrid extends Application {

    private SketchLoader loader = new SketchLoader("res/sketch");
    private final XmppConnector jabber = new XmppConnector();

    private Text upperListLabel = new Text("Upper");
    private Text bottomListLabel = new Text("Lower");

    private ListView<String> sketchList = new ListView<>();
    private ListView<RosterEntry> userList = new ListView<>();
    private ListView<Message> messageList = new ListView<>();
    private TextField messageField = new TextField();
    private Button messageSend = new Button("Send");

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

    private static final String APP_TITLE = "Collaboright";

    @Override
    public void start(Stage primaryStage) {
        int width = 800;
        int height = 800;
        Control pane = createAndInitUI(width, height);
        initXmpp();

        final Scene scene = new Scene(pane, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();

        // set title
        Preferences p = PreferenceLoader.getInstance().getCurrentPreferences();
        String user = p.get(PreferenceWrapper.JABBER_USER, "Geoffrey");

        primaryStage.setTitle(APP_TITLE + " - " + user);

        xmppConnectLogin();
    }

    private Control createAndInitUI(double maxWdith, double maxHeight) {
        /* **********************
         * Load Painting
         */
        Sketch painting = null;
        try {
            painting = loader.loadSketch(lastPainting, false);

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
        final Pane messageBox = new HBox();

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
        listBox.getChildren().addAll(upperListLabel, userList, sketchList, bottomListLabel, messageList, messageBox);
        messageBox.getChildren().addAll(messageField, messageSend);

        /* **********************
         * Create Control
         */
        btPartners.setOnAction(new ToggleButtonEventHandler(userList, jabber.getXmppSktechPartnerContacts(),
                upperListLabel, "Partners")
                .addListAndData(messageList, jabber.getXmppSelectedPartnerChat(), bottomListLabel, "PartnerMessages")
                .addHide(sketchList).setSelected(btPartners).addShow(messageBox));

        btContacts.setOnAction(new ToggleButtonEventHandler(userList, jabber.getXmppOnlineContacts(), upperListLabel,
                "Contacts").addListAndData(messageList, jabber.getXmppSelectedContactChat(), bottomListLabel,
                "ContactMessages").addHide(sketchList, messageList, bottomListLabel, messageBox));

        btSketches.setOnAction(new ToggleButtonEventHandler(sketchList, loader.getSketchFileNames(), upperListLabel,
                "Sketches").addHide(userList, messageList, bottomListLabel, messageBox));

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

        messageSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent ke) {
                sendMessageToSelectedUser();
            }
        });
        // set messages sender
        messageField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    sendMessageToSelectedUser();
                }
            }
        });

        // set list cell factories
        userList.setCellFactory(new Callback<ListView<RosterEntry>, ListCell<RosterEntry>>() {
            @Override
            public ListCell<RosterEntry> call(ListView<RosterEntry> list) {
                return new RosterEntryCell();
            }
        });

        messageList.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> list) {
                return new MessageCell(jabber);
            }
        });

        userList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RosterEntry>() {
            public void changed(ObservableValue<? extends RosterEntry> ov, RosterEntry oldVal, RosterEntry newVal) {
                if (btPartners.isSelected()) {

                } else if (btContacts.isSelected()) {
                    if (newVal != null) {
                        jabber.setSelectedContact(newVal.getUser());
                        bottomListLabel.setVisible(true);
                        messageList.setVisible(true);
                        messageBox.setVisible(true);
                    } else {
                        messageBox.setVisible(false);
                    }
                } else if (btSketches.isSelected()) {

                }
            }
        });

        notificationPane = new NotificationPane(main);
        return notificationPane;
    }

    private void sendMessageToSelectedUser() {
        String msg = messageField.getText();
        if (msg.isEmpty()) {
            return;
        }

        try {
            sendMessageToSelectedUser(msg);

        } catch (XMPPException e) {
            System.err.println("Could not send message " + msg);
            e.printStackTrace();
        }
        messageField.setText("");
    }

    private void sendMessageToSelectedUser(String message) throws XMPPException {
        if (btPartners.isSelected()) {

        } else if (btContacts.isSelected()) {
            RosterEntry selected = userList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                jabber.sendMessage(message, selected.getUser());
            }
        }
    }

    private static String getUser(String from) {
        if (from != null && from.indexOf("@") > 0) {
            String usr = from.substring(0, from.indexOf("@"));

            return usr;
        }

        return null;
    }

    static class MessageCell extends ListCell<Message> {
        private XmppConnector jabber;

        private Text from = new Text();
        private Text message = new Text();
        private VBox box = new VBox(from, message);

        public MessageCell(XmppConnector jabber) {
            this.jabber = jabber;
            init();
        }

        private void init() {
            from.setFont(Font.font("Verdana", FontWeight.BOLD, from.getFont().getSize()));
        }

        private void clear() {
            box.setStyle("");
            from.setText("");
            message.setText("");
        }

        @Override
        public void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                if (item.getFrom().equals(jabber.getXmppUser().getValue())) {
                    box.setStyle("-fx-background-color: #336699;");
                } else {
                    box.setStyle("-fx-background-color: #44aa99;");
                }

                String usr = getUser(item.getFrom());
                from.setText(usr);
                message.setText(item.getBody());

                setGraphic(box);
            } else {
                clear();
            }
        }
    }

    static class RosterEntryCell extends ListCell<RosterEntry> {
        private Text user = new Text();
        private VBox box = new VBox(user);

        public RosterEntryCell() {
            init();
        }

        private void init() {

        }

        private void clear() {
            box.setStyle("");
            user.setText("");
        }

        @Override
        public void updateItem(RosterEntry item, boolean empty) {
            super.updateItem(item, empty);
            if (getGraphic() == null) {
                setGraphic(box);
            }
            if (item != null) {
                user.setText(item.getName());
            } else {
                clear();
            }
        }
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

    }

    private void xmppConnectLogin() {
        String e = "";

        Preferences p = PreferenceLoader.getInstance().getCurrentPreferences();
        String host = p.get(PreferenceWrapper.JABBER_HOST, e);
        int port = p.getInt(PreferenceWrapper.JABBER_PORT, 0);
        String user = p.get(PreferenceWrapper.JABBER_USER, e);
        String pw = p.get(PreferenceWrapper.JABBER_PASSWORD, e);
        boolean autoConnect = p.getBoolean(PreferenceWrapper.JABBER_AUTO_CONNECT, true);

        jabber.setConnectionData(host, port, user, pw);
        if (autoConnect) {
            System.out.println("Auto-connecting to " + host + " at port " + port + " as user " + user);
            jabber.connectAndLoginAsync();
        } else {
            System.out.println("Not Auto-connecting to " + host + " at port " + port + " as user " + user);
        }
    }

    private static String array2String(Object[] array) {
        String str = "";
        for (Object o : array) {
            if (!str.isEmpty()) {
                str += ", ";
            }
            str += o;
        }

        return "[" + str + "]";
    }

    public static void main(String[] args) throws BackingStoreException {
        // TODO save preferences again
        // TODO disconnect from xmpp???

        final int MAX_ARGS = 5;

        if (!(args.length == 0 || args.length == MAX_ARGS)) {
            System.err.println("Argument length is " + args.length);
            System.err.println("Arguments must be exactly 0 or 5 and contain: Xmpp host, port, user, password "
                    + "and a boolean flag if auto-connect to xmpp");
            System.err.println("Arguments are instead: " + array2String(args));

            System.exit(-1);
        }

        PreferenceLoader loader = PreferenceLoader.getInstance();
        if (args.length == MAX_ARGS) {
            Preferences temp = getArgsPreferences(args);
            loader.setCurrentPreferences(temp);
            System.out.println("Using temporary Preferences");
        } else {
            System.out.println("Loading Preferences");
            String mainPrefName = "Main.pref";
            if (!loader.setCurrentPreferences(mainPrefName)) {
                System.err.println("Could not load " + mainPrefName + " ; Known preferences are "
                        + loader.propertyPreferenceFileList());
            }
        }

        Preferences p = loader.getCurrentPreferences();
        if (p == null) {
            System.err.println("Could not find any preferences");
            System.exit(-1);
        }

        // PreferenceWrapper.printPreference(p);

        // try {
        // PreferenceWrapper.save();
        // } catch (IOException | BackingStoreException e) {
        // System.err.println("Could not save preferences");
        // e.printStackTrace();
        // }

        System.out.println("Launching App");
        Application.launch(TestGrid.class);
    }

    private static Preferences getArgsPreferences(String[] args) throws BackingStoreException {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String user = args[2];
        String pw = args[3];
        boolean autoConnect = Boolean.parseBoolean(args[4]);
        return PreferenceWrapper.create("temp", host, port, user, pw, autoConnect);
    }

}
