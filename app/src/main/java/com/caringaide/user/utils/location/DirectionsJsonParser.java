package com.caringaide.user.utils.location;

import android.util.Log;

import com.caringaide.user.model.direction.Direction;
import com.caringaide.user.model.direction.Leg;
import com.caringaide.user.model.direction.Path;
import com.caringaide.user.model.direction.Point;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by renjit.
 */
public class DirectionsJsonParser {

    private static final String TAG = "DirectionParser";
    public List<Direction> parse(JSONObject jsonObject){
        // The returned direction
        List<Direction> directionsList = null;
        // The current GDirection
        Direction currentDirection = null;
        // The legs
        List<Leg> legs = null;
        // The current leg
        Leg currentLeg = null;
        // The paths
        List<Path> paths = null;
        // The current path
        Path currentPath = null;
        // JSON Array representing Routes
        JSONArray jRoutes = null;
        JSONObject jRoute;
        JSONObject jBound;
        // JSON Array representing Legs
        JSONArray jLegs = null;
        JSONObject jLeg;
        // JSON Array representing Step
        JSONArray jSteps = null;
        JSONObject jStep;
        String polyline = "";

        try {
            jRoutes = jsonObject.getJSONArray("routes");
            Log.d(TAG, "Routes " + jRoutes.length());
            directionsList = new ArrayList<>();
            for(int i=0;i<jRoutes.length();i++){
                jRoute = jRoutes.getJSONObject(i);
                jLegs = jRoute.getJSONArray("legs");
                legs = new ArrayList<>();
                for(int j=0;j<jLegs.length();j++) {
                    jLeg = jLegs.getJSONObject(j);
                    Log.d(TAG, "each jleg distance " + jLeg.getJSONObject("distance").get("text"));
                    jSteps = jLeg.getJSONArray("steps");
                    paths = new ArrayList<>();
                    for (int k = 0; k < jSteps.length(); k++) {
                        jStep = jSteps.getJSONObject(k);
                        polyline = jStep.getJSONObject("polyline").getString("points");
                        Log.d(TAG, "polyline " + polyline);
                        List<Point> pointList = decodePoly(polyline);
                        currentPath = new Path(pointList);
                        int distanceInMeters = jStep.getJSONObject("distance").getInt("value");
                        int durationInSecs = jStep.getJSONObject("duration").getInt("value");
                        currentPath.setDistance(distanceInMeters);
                        currentPath.setDuration(durationInSecs);
                        currentPath.setStart_lat(jStep.getJSONObject("start_location").getString("lat"));
                        currentPath.setStart_lng(jStep.getJSONObject("start_location").getString("lng"));
                        currentPath.setEnd_lat(jStep.getJSONObject("end_location").getString("lat"));
                        currentPath.setEnd_lng(jStep.getJSONObject("end_location").getString("lng"));
                        Log.d(TAG, "Distance and Duration in metres and seconds " + distanceInMeters + "," + durationInSecs);
                        currentPath.setHtmlText(jStep.getString("html_instructions"));
                        currentPath.setTravelMode(jStep.getString("travel_mode"));
                        paths.add(currentPath);
                    }//step done
                    currentLeg = new Leg(paths);
                    int legDistInMetres = jLeg.getJSONObject("distance").getInt("value");
                    int legDurationInSecs = jLeg.getJSONObject("duration").getInt("value");
                    String startAddress = jLeg.getString("start_address");
                    String endAddress = jLeg.getString("end_address");
                    currentLeg.setDistance(legDistInMetres);
                    currentLeg.setDuration(legDurationInSecs);
                    currentLeg.setStartAddress(startAddress);
                    currentLeg.setEndAddress(endAddress);
                    legs.add(currentLeg);
                    Log.d(TAG, "Leg distance and duration in metres and seconds" + legDistInMetres +","+legDurationInSecs );
                    Log.d(TAG, "Added new path and path size is " + paths.size());
                }// leg done
                currentDirection = new Direction(legs);
                jBound=(JSONObject)jRoute.get("bounds");
                currentDirection.setNorthEastBound(new LatLng(
                        (jBound.getJSONObject("northeast")).getDouble("lat"),
                        (jBound.getJSONObject("northeast")).getDouble("lng")));
                currentDirection.setSouthWestBound(new LatLng(
                        (jBound.getJSONObject("southwest")).getDouble("lat"),
                        (jBound.getJSONObject("southwest")).getDouble("lng")));
                directionsList.add(currentDirection);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return directionsList;
    }

    /**
     * Method to decode polyline points
     * Courtesy :
     * http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction
     * -api-with-java
     */
    private List<Point> decodePoly(String encoded) {

        List<Point> poly = new ArrayList<Point>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            poly.add(new Point((double) lat / 1E5, (double) lng / 1E5));
        }

        return poly;
    }


}
