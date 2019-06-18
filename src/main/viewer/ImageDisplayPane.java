package main.viewer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.utils.OverlayHelper;
import main.viewer.canvas.FxImageCanvas;
import main.viewer.overlay.*;
import net.imagej.display.DefaultOverlayView;
import net.imagej.overlay.*;

public class ImageDisplayPane extends StackPane {


    private FxImageCanvas canvas;



    public void rescale() {
        if (!getChildren().isEmpty()) {
            getChildren().forEach((c) -> {
                double xScale = getWidth() / c.getBoundsInLocal().getWidth();
                double yScale = getHeight() / c.getBoundsInLocal().getHeight();
                if (autoScale.get() == AutoScale.FILL) {
                    c.setScaleX(xScale);
                    c.setScaleY(yScale);
                } else if (autoScale.get() == AutoScale.FIT) {
                    double scale = Math.min(xScale, yScale);
                    c.setScaleX(scale);
                    c.setScaleY(scale);
                } else {
                    c.setScaleX(1d);
                    c.setScaleY(1d);
                }
                if(canvas!=null)
                    canvas.repaint();
            });
        }
    }

    private void redrawOverlays(FxImageCanvas canvas){

        Overlay overlay = OverlayHelper.getInstance().getOverlay();
        if(overlay==null) {
            return;
        }
        canvas.getGraphicsContext2D().setFill(Color.RED);
        canvas.getGraphicsContext2D().setStroke(Color.RED);


        OverlayDrawer drawer;
        if(overlay instanceof PolygonOverlay){
            drawer = new PolygonDrawer();
        }else if(overlay instanceof LineOverlay){
            drawer = new LineDrawer();

        }else if(overlay instanceof RectangleOverlay){
            drawer = new RectangleDrawer();

        } else if(overlay instanceof PointOverlay){
            drawer = new PointOverlayDrawer();

        } else {
            return;
        }


            drawer.update(new DefaultOverlayViewConfiguration(new DefaultOverlayView(), overlay), canvas.getCamera(), canvas);




    };


    private void init() {
        canvas.setAfterDrawing(this::redrawOverlays);

        widthProperty().addListener((b, o, n) -> rescale());
        heightProperty().addListener((b, o, n) -> rescale());
    }

    /**
     * No argument constructor required for Externalizable (need this to work
     * with SceneBuilder).
     */
    public ImageDisplayPane() {
        super();

        init();
    }


    public ImageDisplayPane(FxImageCanvas canvas, StackPane container) {
        super(canvas);
        setCanvas(canvas);
        canvas.setImageDisplayPane(this);
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());
        this.prefWidthProperty().bind(container.prefWidthProperty());
        this.maxWidthProperty().bind(container.maxWidthProperty());
        this.minWidthProperty().bind(container.minWidthProperty());
        this.prefHeightProperty().bind(container.prefHeightProperty());
        this.maxHeightProperty().bind(container.maxHeightProperty());
        this.minHeightProperty().bind(container.minHeightProperty());
        container.getChildren().add(this);


        init();
    }

    public ImageDisplayPane(Node content) {
        super(content);
        init();
    }

    public FxImageCanvas getCanvas() {
        return canvas;
    }

    public void setCanvas(FxImageCanvas canvas) {
        this.canvas = canvas;
    }

    /**
     * AutoScale scaling options:
     * {@link AutoScale#NONE}, {@link AutoScale#FILL}, {@link AutoScale#FIT}
     */
    public enum AutoScale {

        /**
         * No scaling - revert to behaviour of <code>StackPane</code>.
         */
        NONE,
        /**
         * Independently scaling in x and y so content fills whole region.
         */
        FILL,
        /**
         * Scale preserving content aspect ratio and center in available space.
         */
        FIT
    }

    // AutoScale Property
    private ObjectProperty<AutoScale> autoScale = new SimpleObjectProperty<AutoScale>(this, "autoScale",
            AutoScale.FIT);

    /**
     * ImageDisplayPane scaling property
     *
     * @return ImageDisplayPane scaling property
     * @see AutoScale
     */
    public ObjectProperty<AutoScale> autoScaleProperty() {
        return autoScale;
    }

    /**
     * Get AutoScale option
     *
     * @return the AutoScale option
     * @see AutoScale
     */
    public AutoScale getAutoScale() {
        return autoScale.getValue();
    }

    /**
     * Set the AutoScale option
     *
     * @param newAutoScale
     * @see AutoScale
     *
     */
    public void setAutoScale(AutoScale newAutoScale) {
        autoScale.setValue(newAutoScale);
    }
}