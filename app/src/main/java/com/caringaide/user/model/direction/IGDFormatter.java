package com.caringaide.user.model.direction;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by renjit.
 * Interface between client and method drawGDirection
 * Can custom the display of infoWindows, or just change title and snippet
 */
public interface IGDFormatter {
    /**
     * Method to get the title of this path
     * @param path, the current path which display on maps
     * @return the title of this path
     */
    public abstract String getTitle(Path path);

    /**
     * Method to get the string in snippet to display of this path
     * @param path, the current path which display on maps
     * @return the snippet string of this path
     */
    public abstract String getSnippet(Path path);

    /**
     * is a info windows or not ?
     * for custom snippet it will be true, else true
     * @return yes or no
     */
    public abstract boolean isInfoWindows();

    /**
     * Display custom snippet if isInfoWindows is true
     * Construct your custom view here
     * @param marker, the current marker
     * @param direction, the current direction
     * @param legs, the current legs
     * @param path, the current path
     */
    public abstract void setContents(Marker marker, Direction direction, Leg legs, Path path);
}
