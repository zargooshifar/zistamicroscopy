package main.viewer.overlay;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.viewer.canvas.FxImageCanvas;
import main.viewer.canvas.utils.ViewPort;
import net.imagej.overlay.RectangleOverlay;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public class RectangleDrawer implements OverlayDrawer<RectangleOverlay> {

    RectangleOverlayHelper helper;

    public void update(OverlayViewConfiguration<RectangleOverlay> viewConfig, ViewPort viewport, FxImageCanvas canvas) {

        RectangleOverlay overlay = viewConfig.getOverlay();
        
        GraphicsContext context2d = canvas.getGraphicsContext2D();
        
        helper = new RectangleOverlayHelper(overlay);
        Point2D a = helper.getMinEdge();
        Point2D b = helper.getMaxEdge();
        a = viewport.getPositionOnCamera(a);
        b = viewport.getPositionOnCamera(b);

        double x = a.getX();
        double y = a.getY();
        double w = b.getX() - a.getX();
        double h = b.getY() - a.getY();
        
        //viewConfig.configureContext(context2d);
        context2d.setFill(Color.TRANSPARENT);
        context2d.setStroke(Color.YELLOW);
        context2d.setLineWidth(1.0);
        context2d.fillRect(x, y, w, h);
        context2d.strokeRect(x, y, w, h);

    }

    public boolean canHandle(Class<?> t) {
        return t == RectangleOverlay.class;
    }

}
