package main.Controllers;

import ij.ImagePlus;
import ij.gui.ImageCanvas;

public class ImageWindow extends ij.gui.ImageWindow {


    public ImageWindow(String title) {
        super(title);
    }

    public ImageWindow(ImagePlus imp) {
        super(imp);
    }

    public ImageWindow(ImagePlus imp, ImageCanvas ic) {
        super(imp, ic);
    }

    public void setCanvas(ImageCanvas ic) {
        this.ic = ic;
    }


}
