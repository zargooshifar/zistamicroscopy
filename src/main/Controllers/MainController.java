package main.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import ij.ImageJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.gui.Toolbar;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import main.ImageUtils.ImageCanvas;
import main.Singletons;
import mmcorej.CMMCore;
import mmcorej.MMCoreJ;
import mmcorej.StrVector;
import org.micromanager.utils.ImageUtils;
import org.micromanager.utils.PropertyItem;
import org.scijava.Context;
import org.scijava.plugin.Parameter;

import javax.swing.*;
import java.awt.*;
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

    @FXML
    private Button rectangleToolButton;



    @FXML
    private Button ovalToolButton;


    @FXML
    private Button polygonToolButton;

    @FXML
    private Button lineToolButton;

    @FXML
    private Button freeLineToolButton;

    @FXML
    private Button angleToolButton;

    @FXML
    private Button pointToolButton;

    @FXML
    private Button multiPointToolButton;

    @FXML
    private Button arrowToolButton;

    @FXML
    private Button handToolButton;

    @FXML
    private Button zoomToolButton;

    @Parameter
    Context context;

    private static CMMCore core;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        core = Singletons.getCoreInstance();

        loadConfig();

        Toolbar toolbar = new Toolbar();
        ImageCanvas ic = ImageCanvas.getInstance();







        imageCanvasContainer.getChildren().add(ic.getSwingNode(imageCanvasContainer));
        cameraControllerContainer.getChildren().add(Singletons.getCameraControllerInstance());
        acquisitionControllerContainer.getChildren().add(Singletons.getAcquisitionControllerInstance());
        statusbarContainer.getChildren().add(Singletons.getStatusbarControllerInstance());
        stageStateContainer.getChildren().add(Singletons.getStageStateInstance());

        imageCanvasContainer.getChildren().add(Singletons.getStageControllerInstance());

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

 private SwingNode getACanvasNode(){
        ImagePlus imp = null;

        try {
            core.snapImage();
            imp = new ImagePlus("", ImageUtils.makeProcessor(core.getTaggedImage()));
        } catch (Exception e) {
            e.printStackTrace();
        }
             ij.gui.ImageCanvas imageCanvas = new ij.gui.ImageCanvas(imp);

        JPanel jPanel = new JPanel();
        jPanel.setSize(500,500);
        System.out.println(jPanel.getWidth()+ " "+ jPanel.getHeight());
        jPanel.add(imageCanvas);
        SwingNode node = new SwingNode();
        node.setContent(jPanel);

        return node;
    }

    @FXML
    void selectAngleTool(ActionEvent event) {
        Toolbar.getInstance().setTool(Toolbar.ANGLE);
        refreshToolbar();

    }


    @FXML
    void selectFreeLineTool(ActionEvent event) {
        Toolbar.getInstance().setTool(Toolbar.FREEROI);
        refreshToolbar();

    }

    @FXML
    void selectLineTool(ActionEvent event) {
        Toolbar.getInstance().setTool(Toolbar.LINE);
        refreshToolbar();

    }

    @FXML
    void selectMultiPointTool(ActionEvent event) {
        Toolbar.getInstance().setTool(Toolbar.UNUSED);
        refreshToolbar();

    }

    @FXML
    void selectOvalTool(ActionEvent event) {
        Toolbar.getInstance().setTool(Toolbar.OVAL);
        refreshToolbar();

    }

    @FXML
    void selectPointTool(ActionEvent event) {
        Toolbar.getInstance().setTool(Toolbar.POINT);
        refreshToolbar();

    }

    @FXML
    void selectPolygonTool(ActionEvent event) {
        Toolbar.getInstance().setTool(Toolbar.POLYGON);
        refreshToolbar();

    }

    @FXML
    void selectRectangleTool(ActionEvent event) {
        Toolbar.getInstance().setTool(Toolbar.RECTANGLE);
        refreshToolbar();

    }

    @FXML
    void selectArrowTool(ActionEvent event) {
        Toolbar.getInstance().setTool(Toolbar.UNUSED);
        refreshToolbar();

    }

    @FXML
    void selectHandTool(ActionEvent event) {
        Toolbar.getInstance().setTool(Toolbar.HAND);
        refreshToolbar();
    }

    @FXML
    void selectZoomTool(ActionEvent event) {
        Toolbar.getInstance().setTool(Toolbar.MAGNIFIER);
        refreshToolbar();
    }


    private void refreshToolbar(){
        rectangleToolButton.setDefaultButton(Toolbar.getToolId() == Toolbar.RECTANGLE);
        zoomToolButton.setDefaultButton(Toolbar.getToolId() == Toolbar.MAGNIFIER);
        handToolButton.setDefaultButton(Toolbar.getToolId() == Toolbar.HAND);
        arrowToolButton.setDefaultButton(Toolbar.getToolId() == Toolbar.UNUSED);
        ovalToolButton.setDefaultButton(Toolbar.getToolId() == Toolbar.OVAL);
        polygonToolButton.setDefaultButton(Toolbar.getToolId() == Toolbar.POLYGON);
        lineToolButton.setDefaultButton(Toolbar.getToolId() == Toolbar.LINE);
        freeLineToolButton.setDefaultButton(Toolbar.getToolId() == Toolbar.FREEROI);
        angleToolButton.setDefaultButton(Toolbar.getToolId() == Toolbar.ANGLE);
        pointToolButton.setDefaultButton(Toolbar.getToolId() == Toolbar.POINT);
        multiPointToolButton.setDefaultButton(Toolbar.getToolId() == Toolbar.UNUSED);
    }

    @FXML
    void changeAspectRatio(ActionEvent event) {

    }

    @FXML
    void fitZoom(ActionEvent event) {
        ImageCanvas.getInstance().zoom100Percent();
    }

    @FXML
    void flipHorizental(ActionEvent event) {
        ImageCanvas.getInstance().flipHorizental();
    }

    @FXML
    void flipVertical(ActionEvent event) {
        ImageCanvas.getInstance().flipVertical();
    }




    @FXML
    void rotateLeft(ActionEvent event) {
        ImageCanvas.getInstance().rotateLeft();
    }

    @FXML
    void rotateRight(ActionEvent event) {
        ImageCanvas.getInstance().rotateRight();
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
