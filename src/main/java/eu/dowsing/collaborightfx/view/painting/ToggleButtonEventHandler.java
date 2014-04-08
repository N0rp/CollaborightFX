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
public class ToggleButtonEventHandler implements EventHandler<ActionEvent> {

    private class ListDataTuple<L> {

        private final ListView<L> list;
        private final ObservableList<L> data;
        private final Text label;
        private final String labelText;

        private ListDataTuple(ListView<L> list, ObservableList<L> data, Text label, String labelText) {
            this.list = list;
            this.data = data;
            this.label = label;
            this.labelText = labelText;
        }

        public void show() {
            this.list.setItems(data);
            this.label.setText(this.labelText);

            this.list.setVisible(true);
            this.label.setVisible(true);
        }

    }

    private List<ListDataTuple<?>> data = new LinkedList<>();
    private List<Node> show = new LinkedList<>();
    private List<Node> hide = new LinkedList<>();

    public <L> ToggleButtonEventHandler(ListView<L> list, ObservableList<L> data, Text label, String labelText) {
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
    public <L> ToggleButtonEventHandler addListAndData(ListView<L> list, ObservableList<L> data, Text label,
            String labelText) {
        this.data.add(new ListDataTuple<>(list, data, label, labelText));
        return this;
    }

    /**
     * Add a node that will be shown when this button is selected.
     * 
     * @param show
     * @return
     */
    public ToggleButtonEventHandler addShow(Node... show) {
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
    public ToggleButtonEventHandler addHide(Node... hide) {
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
    public ToggleButtonEventHandler setSelected(ToggleButton button) {
        button.setSelected(true);
        showContent();
        return this;
    }

    private void showContent() {
        for (ListDataTuple<?> t : data) {
            t.show();
        }
        // hide has lower priority than show
        for (Node h : hide) {
            h.managedProperty().bind(h.visibleProperty());
            h.setVisible(false);
        }
        for (Node s : show) {
            s.managedProperty().unbind();
            s.setVisible(true);
        }
    }

    @Override
    public void handle(ActionEvent event) {
        showContent();
    }

}
