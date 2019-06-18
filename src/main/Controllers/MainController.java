package main.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.viewer.ImageDisplayPane;
import main.utils.Prefs;
import main.viewer.canvas.FxImageCanvas;
import main.Singletons;
import main.viewer.overlay.*;
import mmcorej.CMMCore;
import mmcorej.StrVector;
import net.imagej.display.DefaultOverlayView;
import net.imagej.overlay.*;
import org.scijava.Context;
import org.scijava.plugin.Parameter;

import java.net.URL;
import java.util.ResourceBundle;

import main.utils.OverlayHelper;

public class MainController implements Initializable {


    @FXML
    private StackPane imageCanvasContainer;
    @FXML
    private AnchorPane cameraControllerContainer;
    @FXML
    private AnchorPane acquisitionControllerContainer;
    @FXML
    private VBox toolbarContainer;
    @FXML
    private HBox statusbarContainer;
    @FXML
    private AnchorPane stageStateContainer;
    @FXML
    private RadioMenuItem window_stagestate;
    @FXML
    private VBox sidePanel;

    @FXML
    private RadioMenuItem window_histogram;

    @FXML
    private RadioMenuItem window_deviceproperty;

    @FXML
    private TitledPane tile_stagestate;

    @FXML
    private TitledPane tile_histogram;

    @FXML
    private TitledPane tile_deviceproperty;
    @FXML
    private AnchorPane deviceProprtyContainer;

    @FXML
    private ScrollPane scrollSidePanel;





    @Parameter
    Context context;

    private static CMMCore core;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        core = Singletons.getCoreInstance();

        loadConfig();
        try {
            getProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }



        FxImageCanvas canvas = new FxImageCanvas();
        Singletons.getCameraControllerInstance().setCanvas(canvas);

        new ImageDisplayPane(canvas, imageCanvasContainer); //create and add imagedisplaypane

        imageCanvasContainer.getChildren().add(Singletons.getToolbarController());


//        imageCanvasContainer.getChildren().add(ic.getSwingNode(imageCanvasContainer));
        cameraControllerContainer.getChildren().add(Singletons.getCameraControllerInstance());
        acquisitionControllerContainer.getChildren().add(Singletons.getAcquisitionControllerInstance());
        statusbarContainer.getChildren().add(Singletons.getStatusbarControllerInstance());
        stageStateContainer.getChildren().add(Singletons.getStageStateInstance());

//        imageCanvasContainer.getChildren().add(Singletons.getStageControllerInstance());
//        toolbarContainer.getChildren().add(Singletons.getToolbarController());



        tile_stagestate.managedProperty().bind(tile_stagestate.visibleProperty());
        tile_histogram.managedProperty().bind(tile_histogram.visibleProperty());
        tile_deviceproperty.managedProperty().bind(tile_deviceproperty.visibleProperty());
        sidePanel.managedProperty().bind(sidePanel.visibleProperty());

        window_deviceproperty.selectedProperty().setValue(Prefs.Window.isDeviceProperty());
        window_histogram.selectedProperty().setValue(Prefs.Window.isHistogram());
        window_stagestate.selectedProperty().setValue(Prefs.Window.isStageState());


        tile_deviceproperty.visibleProperty().setValue(Prefs.Window.isDeviceProperty());
        tile_histogram.visibleProperty().setValue(Prefs.Window.isHistogram());
        tile_stagestate.visibleProperty().setValue(Prefs.Window.isStageState());
//        checkSidePanelEmptiness();


        window_stagestate.selectedProperty().addListener((observable, oldValue, newValue) -> {
            tile_stagestate.setVisible(newValue);
            Prefs.Window.setStageState(newValue);
//            checkSidePanelEmptiness();
        });

        window_histogram.selectedProperty().addListener((observable, oldValue, newValue) -> {
            tile_histogram.setVisible(newValue);
            Prefs.Window.setHistogram(newValue);
//            checkSidePanelEmptiness();

        });

        window_deviceproperty.selectedProperty().addListener((observable, oldValue, newValue) -> {
            tile_deviceproperty.setVisible(newValue);
            Prefs.Window.setDeviceProperty(newValue);
//            checkSidePanelEmptiness();

        });
    }

    private void checkSidePanelEmptiness(){
        if(!tile_stagestate.isVisible() && !tile_stagestate.isVisible() && !tile_histogram.isVisible()){
            sidePanel.visibleProperty().setValue(false);
        }else {
            sidePanel.visibleProperty().setValue(true);
        }
    }


    public void loadConfig() {
        String configAddress = "config.cfg";

        try {

            core.loadSystemConfiguration(configAddress);
            core.initializeAllDevices();
            Singletons.getCameraControllerInstance().loadCameraConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    private void getProperties() throws Exception {
        StrVector devices  =core.getLoadedDevices();
        VBox container = new VBox();
        container.setSpacing(4);
        for(String device: devices){
            VBox deviceContainer = new VBox();
            VBox.setMargin(deviceContainer, new Insets(0));

            deviceContainer.setSpacing(2);
            Label deviceLabel = new Label(device);
            VBox.setMargin(deviceLabel,new Insets(8,4,8,4));


            deviceContainer.getChildren().add(deviceLabel);
            StrVector properties = core.getDevicePropertyNames(device);
            for(String property: properties){
                HBox propertyContainer = new HBox();
                VBox.setMargin(propertyContainer, new Insets(0));

                Label propertyLabel = new Label(property);
                Pane pane = new Pane();
                HBox.setHgrow(pane,Priority.ALWAYS);
                ChoiceBox<String> propertyChoices = new ChoiceBox<>();
                StrVector values = core.getAllowedPropertyValues(device, property);
                if(!values.isEmpty()){
                    for(String value: values){
                        propertyChoices.getItems().add(value);
                    }
                    propertyChoices.valueProperty().addListener((observable, oldValue, newValue) -> {
                        try {
                            core.setProperty(device, property, newValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    propertyContainer.getChildren().addAll(propertyLabel, pane, propertyChoices);
                }else {
                    int up = (int) core.getPropertyUpperLimit(device, property);
                    int low = (int) core.getPropertyLowerLimit(device, property);
                    TextField textField = new TextField();
                    propertyLabel.setText(propertyLabel.getText() + String.format("  (%d   %d)",low,up));
                    propertyContainer.getChildren().addAll(propertyLabel, pane, textField);
                    textField.textProperty().addListener((observable, oldValue, newValue) -> {
                        try {
                            core.setProperty(device, property, newValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                }
                VBox.setMargin(propertyContainer,new Insets(0,8,0,8));
                VBox.setMargin(propertyContainer,new Insets(0));
                deviceContainer.getChildren().add(propertyContainer);
            }
            if(!properties.isEmpty()){
                container.getChildren().add(deviceContainer);
            }
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(container);

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
//        scrollPane.setPrefHeight(scrollPane.getPrefViewportHeight());
        deviceProprtyContainer.getChildren().add(scrollPane);
        AnchorPane.setRightAnchor(scrollPane,0d);
        AnchorPane.setLeftAnchor(scrollPane,0d);
        AnchorPane.setBottomAnchor(scrollPane,0d);
        AnchorPane.setTopAnchor(scrollPane,0d);
        sidePanel.setMaxWidth(scrollPane.getPrefWidth());
        sidePanel.setPrefWidth(scrollPane.getPrefWidth());
        sidePanel.setMinWidth(scrollPane.getPrefWidth());
    }


    public void invalidInput() {
        JFXSnackbar bar = new JFXSnackbar(imageCanvasContainer);
        JFXButton button = new JFXButton();
        button.setText("Invalid Input!");
        button.setStyle("-fx-background-color: #fff;");
        button.setPrefHeight(50);
        button.setPrefWidth(200);
        Pane pane = new Pane();
        pane.setPadding(new Insets(16, 16, 16, 16));
        pane.getChildren().add(button);
        bar.enqueue(new JFXSnackbar.SnackbarEvent(pane));
    }


}
