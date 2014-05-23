package eu.dowsing.collaborightfx.view;

import java.util.LinkedList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

/**
 * An event handler for toggle buttons.
 * 
 * @author richardg
 * 
 * @param <L>
 */
public class ToggleButtonListContentHandler extends ToggleButtonShowHideHandler {

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

    public <L> ToggleButtonListContentHandler(ListView<L> list, ObservableList<L> data, Text label, String labelText) {
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
    public <L> ToggleButtonListContentHandler addListAndData(ListView<L> list, ObservableList<L> data, Text label,
            String labelText) {
        this.data.add(new ListDataTuple<>(list, data, label, labelText));
        return this;
    }

    @Override
    protected void onShowHideContent() {
        for (ListDataTuple<?> t : data) {
            t.show();
        }
    }

}
