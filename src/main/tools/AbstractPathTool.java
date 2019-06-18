package main.tools;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import main.Singletons;
import main.utils.OverlayHelper;
import main.viewer.canvas.FxImageCanvas;
import main.viewer.overlay.LineDrawer;
import net.imagej.display.DefaultOverlayView;
import net.imagej.display.OverlayView;
import net.imagej.overlay.Overlay;
import net.imagej.overlay.PolygonOverlay;
import net.imglib2.RealPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractPathTool implements FxTool {

    private FxPath currentPath;
    private FxImageCanvas canvas = Singletons.getCameraControllerInstance().getCanvas();
    private ToggleButton button;

    private EventHandler<MouseEvent> onMouseDragged = this::onMouseDragged;
    private EventHandler<MouseEvent> onMouseReleased = this::onMouseReleased;
    private EventHandler<MouseEvent> onMouseClicked = this::onClick;
    private EventHandler<MouseEvent> onMouseMoved = this::onMouseMoved;
    private EventHandler<MouseEvent> onMousePressed = this::onMousePressed;

    
    
    public FxImageCanvas getCanvas() {
        return canvas;
    }

    public void onMousePressed(MouseEvent event) {
        currentPath = new FxPath();
        elongPath(event);
        beforeDrawing(currentPath);
    }
    public void onMouseReleased(MouseEvent event) {
        if(currentPath != null && currentPath.size() > 0)
        afterDrawing(currentPath);

        currentPath = null;
    }
    public void onMouseDragged(MouseEvent event) {
        canvas = (FxImageCanvas)event.getTarget();
   
        elongPath(event);
        event.consume();
        duringDrawing(currentPath);
    }
    
    public void onMouseClicked(MouseEvent event) {

    }
    
    public void onMouseMoved(MouseEvent event) {
        
    }

    public void addOverlays(Overlay overlay, FxPath path) {

        OverlayHelper.getInstance().setOverlay(overlay);
        OverlayHelper.getInstance().setPath(path);
        OverlayHelper.getInstance().addToDrawerMap(FreeHandTool.class, new LineDrawer());

    }

    
    public void addOverlay(FxPath path) {
        
        List<Point2D> pathOnImage = path.getPathOnImage();
        
        PolygonOverlay overlay = new PolygonOverlay();
        
        overlay.setName("new overlay");
        overlay.setFillColor(OverlayHelper.getInstance().colorProperty().getValue());
        overlay.setLineColor(OverlayHelper.getInstance().colorProperty().getValue());
        overlay.setLineWidth(OverlayHelper.getInstance().widthProperty().getValue());

        
        long[] position =  new long[1]; //TODO: review this
        
//        display.localize(position);
//                 for(int i = 2; i!= display.numDimensions();i++) {
//                     overlay.setAxis(imageDisplayService.getActiveDataset().axis(i), i);
//
//                 }


        
        for(int i = 0;i!=pathOnImage.size();i++) {
            Point2D p = pathOnImage.get(i);
            
            double[] vertex = new double[1]; //TODO: review this
            
            vertex[0] = p.getX();
            vertex[1] = p.getY();
            
            for(int j= 2;j!=position.length;j++) {
                vertex[j] = position[j];
            }
            
            Arrays.toString(vertex);
            
            overlay.getRegionOfInterest().addVertex(i, new RealPoint(vertex));
        }
        
        addOverlays(overlay, path);
    }
  

    public FxPath getCurrentPath() {
        if (currentPath == null) {
            currentPath = new FxPath();
        }
        return currentPath;
    }

    protected void drawPath(FxPath fxPath) {
         List<Point2D> points = fxPath.getPathOnScreen();

        double[] xList = FxPath.xList(points);
        double[] yList = FxPath.yList(points);

        getCanvas().repaint();
        getCanvas().getGraphicsContext2D().setStroke(Color.YELLOW);
        getCanvas().getGraphicsContext2D().setLineWidth(1.0);
        getCanvas().getGraphicsContext2D().strokePolygon(xList, yList, xList.length);
    }
    
    
    public void elongPath(MouseEvent event) {

        //System.out.println(getCurrentPath().getPathOnScreen().size());
        Point2D cursorPosition = new Point2D(event.getX(), event.getY());

        Point2D onImage = canvas.getPositionOnImage(cursorPosition);
        getCurrentPath().newPoint(cursorPosition, onImage);

    }
    
    public void elongPath(MouseEvent event, FxPath path) {
        
          Point2D cursorPosition = new Point2D(event.getX(), event.getY());

        Point2D onImage = canvas.getPositionOnImage(cursorPosition);
        path.newPoint(cursorPosition, onImage);
    }

    public void elongPath(DragEvent event) {
        Point2D cursorPosition = new Point2D(event.getX(), event.getY());
        Point2D onImage = canvas.getPositionOnImage(cursorPosition);
        getCurrentPath().newPoint(cursorPosition, onImage);

    }

    @Override
    public void subscribe(FxImageCanvas canvas) {
        
        //canvas.addEventHandler(DragEvent.DRAG_ENTERED, this::onMousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleased);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, onMouseClicked);
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED,onMouseMoved);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,onMousePressed);
        onActivated();
        //canvas.addEventHandler(MouseDragEvent.MOUSE_DRAGGED,event->event.consume());
    }

    @Override
    public void unsubscribe(FxImageCanvas canvas) {
     
        //canvas.removeEventHandler(DragEvent.DRAG_ENTERED, this::onMousePressed);
        canvas.removeEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDragged);
        canvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleased);
        canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, onMouseClicked);
        canvas.removeEventHandler(MouseEvent.MOUSE_MOVED,onMouseMoved);
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED,onMousePressed);

    }

    public Node getNode() {
        if (button == null) {
            button = new ToggleButton(getClass().getSimpleName().replace("Tool", ""), getIcon());
            //button.getStyleClass().add(TOOL_BUTTON_CSS_CLASS);
            button.setOnAction(this::onButtonClick);
        }
        return button;
    }

    public void update(FxTool tool) {
        if(button != null)
        button.setSelected(tool == this);
    }

    @org.scijava.event.EventHandler
    public void handleEvent(ToolChangeEvent event) {
        update(event.getTool());
    }

    private void onButtonClick(ActionEvent event) {
//        toolService.setCurrentTool(this.getClass());
    }
    
    public void onActivated() {
        
    }

    public abstract void beforeDrawing(FxPath path);

    public abstract void duringDrawing(FxPath fxPath);

    public abstract void afterDrawing(FxPath path);

    public abstract void onClick(MouseEvent event);

    public abstract Node getIcon();

    public Rectangle2D getRectangle(Point2D p1, Point2D p2) {

        double x;
        double y;

        double w;
        double h;

        if (p1.getX() < p2.getX()) {
            x = p1.getX();
        } else {
            x = p2.getX();
        }

        if (p1.getY() < p2.getY()) {
            y = p1.getY();
        } else {
            y = p2.getY();
        }

        w = Math.abs(p1.getX() - p2.getX());
        h = Math.abs(p1.getY() - p2.getY());

        return new Rectangle2D(x, y, w, h);

    }

    
    
    
}
