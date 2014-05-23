package eu.dowsing.collaborightfx.view.app.toolbar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.controlsfx.control.SegmentedButton;

import eu.dowsing.collaborightfx.sketch.toolbox.ToolBarSettings;
import eu.dowsing.collaborightfx.sketch.toolbox.ToolBarSettings.ToolChoice;
import eu.dowsing.collaborightfx.view.sketch.SketchView;

public class ToolBar extends VBox {

    private ToggleButton tbDraw = new ToggleButton("Draw");
    private ToggleButton tbText = new ToggleButton("Text");
    private ToggleButton tbSelect = new ToggleButton("Select");
    private SegmentedButton toolButtons = new SegmentedButton(tbDraw, tbText, tbSelect);

    private ColorPicker strokePicker = new ColorPicker();

    private ObservableList<Double> lineWidthOptions = FXCollections.observableArrayList(1.0, 2.0, 5.0, 7.5, 10.0, 15.0);
    private ComboBox<Double> lineWidthCombo = new ComboBox<>(lineWidthOptions);

    private SketchView sketchView;

    private Button bClearSketch = new Button("Clear");

    private ToolBarSettings toolData;

    public ToolBar(ToolBarSettings toolData, SketchView sketchView) {
        this.sketchView = sketchView;
        this.toolData = toolData;
        init();
        initData(toolData);
    }

    public ToolBarSettings getToolData() {
        return toolData;
    }

    private void init() {
        Text header = new Text("Sketch");

        Button bExportSketch = new Button("Export");
        Button details = new Button("Details");
        // control
        strokePicker.setValue(sketchView.getStrokeColor());
        lineWidthCombo.setEditable(true);
        lineWidthCombo.setPromptText("Line Width");
        lineWidthCombo.setValue(sketchView.getLineWidth());

        tbDraw.setSelected(true);

        getChildren().addAll(header, details, bExportSketch, toolButtons, strokePicker, lineWidthCombo, bClearSketch);

        tbDraw.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                toolData.setTool(ToolChoice.DRAW);
            }
        });
        tbText.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                toolData.setTool(ToolChoice.TEXT);
            }
        });
        tbSelect.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                toolData.setTool(ToolChoice.SELECT);
            }
        });

        strokePicker.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                sketchView.setStrokeColor(strokePicker.getValue());
            }
        });

        // lineWidth picker
        lineWidthCombo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {
                    sketchView.setLineWidth(lineWidthCombo.getValue());
                } catch (ClassCastException e) {
                    System.out.println("TestGrid: Info line width was wrong");
                    lineWidthCombo.setValue(1.0);
                    lineWidthCombo.setValue(sketchView.getLineWidth());
                }

            }
        });

        bClearSketch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                // TODO clear sketch
            }
        });
    }

    private void initData(ToolBarSettings toolData) {
        switch (toolData.getTool()) {
            case DRAW:
                tbDraw.setSelected(true);
                break;
            case TEXT:
                tbText.setSelected(true);
                break;
            case SELECT:
                tbSelect.setSelected(true);
                break;
        }
    }
}
