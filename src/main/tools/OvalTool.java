package main.tools;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import main.utils.OverlayHelper;

import java.util.List;

public class OvalTool extends AbstractPathTool {


    @Override
    public void beforeDrawing(FxPath path) {

    }

    @Override
    public void duringDrawing(FxPath fxPath) {
        List<Point2D> points = fxPath.getPathOnScreen();

        if(points.size() <2){
            return;
        }

        Point2D firstpoint = FxPath.getFirst(points);
        Point2D lastPoint = FxPath.getLast(points);

        double x = firstpoint.getX();
        double y = firstpoint.getY();

        double w = lastPoint.getX() - firstpoint.getX();
        double h = lastPoint.getY() - firstpoint.getY();

        if(w<0){
            x = x + w;
            w = -w;
        }

        if(h<0){
            y = y + h;
            h = -h;
        }



        getCanvas().repaint();
        getCanvas().getGraphicsContext2D().setStroke(OverlayHelper.toFxColor(OverlayHelper.getInstance().colorProperty().getValue()));
        getCanvas().getGraphicsContext2D().setLineWidth(OverlayHelper.getInstance().widthProperty().getValue());
        getCanvas().getGraphicsContext2D().strokeOval(x,y,w,h);

    }

    @Override
    public void afterDrawing(FxPath path) {

    }

    @Override
    public void onClick(MouseEvent event) {
        getCanvas().repaint();
    }

    @Override
    public Node getIcon() {
        return null;
    }
}
