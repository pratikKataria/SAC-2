package com.caringaide.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.caringaide.user.R;
import com.caringaide.user.activities.TrackBuddyDetails;
import com.caringaide.user.fragments.CustomMapFragment;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.ContactUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;

import java.util.ArrayList;

import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.getDateAndTimeAsString;
import static com.caringaide.user.utils.CommonUtilities.getDayAfterGivenHour;
import static com.caringaide.user.utils.CommonUtilities.getHoursAndMinutes;
import static com.caringaide.user.utils.CommonUtilities.setToCamelCase;


public class BuddyTransitAdapter extends ArrayAdapter<BuddyService> implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "BuddyTransitAdapter";
    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private ArrayList<BuddyService> buddyserviceList;
    private Context context;
    private Fragment reqFragment;
    private int resource;
    private GoogleMap googleMap;
    private UiSettings googleMapUiSettings;
    private CustomMapFragment mapFragment;

    public BuddyTransitAdapter(Context context, int resource,
                               ArrayList<BuddyService> objects, Fragment fragment) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        buddyserviceList = objects;
        this.context = context;
        this.resource = resource;
        this.reqFragment = fragment;
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] strings, @NonNull int[] grantResult) {
        Log.d(TAG,"permission granted"+reqCode+"permissions"+strings);
        Intent intent=null;
        if (grantResult[0]==0) {
            //permission granted
            //do nothing for now
        }else{
            if(reqCode != 1) {
                alertAndAction(reqFragment.getActivity(),
                        context.getString(R.string.Manifest_permission_CALL_PHONE), context.getString(R.string.ok),
                        null, new AlertAction() {
                            @Override
                            public void positiveAction() {

                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        }
    }

    static class ViewHolder {
        TextView benName;
        Button buddyPhone;
//        ImageView mapView;
        ImageView benImage;
        TextView moreDetailsBtn;
        TextView bookingId;
        TextView expectedStartTime;
        TextView actualStartTime;
        TextView expectedEndTime;
        TextView bookingexpectedDuration;
        TextView trackingStatusTxt;
        LinearLayout expectedStartTimeLayout,serviceStartTimeLayout;
    }

    @Override
    public BuddyService getItem(int position) {
        return buddyserviceList.get(position);
    }

    @Override
    public int getCount() {
        return buddyserviceList.size();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View row = convertView;
        if (row == null) {

            viewHolder = new ViewHolder();
            row = inflater.inflate(resource, null);
            viewHolder.benName = row
                    .findViewById(R.id.buddy_transit_ben_name_result);
            viewHolder.buddyPhone = row
                    .findViewById(R.id.buddy_transit_buddy_phone_result);
            viewHolder.benImage = row
                    .findViewById(R.id.ben_gender_image);
            viewHolder.moreDetailsBtn = row
                    .findViewById(R.id.more_details);
            viewHolder.bookingId = row
                    .findViewById(R.id.ben_booking_id_result);
            viewHolder.expectedStartTime = row
                    .findViewById(R.id.ben_exp_service_date_result);
            viewHolder.expectedEndTime = row
                    .findViewById(R.id.ben_service_end_date_result);
            viewHolder.bookingexpectedDuration = row
                    .findViewById(R.id.ben_service_duration_result);
            viewHolder.trackingStatusTxt = row
                    .findViewById(R.id.track_service_status);
            viewHolder.expectedStartTimeLayout = row
                    .findViewById(R.id.ben_exp_service_date_layout);
            viewHolder.serviceStartTimeLayout = row
                    .findViewById(R.id.ben_service_date_layout);
            viewHolder.actualStartTime = row
                    .findViewById(R.id.ben_service_date_result);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        final BuddyService buddyService = getItem(position);
        viewHolder.bookingId.setText(buddyService.getDisplayId());
        if(null!=buddyService.getGender()) {
            viewHolder.benImage.setImageResource(CommonUtilities.getGenderImage(buddyService.getGender()));
        }
        String statusText = "";
        if (buddyService.getStatus().equalsIgnoreCase(context.getString(R.string.transit_label))){
            viewHolder.expectedStartTimeLayout.setVisibility(View.VISIBLE);
            viewHolder.serviceStartTimeLayout.setVisibility(View.GONE);
            viewHolder.expectedStartTime.setText(getDateAndTimeAsString(buddyService.getBookingData()));
            statusText = context.getString(R.string.on_the_way);
            viewHolder.expectedEndTime.setText(getDateAndTimeAsString(buddyService.getEndDate()));
            viewHolder.bookingexpectedDuration.setText(getHoursAndMinutes(Integer.valueOf(buddyService.getExpectedServicetime())));
        }else {
            //ongoing
            viewHolder.expectedStartTimeLayout.setVisibility(View.GONE);
            viewHolder.serviceStartTimeLayout.setVisibility(View.VISIBLE);
            viewHolder.actualStartTime.setText(getDateAndTimeAsString(buddyService.getStartDate()));
            statusText = context.getString(R.string.started_service);
            getStartDateAndDuration(buddyService);
          }
       viewHolder.benName.setText(setToCamelCase(buddyService.getBenFullName()));
        viewHolder.buddyPhone.setText(context.getString(R.string.call_who_label).concat(buddyService.getBuddyName()));
        viewHolder.trackingStatusTxt.setText(context.getString(R.string.your_buddy)
                .concat(context.getString(R.string.mid_space)).concat(buddyService.getBuddyName().toUpperCase()
                        .concat(context.getString(R.string.mid_space)).concat(statusText)));
        viewHolder.buddyPhone.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ContactUtil.connectContact(reqFragment.getActivity(),buddyService.getBuddyName(),buddyService.getBuddyMobile());
            }
        });
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        String width = String.valueOf(display.getWidth());
//        String height = String.valueOf(200*(200/160));
       // String url = "http://maps.google.com/maps/api/staticmap?center=" + buddyService.getLatitude() + "," + buddyService.getLongitude() +
       //         "&zoom=15&size=300x200&sensor=false&markers=icon:http://cdn.sstatic.net/Sites/stackoverflow/img/favicon.ico|"+
       //         buddyService.getLatitude() + "," + buddyService.getLongitude()+"&key="+context.getString(R.string.google_api_key);
//        String url = "http://maps.google.com/maps/api/staticmap?center="
//                + buddyService.getLatitude() + "," + buddyService.getLongitude() +
//                "&zoom=15&size="
//                +width+"x"+height+"&sensor=false&markers="
//                +buddyService.getLatitude() + "," + buddyService.getLongitude()+
//                "||"+buddyService.getBuddyLat()+","+buddyService.getBuddyLng()+
//                "&key="+context.getString(R.string.google_api_key);
//        Picasso.get().load(url).into(viewHolder.mapView);
        viewHolder.moreDetailsBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //track buddy on map
                Intent intent = new Intent(reqFragment.getActivity(), TrackBuddyDetails.class);
                intent.putExtra("buddy_id",buddyService.getBuddyId());
                intent.putExtra("ben_name",buddyService.getBenFullName());
                intent.putExtra("client_lat",buddyService.getLatitude());
                intent.putExtra("client_lng",buddyService.getLongitude());
                intent.putExtra("client_loc",buddyService.getLocation());
                context.startActivity(intent);
            }
        });
        return row;
    }

    private void getStartDateAndDuration(BuddyService buddyService) {
        String newEndTime = "",newDuration = "";
//        if (null!=buddyService.getExtnDuration()) {
        int extnHrs =  null != buddyService.getExtnDuration() ?
                Integer.parseInt(buddyService.getExtnDuration()):0;
        int durationInHrs = null != buddyService.getExpectedServicetime() ?
                (Integer.parseInt(buddyService.getExpectedServicetime()) / 60 +extnHrs): 0;
        newEndTime = getDayAfterGivenHour(buddyService.getStartDate(), durationInHrs);
        newDuration = getHoursAndMinutes(durationInHrs*60);
////        }else{
//            int durationInHrs = null != buddyService.getExpectedServicetime() ?
//                    ( Integer.parseInt(buddyService.getExpectedServicetime()) / 60 + extnHrs) : 0;

//        }
        viewHolder.expectedEndTime.setText(getDateAndTimeAsString(newEndTime));
        viewHolder.bookingexpectedDuration.setText(newDuration);

    }

    //    private void initializeGoogleMaps() {
//
//        MapsInitializer.initialize(context);
////        if (null == mapFragment){
//           mapFragment = (CustomMapFragment) context.getFragmentManager().findFragmentById(R.id.map);
////            mapFragment.getMapAsync(this);
////        }
//
//    }
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = reqFragment.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.end_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
