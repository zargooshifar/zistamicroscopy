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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageCanvas extends ij.gui.ImageCanvas {

    private JPanel panel;
    private static ImageCanvas instance;
    private SwingNode node;
    private static ImageWindowX imageWindowX;
    private boolean isFlipedVertical;
    private boolean isFlipedHorizental;
    private int rotationAngle = 0;
    private AtomicInteger cw;
    private AtomicInteger ch;
    public SwingNode getSwingNode(StackPane stackPane) {
        panel.add(instance);
        panel.addMouseListener(instance.getMouseListeners()[0]);
        panel.addMouseMotionListener(instance.getMouseMotionListeners()[0]);
        node.setContent(panel);
        cw = new AtomicInteger();
        ch = new AtomicInteger();


        stackPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            cw.set(newValue.intValue());
            refreshCanvasSizes();

//            int x = (cw.get() / 2) - (getWidth() / 2);
//            int y = (ch.get() / 2) - (getHeight() / 2);



//            this.setSourceRect(new Rectangle(x,y,cw.get(),ch.get()));



        });

        stackPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            ch.set(newValue.intValue());
            refreshCanvasSizes();
//            int x = (cw.get() / 2) - (getWidth() / 2);
//            int y = (ch.get() / 2) - (getHeight() / 2);


//            this.setSourceRect(new Rectangle(0,0,cw.get(),ch.get()));

        });

        return node;
    }

    @Override
    public void zoomIn(int sx, int sy) {
        super.zoomIn(sx, sy);
        int min = Math.min(cw.get(),ch.get());
        instance.setSize(min,min);
        imageWindowX.setSize(cw.get(),ch.get());
    }

    @Override
    public void zoomOut(int sx, int sy) {
        super.zoomOut(sx, sy);
        int min = Math.min(cw.get(),ch.get());
        instance.setSize(min,min);
        imageWindowX.setSize(cw.get(),ch.get());
    }

    @Override
    public void zoom100Percent() {
        super.zoom100Percent();
    }


    private void refreshCanvasSizes(){
        int min = Math.min(cw.get(),ch.get());
        instance.setSize(min,min);
        imageWindowX.setSize(min,min);
        panel.setMaximumSize(new Dimension(100,100));
        panel.setBackground(Color.BLACK);



        System.out.println(String.format("canvas x:%d y:%d  panel x:%d y:%d",getWidth(),getHeight(),panel.getWidth(),panel.getHeight()));
        instance.fitToContainer();

    }

    private ImageCanvas() {
        super(new ImagePlus("title", new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB)));
        node = new SwingNode();
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
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


    private void refresh(){
        if (getImage().getRoi()!=null){
            getImage().getRoi().setStrokeColor(Color.RED);
            getImage().getRoi().setStrokeWidth(1d);
        }
        panel.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        refresh();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        refresh();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        refresh();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        refresh();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        refresh();

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        refresh();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        refresh();
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


    public void fitToContainer() {
        Rectangle bounds = this.getBounds();
//        Insets insets = win.getInsets();
//        int sliderHeight = win.getSliderHeight();



        double xmag = (bounds.width / (double) this.srcRect.width);
        double ymag = (bounds.height / (double) this.srcRect.height);
        double mag = Math.min(xmag,ymag) ;
        this.setMagnification(mag);
        instance.doLayout();

    }

    public void repaint() {
        this.setImageUpdated();
//        this.repaint();
        refresh();
    }

    public void rotateLeft(){
        rotationAngle -= 90;
        if (rotationAngle== 360 ||rotationAngle == -360) {
            rotationAngle = 0;
        }
        getImage().getProcessor().rotate(-90);
        repaint();
    }

    public void rotateRight(){
        rotationAngle += 90;
        if (rotationAngle== 360 ||rotationAngle == -360) {
            rotationAngle = 0;
        }
        getImage().getProcessor().rotate(90);
        repaint();
    }

    public void flipVertical(){
        isFlipedVertical = !isFlipedVertical;
        getImage().getProcessor().flipVertical();
        repaint();
    }

    public void flipHorizental(){
        isFlipedHorizental = !isFlipedHorizental;
        getImage().getProcessor().flipHorizontal();
        repaint();
    }

    public void updateImage(TaggedImage image) {
        ImageProcessor imageProcessor = ImageUtils.makeProcessor(image);
//        imageProcessor.autoThreshold();
        if(isFlipedHorizental)
            imageProcessor.flipHorizontal();
        if(isFlipedVertical)
            imageProcessor.flipVertical();
        imageProcessor.rotate(rotationAngle);
        ImagePlus imagePlus = new ImagePlus("", imageProcessor);
        this.getImage().setImage(imagePlus);
        repaint();
    }





}
