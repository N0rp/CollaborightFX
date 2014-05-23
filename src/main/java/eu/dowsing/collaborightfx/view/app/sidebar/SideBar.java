package eu.dowsing.collaborightfx.view.app.sidebar;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

import org.controlsfx.control.SegmentedButton;

import eu.dowsing.collaborightfx.view.BasicToggleButtonHandler;
import eu.dowsing.collaborightfx.view.app.sidebar.contacts.ContactsBar;
import eu.dowsing.collaborightfx.view.app.sidebar.conversation.ConversationBar;
import eu.dowsing.collaborightfx.view.app.sidebar.media.MediaBar;
import eu.dowsing.collaborightfx.xmpp.app.XmppConnector;

public class SideBar extends VBox {

    private UserInfoView xmppUserInfo;
    private Button bXUser;

    private ToggleButton tbMedia = new ToggleButton("M");
    private ToggleButton tbPeople = new ToggleButton("P");
    private ToggleButton tbConversations = new ToggleButton("C");
    private SegmentedButton tabButtons = new SegmentedButton(tbMedia, tbPeople, tbConversations);

    private VBox mediaBox = new VBox();
    private VBox conversationBox = new VBox();
    private VBox peopleBox = new VBox();

    private XmppConnector xmpp;

    public SideBar(XmppConnector jabber) {
        this.xmpp = jabber;
        this.xmppUserInfo = new UserInfoView(jabber);
        this.bXUser = xmppUserInfo.getPopOverButton();

        init(jabber);
        initData();
    }

    private void init(XmppConnector xmpp) {
        MediaBar mediaBar = new MediaBar(xmpp);
        ConversationBar conversationBar = new ConversationBar(xmpp);
        ContactsBar contactsBar = new ContactsBar(xmpp);

        mediaBox.getChildren().addAll(mediaBar);
        conversationBox.getChildren().addAll(conversationBar);
        peopleBox.getChildren().addAll(contactsBar);

        // details
        this.getChildren().addAll(bXUser, tabButtons, mediaBox, conversationBox, peopleBox);

    }

    private void initData() {
        tbMedia.setOnAction(new BasicToggleButtonHandler().addHide(conversationBox, peopleBox).addShow(mediaBox)
                .setSelected(tbMedia));
        tbConversations.setOnAction(new BasicToggleButtonHandler().addHide(mediaBox, peopleBox)
                .addShow(conversationBox));
        tbPeople.setOnAction(new BasicToggleButtonHandler().addHide(mediaBox, conversationBox).addShow(peopleBox));

        // btContacts.setOnAction(new ToggleButtonListContentHandler(userList, jabber.getXmppOnlineContacts(),
        // upperListLabel, "Contacts").addListAndData(messageList,
        // jabber.getHistory().getXmppSelectedContactChat(), bottomListLabel, "ContactMessages").addHide(
        // sketchList, messageList, bottomListLabel, messageBox));

        // btSketches.setOnAction(new ToggleButtonEventHandler(sketchList, sketchLoader.getSketchFileNames(),
        // upperListLabel, "Sketches").addHide(userList, messageList, bottomListLabel, messageBox));
    }

}
