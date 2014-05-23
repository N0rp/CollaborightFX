package eu.dowsing.collaborightfx.view.app.sidebar.conversation;

import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import org.jivesoftware.smack.packet.Message;

import eu.dowsing.collaborightfx.xmpp.app.XmppConnector;

public class ConversationCell extends ListCell<Message> {
    private XmppConnector jabber;

    private Text from = new Text();
    private Text message = new Text();
    private VBox box = new VBox(from, message);

    public ConversationCell(XmppConnector jabber) {
        this.jabber = jabber;
        init();
    }

    private void init() {
        from.setFont(Font.font("Verdana", FontWeight.BOLD, from.getFont().getSize()));
    }

    private static String getUser(String from) {
        if (from != null && from.indexOf("@") > 0) {
            String usr = from.substring(0, from.indexOf("@"));

            return usr;
        }

        return null;
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
