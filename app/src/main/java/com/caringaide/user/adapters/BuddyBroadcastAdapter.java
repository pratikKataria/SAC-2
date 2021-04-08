package com.caringaide.user.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.caringaide.user.R;
import com.caringaide.user.interfaces.RefreshListAdapterListener;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.CommonUtilities;

import java.util.ArrayList;

import static com.caringaide.user.R.string.ask_cancel;
import static com.caringaide.user.R.string.ask_cancel_title;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.calculateAge;
import static com.caringaide.user.utils.CommonUtilities.getDateAsString;
import static com.caringaide.user.utils.CommonUtilities.getHoursAndMinutes;
import static com.caringaide.user.utils.CommonUtilities.setToCamelCase;
import static com.caringaide.user.utils.CommonUtilities.showReasonAlert;


public class BuddyBroadcastAdapter extends ArrayAdapter<BuddyService> {

    private LayoutInflater inflater;
   // private BuddyBroadcastAdapter.ViewHolder viewHolder;
    private ArrayList<BuddyService> buddyserviceList;
    private Context context;
    private Fragment reqFragment;
    private int position;
    private int resource;
    private RefreshListAdapterListener listener;
    private static final String TAG = "BudddyBroadcast";
    public BuddyBroadcastAdapter(Context context, int resource,
                                 ArrayList<BuddyService> objects, Fragment fragment, RefreshListAdapterListener listener) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        buddyserviceList = objects;
        this.context = context;
        this.resource = resource;
        this.reqFragment = fragment;
        this.listener = listener;
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
        TextView bookingEndDate;
        TextView benZipcode;
        TextView location;
        TextView bookingId;
        TextView serviceTime;

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
        View view = convertView;
        ViewHolder viewHolder = null;
        if (view == null) {

            viewHolder = new ViewHolder();
            view = inflater.inflate(resource, null);
            viewHolder.dob =  view
                    .findViewById(R.id.ben_age);
            viewHolder.about =  view
                    .findViewById(R.id.ben_about);
            viewHolder.serviceimage =  view
                    .findViewById(R.id.ben_gender_image);
            viewHolder.favImage =  view
                    .findViewById(R.id.fav_booking);
            viewHolder.cancelRequests =  view
                    .findViewById(R.id.cancel_request);
            viewHolder.cancelRequests.setTag(position);
            viewHolder.benName = (TextView) view
                    .findViewById(R.id.ben_name);
            /*viewHolder.benMobile = view
                    .findViewById(R.id.ben_mobile);*/
            viewHolder.benComments = view
                    .findViewById(R.id.ben_booking_requirement_result);
            viewHolder.bookingDate = view
                    .findViewById(R.id.ben_service_date_result);
            viewHolder.bookingEndDate = view
                    .findViewById(R.id.ben_service_end_date_result);
            viewHolder.benZipcode = view
                    .findViewById(R.id.ben_booking_zipcode_result);
            viewHolder.location = view
                    .findViewById(R.id.ben_booking_location_result);
            viewHolder.serviceTime = view
                    .findViewById(R.id.ben_service_duration_result);
            viewHolder.bookingId = view
                    .findViewById(R.id.ben_booking_id_result);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        BuddyService buddyService = getItem(position);
        viewHolder.bookingId.setText(buddyService.getDisplayId());
        viewHolder.dob.setText(String.valueOf(calculateAge(getDateAsString(buddyService.getBenDob()))));
        viewHolder.benName.setText(setToCamelCase(buddyService.getBenFullName()));
        viewHolder.benComments.setText(buddyService.getMessage().isEmpty()? context.getString(R.string.no_requirements) :
                        buddyService.getMessage());
        viewHolder.bookingDate.setText(CommonUtilities.getDateAndTimeAsString(buddyService.getBookingData()));
        viewHolder.bookingEndDate.setText(CommonUtilities.getDateAndTimeAsString(buddyService.getEndDate()));
        viewHolder.location.setText(buddyService.getLocation());
        viewHolder.benZipcode.setText(buddyService.getZipcode());
        if (null!=buddyService.getExpectedServicetime()) {
            String duration = getHoursAndMinutes(Integer.parseInt(buddyService.getExpectedServicetime()));
            viewHolder.serviceTime.setText(duration);
        }
        //viewHolder.gender.setText(buddyService.getGender());
        if(null!=buddyService.getGender()) {
            viewHolder.serviceimage.setImageResource(CommonUtilities.getGenderImage(buddyService.getGender()));
        }
        viewHolder.about.setText(buddyService.getBenComments());
        if (null!=buddyService.getBuddyId()&&!buddyService.getBuddyId().isEmpty()){
            viewHolder.favImage.setVisibility(View.VISIBLE);
        }else{
            viewHolder.favImage.setVisibility(View.GONE);
        }

        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.cancelRequests.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                int pos = (int) finalViewHolder.cancelRequests.getTag();
                Log.d(TAG, "onClick: "+pos+"  && position "+position+ " && btnpos "+v.getTag());
                alertAndAction((Activity) context, context.getString(ask_cancel_title), context.getString(ask_cancel),
                        context.getString(R.string.yes), context.getString(R.string.no), new AlertAction() {
                            @Override
                            public void positiveAction() {
                                //show reason alert and cancel the booking
                                showReasonAlert(BuddyBroadcastAdapter.this,buddyserviceList,position,listener);
                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        });
        return view;
    }
}
