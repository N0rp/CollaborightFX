package eu.dowsing.collaborightfx.view;

import java.util.LinkedList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

/**
 * An event handler for toggle buttons.
 * 
 * @author richardg
 * 
 * @param <L>
 */
public abstract class ToggleButtonShowHideHandler implements EventHandler<ActionEvent> {

    private List<Node> show = new LinkedList<>();
    private List<Node> hide = new LinkedList<>();

    /**
     * Add a node that will be shown when this button is selected.
     * 
     * @param show
     * @return
     */
    public ToggleButtonShowHideHandler addShow(Node... show) {
        for (Node s : show) {
            this.show.add(s);
            s.managedProperty().bind(s.visibleProperty());
        }
        return this;
    }

    /**
     * Add a node that will be hidden when this button is selected.
     * 
     * @param hide
     * @return
     */
    public ToggleButtonShowHideHandler addHide(Node... hide) {
        for (Node h : hide) {
            this.hide.add(h);
            h.managedProperty().bind(h.visibleProperty());
        }
        return this;
    }

    /**
     * Set the button as selected and display content.
     * 
     * @param button
     *            the button to which this toggle button handler belongs to and whose content will be displayed
     * @return
     */
    public ToggleButtonShowHideHandler setSelected(ToggleButton button) {
        button.setSelected(true);
        showContent();
        return this;
    }

    /**
     * Called when the toggle button is clicked and some content needs to be shown or hidden. Afterwards first the hide,
     * then the show nodes are used.
     */
    protected abstract void onShowHideContent();

    private void showContent() {
        onShowHideContent();

        // System.out.println("ToggleButtonAbstract: Showing: " + show.size() + " Hiding: " + hide.size());
        // hide has lower priority than show
        for (Node h : hide) {
            h.setVisible(false);
        }
        for (Node s : show) {
            // s.managedProperty().unbind();
            s.setVisible(true);
        }
    }

    @Override
    public void handle(ActionEvent event) {
        showContent();
    }

}
