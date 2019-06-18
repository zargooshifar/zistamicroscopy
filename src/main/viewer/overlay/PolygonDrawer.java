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

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import main.viewer.canvas.FxImageCanvas;
import main.viewer.canvas.utils.ViewPort;
import net.imagej.overlay.PolygonOverlay;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.PolygonRegionOfInterest;
import org.scijava.plugin.Plugin;

import java.awt.geom.GeneralPath;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = OverlayDrawer.class)
public class PolygonDrawer implements OverlayDrawer<PolygonOverlay> {

    Polygon shape;

    /**
     * @Override public Node update(PolygonOverlay overlay, ViewPort viewport) {
     *
     * if (shape == null) {
     *
     * shape = new Polygon();
     *
     * }
     * shape.getPoints().clear();
     *
     * PolygonRegionOfInterest roi = overlay.getRegionOfInterest();
     *
     * Double[] points = new Double[roi.getVertexCount() * 2]; for (int i = 0; i
     * != roi.getVertexCount(); i++) {
     *
     * Point2D positionOnViewPort = new
     * Point2D(roi.getVertex(i).getDoublePosition(0),roi.getVertex(i).getDoublePosition(1));
     * positionOnViewPort = viewport.getPositionOnCamera(positionOnViewPort);
     * points[i * 2] = positionOnViewPort.getX(); points[i * 2 + 1] =
     * positionOnViewPort.getY();
     *
     * }
     *
     * shape.getPoints().addAll(points);
     *
     * OverlayDrawer.color(overlay, shape); return shape;
     *
     * }\
     */

    FxImageCanvas canvas;

    @Override
    public void update(OverlayViewConfiguration<PolygonOverlay> viewConfig, ViewPort viewport, FxImageCanvas canvas) {

        this.canvas = canvas;

        PolygonOverlay overlay = viewConfig.getOverlay();

        GraphicsContext context = canvas.getGraphicsContext2D();
        PolygonRegionOfInterest roi = overlay.getRegionOfInterest();
        double[] position = new double[2];
        context.setFill(viewConfig.getFillCollor());
        context.setLineWidth(viewConfig.getStrokeWidth());
        double[] xPoints = new double[roi.getVertexCount()];
        double[] yPoints = new double[roi.getVertexCount()];
        for (int i = 0; i != roi.getVertexCount(); i++) {
            RealLocalizable vertex = roi.getVertex(i);
            vertex.localize(position);
            viewport.localizeOnCamera(position);
            xPoints[i] = position[0];
            yPoints[i] = position[1];
        }

        drawSpline(xPoints,yPoints);
    }


    private void drawSpline(double[] xpoints, double[] ypoints) {
        GraphicsContext g2d = canvas.getGraphicsContext2D();
        GeneralPath path = new GeneralPath();
        path.moveTo(xpoints[0], ypoints[0]);
        for (int i=1; i<xpoints.length; i++)
            path.lineTo(xpoints[i], ypoints[i]);
            g2d.stroke();
    }

    public boolean canHandle(Class<?> t) {
        return t == PolygonOverlay.class;
    }

}
