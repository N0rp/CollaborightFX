package eu.dowsing.collaborightfx.app.xmpp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

/**
 * Updates all the label's text with a prefix, the new value and a postfix.
 * 
 * @author richardg
 * 
 * @param <T>
 */
public class ChangeUpdateTextHandler<K> implements ChangeListener<K> {

    private Text[] labels = new Text[0];
    private Button[] buttons = new Button[0];
    private String prefix;
    private String postfix;

    public ChangeUpdateTextHandler(String prefix, String postfix) {
        this.prefix = prefix;
        this.postfix = postfix;
    }

    public ChangeUpdateTextHandler<K> setLables(Text... labels) {
        this.labels = labels;
        return this;
    }

    public ChangeUpdateTextHandler<K> setButtons(Button... buttons) {
        this.buttons = buttons;
        return this;
    }

    @Override
    public void changed(ObservableValue<? extends K> observable, K oldValue, K newValue) {
        // create text
        String text = "" + prefix;
        if (!prefix.toString().isEmpty()) {
            text += " ";
        }
        text += newValue;
        if (!postfix.toString().isEmpty()) {
            text += " ";
        }
        text += postfix;

        // update buttons and labels
        for (Button b : buttons) {
            b.setText(text);
        }
        for (Text l : labels) {
            l.setText(text);
        }
    }
}
