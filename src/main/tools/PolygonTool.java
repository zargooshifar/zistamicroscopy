package main.tools;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class PolygonTool extends AbstractPathTool {

    FxPath polygonPath = new FxPath();

    FxPath smoothPath = new FxPath();
    
    //Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.STAR_ALT));
    
    
    
    @Override
    public void beforeDrawing(FxPath path) {

    }

    @Override
    public void duringDrawing(FxPath fxPath) {

    }

    @Override
    public void afterDrawing(FxPath path) {

    }

    public void updateSmoothPath() {
       
    }
    
    @Override
    public void onClick(MouseEvent event) {
        
        
       
        elongPath(event, polygonPath);
        
        if(event.getButton() == MouseButton.SECONDARY) {
            addOverlay(polygonPath);
            polygonPath = new FxPath();
            smoothPath = new FxPath();

            event.consume();
            return;
        }
        

        
        
        elongPath(event, polygonPath);
      
        updateSmoothPath();
        drawPath(polygonPath);
        drawPath(smoothPath);
        
        //getCanvas().repaint();

    }

    @Override
    public Node getIcon() {
        
        return GlyphsDude.createIcon(FontAwesomeIcon.STAR_ALT);
    }

    @Override
    public void onMouseMoved(MouseEvent event) {

        if (polygonPath.size() > 1) {
            polygonPath.removeLast();
            elongPath(event,polygonPath);
            drawPath(polygonPath);
        }
    }
    
    
    @Override
    public void onActivated() {
        polygonPath = new FxPath();
    }
    
    private StackPane getContainer() {
        return (StackPane)getCanvas().getParent();
    }
    
    @Override
    public Cursor getDefaultCursor() {
        return Cursor.CROSSHAIR;
    }
    
}
