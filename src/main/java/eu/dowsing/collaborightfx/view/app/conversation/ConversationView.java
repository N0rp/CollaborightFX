package eu.dowsing.collaborightfx.view.app.conversation;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;

import eu.dowsing.collaborightfx.sketch.Sketch;
import eu.dowsing.collaborightfx.sketch.toolbox.ToolBarSettings;
import eu.dowsing.collaborightfx.view.app.toolbar.ToolBar;
import eu.dowsing.collaborightfx.view.sketch.SketchView;
import eu.dowsing.collaborightfx.xmpp.app.XmppConnector;

/**
 * Shows the conversation. That includes messages, user (dis)connect and in the background possibly a sketch.
 * 
 * @author richardg
 *
 */
public class ConversationView extends HBox {

    /** Contains sketch and messages. **/
    private StackPane sketchAndMsgStack = new StackPane();

    /** Contains the sketch. **/
    private SketchView sketchView;

    private ToolBar toolbar;

    private VBox messageListBox = new VBox();
    private Pane messageSendBox = new HBox();
    private Pane stackAndMsgSendBox = new VBox();
    private ObservableList<String> unreadMessages = FXCollections.observableArrayList("Old", "Not so old", "Latest");
    private ObservableList<String> allMessages = FXCollections.observableArrayList("First", "Very Old", "Old",
            "Not so old", "Latest");
    private ListView<String> messageList = new ListView<>();
    private Button bShowunreadMmsg = new Button("");
    private TextField messageField = new TextField();
    private Button messageSend = new Button("Send");

    private int unreadCount = 0;

    private XmppConnector xmpp;

    /** Maximum number of unread messages. **/
    private final int MAX_UNREAD_MSG = 3;

    public ConversationView(XmppConnector xmpp, ToolBarSettings toolData, Sketch sketch, double maxWidth,
            double maxHeight) {
        this.xmpp = xmpp;
        initLayout(toolData, sketch, maxWidth);
        initData();
        initControl(maxHeight);
    }

    public RosterEntry getConversationPartner() {
        // TODO
        return null;
    }

    private void initLayout(ToolBarSettings toolBarSettings, Sketch sketch, double maxWidth) {

        /* **********************
         * Load Painting
         */
        sketchView = new SketchView(toolBarSettings, sketch, maxWidth, 600);
        toolbar = new ToolBar(toolBarSettings, sketchView);

        /* **********************
         * Load Message Area
         */
        messageListBox.getChildren().addAll(messageList);
        messageSendBox.getChildren().addAll(bShowunreadMmsg, messageField, messageSend);

        messageList.setPrefHeight(100);
        messageListBox.alignmentProperty().set(Pos.BOTTOM_CENTER);

        // messages display on top of the current sketch

        sketchAndMsgStack.getChildren().addAll(sketchView, messageListBox);
        stackAndMsgSendBox.getChildren().addAll(sketchAndMsgStack, messageSendBox);
        getChildren().addAll(toolbar, stackAndMsgSendBox);
    }

    private void initData() {
        messageList.setItems(unreadMessages);

    }

    private void initControl(final double maxHeight) {

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

        bShowunreadMmsg.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent ke) {
                // show either the unread or the list of all the messages
                if (messageList.getItems().equals(unreadMessages)) {
                    System.out.println("Showing All Messages: " + allMessages.size());
                    // show all messages and maximize the list
                    messageList.setItems(allMessages);
                    messageList.setPrefHeight(sketchView.getHeight());
                } else if (messageList.getItems().equals(allMessages)) {
                    // show only unread messages, since we have seen all messages, none are unread
                    System.out.println("Showing unread Messages: " + unreadMessages.size());
                    unreadMessages.clear();
                    messageList.setItems(unreadMessages);
                    messageList.setPrefHeight(getUnreadMsgPrefHeight());
                }
            }
        });

        unreadMessages.addListener(new ListChangeListener<String>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends String> change) {
                // automatically adjust preferred height if unread messages change
                if (messageList.getItems().equals(unreadMessages)) {
                    messageList.setPrefHeight(getUnreadMsgPrefHeight());

                    if (unreadMessages.size() <= MAX_UNREAD_MSG) {
                        unreadCount = unreadMessages.size();
                    } else if (unreadMessages.size() > MAX_UNREAD_MSG) {
                        unreadCount++;
                        unreadMessages.remove(0);
                    }
                    bShowunreadMmsg.setText(unreadCount + "");
                }
            }
        });

        sketchAndMsgStack.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                // TODO : let the mouse event through to the sketch view
            }
        });
        // messageList.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
        // @Override
        // public ListCell<Message> call(ListView<Message> list) {
        // return new MessageCell(xmpp);
        // }
        // });
        // userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RosterEntry>() {
        // public void changed(ObservableValue<? extends RosterEntry> ov, RosterEntry oldVal, RosterEntry newVal) {
        // if (btPartners.isSelected()) {
        //
        // } else if (btContacts.isSelected()) {
        // if (newVal != null) {
        // jabber.getHistory().setSelectedContact(newVal.getUser());
        // bottomListLabel.setVisible(true);
        // messageList.setVisible(true);
        // messageBox.setVisible(true);
        // } else {
        // messageBox.setVisible(false);
        // }
        // } else if (btSketches.isSelected()) {
        //
        // }
        // }
        // });
    }

    private final int ROW_HEIGHT = 24;

    private double getUnreadMsgPrefHeight() {
        return unreadMessages.size() * ROW_HEIGHT + 2;
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
        // if (btPartners.isSelected()) {
        //
        // } else if (btContacts.isSelected()) {
        // RosterEntry selected = userList.getSelectionModel().getSelectedItem();
        // if (selected != null) {
        // jabber.getSender().sendMessage(message, selected.getUser());
        // }
        // }
        allMessages.add(message);
        unreadMessages.add(message);
    }
}
