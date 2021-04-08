package com.caringaide.user.utils.location;

import android.util.Log;

import com.caringaide.user.R;
import com.caringaide.user.model.direction.Direction;
import com.caringaide.user.model.direction.DirectionsMapOptions;
import com.caringaide.user.model.direction.IGDFormatter;
import com.caringaide.user.model.direction.Leg;
import com.caringaide.user.model.direction.Path;
import com.caringaide.user.model.direction.Point;
import com.caringaide.user.model.direction.PolyColor;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.SharedPrefsManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by renjit
 */
public class DrawDirectionUtil  {
    private static int colorCode = 0;
    private PolyLineContext polyLineCtx;
    private DirectionsListener directionsListener;
    private String flag="DEFAULT";
    private static final String TAG = "DrawDirectionUtil";

    public static final String RIDE_END = "RIDE_END";
    public static final String COMPARE_DISTANCE = "COMPARE_DISTANCE";


    /**
     * resetting it wil set the color code to 0, this is basically the position of the polycolor arraylist.
     */
    public static void resetColorCode(){
        colorCode = 0;
    }


    public interface DirectionsListener{
        void listDirections(List<Direction> directions, String... flag);
    }

    public static void drawDirection(Direction direction, GoogleMap googleMap) {
        if (null != googleMap && null != direction) {
            List<PolyColor> colors = DirectionsMapOptions.getPolyColors();
            if(colorCode >=colors.size()){
                resetColorCode();
            }
            int i=0,pathIndex=0,legIndex=0;
            //browse the direction legs
            for(Leg leg:direction.getLegList() ){
                //browse the leg's paths
                for(Path path:leg.getPathList()){
                    PolylineOptions polylineOptions = DirectionsMapOptions.getPolyLineOptions();
                    IGDFormatter formatter = DirectionsMapOptions.getFormatter();
                    i=0;
                    pathIndex++;
                    //now browse the points
                    for(Point point : path.getPointList()){
                        i++;
                        //add LatLng points to the polylines
                        polylineOptions.add(point.getLatLng());
                        // Mark the last Point of the path with a HUE_AZURE marker
//                        if(i == path.getPointList().size() -1 ) {
//                            // create marker
//                            Marker marker = googleMap.addMarker(new MarkerOptions().position(point.getLatLng())
//                                    .title(formatter != null ? formatter.getTitle(path) : "Step " + i)
//                                    .snippet(formatter != null ? formatter.getSnippet(path) : "Step " + i)
//                                    .icon(((colors != null && colors.size() > 0) ?
//                                            BitmapDescriptorFactory.defaultMarker(colors.get(legIndex % colors.size()).colorPin) :
//                                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
//                            // if we have a custom snippet call contents
//                            if (formatter != null && formatter.isInfoWindows()) {
//                                formatter.setContents(marker, direction, leg, path);
//                                marker.showInfoWindow();
//                            }
//                        }

                    }
                    //override polyline color
                    //polylineOptions.color(colors.get(legIndex%colors.size()).colorLine);
                    polylineOptions.color(colors.get(colorCode).colorLine);
                    //draw polyline on map
                    if(null!=googleMap)
                        googleMap.addPolyline(polylineOptions);
                }
                legIndex++;
            }
            colorCode ++; //increment the position
        }
    }

    /**
     * draws a polyline on a map supplied in the PolyLineContext
     * @param polylineContext
     */
    public void drawPolylineOnMap(PolyLineContext polylineContext) throws Exception{
        if(null!=polylineContext) {
            this.polyLineCtx = polylineContext;
            Double startLat = polyLineCtx.getStartLat();
            Double startLng = polyLineCtx.getStartLng();
            Double endLat = polyLineCtx.getEndLat();
            Double endLng = polyLineCtx.getEndLng();
            if(null == startLat || null == startLng || null == endLat || null == endLng){
                Log.e(TAG,"Some value is null " +  startLat + "," + startLng + "," + endLat + "," + endLng);
                throw new Exception("Unable to get directions");
            }
            if(null == polyLineCtx.getContext()){
                Log.e(TAG,"Some value is null " + polyLineCtx.getContext());
                throw new Exception("Unable to get directions");
            }

            if( polyLineCtx.getPaths() <= 0){
                Log.e(TAG,"Paths count cannot be 0 or less  ");
                throw new Exception("Unable to get directions");
            }
            //now call directions, you are safe
            this.directionsListener = polylineContext.getDirectionsListener();
            getDirections(startLat, startLng, endLat, endLng);
        }else{
            Log.e(TAG,"polyline context cannot be null");
            throw new Exception("Unable to get directions");
        }

    }

    /*********************** directions ******************************/

    /**
     * makes a remote service call to fetch the directions between start and end lat lng
     * implement the DirectionsListener for receiving the directions as a callback
     * a flag is set by the caller to identify the callback response for that specific call.
     */
    public void getDirections(PolyLineContext polyLineContext, String flag){
        this.polyLineCtx = polyLineContext;
        String DIRECTIONS_API_KEY = SharedPrefsManager.getInstance().getConfigData(BuddyConstants.APP_GOOGLE_API).getConfigValue();
        this.directionsListener = polyLineContext.getDirectionsListener();
        this.flag = flag;
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleDirectionsResponse(remoteResponse);
            }
        },polyLineCtx.getContext(), UserServiceHandler.GET_DIRECTIONS);
        UserServiceHandler.getDirections(polyLineContext.getStartLat(),polyLineContext.getStartLng(),
                polyLineContext.getEndLat(),polyLineContext.getEndLng(),DIRECTIONS_API_KEY,requestParams);
    }

    /**
     * get the directions
     * @param startLat
     * @param startLng
     * @param endLat
     * @param endLng
     */
    private void getDirections(Double startLat,Double startLng,Double endLat,Double endLng){
        String DIRECTIONS_API_KEY = SharedPrefsManager.getInstance().getConfigData(BuddyConstants.APP_GOOGLE_API).getConfigValue();
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleDirectionsResponse(remoteResponse);
            }
        },polyLineCtx.getContext(),UserServiceHandler.GET_DIRECTIONS);

        UserServiceHandler.getDirections(startLat,startLng,
                endLat,endLng,DIRECTIONS_API_KEY,requestParams);
    }

    /**
     * called from the onResponse after getting the directions.
     * @param remoteResponse
     */
    private void handleDirectionsResponse(RemoteResponse remoteResponse){
        String customErrMessage = "Unable to get directions";
        try {
            if (remoteResponse == null) {
                throw new JSONException("response is null");

            } else {
                remoteResponse.setCustomErrorMessage(customErrMessage);
               // if (!CommonUtilities.isErrorsFromResponse(polyLineCtx.getContext(), remoteResponse)) {
                if (!remoteResponse.isError()){
                    String response = remoteResponse.getResponse();
                    Log.d(TAG, " directions response " + response);
                    DirectionsJsonParser jsonParser = new DirectionsJsonParser();
                    List<Direction> directions  = jsonParser.parse(new JSONObject(response));
                    directionsListener.listDirections(directions,flag);
                    if(null!=polyLineCtx){
                        drawPolyline(directions);
                    }
                }else {
                    Log.e(TAG,"error from direction util response");
                    throw new JSONException("error from response");

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * draws a polyline
     * @param directions
     */
    private void drawPolyline(List<Direction> directions){
        if(null!=polyLineCtx.getGoogleMap()) {
            int i = 0;
            DrawDirectionUtil.resetColorCode();
            for (Direction direction : directions) {
                i++;
                DrawDirectionUtil.drawDirection(direction, polyLineCtx.getGoogleMap());
                //I need only one route , so break after first iteration
                if (i > (polyLineCtx.getPaths() - 1)) {
                    break;
                }
            }
        }
    }

}