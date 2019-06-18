package main.utils;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import main.tools.FxPath;
import main.viewer.canvas.utils.ViewPort;
import main.viewer.overlay.OverlayDrawer;
import net.imagej.overlay.Overlay;
import org.scijava.util.ColorRGB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OverlayHelper {

    private static Overlay overlay;
    private static FxPath path;
    private static Property<ColorRGB> colorProperty;
    private static Property<Double> widthProperty;

    private  Map<Class<?>, OverlayDrawer> drawerMap;


    private static OverlayHelper instance;
    private OverlayHelper(){
        colorProperty = new SimpleObjectProperty<>();
        colorProperty.setValue(new ColorRGB(255, 255, 0));
        widthProperty = new SimpleObjectProperty<>(1.0);
//        overlay;
        drawerMap = new HashMap<>();


    }

    public static OverlayHelper getInstance(){
        if(instance==null)
            instance = new OverlayHelper();
        return instance;
    }

    public static FxPath getPath() {
        return path;
    }

    public static void setPath(FxPath path) {
        OverlayHelper.path = path;
    }


    public Overlay getOverlay() {
        return overlay;
    }

    public void setOverlay(Overlay overlay) {
        instance.overlay = overlay;
    }




    public Property<ColorRGB> colorProperty(){
        return colorProperty;
    }

    public Property<Double> widthProperty(){
        return widthProperty;
    }




    public static Color toFxColor(ColorRGB color) {
        return toFxColor(color,1.0);
    }

    public static Color toFxColor(ColorRGB colorRGB,double f) {
        double red = 1.0 * colorRGB.getRed() / 255;
        double green = 1.0 * colorRGB.getGreen() / 255;
        double blue = 1.0 * colorRGB.getBlue() / 255;
        double alpha = colorRGB.getAlpha() / 255 * f;
        //return new fillColor
        return new Color(red, green, blue, alpha);
    }



    public static Rectangle2D getOverlayBounds(Overlay overlay) {
        double x1 = overlay.getRegionOfInterest().realMin(0);
        double y1 = overlay.getRegionOfInterest().realMin(1);
        double x2 = overlay.getRegionOfInterest().realMax(0);
        double y2 = overlay.getRegionOfInterest().realMax(1);

        //System.out.println(String.format("(%.0f,%.0f), (%.0f,%.0f)", x1, y1, x2, y2));
        Rectangle2D r = new Rectangle2D(x1, y1, x2 - x1, y2 - y1);
        return r;
    }

    public boolean isOverlayOnViewPort(Overlay overlay, ViewPort viewport) {
        return viewport.getSeenRectangle().contains(getOverlayBounds(overlay));
    }

    public static void color(Overlay overlay, Shape shape) {
        ColorRGB fillColor = overlay.getFillColor();
        Color fxFillColor = toFxColor(fillColor,0).deriveColor(1.0, 0, 0, 0.0);
        shape.setFill(fxFillColor);
        shape.setStroke(toFxColor(overlay.getLineColor(),1.0));

        shape.setStrokeWidth(overlay.getLineWidth());
        shape.setOpacity(1.0);
    }

    public Map<Class<?>, OverlayDrawer> getDrawerMap() {
        return drawerMap;
    }

    public void addToDrawerMap(Class<?> c, OverlayDrawer d) {
        drawerMap.put(c, d);
    }
}
