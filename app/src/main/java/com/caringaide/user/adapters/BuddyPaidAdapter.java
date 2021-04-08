package com.caringaide.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.caringaide.user.R;
import com.caringaide.user.activities.RatingActivity;
import com.caringaide.user.interfaces.ChangeFragmentListener;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.SharedPrefsManager;

import java.util.ArrayList;

import static com.caringaide.user.utils.CommonUtilities.adminEmail;
import static com.caringaide.user.utils.CommonUtilities.calculateAge;
import static com.caringaide.user.utils.CommonUtilities.getDateAsString;
import static com.caringaide.user.utils.CommonUtilities.getHoursAndMinutes;
import static com.caringaide.user.utils.CommonUtilities.ratingMap;
import static com.caringaide.user.utils.CommonUtilities.setToCamelCase;

public class BuddyPaidAdapter extends ArrayAdapter {
    private LayoutInflater inflater;
    private ArrayList<BuddyService> buddyserviceList;
    private ListView serviceListView;
    private Context context;
    private ChangeFragmentListener changeFragmentListener;
    private Fragment paidFragment;
    private int resource;

    public BuddyPaidAdapter(Context context, int resource, ArrayList<BuddyService> objects, ListView listView, Fragment fragment,ChangeFragmentListener listener){
        super(context,resource,objects);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.buddyserviceList = objects;
        this.serviceListView = listView;
        this.context = context;
        this.resource = resource;
        this.paidFragment = fragment;
        this.changeFragmentListener = listener;
    }
    static class ViewHolder {
        ImageView serviceimage,ratingImage;
        TextView dob;TextView about;
        TextView benName;
//        TextView benMobile;
        TextView bookingDate;
        TextView bookingEndDate;
        TextView location;
        TextView serviceTime;
        TextView benZipcode;
        TextView bookingId;
        TextView benAmount;
//        TextView benBaseFareAmount;
//        TextView benTaxAmount;
        TextView benTipAmount;
        TextView buddyName;
        TextView buddyEmail;
        TextView reqDuration;
        TextView extenDuration;
        LinearLayout extnDurLayout;
//        TextView buddyMobile;
        Button btnAddRating;

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
            viewHolder.serviceimage =  view
                    .findViewById(R.id.paid_gender_avatar);
            viewHolder.about =  view
                    .findViewById(R.id.ben_about);
            viewHolder.benName = view
                    .findViewById(R.id.ben_name);
            viewHolder.benZipcode = view
                    .findViewById(R.id.ben_booking_zipcode_result);
            viewHolder.bookingDate = view
                    .findViewById(R.id.ben_service_start_date_result);
            viewHolder.location = view
                    .findViewById(R.id.ben_booking_location_result);
            viewHolder.serviceTime = view
                    .findViewById(R.id.ben_service_duration_result);
            viewHolder.btnAddRating = view
                    .findViewById(R.id.rate_beneficiary_btn);
            viewHolder.benAmount = view
                    .findViewById(R.id.ben_booking_amount_result);
            viewHolder.reqDuration = view
                    .findViewById(R.id.ben_req_duration_result);
            viewHolder.extenDuration = view
                    .findViewById(R.id.ben_booking_extn_result);
            viewHolder.extnDurLayout = view
                    .findViewById(R.id.extn_layout);
            viewHolder.ratingImage = view
                    .findViewById(R.id.rating_star_image);
            viewHolder.bookingId = view
                    .findViewById(R.id.ben_booking_id_result);
            viewHolder.bookingEndDate = view
                    .findViewById(R.id.ben_service_end_date_result);
            viewHolder.benTipAmount = view
                    .findViewById(R.id.ben_tip_amount_result);
            viewHolder.buddyName = view
                    .findViewById(R.id.ben_booking_buddy_name_result);
            viewHolder.buddyEmail = view
                    .findViewById(R.id.ben_booking_buddy_email_result);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final BuddyService buddyService = getItem(position);
        viewHolder.bookingId.setText(buddyService.getDisplayId());
        viewHolder.dob.setText(String.valueOf(calculateAge(getDateAsString(buddyService.getBenDob()))));
        viewHolder.benName.setText(setToCamelCase(buddyService.getBenFullName()));
        viewHolder.benZipcode.setText(buddyService.getZipcode());
        viewHolder.bookingDate.setText(CommonUtilities.getDateAndTimeAsString(buddyService.getStartDate()));
        viewHolder.bookingEndDate.setText(CommonUtilities.getDateAndTimeAsString(buddyService.getEndDate()));
        viewHolder.location.setText(buddyService.getLocation());
        viewHolder.buddyName.setText(buddyService.getBuddyName());
        viewHolder.buddyEmail.setText(adminEmail);
        viewHolder.reqDuration.setText(getHoursAndMinutes(Integer.valueOf(buddyService.getExpectedServicetime())));
        int extnHrs = Integer.valueOf(buddyService.getExtnDuration())*60;
        if (extnHrs>0) {
            viewHolder.extnDurLayout.setVisibility(View.VISIBLE);
            viewHolder.extenDuration.setText(getHoursAndMinutes(extnHrs));
        }
//        viewHolder.buddyMobile.setText(buddyService.getBuddyMobile());
        if (null!= buddyService.getFeedbackRating()){
                viewHolder.ratingImage.setImageResource(ratingMap.get(buddyService.getFeedbackRating()));
        }
        viewHolder.serviceTime.setText(getHoursAndMinutes(Integer.valueOf(buddyService.getTotalTime())));//not shown right now
        if(null!=buddyService.getGender()) {
           viewHolder.serviceimage.setImageResource(CommonUtilities.getGenderImage(buddyService.getGender()));
        }
        viewHolder.about.setText(buddyService.getBenComments());
        String currency = SharedPrefsManager.getInstance().getUserCurrency();
        viewHolder.benAmount.setText(currency.concat(buddyService.getTotalAmount()));
        viewHolder.benTipAmount.setText(currency.concat(buddyService.getTipAmount()));
        if (!buddyService.isRated()){
            viewHolder.btnAddRating.setVisibility(View.VISIBLE);
            viewHolder.btnAddRating.setOnClickListener(new SingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    //rate buddy
                    Intent intent = new Intent(paidFragment.getActivity(), RatingActivity.class);
                    intent.putExtra("ben_booking_id", buddyService.getId());
                    intent.putExtra("buddy_name", buddyService.getBuddyName());
                    intent.putExtra("buddy_gen", buddyService.getBuddyGender());
                    intent.putExtra("buddy_mobile", buddyService.getBuddyMobile());
                    intent.putExtra("buddy_id", buddyService.getBuddyId());
                    context.startActivity(intent);
                }
            });
        }else {
            viewHolder.btnAddRating.setVisibility(View.INVISIBLE);

        }
        return view;
    }
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = paidFragment.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.paid_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
