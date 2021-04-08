package com.caringaide.user.model.direction;

import android.os.Build;
import android.text.Html;

import com.caringaide.user.utils.CommonUtilities;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by renjit.
 * a simple formatter for direction paths
 */

public class SimpleGDFormatter implements IGDFormatter {
    @Override
    public String getTitle(Path path) {
        if(null!=path) {
            int distInMetres = path.getDistance();
            Double distInMiles = CommonUtilities.getMilesForMeters(distInMetres);
            return "Distance : " + distInMiles + " miles";
        }
        return "Distance : " + 0.0 + " miles";
    }

    @Override
    public String getSnippet(Path path) {
        if(Build.VERSION.SDK_INT >=24 )
            return Html.fromHtml(path.getHtmlText(),Html.FROM_HTML_MODE_LEGACY).toString();
        else if(Build.VERSION.SDK_INT >=16)
            return Html.fromHtml(path.getHtmlText()).toString();
        else
            return "";
    }

    @Override
    public boolean isInfoWindows() {
        return false;
    }

    @Override
    public void setContents(Marker marker, Direction direction, Leg legs, Path path) {

    }
}
