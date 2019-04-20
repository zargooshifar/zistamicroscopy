package main.Controllers;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import javafx.embed.swing.SwingNode;
import javafx.scene.Node;
import main.Singletons;
import mmcorej.CMMCore;
import mmcorej.TaggedImage;
import org.json.JSONException;
import org.micromanager.utils.MDUtils;
import org.micromanager.utils.MMScriptException;

import javax.swing.*;

public class ImageWindow extends ImageCanvas {

    private static ImageWindow instance;

    private ImagePlus imagePlus;
    private ImageProcessor imageProcessor;
    private JPanel jPanel;

    private ImageWindow() {
        super(main.ImageUtils.ImageCanvas.getInstance().getImagePlus(null));
        this.imageProcessor = this.getImage().getProcessor();
    }

    public static ImageWindow getInstance() {
        if (instance == null)
            instance = new ImageWindow();
        return instance;
    }


    public Node getNode() {
        SwingNode swingNode = new SwingNode();
        jPanel = new JPanel();
        jPanel.add(this);
        swingNode.setContent(jPanel);

        return swingNode;
    }


    public void updateImage(TaggedImage taggedImage) {
        this.getImage().setImage(getImagePlus(taggedImage));
        redraw();


    }

    public void redraw() {
        this.setImageUpdated();
        this.repaint();
        jPanel.repaint();
    }


    public ImagePlus getImagePlus(TaggedImage image) {

        CMMCore core = Singletons.getCoreInstance();
        int width = (int) core.getImageWidth();
        int height = (int) core.getImageHeight();

        String pixelType = null;
        Object img = image.pix;

        this.setSize(width, height);

        try {
            pixelType = MDUtils.getPixelType(image.tags);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MMScriptException e) {
            e.printStackTrace();
        }

        switch (pixelType) {
            case "GRAY8":
                imageProcessor = new ByteProcessor(width, height);
                imageProcessor.setPixels(img);
//                setImageProcessorAttributes(ip);
                imagePlus = new ImagePlus("", imageProcessor);
                break;
//            case "GRAY16":
//                ip = new ShortProcessor(width, height);
//                ip.setPixels(img);
//                setImageProcessorAttributes(ip);
//                imagePlus = new ImagePlus("", ip);
//                break;
//            case "GRAY32":
//                ip = new FloatProcessor(width, height);
//                ip.setPixels(img);
//                setImageProcessorAttributes(ip);
//                imagePlus = new ImagePlus("", ip);
//                break;
//            case "RGB32":
//                byte[][] planes = ImageUtils.getColorPlanesFromRGB32((byte[]) img);
//                ColorProcessor cp = new ColorProcessor(width, height);
//                cp.setRGB(planes[0], planes[1], planes[2]);
//                setImageProcessorAttributes(cp);
//                imagePlus = new ImagePlus("", cp);
//                break;
//            case "RGB64":
//                short[][] planesx = ImageUtils.getColorPlanesFromRGB64((short[]) img);
//                ImageStack stack = new ImageStack(width, height);
//                stack.addSlice("Red", planesx[0]);
//                stack.addSlice("Green", planesx[1]);
//                stack.addSlice("Blue", planesx[2]);
//                ImagePlus imp = new ImagePlus("", stack);
//                imp.setDimensions(3, 1, 1);
//                imp = new CompositeImage(imp, CompositeImage.COLOR);
//                if (autoContrast) {
//                    new ContrastEnhancer().stretchHistogram(imp, 0.5);
//                }
//                imagePlus = imp;
//                break;
        }

        return imagePlus;

    }


}
