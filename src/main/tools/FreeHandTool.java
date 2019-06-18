/*
 * /*
 *     This file is part of ImageJ FX.
 *
 *     ImageJ FX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ImageJ FX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ImageJ FX.  If not, see <http://www.gnu.org/licenses/>. 
 *
 * 	Copyright 2015,2016 Cyril MONGIS, Michael Knop
 *
 */
package main.tools;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import main.utils.OverlayHelper;
import net.imagej.overlay.PolygonOverlay;
import net.imglib2.RealPoint;
import org.scijava.Context;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class FreeHandTool extends AbstractPathTool {

    
    
  // @Parameter
   //LegacyService legacyService;
    
//    @Parameter
//    Context context;
//
//    @Parameter
//    OverlayOptionsService overlayOptionsService;
    
    @Override
    public void beforeDrawing(FxPath path) {


    }
    
    

    @Override
    public void duringDrawing(FxPath fxPath) {

        List<Point2D> points = fxPath.getPathOnScreen();

        System.out.println(points.size());
        if (points.size() == 2) {
            getCanvas().getGraphicsContext2D().moveTo(points.get(1).getX(),points.get(1).getY());
            getCanvas().getGraphicsContext2D().beginPath();
            return;
        }

        double x = FxPath.getLast(points).getX();
        double y = FxPath.getLast(points).getY();

        getCanvas().repaint();
        getCanvas().getGraphicsContext2D().setStroke(OverlayHelper.toFxColor(OverlayHelper.getInstance().colorProperty().getValue()) );
        getCanvas().getGraphicsContext2D().setLineWidth(OverlayHelper.getInstance().widthProperty().getValue());
//        getCanvas().getGraphicsContext2D().strokePolygon(xList, yList, xList.length);
        getCanvas().getGraphicsContext2D().lineTo(x,y);
        getCanvas().getGraphicsContext2D().stroke();

    }

    
    
    @Override
    public void afterDrawing(FxPath path) {
        
        if(path == null) return;
        ArrayList<Point2D> pathOnImage = path.getPathOnImage();


        PolygonOverlay overlay = new PolygonOverlay();
        
        overlay.setName("new overlay");
        overlay.setFillColor(OverlayHelper.getInstance().colorProperty().getValue());
        overlay.setLineColor(OverlayHelper.getInstance().colorProperty().getValue());
        overlay.setLineWidth(OverlayHelper.getInstance().widthProperty().getValue());


        
        for(int i = 0; i!=pathOnImage.size(); i++) {
            Point2D p = pathOnImage.get(i);
            overlay.getRegionOfInterest().addVertex(i, new RealPoint(p.getX(),p.getY()));
        }
        overlay.rebuild();


        addOverlays(overlay, path);
        
        
    }
    
   

    @Override
    public void onClick(MouseEvent event) {
    }

    @Override
    public Node getIcon() {
        return GlyphsDude.createIcon(FontAwesomeIcon.PENCIL);
    }
    
    
    
    
}
