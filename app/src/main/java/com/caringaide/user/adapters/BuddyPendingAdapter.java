package com.caringaide.user.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import static com.caringaide.user.utils.CommonUtilities.getDateAndTimeAsString;
import static com.caringaide.user.utils.CommonUtilities.getDateAsString;
import static com.caringaide.user.utils.CommonUtilities.getHoursAndMinutes;
import static com.caringaide.user.utils.CommonUtilities.setToCamelCase;
import static com.caringaide.user.utils.CommonUtilities.showReasonAlert;


public class BuddyPendingAdapter extends ArrayAdapter<BuddyService> {

    private LayoutInflater inflater;
    private ArrayList<BuddyService> buddyserviceList;
    private Context context;
    private Fragment reqFragment;
    private int resource;
    private RefreshListAdapterListener refreshListAdapterListener;
    private static final String TAG="PendingAdapter";
    public BuddyPendingAdapter(Context context, int resource,
                               ArrayList<BuddyService> objects, ListView listView,
                               Fragment fragment, RefreshListAdapterListener refreshListAdapterListener) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        buddyserviceList = objects;
//        serviceListView = listView;
        this.context = context;
        this.resource = resource;
        this.reqFragment = fragment;
        this.refreshListAdapterListener = refreshListAdapterListener;
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
        ViewHolder viewHolder = null;
        if (row == null) {

            viewHolder = new ViewHolder();
            row = inflater.inflate(resource, null);
            viewHolder.dob =  row
                    .findViewById(R.id.ben_age);
            viewHolder.favImage =  row
                    .findViewById(R.id.fav_booking);
            viewHolder.buddyDetailsLayout =  row
                    .findViewById(R.id.buddy_details_layout);
            viewHolder.serviceimage =  row
                    .findViewById(R.id.ben_gender_image);
            viewHolder.about =  row
                    .findViewById(R.id.ben_about);
            viewHolder.cancelRequests =  row
                    .findViewById(R.id.cancel_request);
            viewHolder.benName = (TextView) row
                    .findViewById(R.id.ben_name);
            viewHolder.benComments = row
                    .findViewById(R.id.ben_booking_requirement_result);
            viewHolder.bookingDate = row
                    .findViewById(R.id.ben_service_date_result);
            viewHolder.location = row
                    .findViewById(R.id.ben_booking_location_result);
            viewHolder.benZipcode = row
                    .findViewById(R.id.ben_booking_zipcode_result);
            viewHolder.serviceTime = row
                    .findViewById(R.id.ben_service_duration_result);
            viewHolder.buddyName = row
                    .findViewById(R.id.ben_booking_buddy_name_result);
            viewHolder.buddyEmail = row
                    .findViewById(R.id.ben_booking_buddy_email_result);
            viewHolder.bookingId = row
                    .findViewById(R.id.ben_booking_id_result);
            viewHolder.serviceEndDate = row
                    .findViewById(R.id.ben_service_end_date_result);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        final  BuddyService buddyService = getItem(position);
//        this.position = position;
        viewHolder.bookingId.setText(buddyService.getDisplayId());
        viewHolder.dob.setText(String.valueOf(calculateAge(getDateAsString(buddyService.getBenDob()))));
        viewHolder.benName.setText(setToCamelCase(buddyService.getBenFullName()));
//        viewHolder.benMobile.setText(buddyService.getBenMobile());
        viewHolder.benComments.setText(!buddyService.getMessage().isEmpty() && null!=buddyService.getMessage()?
                buddyService.getMessage(): context.getString(R.string.no_requirements));
        viewHolder.benZipcode.setText(buddyService.getZipcode());
        viewHolder.bookingDate.setText(getDateAndTimeAsString(buddyService.getBookingData()));
        viewHolder.serviceEndDate.setText(getDateAndTimeAsString(buddyService.getEndDate()));
        viewHolder.location.setText(buddyService.getLocation());
        if (null!=buddyService.getExpectedServicetime()) {
            String duration = getHoursAndMinutes(Integer.parseInt(buddyService.getExpectedServicetime()));
            viewHolder.serviceTime.setText(duration);
        }
        viewHolder.about.setText(buddyService.getBenComments());
        if (null!=buddyService.getBuddyId()&&!buddyService.getBuddyId().isEmpty()){
            viewHolder.favImage.setVisibility(View.VISIBLE);
            viewHolder.buddyDetailsLayout.setVisibility(View.VISIBLE);
            viewHolder.buddyName.setText(buddyService.getBuddyName());
            viewHolder.buddyEmail.setText(adminEmail);
            int minDiff = (int) (diffTwoDatesInMinutes(getCurrentDateTimeAsString(),buddyService.getBookingData()));
            if (minDiff<=30){
                viewHolder.buddyName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_phone_green,0);
                viewHolder.buddyName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContactUtil.connectContact(reqFragment.getActivity(),buddyService.getBuddyName(),buddyService.getBuddyMobile(),buddyService.getId());
                    }
                });
            }
        }else{
            viewHolder.favImage.setVisibility(View.GONE);
            viewHolder.buddyDetailsLayout.setVisibility(View.GONE);
        }
        if(null!=buddyService.getGender()) {
            viewHolder.serviceimage.setImageResource(CommonUtilities.getGenderImage(buddyService.getGender()));
        }

        viewHolder.cancelRequests.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                alertAndAction((Activity) context, context.getString(R.string.ask_cancel_title), context.getString(R.string.ask_cancel),
                        context.getString(R.string.yes), context.getString(R.string.no), new AlertAction() {
                            @Override
                            public void positiveAction() {
                                //cancel the booking after showing reason alert
                                showReasonAlert(BuddyPendingAdapter.this,buddyserviceList,position,
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


    static class ViewHolder {
        ImageView serviceimage;
        ImageView favImage;
        TextView dob;
        TextView about;
        TextView cancelRequests;
        TextView benName;
//        TextView benMobile;
        TextView benComments;
        TextView bookingDate;
        TextView serviceEndDate;
        TextView location;
        TextView serviceTime;
        TextView benZipcode;
        TextView buddyName;
        TextView buddyEmail;
        TextView bookingId;
        LinearLayout buddyDetailsLayout;

    }
}
