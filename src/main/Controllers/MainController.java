package main.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import ij.ImagePlus;
import ij.gui.HistogramWindow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
    private Pane viewPane;
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
        imageCanvas.setSizeListener(viewPane);


        viewPane.getChildren().add(imageCanvas);
        viewPane.getChildren().add(Singletons.getStageControllerInstance());
        viewPane.setFocusTraversable(true);
        viewPane.setOnKeyPressed(Singletons.getStageControllerInstance().keyListener);


        cameraControllerContainer.getChildren().add(Singletons.getCameraControllerInstance());
        acquisitionControllerContainer.getChildren().add(Singletons.getAcquisitionControllerInstance());
        statusbarContainer.getChildren().add(Singletons.getStatusbarControllerInstance());
        stageStateContainer.getChildren().add(Singletons.getStageStateInstance());




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
    void changeViewScale(ActionEvent event) {
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


    public void invalidInput() {
        JFXSnackbar bar = new JFXSnackbar(viewPane);
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
