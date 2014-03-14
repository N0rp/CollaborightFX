package eu.dowsing.collaborightfx.app;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class TestGrid extends Application {

    @Override
    public void start(Stage primaryStage) {
        GridPane gridpane = new GridPane();

        // Set one constraint at a time...
        Button button = new Button();
        button.setText("Button");
        GridPane.setRowIndex(button, 1);
        GridPane.setColumnIndex(button, 2);

        // or convenience methods set more than one constraint at once...
        Label label = new Label();
        label.setText("Foo");
        GridPane.setConstraints(label, 3, 1); // column=3 row=1

        // don't forget to add children to gridpane
        gridpane.getChildren().addAll(button, label);
        AnchorPane root = new AnchorPane();
//        root.getChildren().add(gridpane);
        String[][] data = new String[][]{{"first:1", "first:2"},{"second:first", "second:second"}};
        showData(data, gridpane);
        
        final Scene scene = new Scene(gridpane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // TODO implement graphs with JUNG, JFreeChart or JGraphX
    }
    
    /**
     * Show data in the list grid
     * 
     * @param data the data to show, first array is row, second defines column. data[1][2] is the first row, second column
     */
    private void showData(String[][] data, GridPane grid){
        grid.getChildren().clear();
        for(int r = 0; r < data.length; r++){
            String[] row = data[r];
            for(int c = 0; c < row.length; c++){
                String column = row[c];
                TextField cell = new TextField();
                cell.setText(column);
                GridPane.setConstraints(cell, c, r);
                grid.getChildren().add(cell);
            }
        }
    }
    
    public static void main(String[] args) {
        Application.launch(TestGrid.class);
    }
    
}
