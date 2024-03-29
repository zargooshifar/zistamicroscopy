
package main.viewer.overlay;



import main.tools.overlay.MoveablePoint;
import main.viewer.canvas.utils.ViewPort;
import net.imagej.overlay.Overlay;
import net.imagej.overlay.RectangleOverlay;
import org.scijava.Context;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = OverlayModifier.class)
public class RectangleModifier implements OverlayModifier<RectangleOverlay>{
    
    MoveablePoint a;
    MoveablePoint b;
    
    List<MoveablePoint> hashSet;

    RectangleOverlay overlay;
    
    RectangleOverlayHelper helper;
    
    @Parameter
    Context context;
    
    @Override
    public boolean canHandle(Overlay o) {
        return o instanceof RectangleOverlay;
    }

    @Override
    public List<MoveablePoint> getModifiers(ViewPort viewport, RectangleOverlay overlay) {
        
    
        if(hashSet == null || hashSet.size() == 0 || hashSet.get(1) == null) {
            
            
            
            a = new MoveablePoint(viewport);
            b = new MoveablePoint(viewport);
            
            // initialixing
            hashSet = new ArrayList<>(2);
            hashSet.add(a);
            hashSet.add(b);
            
            // creating the helper 
            helper = new RectangleOverlayHelper(overlay);
            context.inject(helper);
            a.placeOnScreen(viewport.getPositionOnCamera(helper.getMinEdge()));
            b.placeOnScreen(viewport.getPositionOnCamera(helper.getMaxEdge()));
            
            
            
            a.positionOnImageProperty().setValue(helper.getMinEdge());
            b.positionOnImageProperty().setValue(helper.getMaxEdge());
            
            
            // change of A and B will update automatically the overlay via the helper
            // because each time the point is moved on the screen, the position
            // on the image is also updated
            helper.minEdgeProperty().bind(a.positionOnImageProperty());
            helper.maxEdgeProperty().bind(b.positionOnImageProperty());

        }
        
        return hashSet;
    
    }
    
    
    
    
}
