package com.caringaide.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.caringaide.user.R;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.ContactUtil;

import java.util.ArrayList;

import static com.caringaide.user.utils.CommonUtilities.adminEmail;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.calculateAge;
import static com.caringaide.user.utils.CommonUtilities.getDateAsString;
import static com.caringaide.user.utils.CommonUtilities.getDayAfterGivenHour;
import static com.caringaide.user.utils.CommonUtilities.getHoursAndMinutes;
import static com.caringaide.user.utils.CommonUtilities.getTimeInMillis;
import static com.caringaide.user.utils.CommonUtilities.setToCamelCase;


public class BuddyOngoingAdapter extends ArrayAdapter<BuddyService> implements ActivityCompat.OnRequestPermissionsResultCallback {

    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private ArrayList<BuddyService> buddyserviceList;
    private Context context;
    private Fragment reqFragment;
    private int resource;
    androidx.appcompat.app.AlertDialog cancelReasonDialog;
    private static final String TAG = "BuddyConfirmAdapter";
    public BuddyOngoingAdapter(Context context, int resource,
                               ArrayList<BuddyService> objects, ListView listView, Fragment fragment) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        buddyserviceList = objects;
        ListView serviceListView = listView;
        this.context = context;
        this.resource = resource;
        this.reqFragment = fragment;
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
            viewHolder.dob =  row
                    .findViewById(R.id.ben_age);
            viewHolder.about =  row
                    .findViewById(R.id.ben_about);
            viewHolder.benName = (TextView) row
                    .findViewById(R.id.ben_name);
            viewHolder.bookingDate = row
                    .findViewById(R.id.ben_service_date_result);
            viewHolder.location = row
                    .findViewById(R.id.ben_booking_location_result);
            viewHolder.serviceTime = row
                    .findViewById(R.id.ben_service_duration_result);
            viewHolder.extendedDuration = row
                    .findViewById(R.id.ben_extn_duration_result);
            viewHolder.serviceimage = row
                    .findViewById(R.id.ongoing_gender_avatar);
            viewHolder.benZipcode = row
                    .findViewById(R.id.ben_booking_zipcode_result);
            viewHolder.startedTime = row
                    .findViewById(R.id.cm_ongoing_timer);
            viewHolder.buddyName = row
                    .findViewById(R.id.ben_booking_buddy_name_result);
            viewHolder.buddyPhone = row
                    .findViewById(R.id.ben_booking_buddy_phone_result);
            viewHolder.buddyEmail = row
                    .findViewById(R.id.ben_booking_buddy_email_result);
            viewHolder.expBookingEndDate = row
                    .findViewById(R.id.ben_exp_end_date_result);
            viewHolder.bookingId = row
                    .findViewById(R.id.ben_booking_id_result);
            viewHolder.benExtnDurationLayout = row
                    .findViewById(R.id.ben_extension_dur_layout);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        final BuddyService buddyService = getItem(position);
        viewHolder.bookingId.setText(buddyService.getDisplayId());
        viewHolder.dob.setText(String.valueOf(calculateAge(getDateAsString(buddyService.getBenDob()))));
        viewHolder.benName.setText(setToCamelCase(buddyService.getBenFullName()));
        viewHolder.benZipcode.setText(buddyService.getZipcode());
        viewHolder.location.setText(buddyService.getLocation());
        getStartnEndTimeDetails(buddyService);
        viewHolder.buddyName.setText(buddyService.getBuddyName());
        viewHolder.buddyEmail.setText(adminEmail);
        viewHolder.buddyPhone.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
             ContactUtil.connectContact(reqFragment.getActivity(),buddyService.getBuddyName(),buddyService.getBuddyMobile());
            }
        });
        if(null!=buddyService.getGender()) {
            viewHolder.serviceimage.setImageResource(CommonUtilities.getGenderImage(buddyService.getGender()));
        }
        viewHolder.about.setText(buddyService.getBenComments());
        if(null!=buddyService.getStartDate()&&!buddyService.getStartDate().isEmpty()){
            long now = SystemClock.elapsedRealtime();
            Log.d(TAG, "openTimer: now"+now);
            long time = getTimeInMillis(buddyService.getStartDate());
            long elp = System.currentTimeMillis() - time;
            Log.d(TAG, "openTimer: elp "+(SystemClock.elapsedRealtime()-elp));
            viewHolder.startedTime.setBase(SystemClock.elapsedRealtime()-elp);
        }else{
            viewHolder.startedTime.setBase(SystemClock.elapsedRealtime());
        }
        viewHolder.startedTime.start();
        viewHolder.startedTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long minutes = ((SystemClock.elapsedRealtime() -  viewHolder.startedTime.getBase())/1000) / 60;
                long seconds = ((SystemClock.elapsedRealtime() -  viewHolder.startedTime.getBase())/1000) % 60;
                //startTime = SystemClock.elapsedRealtime();
                Log.d(TAG, "onChronometerTick: " + minutes + " : " + seconds);
            }
        });


        return row;
    }

    private void getStartnEndTimeDetails(BuddyService buddyService) {
        int expDurationInHr =0;String resultantEndDate = "";
        String startDate = buddyService.getStartDate();
        String expServiceTime = buddyService.getExpectedServicetime();
        int extnDurationInHr = null!= buddyService.getExtnDuration()?Integer.parseInt(buddyService.getExtnDuration()):0;
        //set start date
        viewHolder.bookingDate.setText(CommonUtilities.getDateAndTimeAsString(startDate));
        //set duration
        if (null!=expServiceTime && !expServiceTime.isEmpty()) {
            expDurationInHr = Integer.parseInt(expServiceTime)/60;
            String durationStr = getHoursAndMinutes(Integer.parseInt(expServiceTime));
            viewHolder.serviceTime.setText(durationStr);
            //set end date. check for extn
        }
        if (extnDurationInHr>0){
            viewHolder.extendedDuration.setText(getHoursAndMinutes(extnDurationInHr*60));
            viewHolder.benExtnDurationLayout.setVisibility(View.VISIBLE);
            //add extn hr to end date and set endfield
            resultantEndDate = getDayAfterGivenHour(startDate,(extnDurationInHr+expDurationInHr));
        }else{
            viewHolder.benExtnDurationLayout.setVisibility(View.GONE);
            resultantEndDate = getDayAfterGivenHour(startDate,(extnDurationInHr+expDurationInHr));
        }
        viewHolder.expBookingEndDate.setText(CommonUtilities.getDateAndTimeAsString(resultantEndDate));
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
        ImageView serviceimage;
        TextView dob;
        TextView about;
        TextView benName;
//        TextView benMobile;
        TextView bookingDate;
        TextView expBookingEndDate;
        TextView benZipcode;
        TextView location;
        TextView serviceTime;
        TextView extendedDuration;
        Chronometer startedTime;
        TextView buddyName;
        ImageButton buddyPhone;
        TextView buddyEmail;
        TextView bookingId;
        LinearLayout benExtnDurationLayout;

    }
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = reqFragment.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
