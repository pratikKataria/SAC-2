package com.caringaide.user.utils.location;

import com.google.android.gms.maps.model.LatLng;

public class LatitudeLongitude {

	public static Double CURRENT_LAT = 0.00;
	public static Double CURRENT_LONG = 0.00;
	public static Double PICKUP_LOC_LAT = 0.00;
	public static Double PICKUP_LOC_LONG = 0.00;
	public static Double DROP_LOC_LAT = 0.00;
	public static Double DROP_LOC_LONG = 0.00;
	public static String PICKUP_COUNTRY = "";//like IN,US, this is set from user home
	public static LocationGeocodeData.LocationInfo PICKUP_LOCATION_INFO;
	public static LocationGeocodeData.LocationInfo DROP_LOCATION_INFO;

	public static LatLng PICK_UP_LATLNG = null;
    public static LatLng SCHEDULED_PICK_UP_LATLNG = null;
    public static LatLng SCHEDULED_DROP_LATLNG = null;
	
	
	public static String CURRENT_LOCATION_ADDRESS = "";
	public static String PICK_UP_LOCATION_ADDRESS = "";
	public static String DROP_LOCATION_ADDRESS = "";

	public static String PICKUP_SCHEDULED_ADDRESS="";
	public static String DROP_SCHEDULED_ADDRESS="";
    public static String SCHEDULED_DATE_TIME="";

	// the location address selected from the location fragment
	public static String LOCATION_ADDRESS;
	public static LocationGeocodeData.LocationInfo locationInfo; //remove this later
	public static LocationGeocodeData.LocationInfo CURRENT_LOC_INFO; //this is set from AppLocationManager
	
	
	/*********** Source and Dest Global ***********/
	public static String SOURCE_LOCATION_ADDRESS = "";
	public static String DEST_LOCATION_ADDRESS = "";
	
	/*********** Fare Estimate ***********/
	public static String FARE_ESTIMATE_ADDRESS = "";

	/**
	 * reset the pickup location details while user exit the application
	 */
	public static void resetPickupLocationDetails(){
		PICK_UP_LOCATION_ADDRESS = null;
		PICK_UP_LATLNG = null;
		PICKUP_LOC_LAT = 0.00;
		PICKUP_LOC_LONG = 0.00;
	}

	/**
	 * reset the current location details while user exit the application
	 */
	public static void resetCurrentLocationDetails(){
		CURRENT_LOCATION_ADDRESS = null;
		CURRENT_LAT = 0.00;
		CURRENT_LONG = 0.00;
	}
	
}
