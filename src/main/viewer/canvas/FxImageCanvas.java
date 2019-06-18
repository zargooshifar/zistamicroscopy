package main.viewer.canvas;

import ij.process.ImageProcessor;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import main.viewer.ImageDisplayPane;
import main.viewer.canvas.utils.CanvasCamera;
import mmcorej.TaggedImage;
import net.imagej.overlay.Overlay;
import org.micromanager.utils.ImageUtils;

import java.util.ArrayList;
import java.util.function.Consumer;


public class FxImageCanvas extends Canvas {

    protected Image image;
    final protected CanvasCamera camera = new CanvasCamera();
    protected Property<Point2D> mousePositionOnScreen = new SimpleObjectProperty<>();
    protected Property<Point2D> mousePositionOnImage = new SimpleObjectProperty<>();
    private ArrayList<Overlay> overlays;
    private Overlay overlay;

  


    private ImageDisplayPane imageDisplayPane;
    
    
    private Consumer<FxImageCanvas> afterDrawing;
    
//    private static final String CSS_STYLE_CLASS = "fx-image-canvas";
    

    public FxImageCanvas() {
        super();

        // add width and height listenr that will repain the
        // canvas when this one is resized
        camera.widthProperty().bind(widthProperty());
        camera.heightProperty().bind(heightProperty());

        // add the different mouse listener (only the zoom)
        addMouseListeners();
//        getStyleClass().add(CSS_STYLE_CLASS);

    }


    
    
    /**
     *
     * @return the image currently displayed
     */
    public Image getImage() {
        return image;
    }

    @Override
    public boolean isResizable() {
        //  System.out.println("isResizable");
        return true;
    }

    @Override
    public double prefWidth(double height) {
        //   System.out.println(height);
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    /**
     *
     * @param image
     * @return the canvas itself for method chaining
     */
    public synchronized FxImageCanvas setImage(Image image) {

        this.image = image;
        camera.setImageSpace(new Rectangle2D(0, 0, image.getWidth(), image.getHeight()));

        repaint();

        return this;
    }

    public void setTaggedImage(TaggedImage taggedImage){
        ImageProcessor imageProcessor = ImageUtils.makeProcessor(taggedImage);
        Image finalImage = SwingFXUtils.toFXImage(imageProcessor.getBufferedImage(), null);
        setImage(finalImage);
    }

    /**
     *
     * @return the camera
     */
    public CanvasCamera getCamera() {
        return camera;
    }

    /**
     * Repaints the canvas
     */
    public synchronized void repaint() {

       if (image != null && camera != null) {
            camera.accordToSpace();

            // getting the part of the image seen the camera
            final Rectangle2D camRectangle = camera.getSeenRectangle();
            ;
            final double sx = camRectangle.getMinX();
            final double sy = camRectangle.getMinY();
            final double sw = camRectangle.getWidth();
            final double sh = camRectangle.getHeight();

            // the target image (which is the canvas itself
            final double tx = 0;
            final double ty = 0;
            final double tw = camera.getWidth();
            final double th = camera.getHeight();

            // drawing the part of the image seen by the camera into
            // the canvas
            getGraphicsContext2D().drawImage(image, sx, sy, sw, sh, tx, ty, tw, th);
            
            if(afterDrawing != null) afterDrawing.accept(this);
            
            drawBorders();

        }
    }

   

    /**
     * Draw the borders when the size of the displayed image is smaller than the
     * screen
     */
    public void drawBorders() {
        double borderWidth = 0;
        double borderHeight = 0;

        // if the camera field of view is bigger than the image
        if (camera.getImageEffectiveWidth() < camera.getWidth()) {
            borderWidth = camera.getWidth() - camera.getImageEffectiveWidth();
            borderWidth = borderWidth / 2;
        }

        if (camera.getImageEffectiveHeight() < camera.getHeight()) {
            borderHeight = camera.getHeight() - camera.getImageEffectiveHeight();
            borderHeight = borderHeight / 2;
        }

        if (borderHeight == 0 && borderWidth == 0) {
            return;
        }

        final double bottomBorderX = camera.getWidth() - borderWidth;
        final double bottomBorderY = camera.getHeight() - borderHeight;

        getGraphicsContext2D().setFill(Color.DARKGREY);

//        logger.info(String.format("Drawing borders : %.3f x %.3f", borderWidth, borderHeight));
//        System.out.println(String.format("Drawing borders : %.3f x %.3f", borderWidth, borderHeight));


        GraphicsContext ctx = getGraphicsContext2D();

        ctx.clearRect(0, 0, getWidth(), borderHeight);
        ctx.clearRect(0, 0, borderWidth, getHeight());
        ctx.clearRect(0, bottomBorderY, getWidth(), borderHeight);
        ctx.clearRect(bottomBorderX, 0, borderWidth, getHeight());
    }

    /**
     *
     */
    public void addMouseListeners() {

        setOnScroll(event -> {
            if (event.getDeltaY() > 0) {
                camera.zoomIn();
                repaint();
            } else {
                camera.zoomOut();
                repaint();
            }
        });
        
        addEventHandler(MouseEvent.MOUSE_MOVED, this::onMouseMoved);

    }

    /**
     *
     * @param point
     * @return the position of this point on the image
     */
    public Point2D getPositionOnImage(Point2D point) {

        final double mouseX = point.getX();
        final double mouseY = point.getY();

        final double dx = ((mouseX) - (camera.getWidth() / 2)) / camera.getZoom();
        final double dy = ((mouseY) - (camera.getHeight() / 2)) / camera.getZoom();

        return new Point2D(camera.getX() + dx, camera.getY() + dy);

    }

    public Point2D getPositionOnImage(double x, double y) {
        final Point2D p = new Point2D(x, y);
        return getPositionOnImage(p);
    }

    /**
     *
     * @return the cursor position of the camera
     */
    public Point2D getCursorPositionOnScreen() {
        return mousePositionOnScreen.getValue();
    }

    /**
     *
     * @return the cursor position on the image
     */
    public Point2D getCursorPositionOnImage() {
        return mousePositionOnImage.getValue();
    }
    
    public ReadOnlyProperty<Point2D> positionOnImageProperty() {
        return mousePositionOnImage;
    }



    public void setAfterDrawing(Consumer<FxImageCanvas> afterDrawing) {
        this.afterDrawing = afterDrawing;
    }


    
    public void scaleToFit() {
        
        
        double zoom = image.getWidth() / getWidth();
        getCamera().setZoom(zoom);
        
    }
    
    
    private void onMouseMoved(MouseEvent event) {
        
        Point2D positionOnScreen = new Point2D(event.getX(),event.getY());
        Point2D positionOnImage = getPositionOnImage(positionOnScreen);
        
        mousePositionOnScreen.setValue(positionOnScreen);
        mousePositionOnImage.setValue(positionOnImage);
        
        
    }


    public ImageDisplayPane getImageDisplayPane() {
        return imageDisplayPane;
    }

    public void setImageDisplayPane(ImageDisplayPane imageDisplayPane) {
        this.imageDisplayPane = imageDisplayPane;
    }
}
