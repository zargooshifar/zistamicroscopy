package main.ImageUtils;


import ij.CompositeImage;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.plugin.ContrastEnhancer;
import ij.process.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import main.Singletons;
import main.utils.FPSCounter;
import mmcorej.CMMCore;
import mmcorej.TaggedImage;
import org.json.JSONException;
import org.micromanager.utils.ImageUtils;
import org.micromanager.utils.MDUtils;
import org.micromanager.utils.MMScriptException;

import java.util.concurrent.atomic.AtomicReference;


/*

@author: Jacob Zargooshifar


 */

public class ImageCanvas extends Canvas {


    int ver = 1;
    int hor = 1;
    private boolean flipHorizentally;
    private boolean flipVertically;
    private boolean autoContrast = true;
    private static TaggedImage lastTaggedImage;


    public enum CursorMode {Normal, Move}

    private CursorMode cursorMode = CursorMode.Normal;

    private static ImageCanvas instance;
    private FPSCounter fpsCounter;
    private int angle = 0;
    private boolean keepAspectRatio = true;
    private boolean rotated;

    private Number lastWidth = 0;
    private Number lastHeight = 0;
    private float magnification = 1;
    ImageProcessor ip;


    private ImageCanvas() {
        fpsCounter = new FPSCounter();
        fpsCounter.start();
        setMouseListeners();


    }

    public static ImageCanvas getInstance() {
        if (instance == null) {
            instance = new ImageCanvas();
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
        CMMCore core = Singletons.getCoreInstance();
        int width = (int) core.getImageWidth();
        int height = (int) core.getImageHeight();

        String pixelType = null;
        Object img = image.pix;

        ImagePlus imagePlus = null;

        try {
            pixelType = MDUtils.getPixelType(image.tags);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MMScriptException e) {
            e.printStackTrace();
        }

        switch (pixelType) {
            case "GRAY8":
                ip = new ByteProcessor(width, height);
                ip.setPixels(img);
                setImageProcessorAttributes(ip);
                imagePlus = new ImagePlus("", ip);
                break;
            case "GRAY16":
                ip = new ShortProcessor(width, height);
                ip.setPixels(img);
                setImageProcessorAttributes(ip);
                imagePlus = new ImagePlus("", ip);
                break;
            case "GRAY32":
                ip = new FloatProcessor(width, height);
                ip.setPixels(img);
                setImageProcessorAttributes(ip);
                imagePlus = new ImagePlus("", ip);
                break;
            case "RGB32":
                byte[][] planes = ImageUtils.getColorPlanesFromRGB32((byte[]) img);
                ColorProcessor cp = new ColorProcessor(width, height);
                cp.setRGB(planes[0], planes[1], planes[2]);
                setImageProcessorAttributes(cp);
                imagePlus = new ImagePlus("", cp);
                break;
            case "RGB64":
                short[][] planesx = ImageUtils.getColorPlanesFromRGB64((short[]) img);
                ImageStack stack = new ImageStack(width, height);
                stack.addSlice("Red", planesx[0]);
                stack.addSlice("Green", planesx[1]);
                stack.addSlice("Blue", planesx[2]);
                ImagePlus imp = new ImagePlus("", stack);
                imp.setDimensions(3, 1, 1);
                imp = new CompositeImage(imp, CompositeImage.COLOR);
                if (autoContrast) {
                    new ContrastEnhancer().stretchHistogram(imp, 0.5);
                }
                imagePlus = imp;
                break;
        }

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
                setCursor(javafx.scene.Cursor.DEFAULT);
                break;
            case Move:
                setCursor(javafx.scene.Cursor.OPEN_HAND);
                break;
        }

    }


    private void setMouseListeners() {
        AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
        AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);
        Rectangle rectangle = new Rectangle();
        rectangle.setFill(Color.RED);

        Overlay overlay = new Overlay();


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


                    getGraphicsContext2D().strokeRect(xOffset.get(), yOffset.get(), event.getX() - xOffset.get(), event.getY() - yOffset.get());
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
