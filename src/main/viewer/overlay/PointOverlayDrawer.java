/*
    This file is part of ImageJ FX.

    ImageJ FX is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ImageJ FX is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ImageJ FX.  If not, see <http://www.gnu.org/licenses/>. 
    
     Copyright 2015,2016 Cyril MONGIS, Michael Knop
	
 */
package main.viewer.overlay;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import main.utils.OverlayHelper;
import main.viewer.canvas.FxImageCanvas;
import main.viewer.canvas.utils.ViewPort;
import net.imagej.overlay.PointOverlay;


public class PointOverlayDrawer implements OverlayDrawer<PointOverlay>{

    
    public void update(OverlayViewConfiguration<PointOverlay> viewConfig, ViewPort viewport, FxImageCanvas canvas) {
        
        
        PointOverlay overlay = viewConfig.getOverlay();
        //Point2D onScreen = viewport.getPositionOnCamera(PointOverlayHelper.getOverlayPosition(overlay));
        
        //double[] xy = new double[] { onScreen.getX(), onScreen.getY() };

        for(double[] xy : overlay.getPoints()) {
           Point2D onScreen = new Point2D(xy[0],xy[1]);
           xy = new double[] { onScreen.getX(), onScreen.getY() };

           double x = xy[0];
           double y = xy[1];

           canvas.getGraphicsContext2D().setStroke(OverlayHelper.toFxColor(OverlayHelper.getInstance().colorProperty().getValue()));
//           canvas.getGraphicsContext2D().setGlobalAlpha(0.5);
           canvas.getGraphicsContext2D().strokeRect(x-5,y, 10, 0  );
           canvas.getGraphicsContext2D().strokeRect(x,y-5, 0, 10  );
       }
        
        
    }
    
    

    public boolean canHandle(Class<?> t) {
        return t ==  PointOverlay.class;
    }
    
    @Override
    public boolean isOnOverlay(PointOverlay overlay, ViewPort viewport, double xOnImage, double yOnImage) {
        
        if(overlay instanceof PointOverlay) {
            
            PointOverlay pointOverlay = (PointOverlay)overlay;
            
            double[] point = pointOverlay.getPoint(0);
            
            double dx = Math.abs(xOnImage-point[0]);
            double dy = Math.abs(yOnImage-point[1]);
            
            return dx * dy < 10*10;
        }
        else return false;
    }
    
   
   
}
