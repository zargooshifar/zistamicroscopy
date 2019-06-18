package main.tools;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;

import main.Singletons;
import main.utils.OverlayHelper;
import main.utils.Point2DUtils;
import main.viewer.canvas.utils.CanvasCamera;
import main.viewer.canvas.FxImageCanvas;
import net.imagej.event.OverlayCreatedEvent;
import net.imagej.overlay.LineOverlay;
import net.imagej.overlay.Overlay;
import net.imagej.overlay.PointOverlay;

import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.util.ColorRGB;

import java.util.ArrayList;
import java.util.List;


public class PointTool extends AbstractPathTool {


    PointOverlay currentOverlay;
    FxImageCanvas canvas = Singletons.getCameraControllerInstance().getCanvas();

    Point2D cursorPosition;


    public PointTool() {
        super();
    }

    @Override
    public void beforeDrawing(FxPath fxPath) {
        System.out.println(fxPath.getPathOnScreen().size());

//        if(fxPath.size()<2)
//            return;
        Point2D point = fxPath.pathOnScreen.get(0);
        double x = point.getX();
        double y = point.getY();
        canvas.repaint();
        canvas.getGraphicsContext2D().setStroke(OverlayHelper.toFxColor(OverlayHelper.getInstance().colorProperty().getValue()));
//        canvas.getGraphicsContext2D().setGlobalAlpha(0.5);
        canvas.getGraphicsContext2D().strokeRect(x-5,y, 10, 0  );
        canvas.getGraphicsContext2D().strokeRect(x,y-5, 0, 10  );

        PointOverlay pointOverlay = new PointOverlay();
        double[] xy = new double[]{x, y};
        pointOverlay.getPoints().add(xy);
        pointOverlay.setFillColor(new ColorRGB(0, 255, 0));
        pointOverlay.setAlpha(255);
        pointOverlay.setLineWidth(1.0);
        FxPath path = new FxPath();
        path.newPoint(fxPath.pathOnScreen.get(0), fxPath.pathOnImage.get(0));
        addOverlays(pointOverlay,path);


    }

    @Override
    public void duringDrawing(FxPath fxPath) {

    }

    @Override
    public void onClick(MouseEvent event) {



//        getCanvas().getGraphicsContext2D().fillRect(x,y,10,10);
//        getCanvas().getGraphicsContext2D().strokeRect(x,y,10,10);
    }

    @Override
    public Node getIcon() {
        return null;
    }

    @Override
    public void afterDrawing(FxPath path) {





    }

    protected void updateCurrentOverlay(FxPath path) {

    }




}
