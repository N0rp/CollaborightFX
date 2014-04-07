package eu.dowsing.collaborightfx.view.painting;

import java.util.LinkedList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

/**
 * An event handler for toggle buttons.
 * 
 * @author richardg
 * 
 * @param <L>
 */
public class ToggleButtonEventHandler<L> implements EventHandler<ActionEvent> {

    private List<ListView<L>> list = new LinkedList<>();
    private List<ObservableList<L>> data = new LinkedList<>();
    private List<Text> labels = new LinkedList<>();
    private List<String> labelText = new LinkedList<>();

    private List<Node> show = new LinkedList<>();
    private List<Node> hide = new LinkedList<>();

    public ToggleButtonEventHandler(ListView<L> list, ObservableList<L> data, Text label, String labelText) {
        addListAndData(list, data, label, labelText);
    }

    /**
     * Add a list and the content that will be displayed, when this button is selected. The list is set to visible
     * automatically.
     * 
     * @param list
     * @param data
     * @return
     */
    public ToggleButtonEventHandler<L> addListAndData(ListView<L> list, ObservableList<L> data, Text label,
            String labelText) {
        this.list.add(list);
        this.data.add(data);
        this.labels.add(label);
        this.labelText.add(labelText);
        return this;
    }

    /**
     * Add a node that will be shown when this button is selected.
     * 
     * @param show
     * @return
     */
    public ToggleButtonEventHandler<L> addShow(Node... show) {
        for (Node s : show) {
            this.show.add(s);
        }
        return this;
    }

    /**
     * Add a node that will be hidden when this button is selected.
     * 
     * @param hide
     * @return
     */
    public ToggleButtonEventHandler<L> addHide(Node... hide) {
        for (Node h : hide) {
            this.hide.add(h);
        }
        return this;
    }

    /**
     * Set the button as selected and display content.
     * 
     * @param button
     * @return
     */
    public ToggleButtonEventHandler<L> setSelected(ToggleButton button) {
        button.setSelected(true);
        showContent();
        return this;
    }

    private void showContent() {
        for (int i = 0; i < list.size(); i++) {
            this.list.get(i).setItems(data.get(i));
            this.labels.get(i).setText(this.labelText.get(i));
            this.list.get(i).setVisible(true);
            this.labels.get(i).setVisible(true);
        }
        // hide has lower priority than show
        for (Node h : hide) {
            h.setVisible(false);
        }
        for (Node s : show) {
            s.setVisible(true);
        }
    }

    @Override
    public void handle(ActionEvent event) {
        showContent();
    }

}
