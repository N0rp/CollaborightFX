package eu.dowsing.collaborightfx.view.app.sidebar.contacts;

import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.jivesoftware.smack.RosterEntry;

public class RosterEntryCell extends ListCell<RosterEntry> {
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
