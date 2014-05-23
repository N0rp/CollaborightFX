package eu.dowsing.collaborightfx.view.app.sidebar.media;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import eu.dowsing.collaborightfx.com.media.Media;
import eu.dowsing.collaborightfx.xmpp.app.XmppConnector;

/**
 * Shows all media that the user has received, created.
 * 
 * @author richardg
 *
 */
public class MediaBar extends VBox {

    private final ContextMenu cm = new ContextMenu();
    private XmppConnector xmpp;

    private ListView<Media> mediaList = new ListView<>();

    public MediaBar(XmppConnector xmpp) {
        this.xmpp = xmpp;
        init();
        initContextMenu();
    }

    private void init() {
        getChildren().addAll(new Text("Media"), mediaList);
    }

    private void initContextMenu() {
        MenuItem cmItem1 = new MenuItem("Copy Media");
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
