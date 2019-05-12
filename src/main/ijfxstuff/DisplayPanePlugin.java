package main.ijfxstuff;

import javafx.beans.property.StringProperty;
import javafx.scene.layout.Pane;
import org.scijava.display.Display;
import org.scijava.plugin.SciJavaPlugin;

public interface DisplayPanePlugin<T extends Display> extends SciJavaPlugin {
    void display(T display);
    void dispose();
    StringProperty titleProperty();
    Pane getPane();
}
