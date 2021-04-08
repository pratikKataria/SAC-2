package com.caringaide.user.model.direction;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by renjit.
 */
public class DirectionsMapOptions {

    private static int MAX_COLOR = 0;
    private static List<PolyColor> colors=null;
    private static IGDFormatter formatter;

    public static List<PolyColor> getPolyColors(){
        if(null == colors){
            colors = new ArrayList<PolyColor>();
            colors.add(new PolyColor(Color.BLUE, BitmapDescriptorFactory.HUE_AZURE));
            colors.add(new PolyColor(Color.BLACK, BitmapDescriptorFactory.HUE_AZURE));
            colors.add(new PolyColor(Color.YELLOW, BitmapDescriptorFactory.HUE_YELLOW));
            colors.add(new PolyColor(Color.CYAN, BitmapDescriptorFactory.HUE_CYAN));
        }
        return colors;
    }

    public static int getMaxColor(){
        MAX_COLOR =  getPolyColors().size();
        return MAX_COLOR;
    }

    public static PolylineOptions getPolyLineOptions(){
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(15);
        polylineOptions.color(Color.BLUE);
        return polylineOptions;
    }

    public static IGDFormatter getFormatter() {
        if(null == DirectionsMapOptions.formatter){
            formatter = new SimpleGDFormatter();
        }
        return formatter;
    }

    public static void setFormatter(IGDFormatter formatter) {
        DirectionsMapOptions.formatter = formatter;
    }
}
