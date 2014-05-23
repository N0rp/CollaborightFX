package eu.dowsing.collaborightfx.view.app;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SketchDetailsBox extends VBox {

    public SketchDetailsBox() {
        Text hunger = new Text("Hunger");
        getChildren().add(hunger);
    }

}
