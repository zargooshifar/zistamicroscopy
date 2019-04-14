package main.ImageUtils;


import ij.ImagePlus;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import main.FPSCounter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/*

@author: Jacob Zargooshifar


 */

public class ImageCanvas extends Canvas implements MouseListener, MouseMotionListener, Cloneable {

    protected static Cursor defaultCursor = new Cursor(0);
    protected static Cursor handCursor = new Cursor(12);
    protected static Cursor moveCursor = new Cursor(13);
    protected static Cursor crosshairCursor = new Cursor(1);

    private static ImageCanvas instance;
    private FPSCounter fpsCounter;
    private int angle = 0;
    private boolean keepAspectRatio = true;
    private boolean rotated;
    private ImagePlus lastImagePlus;

    private Number lastWidth = 0;
    private Number lastHeight = 0;
    private double magnification;


    private ImageCanvas() {
        fpsCounter = new FPSCounter();
        fpsCounter.start();
    }

    public static ImageCanvas getInstance() {
        if (instance == null) {
            instance = new ImageCanvas();
        }

        return instance;
    }


    public void setSizeListener(Pane pane) {


        pane.widthProperty().addListener((observable, oldValue, newValue) -> {
            widthProperty().setValue(newValue.intValue());
            if (lastWidth.doubleValue() != getWidth()) {
                updateImage(lastImagePlus);
            }
//            getGraphicsContext2D().fillRect(0,0,newValue.intValue(), getHeight());
        });


        pane.heightProperty().addListener((observable, oldValue, newValue) -> {
            heightProperty().setValue(newValue.intValue());
            if (lastHeight.doubleValue() != getHeight()) {
                updateImage(lastImagePlus);
            }
//            getGraphicsContext2D().fillRect(0,0, getWidth(), newValue.intValue());
        });
    }

    public void refreshSize() {
        updateImage(lastImagePlus);
    }

    public boolean getKeepAspectRatio() {
        return keepAspectRatio;
    }

    public void setKeepAspectRatio(boolean bool) {
        keepAspectRatio = bool;
    }

    public void updateImage(ImagePlus imp) {
        lastImagePlus = imp;
        Platform.runLater(() -> {

            double w = getWidth();
            double h = getHeight();
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
            rotate(angle, x + w / 2, y + h / 2);
            switch (angle) {
                case 0:
                    //nothing to do!
                    break;
                case 90:
                case -270:
                    //rotated cw! so x has increased as w, y increased as h, 
                    break;
                case 180:
                case -180:

                    break;
                case 270:
                case -90:

                    break;
            }

            getGraphicsContext2D().drawImage(finalImage, x, y, w, h);


        });
    }

    private void rotate(double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        getGraphicsContext2D().setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    public void rotate(int angle) {

        this.angle += angle;
        if (this.angle == 360 || this.angle == -360) {
            this.angle = 0;
        }
        rotated = !rotated;
    }


    public double getMagnification() {
        return this.magnification;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        int sx = e.getX();
        int sy = e.getY();

        System.out.println(sx + " " + sy);

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
