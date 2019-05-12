package main.ImageUtils;


import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.ContrastEnhancer;
import ij.process.ImageProcessor;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.Singletons;
import main.utils.FPSCounter;
import mmcorej.TaggedImage;
import org.micromanager.utils.ImageUtils;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;


/*

@author: Jacob Zargooshifar


 */

public class ImageCanvas_old extends Canvas {


    int ver = 1;
    int hor = 1;
    private boolean flipHorizentally;
    private boolean flipVertically;
    private boolean autoContrast = true;
    private static TaggedImage lastTaggedImage;
    private Rectangle rectangle;
    private Roi roi;

    public ImagePlus getImagePlus() {
        return imagePlus;
    }

    public void setImagePlus(ImagePlus imagePlus) {
        this.imagePlus = imagePlus;
    }

    public Graphics getGraphics() {
        return null;
    }


    public enum CursorMode {Normal, Move}

    private CursorMode cursorMode = CursorMode.Normal;

    private static ImageCanvas_old instance;
    private FPSCounter fpsCounter;
    private int angle = 0;
    private boolean keepAspectRatio = true;
    private boolean rotated;

    private Number lastWidth = 0;
    private Number lastHeight = 0;
    private float magnification = 1;
    ImageProcessor ip;

    private ImagePlus imagePlus;

    private ImageCanvas_old() {
        fpsCounter = new FPSCounter();
        fpsCounter.start();
        setMouseListeners();


    }

    public static ImageCanvas_old getInstance() {
        if (instance == null) {
            instance = new ImageCanvas_old();
        }

        return instance;
    }


    public void setSizeListener(StackPane pane) {


        pane.widthProperty().addListener((observable, oldValue, newValue) -> {
            widthProperty().setValue(newValue.intValue());
            if (lastWidth.doubleValue() != getWidth()) {
                updateImage(lastTaggedImage);
            }
//            getGraphicsContext2D().fillRect(0,0,newValue.intValue(), getHeight());
        });


        pane.heightProperty().addListener((observable, oldValue, newValue) -> {
            heightProperty().setValue(newValue.intValue());
            if (lastHeight.doubleValue() != getHeight()) {
                updateImage(lastTaggedImage);
            }
//            getGraphicsContext2D().fillRect(0,0, getWidth(), newValue.intValue());
        });
    }

    public void refreshSize() {
        updateImage(lastTaggedImage);
    }


    public void repaint() {
        Platform.runLater(() -> {
            try {

                double w = getWidth() * magnification;
                double h = getHeight() * magnification;
                if (keepAspectRatio) {
                    double min = Math.min(w, h);
                    w = h = min;
                }
                lastWidth = getWidth();
                lastHeight = getHeight();
                double x = (getWidth() / 2) - (w / 2);
                double y = (getHeight() / 2) - (h / 2);


                Image finalImage = SwingFXUtils.toFXImage(imagePlus.getBufferedImage(), null);


                getGraphicsContext2D().setFill(Color.WHITE);
                getGraphicsContext2D().fillRect(0, 0, getWidth(), getHeight());


                getGraphicsContext2D().drawImage(finalImage, x, y, w, h);
            } catch (NullPointerException e) {

            }


        });
    }

    public void updateImage(TaggedImage taggedImage) throws NullPointerException {

        lastTaggedImage = taggedImage;
        ImagePlus imp = getImagePlus(taggedImage);

        Platform.runLater(() -> {
            try {

                double w = getWidth() * magnification;
                double h = getHeight() * magnification;
                if (keepAspectRatio) {
                    double min = Math.min(w, h);
                    w = h = min;
                }
                lastWidth = getWidth();
                lastHeight = getHeight();
                double x = (getWidth() / 2) - (w / 2);
                double y = (getHeight() / 2) - (h / 2);


                Image finalImage = SwingFXUtils.toFXImage(imp.getBufferedImage(), null);


//                getGraphicsContext2D().drawImage();

                getGraphicsContext2D().setFill(Color.WHITE);
                getGraphicsContext2D().fillRect(0, 0, getWidth(), getHeight());


                getGraphicsContext2D().drawImage(finalImage, x, y, w, h);
            } catch (NullPointerException e) {

            }


        });
    }


    public ImagePlus getImagePlus(TaggedImage image) {

        if (image == null)
            return null;

        ImageProcessor ip = ImageUtils.makeProcessor(image);
        if (roi != null) {

//            ip.setRoi(roi);
            ip.drawRoi(roi);
            System.out.println(roi.toString());
        }
        imagePlus = new ImagePlus("", ip);


        return imagePlus;

    }

    private ImageProcessor setImageProcessorAttributes(ImageProcessor ip) {

        ip.rotate(angle);
        if (flipHorizentally)
            ip.flipHorizontal();
        if (flipVertically)
            ip.flipVertical();
        if (autoContrast) {
            new ContrastEnhancer().stretchHistogram(ip, 0.5);
        }
//        System.out.println(ip.rotateLeft());

        return ip;
    }

    public boolean getKeepAspectRatio() {
        return keepAspectRatio;
    }

    public void setKeepAspectRatio(boolean bool) {
        keepAspectRatio = bool;
        if (!Singletons.getCameraControllerInstance().isLive())
            updateImage(lastTaggedImage);

    }

    public void rotate(int angle) {

        this.angle += angle;
        if (this.angle == 360 || this.angle == -360) {
            this.angle = 0;
        }
        if (!Singletons.getCameraControllerInstance().isLive()) {
//            ip.reset();
            updateImage(lastTaggedImage);
        }


    }

    public float getMagnification() {
        return this.magnification;
    }

    public void zoomIn() {
        magnification += 0.1;
        if (!Singletons.getCameraControllerInstance().isLive())
            updateImage(lastTaggedImage);
        Singletons.getStatusbarControllerInstance().setLeftStatusText(String.format("%.0f%% Magnification", magnification * 100));

    }

    public void zoomOut() {
        if (magnification > 1) {
            magnification -= 0.1;
            if (!Singletons.getCameraControllerInstance().isLive())
                updateImage(lastTaggedImage);

        }
        Singletons.getStatusbarControllerInstance().setLeftStatusText(String.format("%.0f%% Magnification", magnification * 100));


    }

    public void zoomFit() {
        magnification = 1;
        this.setTranslateX(0);
        this.setTranslateY(0);
        if (!Singletons.getCameraControllerInstance().isLive())
            updateImage(lastTaggedImage);
        Singletons.getStatusbarControllerInstance().setLeftStatusText(String.format("%.0f%% Magnification", magnification * 100));

    }

    public void flipHorizentally() {
        flipHorizentally = !flipHorizentally;
        if (!Singletons.getCameraControllerInstance().isLive())
            updateImage(lastTaggedImage);
    }

    public void flipVertically() {
        flipVertically = !flipVertically;
        if (!Singletons.getCameraControllerInstance().isLive())
            updateImage(lastTaggedImage);

    }

    public void drawRectangle() {

    }

    public CursorMode getCursorMode() {
        return cursorMode;
    }

    public void setCursorMode(CursorMode cursorMode) {
        this.cursorMode = cursorMode;
        switch (cursorMode) {
            case Normal:
                setCursor(Cursor.DEFAULT);
                break;
            case Move:
                setCursor(Cursor.OPEN_HAND);
                break;
        }

    }


    private void setMouseListeners() {
        AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
        AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);
//        rectangle.setFill(Color.RED);


        setOnMousePressed(event -> {
            xOffset.set(event.getX());
            yOffset.set(event.getY());
            switch (cursorMode) {
                case Normal:
                    break;
                case Move:

                    setCursor(Cursor.CLOSED_HAND);
                    break;
            }
        });

        setOnMouseReleased(event -> {
            switch (cursorMode) {
                case Normal:
                    break;
                case Move:
                    setCursor(Cursor.OPEN_HAND);
                    break;
            }

        });


        setOnMouseDragged(event -> {
            switch (cursorMode) {
                case Normal:


//                    getGraphicsContext2D().strokeRect(xOffset.get(), yOffset.get(), event.getX() - xOffset.get(), event.getY() - yOffset.get());

                    rectangle = new Rectangle(xOffset.get().intValue(), yOffset.get().intValue(), (int) event.getX() - xOffset.get().intValue(), (int) event.getY() - yOffset.get().intValue());
                    roi = new Roi(rectangle);
                    repaint();
                    break;
                case Move:
                    setManaged(false);
                    setTranslateX(event.getX() + getTranslateX() - xOffset.get());
                    setTranslateY(event.getY() + getTranslateY() - yOffset.get());
                    event.consume();
                    break;
            }
        });

        setOnMouseClicked(event -> {
        });
    }


}
