package eu.dowsing.collaborightfx.view.app.sidebar.conversation;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import eu.dowsing.collaborightfx.com.contacts.Person;
import eu.dowsing.collaborightfx.xmpp.app.XmppConnector;

/**
 * Holds all conversations with (multiple or single) users. Pinned at the top are the current
 * painting conversations.
 * 
 * @author richardg
 * 
 */
public class ConversationBar extends VBox {

    private final ContextMenu cm = new ContextMenu();
    private XmppConnector xmpp;

    private ListView<Person> conversationList = new ListView<>();

    public ConversationBar(XmppConnector xmpp) {
        this.xmpp = xmpp;
        init();
        initContextMenu();
    }

    private void init() {
        getChildren().addAll(new Text("Conversation"), conversationList);
    }

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
}
