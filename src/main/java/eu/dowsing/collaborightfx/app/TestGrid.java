package eu.dowsing.collaborightfx.app;

import java.io.IOException;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.simpleframework.xml.core.ValueRequiredException;

import eu.dowsing.collaborightfx.app.xmpp.OnChangeUpdateTextListener;
import eu.dowsing.collaborightfx.app.xmpp.OnRemoteConstructUpdateListener;
import eu.dowsing.collaborightfx.app.xmpp.XmppConnector;
import eu.dowsing.collaborightfx.app.xmpp.XmppConnector.ConnectStatus;
import eu.dowsing.collaborightfx.preferences.PreferenceLoader;
import eu.dowsing.collaborightfx.preferences.PreferenceWrapper;
import eu.dowsing.collaborightfx.sketch.OnConstructUpdateListener;
import eu.dowsing.collaborightfx.sketch.Sketch;
import eu.dowsing.collaborightfx.sketch.SketchLoader;
import eu.dowsing.collaborightfx.sketch.structure.Shape;
import eu.dowsing.collaborightfx.view.painting.SketchView;

public class TestGrid extends Application {

    private SketchLoader sketchLoader = new SketchLoader("res/sketch/");
    private final XmppConnector jabber = new XmppConnector();

    private ObservableList<Integer> lineWidthOptions = FXCollections.observableArrayList(1, 2, 5, 10);
    private ComboBox<Integer> lineWidthCombo = new ComboBox<>(lineWidthOptions);

    private Sketch sketch;
    private SketchView sketchView;

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

    private ToggleButton tbDraw = new ToggleButton("Draw");
    private ToggleButton tbText = new ToggleButton("Text");
    private ToggleButton tbSelect = new ToggleButton("Select");
    private SegmentedButton toolButtons = new SegmentedButton(tbDraw, tbText, tbSelect);

    private NotificationPane notificationPane;

    private DetailBox detailBox;

    private static final String APP_TITLE = "Collaboright";

    @Override
    public void start(Stage primaryStage) {
        int width = 800;
        int height = 800;
        loadSketch();
        Control pane = createAndInitUI(width, height);
        initXmpp();

        final Scene scene = new Scene(pane, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();

        // set title
        Preferences p = PreferenceLoader.getInstance().getCurrentPreferences();
        String user = p.get(PreferenceWrapper.Keys.JABBER_USER.toString(), "Geoffrey");

        primaryStage.setTitle(APP_TITLE + " - " + user);

        xmppConnectLogin();
    }

    @Override
    public void stop() {
        System.out.println("JavaFx Stop");
        jabber.disconnect();
    }

    private void loadSketch() {
        try {
            Preferences p = PreferenceLoader.getInstance().getCurrentPreferences();
            String openSketch = p.get(PreferenceWrapper.Keys.SKETCH_OPEN.toString(), "default.skml");
            sketch = sketchLoader.loadSketch(openSketch, true);
            sketch.addOnConstructUpdateListener(new OnConstructUpdateListener() {

                @Override
                public void onCosntructUpdate(Shape shape, boolean create) {
                    System.out.println("On structure update for " + shape);

                    RosterEntry entry = detailBox.getSelectedUser();
                    if (entry != null) {
                        System.out.println("Sending sketch update to " + entry.getUser());
                        try {
                            jabber.getSender().sendSketchUpdate(entry.getUser(), shape);
                        } catch (XMPPException e) {
                            System.err.println("Could not send sketch update to user " + entry.getUser());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Cannot send sketch update because nothing was selected");
                    }
                }
            });
        } catch (ValueRequiredException e) {
            System.err.println("TestGrid: Could not load initial sketch because it is mallformed");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("TestGrid: Problem accessing file system");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("TestGrid: Some other error when loading initial painting");
            e.printStackTrace();
        }

    }

    private Control createAndInitUI(double maxWdith, double maxHeight) {
        /* **********************
         * Load Painting
         */
        sketchView = new SketchView(sketch, maxWdith, 600);

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
        detailBox = new DetailBox(jabber);
        PaintDetailsBox paintDetailBox = new PaintDetailsBox();

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
        strokePicker.setValue(sketchView.getFillColor());
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
        sketchBox.getChildren().addAll(sketchView);

        /* **********************
         * Create Control
         */

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
                sketchView.setStrokeColor(strokePicker.getValue());
            }
        });

        // lineWidth picker
        lineWidthCombo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sketchView.setLineWidth(lineWidthCombo.getValue());
            }
        });

        notificationPane = new NotificationPane(main);
        return notificationPane;
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
                String nick = jabber.getNick(item.getFrom());
                from.setText(nick);
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
        jabber.propertyConnectedHost().addListener(new OnChangeUpdateTextListener<>("Host:", "").setLables(lXHost));
        jabber.getXmppPort().addListener(new OnChangeUpdateTextListener<>("Port:", "").setLables(lXPort));
        jabber.getXmppUser().addListener(
                new OnChangeUpdateTextListener<>("User:", "").setButtons(bUser).setLables(lXUser));
        jabber.getReceiver().addOnRemoteConstructUpdateListener(new OnRemoteConstructUpdateListener() {

            @Override
            public void onConstructUpdate(Shape shape) {
                sketch.addRemoteConstruct(shape);
            }
        });
    }

    private void xmppConnectLogin() {
        String e = "";

        Preferences p = PreferenceLoader.getInstance().getCurrentPreferences();
        String host = p.get(PreferenceWrapper.Keys.JABBER_HOST.toString(), e);
        int port = p.getInt(PreferenceWrapper.Keys.JABBER_PORT.toString(), 0);
        String user = p.get(PreferenceWrapper.Keys.JABBER_USER.toString(), e);
        String pw = p.get(PreferenceWrapper.Keys.JABBER_PASSWORD.toString(), e);
        boolean autoConnect = p.getBoolean(PreferenceWrapper.Keys.JABBER_AUTO_CONNECT.toString(), true);

        jabber.setConnectionData(host, port, user, pw);
        if (autoConnect) {
            System.out.println("Auto-connecting to " + host + " at port " + port + " as user " + user);
            jabber.connectAndLoginAsync();
        } else {
            System.out.println("Not Auto-connecting to " + host + " at port " + port + " as user " + user);
        }
    }

}
