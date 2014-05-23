package eu.dowsing.collaborightfx.view.app;

import java.io.IOException;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.NotificationPane;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.simpleframework.xml.core.ValueRequiredException;

import eu.dowsing.collaborightfx.persistence.PreferenceLoader;
import eu.dowsing.collaborightfx.persistence.PreferenceWrapper;
import eu.dowsing.collaborightfx.sketch.OnConstructUpdateListener;
import eu.dowsing.collaborightfx.sketch.Sketch;
import eu.dowsing.collaborightfx.sketch.SketchLoader;
import eu.dowsing.collaborightfx.sketch.structure.Shape;
import eu.dowsing.collaborightfx.sketch.toolbox.ToolBarSettings;
import eu.dowsing.collaborightfx.view.app.conversation.ConversationView;
import eu.dowsing.collaborightfx.view.app.sidebar.SideBar;
import eu.dowsing.collaborightfx.xmpp.app.RemoteConstructUpdateListener;
import eu.dowsing.collaborightfx.xmpp.app.XmppConnector;
import eu.dowsing.collaborightfx.xmpp.app.XmppConnector.ConnectStatus;

public class AppView extends Application implements OnConstructUpdateListener {

    private SketchLoader sketchLoader = new SketchLoader("res/sketch/");
    private final XmppConnector jabber = new XmppConnector();

    private Sketch sketch;

    private Button bHideShow = new Button("Hide");
    private NotificationPane notificationPane;

    private SideBar sidebar;

    private static final String APP_TITLE = "Collaboright";

    /** **/
    private final boolean updateDrawLive = false;

    private ConversationView drawArea;

    private ToolBarSettings toolData = new ToolBarSettings();

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
            // add the listener that will be called every time a construct is created/moved/changed
            sketch.addOnConstructUpdateListener(this);
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

    @Override
    public void onConstructUpdate(Shape shape, boolean isRemote, Type type) {
        if (!isRemote) {
            if (type == OnConstructUpdateListener.Type.CREATE_DONE
                    || type == OnConstructUpdateListener.Type.UPDATE_DONE
                    || (type == OnConstructUpdateListener.Type.UPDATE_IN_PROGRESS && updateDrawLive)) {
                RosterEntry entry = drawArea.getConversationPartner();
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
        }
    }

    private Control createAndInitUI(double maxWidth, double maxHeight) {
        drawArea = new ConversationView(jabber, toolData, sketch, maxWidth, maxHeight);
        /* **********************
         * Create layout
         */
        // main
        Pane main = new VBox();

        // control
        BorderPane contentHeaderBox = new BorderPane();
        Text sketchName = new Text("SketchName");
        Text sketchUsers = new Text("SketchUsers");
        contentHeaderBox.getChildren().addAll(sketchName, sketchUsers, bHideShow);

        // details
        sidebar = new SideBar(jabber);

        // main

        final MasterDetailPane contentBox = new MasterDetailPane();
        contentBox.setMasterNode(drawArea);
        contentBox.setDetailNode(sidebar);
        contentBox.setDetailSide(Side.RIGHT);
        // contentBox.setShowDetailNode(true);
        // contentBox.setDividerPosition(200);

        StackPane contentPane = new StackPane();
        contentPane.getChildren().addAll(contentBox);

        // finally
        main.getChildren().addAll(contentHeaderBox, contentPane);

        /* **********************
         * Fill layout
         */
        ButtonBar.setType(bHideShow, ButtonType.RIGHT);

        /* **********************
         * Create Control
         */

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
        jabber.getReceiver().addOnRemoteConstructUpdateListener(new RemoteConstructUpdateListener() {

            @Override
            public void onRemoteConstructUpdate(Shape shape) {
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
