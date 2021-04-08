package com.caringaide.user.fragments;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.caringaide.user.R;
import com.caringaide.user.interfaces.SingleClickListener;

import java.util.Calendar;
import java.util.Locale;

import static com.caringaide.user.utils.BuddyConstants.serviceMaxTimeinHr;
import static com.caringaide.user.utils.BuddyConstants.serviceMinTimeinHr;
import static com.caringaide.user.utils.CommonUtilities.defaultSchdInterval;
import static com.caringaide.user.utils.CommonUtilities.minServiceDuration;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerDialogFragment extends DialogFragment {

    private TimePicker tvTimePicker;
    private Button btnSetTime,cancelSetTime;
    private View timePickerView;
    private TimePickerListener timePickerListener;
    private final int TIME_PICKER_INTERVAL=15;
    private int initalMin = 0,initialHr =0;
    private boolean mIgnoreEvent=false;
    private static int TIME_SCHD_INTERVAL;

    public TimePickerDialogFragment() {
        // Required empty public constructor
    }

    public interface TimePickerListener{
        void onTimePickerSelected(String hr, String min,String am_pm);
    }

    public void setTimePickerListener(TimePickerListener timePickerListener) {
        this.timePickerListener = timePickerListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        timePickerView = inflater.inflate(R.layout.date_and_time_picker,container,false);
        return timePickerView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TIME_SCHD_INTERVAL = defaultSchdInterval;
//        TIME_SCHD_INTERVAL = 15;
        tvTimePicker = timePickerView.findViewById(R.id.tp_time_picker);
        btnSetTime = timePickerView.findViewById(R.id.date_time_set);
        cancelSetTime = timePickerView.findViewById(R.id.cancel_date_time);
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        if (hour< serviceMinTimeinHr || (hour+minServiceDuration+(defaultSchdInterval/60))>=serviceMaxTimeinHr){
            setInitialHrAndMinutes(serviceMinTimeinHr,0);
        }else {
            if (TIME_SCHD_INTERVAL == 15) {
                setPicker15Minute(minute);
            }else{
                setPickerMinute(minute);
            }
            setInitialHrAndMinutes(initialHr, initalMin);
        }
        tvTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int minute) {

                if (mIgnoreEvent)
                    return;
               setPickerMinute(minute);
            }
        });
        cancelSetTime.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismiss();
            }
        });
        btnSetTime.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                int hour, minute;
                String am_pm;
                if (Build.VERSION.SDK_INT >= 23 ){
                    hour = tvTimePicker.getHour();
                    minute = tvTimePicker.getMinute();
                }
                else{
                    hour = tvTimePicker.getCurrentHour();
                    minute = tvTimePicker.getCurrentMinute();
                }
                if(hour > 12) {
                    am_pm = "PM";
                    hour = hour - 12;
                }else if (hour == 12){
                    am_pm = "PM";
                }
                else
                {
                    am_pm="AM";
                }
//                String time =  hour +":"+ minute+" "+am_pm;

                timePickerListener.onTimePickerSelected(String.valueOf(hour),String.valueOf(minute),am_pm);
                dismiss();
            }
        });

    }

    /**
     * set a minute interval of 15 mins
     * and initial time picker settings
     * @param minute can be current minute on initial loading or user-chosen minute
     */
    private void setPickerMinute(int minute){
        int hour = Calendar.getInstance(Locale.getDefault()).get(Calendar.HOUR_OF_DAY);
        if (minute%TIME_PICKER_INTERVAL!=0){
            int minuteFloor=(minute+TIME_PICKER_INTERVAL)-(minute%TIME_PICKER_INTERVAL);
            minute=minuteFloor + (minute==minuteFloor+1 ? TIME_PICKER_INTERVAL : 0);
            if (minute==60)
                minute=0;
            mIgnoreEvent=true;
            tvTimePicker.setCurrentMinute(minute);
            //intial time settings
            this.initalMin = minute;
            if (initalMin == 60)
                initalMin = 0;
            if (initalMin==0){
                initialHr=hour+2;
            }else if (initalMin==15){
                initialHr=hour+1;
            }else if (initalMin==45){
                initialHr=hour+1;
            }else{
                initialHr = hour;
                if (TIME_SCHD_INTERVAL==60)
                    initialHr=hour+1;
            }
            mIgnoreEvent=false;
        }else{
            if (TIME_SCHD_INTERVAL==60){
                initialHr = hour+1;
//                if (minute >=30){
                    if (minute==45) {
                        initalMin = 0;
                        initialHr += 1;
                    }else
                        initalMin = minute+15;
                /*}else{
                    initialHr = hour;
                    initalMin = minute;
                }*/
            }else {
                if (minute >= 30) {
                    initialHr = hour + 1;
                    if (minute == 45)
                        initalMin = 15;
                    else
                        initalMin = 0;
                } else {
                    initialHr = hour;
                    initalMin = minute + 30;
                }
            }
        }
    }

    /**
     * set a minute interval of 15 mins
     * and initial time picker settings
     * @param minute can be current minute on initial loading or user-chosen minute
     */
    private void setPicker15Minute(int minute){
        int hour = Calendar.getInstance(Locale.getDefault()).get(Calendar.HOUR_OF_DAY);
        if (minute%TIME_PICKER_INTERVAL!=0){
            int minuteFloor=(minute+TIME_PICKER_INTERVAL)-(minute%TIME_PICKER_INTERVAL);
            minute=minuteFloor + (minute==minuteFloor+1 ? TIME_PICKER_INTERVAL : 0);
            if (minute==60)
                minute=0;
            mIgnoreEvent=true;
            tvTimePicker.setCurrentMinute(minute);
            //intial time settings
            this.initalMin = minute+15;
            if (initalMin == 60)
                initalMin = 0;
            if (initalMin<=15){
                initialHr=hour+1;
            }else{
                initialHr = hour;
            }
            mIgnoreEvent=false;
        }else{
            if (minute >=30){
                initialHr = hour+1;
                if (minute==45)
                    initalMin = 15;
                else
                    initalMin = 0;
            }else{
                initialHr = hour;
                initalMin = minute+30;
            }
        }
    }
    /**
     * set initial picker values
     */
    private void setInitialHrAndMinutes(int hour, int min){
        if (Build.VERSION.SDK_INT >= 23 ) {
            tvTimePicker.setMinute(min);
            tvTimePicker.setHour(hour);
        }else {
            tvTimePicker.setCurrentMinute(min);
            tvTimePicker.setCurrentHour(hour);
        }
    }

   /* private String getMaxSchdInterval() {
        // DEFAULT_RIDE_MiLES_KEY should be the config_key of ride-radius config from the server
        Config dataConfig = SharedPrefsManager.getInstance().getConfigData(SCHEDULE_INTERVAL);
        return null!=dataConfig.getConfigValue()?dataConfig.getConfigValue():DEFAULT_SCHEDULE_INTERVAL;
    }*/
}
