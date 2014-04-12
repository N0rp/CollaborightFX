package eu.dowsing.collaborightfx.app;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class PaintDetailsBox extends VBox {

    public PaintDetailsBox() {
        Text hunger = new Text("Hunger");
        getChildren().add(hunger);
    }

}
