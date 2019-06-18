package main.ijfxstuff.ui.tool;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import main.ijfxstuff.CanvasCamera;
import main.ijfxstuff.FXImageCanvas;
import net.imagej.display.OverlayService;
import net.imagej.event.OverlayCreatedEvent;
import net.imagej.overlay.Overlay;
import net.imagej.overlay.PointOverlay;
import org.scijava.Context;
import org.scijava.display.DisplayService;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.ColorRGB;

import java.util.ArrayList;
import java.util.List;

public class PointTool implements FxTool {

    @Parameter
    DisplayService imageDisplayService;

    @Parameter
    FxToolService toolService;

    @Parameter
    Context context;

    @Parameter
    EventService eventService;

    @Parameter
    OverlayService overlayService;

    private ToggleButton button;

    EventHandler<MouseEvent> onMouseClick;

    public PointTool() {
        onMouseClick = this::onClick;
    }

    public Node getNode() {
        if (button == null) {
            button = new ToggleButton(getClass().getSimpleName().replace("Tool", ""), new FontAwesomeIconView(FontAwesomeIcon.CROSSHAIRS));
            //button.getStyleClass().add(TOOL_BUTTON_CSS_CLASS);
            button.setOnAction(e -> toolService.setCurrentTool(getClass()));
        }
        return button;

    }

    @Override
    public void update(FxTool currentTool) {
        
    }

    @Override
    public void subscribe(FXImageCanvas canvas) {

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, onMouseClick);

    }

    @Override
    public void unsubscribe(FXImageCanvas canvas) {
        canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, onMouseClick);
    }

    public void onClick(MouseEvent event) {

        CanvasCamera camera = getCanvasFromEvent(event).getCamera();
        Point2D onScreen = new Point2D(event.getX(), event.getY());
        Point2D onImage = camera.getPositionOnImage(onScreen);

        PointOverlay pointOverlay = new PointOverlay(context);
        double[] point = new double[]{onImage.getX(), onImage.getY()};
        pointOverlay.getPoints().add(point);
        pointOverlay.setFillColor(new ColorRGB(0, 255, 0));
        pointOverlay.setAlpha(255);
        pointOverlay.setLineWidth(1.0);
        List<Overlay> overlays = new ArrayList<>();
        overlays.add(pointOverlay);

        overlayService.addOverlays(getCanvasFromEvent(event).getImageDisplay(), overlays);

        eventService.publishLater(new OverlayCreatedEvent(pointOverlay));

    }

    protected FXImageCanvas getCanvasFromEvent(MouseEvent event) {
        return (FXImageCanvas) event.getTarget();
    }

    
    @org.scijava.event.EventHandler
    public void onToolChanged(ToolChangeEvent event) {
        if(event.getTool() != this) button.setSelected(false);
        
    }
    
}
