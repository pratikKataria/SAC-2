package com.caringaide.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.caringaide.user.R;
import com.caringaide.user.interfaces.ChangeFragmentListener;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.CommonUtilities;

import java.util.ArrayList;

import static com.caringaide.user.utils.CommonUtilities.adminEmail;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.calculateAge;
import static com.caringaide.user.utils.CommonUtilities.getDateAsString;
import static com.caringaide.user.utils.CommonUtilities.getHoursAndMinutes;
import static com.caringaide.user.utils.CommonUtilities.getTimeInMillis;
import static com.caringaide.user.utils.CommonUtilities.setToCamelCase;


public class BuddyEndAdapter extends ArrayAdapter<BuddyService> implements ActivityCompat.OnRequestPermissionsResultCallback {

    private LayoutInflater inflater;
    private static final String TAG = "BuddyEndAdapter";
    private ViewHolder viewHolder;
    private ArrayList<BuddyService> buddyserviceList;
    private Context context;
    private Fragment reqFragment;
    private int resource;
    ChangeFragmentListener changeFragmentListener;
    public BuddyEndAdapter(Context context, int resource,
                           ArrayList<BuddyService> objects, Fragment fragment,
                           ChangeFragmentListener changeFragmentListener) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        buddyserviceList = objects;
        this.context = context;
        this.resource = resource;
        this.reqFragment = fragment;
        this.changeFragmentListener = changeFragmentListener;
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
        TextView dob;TextView about;
        TextView benName;
//        TextView benMobile;
        TextView benComments;
        TextView bookingDate;
        TextView bookingEndDate;
        TextView location;
        TextView serviceTime;
        TextView benZipcode;
//        Button payBeneficiaryButton;
        TextView buddyName;
//        TextView buddyPhone;
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
            viewHolder.serviceimage =  row
                    .findViewById(R.id.end_service_avatar);
            viewHolder.about =  row
                    .findViewById(R.id.ben_about);
            viewHolder.benName = row
                    .findViewById(R.id.ben_name);
           /* viewHolder.benMobile = row
                    .findViewById(R.id.ben_mobile);*/
            viewHolder.benComments = row
                    .findViewById(R.id.ben_booking_requirement_result);
            viewHolder.benZipcode = row
                    .findViewById(R.id.ben_booking_zipcode_result);
            viewHolder.bookingDate = row
                    .findViewById(R.id.ben_service_start_date_result);
            viewHolder.location = row
                    .findViewById(R.id.ben_booking_location_result);
            viewHolder.serviceTime = row
                    .findViewById(R.id.ben_service_duration_result);
            /*viewHolder.payBeneficiaryButton = row
                    .findViewById(R.id.pay_beneficiary_btn);*/
            viewHolder.buddyName = row
                    .findViewById(R.id.ben_booking_buddy_name_result);
          /*  viewHolder.buddyPhone = row
                    .findViewById(R.id.ben_booking_buddy_phone_result);*/
            viewHolder.buddyEmail = row
                    .findViewById(R.id.ben_booking_buddy_email_result);
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
        viewHolder.bookingDate.setText(CommonUtilities.getDateAndTimeAsString(buddyService.getStartDate()));
        viewHolder.bookingEndDate.setText(CommonUtilities.getDateAndTimeAsString(buddyService.getEndDate()));
        viewHolder.location.setText(buddyService.getLocation());

        viewHolder.buddyName.setText(buddyserviceList.get(position).getBuddyName());
        viewHolder.buddyEmail.setText(adminEmail);
        long time = getTimeInMillis(buddyService.getEndDate()) -getTimeInMillis(buddyService.getStartDate());
        long mins = time/(1000*60);
        String duration = getHoursAndMinutes((int)mins);
        viewHolder.serviceTime.setText(duration);
        //}
        //viewHolder.gender.setText(buddyService.getGender());
        if(null!=buddyService.getGender()) {
            viewHolder.serviceimage.setImageResource(CommonUtilities.getGenderImage(buddyService.getGender()));
        }
        viewHolder.about.setText(buddyService.getBenComments());

     /*   viewHolder.payBeneficiaryButton.setVisibility(View.VISIBLE);
        viewHolder.payBeneficiaryButton.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //open payment screen
                PaymentFragment fragment = new PaymentFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ben_booking_id", buddyService.getId());
                bundle.putString("ben_name", buddyService.getBenFullName());
                bundle.putString("ben_gen", buddyService.getGender());
                bundle.putString("service_id", buddyService.getServiceId());
                fragment.setArguments(bundle);
                changeFragmentListener.changeToTargetFragment(fragment);
            }
        });*/

        return row;
    }

}
