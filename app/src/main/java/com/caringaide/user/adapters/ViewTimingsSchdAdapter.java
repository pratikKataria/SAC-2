package com.caringaide.user.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.caringaide.user.R;
import com.caringaide.user.model.ScheduledTime;
import com.caringaide.user.utils.CommonUtilities;

import java.util.ArrayList;

public class ViewTimingsSchdAdapter extends ArrayAdapter {
    LayoutInflater inflater;
    Context context;
    int resource;
    // int position;
    ListView listView;
    Fragment fragment;
    ArrayList<ScheduledTime> scheduleList;
    private static String timingMode = "";
    private static final String TAG = "SchedTimeAdapter";

    public ViewTimingsSchdAdapter(Context context, int resource,
                                  ArrayList<ScheduledTime> objects, ListView listView, Fragment fragment) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        scheduleList = objects;
        this.listView = listView;
        this.context = context;
        this.resource = resource;
        this.fragment = fragment;
    }

    @Override
    public ScheduledTime getItem(int position) {
        return scheduleList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        ViewHolder viewHolder = null;
        View view = convertView;
        if (view == null) {

            viewHolder = new ViewHolder();
            view = inflater.inflate(resource, null);
            viewHolder.dayTitle = view
                    .findViewById(R.id.view_schd_day);
            viewHolder.time1Label = view
                    .findViewById(R.id.view_schd_timing1);
            viewHolder.time2Label = view
                    .findViewById(R.id.view_schd_timing2);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ScheduledTime buddyTiming = getItem(position);
        String day = buddyTiming.getDay();
        Log.d(TAG,"Day for timing " + day);
        if (null!= buddyTiming.getDay()) {
            viewHolder.dayTitle.setText(buddyTiming.getDay());
        }
        int timeRangeSize = buddyTiming.getTimeRangeList().size();
        switch (timeRangeSize){
            case 0://available
               viewHolder.time1Label.setText(context.getString(R.string.available_whole_day));
               viewHolder.time1Label.setTextColor(context.getResources().getColor(R.color.colorPrimary));
               break;
            case 1:
                if (buddyTiming.getTimeRangeList().get(0).getFromDate().equals(context.getString(R.string.zero_time))&&
                        buddyTiming.getTimeRangeList().get(0).getToDate().equals(context.getString(R.string.zero_time))){
                    //unavailable
                    viewHolder.time1Label.setText(context.getString(R.string.unavailable_whole_day));
                    viewHolder.time1Label.setTextColor(context.getResources().getColor(R.color.colorAccent));
                }else {
                    //available with one timing
                    String timeRange = CommonUtilities.convertTimeTo12Hr(buddyTiming.getTimeRangeList().get(0).getFromDate()) + " "+context.getString(R.string.to) +
                            " "+CommonUtilities.convertTimeTo12Hr(buddyTiming.getTimeRangeList().get(0).getToDate());
                    viewHolder.time1Label.setText(timeRange);
                    viewHolder.time1Label.setTextColor(context.getResources().getColor(R.color.theme_blue));
                }
                break;
            case 2:
                if (buddyTiming.getTimeRangeList().get(0).getFromDate().equals(context.getString(R.string.zero_time))&&
                buddyTiming.getTimeRangeList().get(0).getToDate().equals(context.getString(R.string.zero_time))&&
                        buddyTiming.getTimeRangeList().get(1).getFromDate().equals(context.getString(R.string.zero_time))&&
                buddyTiming.getTimeRangeList().get(1).getToDate().equals(context.getString(R.string.zero_time))){
                    //unavailable
                    viewHolder.time1Label.setText(context.getString(R.string.unavailable_whole_day));
                    viewHolder.time1Label.setTextColor(context.getResources().getColor(R.color.colorAccent));
                }else{
                    //available with 2 timings
                    String time1Range = CommonUtilities.convertTimeTo12Hr(buddyTiming.getTimeRangeList().get(0).getFromDate()) + " "+context.getString(R.string.to) +
                            " "+ CommonUtilities.convertTimeTo12Hr(buddyTiming.getTimeRangeList().get(0).getToDate());
                    String time2Range = CommonUtilities.convertTimeTo12Hr(buddyTiming.getTimeRangeList().get(1).getFromDate()) + " "+context.getString(R.string.to) +
                            " "+ CommonUtilities.convertTimeTo12Hr(buddyTiming.getTimeRangeList().get(1).getToDate());
                    viewHolder.time1Label.setText(time1Range);
                    viewHolder.time2Label.setText(time2Range);
                    viewHolder.time1Label.setTextColor(context.getResources().getColor(R.color.theme_blue));
                    viewHolder.time2Label.setTextColor(context.getResources().getColor(R.color.theme_blue));
                }
                break;
            default:break;
        }
        return view;
    }
    static class ViewHolder{
        TextView dayTitle,time1Label,time2Label;
    }
}
