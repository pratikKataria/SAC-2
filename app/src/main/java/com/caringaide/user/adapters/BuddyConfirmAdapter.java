package com.caringaide.user.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.caringaide.user.R;
import com.caringaide.user.interfaces.RefreshListAdapterListener;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.ContactUtil;

import java.util.ArrayList;

import static com.caringaide.user.utils.CommonUtilities.adminEmail;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.calculateAge;
import static com.caringaide.user.utils.CommonUtilities.diffTwoDatesInMinutes;
import static com.caringaide.user.utils.CommonUtilities.getCurrentDateTimeAsString;
import static com.caringaide.user.utils.CommonUtilities.getDateAsString;
import static com.caringaide.user.utils.CommonUtilities.getHoursAndMinutes;
import static com.caringaide.user.utils.CommonUtilities.setToCamelCase;
import static com.caringaide.user.utils.CommonUtilities.showReasonAlert;


public class BuddyConfirmAdapter extends ArrayAdapter<BuddyService>  implements ActivityCompat.OnRequestPermissionsResultCallback {

    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private ArrayList<BuddyService> buddyserviceList;
    private ListView serviceListView;
    private Context context;
    private Fragment reqFragment;
    private int resource;
    private RefreshListAdapterListener refreshListAdapterListener;
    private static final String TAG = "BuddyConfirmAdapter";
    public BuddyConfirmAdapter(Context context, int resource,
                               ArrayList<BuddyService> objects, Fragment fragment,
                               RefreshListAdapterListener refreshListAdapterListener) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        buddyserviceList = objects;
        this.context = context;
        this.resource = resource;
        this.reqFragment = fragment;
        this.refreshListAdapterListener = refreshListAdapterListener;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        Log.d(TAG,"permission granted");
        Intent intent=null;
        if (grantResult[0]==0) {
            //permission granted
            //do nothing for now
        }else{
            if(requestCode != 1) {
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
        TextView gender;
        TextView benName;
//        TextView benMobile;
        TextView benComments;
        TextView bookingDate;
        TextView bookingEndDate;
        TextView benZipcode;
        TextView location;
        TextView serviceTime;
        TextView cancelRequest;
        TextView buddyName;
//        ImageButton buddyPhone;
        TextView buddyEmail;
        TextView bookingId;
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
           /* viewHolder.benMobile = row
                    .findViewById(R.id.ben_mobile);*/
            viewHolder.benComments = row
                    .findViewById(R.id.ben_booking_requirement_result);
            viewHolder.bookingDate = row
                    .findViewById(R.id.ben_service_date_result);
            viewHolder.location = row
                    .findViewById(R.id.ben_booking_location_result);
            viewHolder.serviceTime = row
                    .findViewById(R.id.ben_service_duration_result);
            viewHolder.serviceimage = row
                    .findViewById(R.id.confirm_gender_avatar);
            viewHolder.benZipcode = row
                    .findViewById(R.id.ben_booking_zipcode_result);
            viewHolder.cancelRequest = row
                    .findViewById(R.id.cancel_confirm_request);
            viewHolder.buddyName = row
                    .findViewById(R.id.ben_booking_buddy_name_result);
            viewHolder.buddyEmail = row
                    .findViewById(R.id.ben_booking_buddy_email_result);
           /* viewHolder.buddyPhone = row
                    .findViewById(R.id.ben_booking_buddy_phone_result);*/
            viewHolder.bookingId = row
                    .findViewById(R.id.ben_booking_id_result);
            viewHolder.bookingEndDate = row
                    .findViewById(R.id.ben_service_end_date_result);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        final BuddyService buddyService = getItem(position);
        viewHolder.bookingId.setText(buddyService.getDisplayId());
        viewHolder.dob.setText(String.valueOf(calculateAge(getDateAsString(buddyService.getBenDob()))));
        viewHolder.benName.setText(setToCamelCase(buddyService.getBenFullName()));
//        viewHolder.benMobile.setText(buddyService.getBenMobile());
        viewHolder.benComments.setText(buddyService.getMessage().isEmpty()? context.getString(R.string.no_requirements) :
                buddyService.getMessage());
        viewHolder.benZipcode.setText(buddyService.getZipcode());
        viewHolder.bookingDate.setText(CommonUtilities.getDateAndTimeAsString(buddyService.getBookingData()));
        viewHolder.bookingEndDate.setText(CommonUtilities.getDateAndTimeAsString(buddyService.getEndDate()));
        viewHolder.location.setText(buddyService.getLocation());
        if (null!=buddyService.getExpectedServicetime()) {
            String duration = getHoursAndMinutes(Integer.parseInt(buddyService.getExpectedServicetime()));
            viewHolder.serviceTime.setText(duration);
        }
        viewHolder.buddyName.setText(buddyService.getBuddyName());
        int minDiff = (int) (diffTwoDatesInMinutes(getCurrentDateTimeAsString(),buddyService.getBookingData()));
        if (minDiff==0){
            viewHolder.buddyName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_phone_green,0);
            viewHolder.buddyName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactUtil.connectContact(reqFragment.getActivity(),buddyService.getBuddyName(),buddyService.getBuddyMobile(),buddyService.getId());
                }
            });
        }
        viewHolder.buddyEmail.setText(adminEmail);
        if(null!=buddyService.getGender()) {
            viewHolder.serviceimage.setImageResource(CommonUtilities.getGenderImage(buddyService.getGender()));
        }
        viewHolder.about.setText(buddyService.getBenComments());

        viewHolder.cancelRequest.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                alertAndAction((Activity) context, context.getString(R.string.ask_cancel_title),context.getString(R.string.ask_cancel),
                        context.getString(R.string.yes), context.getString(R.string.no), new AlertAction() {
                            @Override
                            public void positiveAction() {
                                //show reason alert and cancel boooking
                                showReasonAlert(BuddyConfirmAdapter.this,buddyserviceList,position,
                                        refreshListAdapterListener);
                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        });

        return row;
    }
}
