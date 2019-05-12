package main.ImageUtils;


/*

@author: Jacob Zargooshifar


 */

import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.process.ImageProcessor;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.StackPane;
import mmcorej.TaggedImage;
import org.micromanager.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageCanvas extends ij.gui.ImageCanvas {

    private JPanel panel;
    private static ImageCanvas instance;
    private SwingNode node;
    private static ImageWindowX imageWindowX;


    public SwingNode getSwingNode(StackPane stackPane) {
        panel.add(instance);
        panel.addMouseListener(instance.getMouseListeners()[0]);
        panel.addMouseMotionListener(instance.getMouseMotionListeners()[0]);
        node.setContent(panel);

        AtomicInteger cw = new AtomicInteger();
        AtomicInteger ch = new AtomicInteger();


        stackPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            cw.set(newValue.intValue());
            instance.setSize(cw.get(), ch.get());
            instance.fitToContainer();


        });

        stackPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            ch.set(newValue.intValue());
            instance.setSize(cw.get(), ch.get());
            instance.fitToContainer();

        });

        return node;
    }

    private ImageCanvas() {
        super(new ImagePlus("title", new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB)));
        node = new SwingNode();
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        imageWindowX = new ImageWindowX("");
    }


    public static ImageCanvas getInstance() {
        if (instance == null) {
            instance = new ImageCanvas();
            instance.getImageWindowX().setImageCanvas(instance);
            instance.getImageWindowX().setImage(instance.getImage());
            instance.getImage().setWindow(instance.getImageWindowX());
        }
        return instance;
    }


    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        panel.repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        panel.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        panel.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        panel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        panel.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        panel.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        panel.repaint();
    }

    public ImageWindowX getImageWindowX() {
        return imageWindowX;
    }

    public void setImageWindowX(ImageWindowX imageWindowX) {
        ImageCanvas.imageWindowX = imageWindowX;
    }

    public static class ImageWindowX extends ImageWindow {
        public ImageWindowX(String title) {
            super(title);
        }

        public void setImageCanvas(ImageCanvas ic) {
            this.ic = ic;
        }
    }


    private void fitToContainer() {
        Rectangle bounds = this.getBounds();
//        Insets insets = win.getInsets();
//        int sliderHeight = win.getSliderHeight();

        double xmag = (bounds.width / (double) this.srcRect.width);
        double ymag = (bounds.height / (double) this.srcRect.height);
        this.setMagnification(Math.min(xmag, ymag));
        instance.doLayout();

    }

    public void repaint() {
        this.setImageUpdated();
//        this.repaint();
        panel.repaint();
    }


    public void updateImage(TaggedImage image) {
        ImageProcessor imageProcessor = ImageUtils.makeProcessor(image);
        ImagePlus imagePlus = new ImagePlus("", imageProcessor);

        this.getImage().setImage(imagePlus);
        repaint();

    }


    public void zoomFit() {
    }

    public void flipHorizentally() {
    }

    public void flipVertically() {
    }

    public void zoomIn() {
    }

    public void zoomOut() {
    }

    public void rotate(int i) {
    }
}
