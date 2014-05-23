package eu.dowsing.collaborightfx.view.app.sidebar.contacts;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import org.jivesoftware.smack.RosterEntry;

import eu.dowsing.collaborightfx.xmpp.app.XmppConnector;

/**
 * Displays all the contacts.
 * 
 * @author richardg
 *
 */
public class ContactsBar extends VBox {

    private final ContextMenu cm = new ContextMenu();
    private ListView<RosterEntry> userList = new ListView<>();
    private XmppConnector xmpp;

    public ContactsBar(XmppConnector xmpp) {
        this.xmpp = xmpp;
        init();
        initControl();
        initContextMenu();
    }

    private void init() {
        Text header = new Text("Contacts");

        getChildren().addAll(header, userList);
    }

    private void initControl() {
        userList.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.SECONDARY) {
                    RosterEntry entry = userList.getSelectionModel().getSelectedItem();
                    if (entry != null) {
                        // cm.show(userList, e.getScreenX(), e.getScreenY());
                    } else {
                        // cm.show(btContacts, e.getScreenX(), e.getScreenY());
                    }
                }
            }
        });
        userList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // set list cell factories
        userList.setCellFactory(new Callback<ListView<RosterEntry>, ListCell<RosterEntry>>() {
            @Override
            public ListCell<RosterEntry> call(ListView<RosterEntry> list) {
                return new RosterEntryCell();
            }
        });
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

    public RosterEntry getSelectedUser() {
        return userList.getSelectionModel().getSelectedItem();
    }
}
