package com.caringaide.user.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.caringaide.user.R;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.SharedPrefsManager;

import java.util.ArrayList;

import static com.caringaide.user.utils.CommonUtilities.adminEmail;
import static com.caringaide.user.utils.CommonUtilities.calculateAge;
import static com.caringaide.user.utils.CommonUtilities.getDateAsString;
import static com.caringaide.user.utils.CommonUtilities.setToCamelCase;

public class BuddyCancelAdapter extends ArrayAdapter<BuddyService> {

    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private ArrayList<BuddyService> buddyserviceList;
    private Context context;
    private Fragment reqFragment;
    private int position;
    private int resource;
    public BuddyCancelAdapter(Context context, int resource,
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
            /*viewHolder.benMobile = row
                    .findViewById(R.id.ben_mobile);*/
            viewHolder.benZipcode = row
                    .findViewById(R.id.ben_booking_zipcode_result);
            viewHolder.bookingDate = row
                    .findViewById(R.id.ben_service_date_result);
            viewHolder.bookingId = row
                    .findViewById(R.id.ben_booking_id_result);
            viewHolder.location = row
                    .findViewById(R.id.ben_booking_location_result);
            viewHolder.cancelledBy = row
                    .findViewById(R.id.cancelled_by_result);
            viewHolder.cancelledReason = row
                    .findViewById(R.id.cancel_reason_result);
            viewHolder.buddyName = row
                    .findViewById(R.id.ben_booking_buddy_name_result);
            viewHolder.buddyEmail = row
                    .findViewById(R.id.ben_booking_buddy_email_result);
           /* viewHolder.buddyPhone = row
                    .findViewById(R.id.ben_booking_buddy_phone_result);*/
            viewHolder.buddyDetailsLayout = row
                    .findViewById(R.id.ben_assigned_buddy_layout);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        final BuddyService buddyService = getItem(position);
        this.position = position;
        viewHolder.bookingId.setText(buddyService.getDisplayId());
        if (null!= buddyService.getBuddyName() && !buddyService.getBuddyName().isEmpty()) {
            viewHolder.buddyDetailsLayout.setVisibility(View.VISIBLE);
            viewHolder.buddyName.setText(buddyService.getBuddyName());
            viewHolder.buddyEmail.setText(adminEmail);
        }else{
            viewHolder.buddyDetailsLayout.setVisibility(View.GONE);
        }
        viewHolder.dob.setText(String.valueOf(calculateAge(getDateAsString(buddyService.getBenDob()))));
        viewHolder.benName.setText(setToCamelCase(buddyService.getBenFullName()));
//        viewHolder.benMobile.setText(buddyService.getBenMobile());
        if (null!=buddyService.getCancelledBy()) {
            viewHolder.cancelledBy.setText(SharedPrefsManager.getInstance().getUserID()
                    .equals(buddyService.getCancelledBy()) ? context.getString(R.string.user) : context.getString(R.string.buddy));
            viewHolder.cancelledReason.setText(buddyService.getCancelledMessage().isEmpty() ?
                    context.getString(R.string.no_reason) : buddyService.getCancelledMessage());
        }else{
            viewHolder.cancelledBy.setText(context.getString(R.string.system));
            viewHolder.cancelledReason.setText(context.getString(R.string.no_buddies_available_reason));
        }
        viewHolder.about.setText(buddyService.getBenComments());
        viewHolder.bookingDate.setText(CommonUtilities.getDateAndTimeAsString(buddyService.getBookingData()));
        if(null!=buddyService.getGender()) {
            viewHolder.serviceimage.setImageResource(CommonUtilities.getGenderImage(buddyService.getGender()));
        }
        return row;
    }

    static class ViewHolder {
        ImageView serviceimage;
        TextView dob;TextView about;
        TextView gender;
        TextView benName;
//        TextView benMobile;
        TextView benComments;
        TextView bookingDate;
        TextView location;
        TextView serviceTime;
        TextView benZipcode;
        TextView cancelledBy;
        TextView cancelledReason;
        TextView bookingId;
        TextView buddyName;
//        TextView buddyPhone;
        TextView buddyEmail;
        LinearLayout buddyDetailsLayout;

    }
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = reqFragment.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

