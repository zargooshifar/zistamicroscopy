package main.Controllers;

import com.jfoenix.controls.JFXButton;
import ij.gui.Toolbar;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import main.ImageUtils.ImageCanvas;
import main.Singletons;
import main.tools.*;
import main.viewer.canvas.FxImageCanvas;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ToolbarController extends VBox {

    @FXML
    private JFXButton cursorToolButton;
    @FXML
    private JFXButton handToolButton;

    @FXML
    private JFXButton zoomOutToolButton;

    @FXML
    private JFXButton zoomIntToolButton;

    @FXML
    private JFXButton fitZoomButton;
    @FXML
    private JFXButton changeAspectRatioButton;
    @FXML
    private JFXButton flipHorizentalButton;
    @FXML
    private JFXButton flipVerticalButton;
    @FXML
    private JFXButton rotateRightButton;
    @FXML
    private JFXButton rotateLeftButton;
    @FXML
    private JFXButton rectangleToolButton;
    @FXML
    private JFXButton ovalToolButton;
    @FXML
    private JFXButton polygonToolButton;
    @FXML
    private JFXButton lineToolButton;
    @FXML
    private JFXButton closeFreeLineToolButton;
    @FXML
    private JFXButton freeLineToolButton;
    @FXML
    private JFXButton angleToolButton;
    @FXML
    private JFXButton pointToolButton;
    @FXML
    private JFXButton multiPointToolButton;

    FxImageCanvas canvas = Singletons.getCameraControllerInstance().getCanvas();

    Hand hand = new Hand();
    FreeHandTool freeHandTool = new FreeHandTool();
    RectangleTool rectangleTool = new RectangleTool();
    LineTool lineTool = new LineTool();
    PointTool pointTool = new PointTool();
    OvalTool ovalTool = new OvalTool();
    CloseFreeHandTool closeFreeHandTool = new CloseFreeHandTool();
    PolygonTool polygonTool = new PolygonTool();



//    private static ToolbarController instance;

    private enum Tools{
        Cursor,
        Hand,
        FreeLine,
        Rectangle,
        Oval,
        Polygon,
        Line,
        CloseFreeLine,
        Point,
        MultiPoint,
        Unused
    }


    private Tools currentTool;

    public ToolbarController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/toolbar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StackPane.setAlignment(this, Pos.CENTER_LEFT);

        this.setOnMouseEntered(event -> {

            FadeTransition ft = new FadeTransition(Duration.millis(300), this);
            ft.setFromValue(0.3);
            ft.setToValue(1);
            ft.play();
        });

        this.setOnMouseExited(event -> {
            FadeTransition ft = new FadeTransition(Duration.millis(300), this);
            ft.setFromValue(1);
            ft.setToValue(0.3);
            ft.play();

        });
        polygonToolButton.setDisable(true);
        closeFreeLineToolButton.setDisable(true);
        multiPointToolButton.setDisable(true);
    }

//    public static ToolbarController getInstance(){
//        if(instance == null)
//            instance = new ToolbarController();
//        return instance;
//    }
//




    @FXML
    void selectFreeLineTool(ActionEvent event) {
        unsubscribeAll();
        freeHandTool.subscribe(canvas);
        currentTool = Tools.FreeLine;
        refreshToolbar();

    }

    @FXML
    void selectCloseFreeLineTool(ActionEvent event) {
        unsubscribeAll();
        closeFreeHandTool.subscribe(canvas);
        currentTool = Tools.CloseFreeLine;
        refreshToolbar();

    }

    @FXML
    void selectLineTool(ActionEvent event) {
        unsubscribeAll();
        lineTool.subscribe(canvas);
        currentTool = Tools.Line;
        refreshToolbar();

    }

    @FXML
    void selectMultiPointTool(ActionEvent event) {

        currentTool = Tools.MultiPoint;
        refreshToolbar();

    }

    @FXML
    void selectOvalTool(ActionEvent event) {
        unsubscribeAll();
        ovalTool.subscribe(canvas);
        currentTool = Tools.Oval;
        refreshToolbar();

    }

    @FXML
    void selectPointTool(ActionEvent event) {
        System.out.println("point tool");
        unsubscribeAll();
        pointTool.subscribe(canvas);
        currentTool = Tools.Point;
        refreshToolbar();

    }

    @FXML
    void selectPolygonTool(ActionEvent event) {
        unsubscribeAll();
        polygonTool.subscribe(canvas);
        currentTool = Tools.Polygon;
        refreshToolbar();

    }

    @FXML
    void selectRectangleTool(ActionEvent event) {
        unsubscribeAll();
        rectangleTool.subscribe(canvas);
        currentTool = Tools.Rectangle;
        refreshToolbar();

    }

    @FXML
    void selectCursorTool(ActionEvent event) {

        currentTool = Tools.Cursor;
        refreshToolbar();

    }

    @FXML
    void selectHandTool(ActionEvent event) {
        unsubscribeAll();
        hand.subscribe(canvas);
        currentTool = Tools.Hand;
        refreshToolbar();
    }

    private void unsubscribeAll(){

        hand.unsubscribe(canvas);
        freeHandTool.unsubscribe(canvas);
        rectangleTool.unsubscribe(canvas);
        lineTool.unsubscribe(canvas);
        pointTool.unsubscribe(canvas);
        ovalTool.unsubscribe(canvas);
        closeFreeHandTool.unsubscribe(canvas);
        polygonTool.unsubscribe(canvas);

    }

    private void refreshToolbar(){
        rectangleToolButton.setDefaultButton(currentTool == Tools.Rectangle);
        handToolButton.setDefaultButton(currentTool == Tools.Hand);
        cursorToolButton.setDefaultButton(currentTool == Tools.Cursor);
        ovalToolButton.setDefaultButton(currentTool == Tools.Oval);
        polygonToolButton.setDefaultButton(currentTool == Tools.Polygon);
        lineToolButton.setDefaultButton(currentTool == Tools.Line);
        closeFreeLineToolButton.setDefaultButton(currentTool == Tools.CloseFreeLine);
        freeLineToolButton.setDefaultButton(currentTool == Tools.FreeLine);
        pointToolButton.setDefaultButton(currentTool == Tools.Point);
        multiPointToolButton.setDefaultButton(currentTool == Tools.MultiPoint);
    }

    @FXML
    void changeAspectRatio(ActionEvent event) {

    }

    @FXML
    void fitZoom(ActionEvent event) {
        canvas.getCamera().setZoom(1d);
        canvas.repaint();
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

    @FXML
    void zoomIn(ActionEvent event) {
        canvas.getCamera().zoomIn();
        canvas.repaint();
    }

    @FXML
    void zoomOut(ActionEvent event) {
        canvas.getCamera().zoomOut();
        canvas.repaint();
    }




}
