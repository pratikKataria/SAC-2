package com.caringaide.user.fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.caringaide.user.R;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.SharedPrefsManager;
import com.caringaide.user.utils.location.LatitudeLongitude;
import com.caringaide.user.utils.location.LocationGeocodeData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * LocationFragment exclusively for LocationPreferncesfragment
 */
public class LocationAutoCompleteFragment extends Fragment implements LocationGeocodeData.LocationGeocodeListener {

    private View searchLocView;
    //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=ban&key=API_KEY
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private AutoCompleteTextView atv;
    private LocationFragmentListener locationFragmentListener;
    private static String address;
    private Context context;
    private LocationGeocodeData.LocationInfo locationInfo = null;
    private String key="";
    //InputMethodManager keyBoardManager = null;

    private static final String TAG="NoPrefLocationFragment";

    public interface LocationFragmentListener{
        /**
         * locationInfo can be null, so please take care of null exceptions
         * @param locationInfo
         */
        void onLocationSelection(LocationGeocodeData.LocationInfo locationInfo);
    }

    public void setLocationListener(LocationFragmentListener locationListener){
        this.locationFragmentListener = locationListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        searchLocView = inflater.inflate(R.layout.location_selection_layout, container, false);

        //back action
        setBackAction();
        Bundle bundle = getArguments();
        if(null!= bundle){
            key = (String)bundle.get("key");
        }
        atv = (AutoCompleteTextView) searchLocView.findViewById(R.id.atv_user_choose_location);
        atv.requestFocus();
        if (null!=key&&!key.isEmpty()){
            atv.setHint("Search "+key+" Location");
        }
//        keyBoardManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        keyBoardManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        atv.setAdapter(new GooglePlacesAutocompleteAdapter(context, R.layout.auto_complete_text_layout));
        atv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                address = (String) parent.getItemAtPosition(position);
                LatitudeLongitude.LOCATION_ADDRESS = address;
                //get the location info using the geocode web service call, the location will be updated by a callback
                LocationGeocodeData.getLocationInfo(getActivity(),address, LocationAutoCompleteFragment.this);
            }
        });

        return searchLocView;
    }

    private void setBackAction(){
        ImageView imgBackFromUserChangeLocation = (ImageView) searchLocView.findViewById(R.id.img_back_choose_location);
        imgBackFromUserChangeLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CommonUtilities.hideKeyPad();
                searchLocView.setVisibility(View.GONE);
                locationFragmentListener.onLocationSelection(locationInfo);
            }
        });

    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ArrayList autocomplete(String input) {
        ArrayList resultList = null;
        HttpURLConnection conn = null;
        String API_KEY = SharedPrefsManager.getInstance().getConfigData(BuddyConstants.APP_GOOGLE_API).getConfigValue();
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE+ TYPE_AUTOCOMPLETE + OUT_JSON);
            //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=ban&key=API_KEY
            sb.append("?input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&components=country:"+ BuddyApp.getCountryAbbrv().toLowerCase());
            sb.append("&key=" + API_KEY);
            Log.d("Places on API", sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            Log.d("Places on API", jsonResults.toString());
        } catch (MalformedURLException e) {
            Log.e("Error on API", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e("Error on API", "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                String description = predsJsonArray.getJSONObject(i).getString("description");
                Log.d(TAG,"============================================================");
                Log.d(TAG,predsJsonArray.getJSONObject(i).toString());
                Log.d(TAG,"============================================================");
                resultList.add(description);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error on Places, Cannot process JSON results", e);
        }
        return resultList;
    }

    @SuppressWarnings("rawtypes")
    class GooglePlacesAutocompleteAdapter extends ArrayAdapter
            implements Filterable {
        private ArrayList resultList;
        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return (String) resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());
                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint,FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    /**
     * a call back for updating the location info by LocationGeocodeData.
     * @param locationInfo
     */
    @Override
    public void onLocationUpdate(LocationGeocodeData.LocationInfo locationInfo) {
        CommonUtilities.hideKeyPad();
        if(!key.isEmpty()){
            locationInfo.setKey(key);
        }
        locationFragmentListener.onLocationSelection(locationInfo);
        searchLocView.setVisibility(View.GONE);
        CommonUtilities.hideKeyPad();
    }

}