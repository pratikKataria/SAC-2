package com.caringaide.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.caringaide.user.R;
import com.caringaide.user.interfaces.RefreshListAdapterListener;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.AvailableBuddy;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.ContactUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.CommonUtilities.adminEmail;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.toastShort;


public class AllBuddiesAdapter extends ArrayAdapter<AvailableBuddy> {

    private LayoutInflater inflater;
    // private BuddyBroadcastAdapter.ViewHolder viewHolder;
    private ArrayList<AvailableBuddy> buddyserviceList;
    private Context context;
    private Fragment reqFragment;
    private int resource;
    RefreshListAdapterListener refreshListAdapterListener;
    androidx.appcompat.app.AlertDialog cancelReasonDialog;
    private static final String TAG = "BudddyBroadcast";
    public AllBuddiesAdapter(Context context, int resource,
                                 ArrayList<AvailableBuddy> objects, ListView listView, Fragment fragment,RefreshListAdapterListener listener) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        buddyserviceList = objects;
        ListView allBuddyListView = listView;
        this.context = context;
        this.resource = resource;
        this.reqFragment = fragment;
        this.refreshListAdapterListener = listener;
    }

    static class ViewHolder {
        TextView buddyName;
        Button buddyPhone;
        TextView buddyEmail;
        TextView buddyAddress;
        TextView buddyZipcode;
        Button addBuddyToBenBtn;
        ImageView buddyGender;

    }

    @Override
    public AvailableBuddy getItem(int position) {
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
            viewHolder.buddyName =  row
                    .findViewById(R.id.list_buddy_name);
            viewHolder.buddyPhone =  row
                    .findViewById(R.id.list_buddy_phone);
            viewHolder.buddyEmail =  row
                    .findViewById(R.id.list_buddy_email);
            viewHolder.buddyAddress =  row
                    .findViewById(R.id.list_buddy_address);
            viewHolder.buddyZipcode = row
                    .findViewById(R.id.list_buddy_zipcode);
            viewHolder.addBuddyToBenBtn =  row
                    .findViewById(R.id.list_buddy_add_btn);
            viewHolder.buddyGender =  row
                    .findViewById(R.id.list_buddy_gender);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        AvailableBuddy availableBuddy = getItem(position);
        final String buddyName = availableBuddy.getBuddyFullName();
        viewHolder.buddyPhone.setText(context.getString(R.string.call_who_label).concat(buddyName));
        final String buddyPhone = availableBuddy.getBuddyPhone();
        String buddyAddress = availableBuddy.getBuddyAddress();
        String buddyZip = availableBuddy.getBuddyZipcode();
        String gender = availableBuddy.getBuddyGender();
        final String buddyId = availableBuddy.getBuddyId();
        final String benId = availableBuddy.getBenId();
        viewHolder.buddyName.setText(buddyName);
        int avatar;
        if (gender.equalsIgnoreCase("F")){
            avatar = R.drawable.girl_avatar;
        }else if (gender.equalsIgnoreCase("M")){
            avatar = R.drawable.boy_avatar;
        }else{
            avatar = R.drawable.ic_boy;
        }
        viewHolder.buddyGender.setImageResource(avatar);
//        viewHolder.buddyName.setCompoundDrawablesWithIntrinsicBounds(0, 0, avatar, 0);
//        viewHolder.buddyPhone.setText(buddyPhone);
        viewHolder.buddyEmail.setText(adminEmail);
        viewHolder.buddyAddress.setText(buddyAddress);
        viewHolder.buddyZipcode.setText(buddyZip);
        viewHolder.buddyPhone.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ContactUtil.connectContact(reqFragment.getActivity(),buddyName,buddyPhone);
            }
        });
        viewHolder.addBuddyToBenBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                alertAndAction(reqFragment.getActivity(), context.getString(R.string.add_buddy_title)
                        , context.getString(R.string.ask_add_fav_buddy), context.getString(R.string.yes),
                        context.getString(R.string.no), new AlertAction() {
                            @Override
                            public void positiveAction() {
                                //remote call for adding buddy to fav list
                                RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                                    @Override
                                    public void onResponse(RemoteResponse remoteResponse) {
                                        handleAddBuddiesResponse(remoteResponse);
                                    }
                                },getContext(),null);
                                Map<String,String> params = new HashMap<>();
                                params.put("ben_id",benId);
                                params.put("buddy_id",buddyId);
                                requestParams.setRequestParams(params);
                                UserServiceHandler.addBuddies(requestParams);
                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        });
        return row;
    }

    /**
     * remote response for adding buddies to favorite list
     * @param remoteResponse server response object
     */
    private void handleAddBuddiesResponse(RemoteResponse remoteResponse) {
        String customErrorMsg = context.getString(R.string.buddy_added_failed);
        if (null == remoteResponse) {
            toastShort(context.getString(R.string.buddy_added_failed));
        } else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(getContext(), remoteResponse)) {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    showAlertMsg(context.getString(R.string.add_buddy_title),context.getString(R.string.buddy_added_success));
                    refreshListAdapterListener.onDataRefresh();
                }else{
                    showAlertMsg(context.getString(R.string.app_name),context.getString(R.string.buddy_added_failed).concat(" ").concat(context.getString(R.string.buddy_already_added)));
                }
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }


}
