package eu.dowsing.collaborightfx.app;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import org.controlsfx.control.SegmentedButton;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import eu.dowsing.collaborightfx.app.TestGrid.MessageCell;
import eu.dowsing.collaborightfx.app.TestGrid.RosterEntryCell;
import eu.dowsing.collaborightfx.app.xmpp.XmppConnector;
import eu.dowsing.collaborightfx.view.painting.ToggleButtonEventHandler;

public class DetailBox extends VBox {

    private Pane messageBox = new HBox();

    private Text upperListLabel = new Text("Upper");
    private Text bottomListLabel = new Text("Lower");

    private ListView<String> sketchList = new ListView<>();
    private ListView<RosterEntry> userList = new ListView<>();
    private ListView<Message> messageList = new ListView<>();
    private TextField messageField = new TextField();
    private Button messageSend = new Button("Send");

    private ToggleButton btPartners = new ToggleButton("Current");
    private ToggleButton btContacts = new ToggleButton("Contacts");
    private ToggleButton btSketches = new ToggleButton("Paintings");
    private SegmentedButton listButtons = new SegmentedButton(btPartners, btContacts, btSketches);

    private XmppConnector jabber;

    public DetailBox(XmppConnector jabber) {
        this.jabber = jabber;
        init();
        initData();
        initControl();
        initContextMenu();
    }

    private final ContextMenu cm = new ContextMenu();

    private void initContextMenu() {
        MenuItem cmItem1 = new MenuItem("Copy Image");
        cmItem1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                // content.putImage(pic.getImage());
                clipboard.setContent(content);
            }
        });

        cm.getItems().add(cmItem1);
    }

    public RosterEntry getSelectedUser() {
        return userList.getSelectionModel().getSelectedItem();
    }

    private void init() {
        VBox detailBox = this;
        Pane listBox = new VBox();

        // details
        detailBox.getChildren().addAll(listButtons, listBox);
        listBox.getChildren().addAll(upperListLabel, userList, sketchList, bottomListLabel, messageList, messageBox);
        messageBox.getChildren().addAll(messageField, messageSend);
    }

    private void initData() {
        btPartners.setOnAction(new ToggleButtonEventHandler(userList, jabber.getHistory()
                .getXmppSktechPartnerContacts(), upperListLabel, "Partners")
                .addListAndData(messageList, jabber.getHistory().getXmppSelectedPartnerChat(), bottomListLabel,
                        "PartnerMessages").addHide(sketchList).setSelected(btPartners).addShow(messageBox));

        btContacts.setOnAction(new ToggleButtonEventHandler(userList, jabber.getXmppOnlineContacts(), upperListLabel,
                "Contacts").addListAndData(messageList, jabber.getHistory().getXmppSelectedContactChat(),
                bottomListLabel, "ContactMessages").addHide(sketchList, messageList, bottomListLabel, messageBox));

        // btSketches.setOnAction(new ToggleButtonEventHandler(sketchList, sketchLoader.getSketchFileNames(),
        // upperListLabel, "Sketches").addHide(userList, messageList, bottomListLabel, messageBox));
    }

    private void initControl() {

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

        userList.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.SECONDARY) {
                    RosterEntry entry = userList.getSelectionModel().getSelectedItem();
                    if (entry != null) {
                        cm.show(userList, e.getScreenX(), e.getScreenY());
                    } else {
                        cm.show(btContacts, e.getScreenX(), e.getScreenY());
                    }
                }
            }
        });
        userList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RosterEntry>() {
            public void changed(ObservableValue<? extends RosterEntry> ov, RosterEntry oldVal, RosterEntry newVal) {
                if (btPartners.isSelected()) {

                } else if (btContacts.isSelected()) {
                    if (newVal != null) {
                        jabber.getHistory().setSelectedContact(newVal.getUser());
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
                jabber.getSender().sendMessage(message, selected.getUser());
            }
        }
    }

}
