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
package main.utils;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public class Point2DUtils {
    
   
    
    public static Property<Point2D> createProperty() {
        return new SimpleObjectProperty<>();
    }
    
    public static Property<Point2D> createProperty(Point2D initialValue) {
        return new SimpleObjectProperty<>(initialValue);
    }

    public static double[] asArray(Point2D point) {
        double[] pAsArray = new double[2];
        pAsArray[0] = point.getX();
        pAsArray[1] = point.getY();
        return pAsArray;
    }
}