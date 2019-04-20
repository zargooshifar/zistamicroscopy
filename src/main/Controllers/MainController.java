package main.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import ij.ImagePlus;
import ij.gui.HistogramWindow;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import main.ImageUtils.ImageCanvas;
import main.Singletons;
import mmcorej.CMMCore;
import mmcorej.StrVector;
import org.micromanager.utils.PropertyItem;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    @FXML
    private StackPane imageCanvasContainer;
    @FXML
    private AnchorPane cameraControllerContainer;
    @FXML
    private AnchorPane acquisitionControllerContainer;
    @FXML
    private AnchorPane histogramPanel;
    @FXML
    private HBox statusbarContainer;
    @FXML
    private AnchorPane stageStateContainer;

    private static CMMCore core;
    private ImageCanvas imageCanvas;
    //    private boolean histogramDrawed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        core = Singletons.getCoreInstance();

        loadConfig();

        imageCanvas = ImageCanvas.getInstance();
        imageCanvas.setSizeListener(imageCanvasContainer);


        imageCanvasContainer.getChildren().add(imageCanvas);
        imageCanvasContainer.setFocusTraversable(true);
        imageCanvasContainer.setOnKeyPressed(Singletons.getStageControllerInstance().keyListener);


        cameraControllerContainer.getChildren().add(Singletons.getCameraControllerInstance());
        acquisitionControllerContainer.getChildren().add(Singletons.getAcquisitionControllerInstance());
        statusbarContainer.getChildren().add(Singletons.getStatusbarControllerInstance());
        stageStateContainer.getChildren().add(Singletons.getStageStateInstance());

        imageCanvasContainer.getChildren().add(Singletons.getStageControllerInstance());


        try {
            Singletons.getCameraControllerInstance().takeLiveStream(null);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void loadConfig() {
        String configAddress = "config2.cfg";

        try {

            core.loadSystemConfiguration(configAddress);
            core.initializeAllDevices();
            Singletons.getCameraControllerInstance().loadCameraConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateHistogram(ImagePlus imagePlus) {

        HistogramWindow histogramWindow = new HistogramWindow(imagePlus);
        histogramWindow.showHistogram(imagePlus, 0);

        JPanel jPanel = new JPanel();
        jPanel.add(histogramWindow.getCanvas());
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(jPanel);

        histogramPanel.getChildren().add(swingNode);

    }

    public ArrayList<PropertyItem> getProps() {
        ArrayList<PropertyItem> propList_ = new ArrayList<>();
        try {
            StrVector devices = core.getLoadedDevices();

            propList_.clear();

            for (int i = 0; i < devices.size(); i++) {
                StrVector properties = core.getDevicePropertyNames(devices.get(i));
                for (int j = 0; j < properties.size(); j++) {
                    PropertyItem item = new PropertyItem();
                    item.readFromCore(core, devices.get(i), properties.get(j), false);

                    if ((!item.readOnly) && !item.preInit) {
                        propList_.add(item);
                    }
                }

            }
        } catch (Exception e) {

        }
        return propList_;

    }


    @FXML
    void setCursorMove(ActionEvent event) {
        imageCanvas.setCursorMode(ImageCanvas.CursorMode.Move);

    }

    @FXML
    void setCursorNormal(ActionEvent event) {
        imageCanvas.setCursorMode(ImageCanvas.CursorMode.Normal);
    }

    @FXML
    void changeAspectRatio(ActionEvent event) {
        imageCanvas.setKeepAspectRatio(!imageCanvas.getKeepAspectRatio());
    }

    @FXML
    void rotateLeft(ActionEvent event) {
        imageCanvas.rotate(-90);
    }

    @FXML
    void rotateRight(ActionEvent event) {
        imageCanvas.rotate(90);
    }

    @FXML
    void zoomIn(ActionEvent event) {
        imageCanvas.zoomIn();
    }

    @FXML
    void zoomOut(ActionEvent event) {
        imageCanvas.zoomOut();
    }

    @FXML
    void zoomFit(ActionEvent event) {
        imageCanvas.zoomFit();
    }


    @FXML
    void flipHorizentally(ActionEvent event) {
        imageCanvas.flipHorizentally();
    }

    @FXML
    void flipVertically(ActionEvent event) {
        imageCanvas.flipVertically();
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
