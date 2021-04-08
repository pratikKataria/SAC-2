package com.caringaide.user.fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.adapters.ViewTimingsSchdAdapter;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.ScheduledTime;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewScheduleTimingFragment extends Fragment {

    private View schdTimingView;
    private static final String TAG = "ViewSchdTimingFragment";
    private String buddyId = "",buddyName ="", viewContext = "";
    ListView viewTimingListLayout;
    TextView buddyWorkTitle;
    ImageButton closeTimingsBtn;
    private ArrayList<ScheduledTime> scheduleList;
    private HashMap<String, ScheduledTime> scheduleMap;
    ViewTimingsSchdAdapter viewTimingsSchdAdapter;
    ScheduleTimingListener scheduleTimingListener;
    public ViewScheduleTimingFragment() {
        // Required empty public constructor
    }

    /**
     * interface for doing things after viewing schedule
     */
    interface ScheduleTimingListener{
        void afterConfirmingTiming(String buddyId, String buddyName,String context);
    }

    /**
     * initializes the listener with calling fragment
     * @param scheduleTimingListener
     */
    public void setTimingListener(ScheduleTimingListener scheduleTimingListener){
        this.scheduleTimingListener = scheduleTimingListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        schdTimingView =  inflater.inflate(R.layout.fragment_view_schedule_timing, container, false);
        setView();
        setBackAction();
        createDefaultTimings();
        Bundle bundle = getArguments();
        if (null!= bundle){
            buddyId = bundle.getString("buddy_id");
            buddyName = bundle.getString("buddy_name");
            viewContext = bundle.getString("context");
            buddyWorkTitle.setText(buddyWorkTitle.getText().toString().concat("(").concat(buddyName).concat(")"));
        }
        getTimingSchedule();
        return schdTimingView;
    }

    /**
     * initialises ui
     */
    private void setView() {
        viewTimingListLayout = schdTimingView.findViewById(R.id.view_schd_data_list);
        buddyWorkTitle = schdTimingView.findViewById(R.id.view_schd_layout_title);
        closeTimingsBtn = schdTimingView.findViewById(R.id.close_timings);
        scheduleList = new ArrayList<>();
        scheduleMap = new HashMap<>();
        closeTimingsBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (viewContext.equalsIgnoreCase("bookNowFragment")){
                    alertAndAction(getActivity(), getString(R.string.note_label),
                            getString(R.string.alert_select_buddy), getString(R.string.proceed_label),
                            getString(R.string.cancel), new AlertAction() {
                                @Override
                                public void positiveAction() {
                                    scheduleTimingListener.afterConfirmingTiming(buddyId,buddyName,viewContext);
//                                       doCommit();
                                }

                                @Override
                                public void negativeAction() {
//                                       doCommit();
                                }
                            });
                }
                getFragmentManager().beginTransaction().remove(ViewScheduleTimingFragment.this).commit();
            }
        });
        viewTimingsSchdAdapter = new ViewTimingsSchdAdapter(getActivity(),R.layout.view_schedulde_time_adapter_view,
                scheduleList, viewTimingListLayout,this);
        viewTimingListLayout.setAdapter(viewTimingsSchdAdapter);
    }

    private void setBackAction() {
        schdTimingView.setFocusableInTouchMode(true);
        schdTimingView.requestFocus();
        schdTimingView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    //do nothing for now

                    return true;
                }
                return false;
            }
        } );
    }

    /**
     * set timings of buddy
     */
    private void createDefaultTimings() {
        for(ScheduledTime.DaysOfWeek day: ScheduledTime.DaysOfWeek.values()){
            ScheduledTime scehduleData = new ScheduledTime(day.name());
            scheduleList.add(scehduleData);
            scheduleMap.put(day.name(),scehduleData);
        }
    }

    /**
     * get timings
     */
    private void getTimingSchedule() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleBuddyScheduleTimingResponse(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.getBuddyScheduledTimings(buddyId,requestParams);
    }

    /**
     * remote response for getting buddy schedule
     * @param remoteResponse
     */
    private void handleBuddyScheduleTimingResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            Context context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.get_schedule_failed);
            if (null == remoteResponse) {
                toastShort(context.getString(R.string.get_schedule_failed));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject responseObject = new JSONObject(remoteResponse.getResponse());
                        Log.d(TAG, "handleTimeSchedulesResponse: " + responseObject);
                        if (responseObject.has("data")) {
                            scheduleList.clear();
                            createDefaultTimings();
                            for (int i = 0; i < responseObject.getJSONArray("data").length(); i++) {
                                JSONObject jsonObject = responseObject.getJSONArray("data").getJSONObject(i);
                                String dayOfWeek = jsonObject.getString("day_of_week");
                                ScheduledTime buddyTime = scheduleMap.get(dayOfWeek);
                                ScheduledTime.TimeRange timeRange = buddyTime.instance();
                                String fromTime = jsonObject.getString("from_time").substring(11, 19);
                                timeRange.setFromDate(fromTime);
                                String toTime = jsonObject.getString("to_time").substring(11, 19);
                                timeRange.setToDate(toTime);
                                timeRange.setTimeRangeId(jsonObject.getString("id"));
                                buddyTime.getTimeRangeList().add(timeRange);
                                buddyTime.setId(jsonObject.getString("id"));
                                buddyTime.setBuddyId(jsonObject.getString("buddy_id"));
                                buddyTime.setDayOfWeek(dayOfWeek);
                                buddyTime.setDefault(false);
                            }
                            viewTimingsSchdAdapter.notifyDataSetChanged();
                        }

                    }
                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
            }
        }
    }

    private void changeFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.buddy_timing_frame_layout,fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }
}
