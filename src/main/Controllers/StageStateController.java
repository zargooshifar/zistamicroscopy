package main.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import main.Singletons;

import java.io.IOException;

public class StageStateController extends VBox {

    @FXML
    private ComboBox<String> unitComboBox;
    @FXML
    private Text xpos;

    @FXML
    private Text ypos;

    @FXML
    private Text zpos;

    @FXML
    private Spinner<Integer> smallStepSizeSpinner;

    @FXML
    private Spinner<Integer> mediumStepSizeSpinner;

    @FXML
    private Spinner<Integer> largeStepSizeSpinner;

    private int unitMultiplier = 1;

    public StageStateController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/stageState.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);


        unitComboBox.getItems().addAll("0.1 µm", "µm", "mm", "cm");
        unitComboBox.setValue("0.1 µm");
        unitComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "0.1 µm":
                    unitMultiplier = 1;
                    break;
                case "µm":
                    unitMultiplier = 10;
                    break;
                case "mm":
                    unitMultiplier = 10000;
                    break;
                case "cm":
                    unitMultiplier = 100000;
                    break;
            }
        });

        smallStepSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        mediumStepSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 1000, 100));
        largeStepSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1000, 10000, 1000));


        smallStepSizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Singletons.getStageControllerInstance().getStepSizes().setSmallStep(newValue);
        });

        mediumStepSizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Singletons.getStageControllerInstance().getStepSizes().setMediumStep(newValue);
        });

        largeStepSizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Singletons.getStageControllerInstance().getStepSizes().setLargeStep(newValue);
        });


    }


    public void updatePosition(double x, double y, double z) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                xpos.setText(String.format("%.1f", x));
                ypos.setText(String.format("%.1f", y));
                zpos.setText(String.format("%.1f", z));
            }
        });
    }
}
