package com.example.golaundry;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.viewModel.LaundryViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LaundryEditInfoActivity extends AppCompatActivity {

    LaundryViewModel mLaundryViewModel;
    private Button timeMonday, timeTuesday, timeWednesday, timeThursday, timeFriday, timeSaturday, timeSunday;
    List<String> allTimeRanges; //store all time ranges
    String currentUserId;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switch1, switch2, switch3, switch4, switch5, switch6, switch7;
    private final Calendar[] startTimes = new Calendar[7];
    private final Calendar[] endTimes = new Calendar[7];
    boolean isChecked1, isChecked2, isChecked3, isChecked4, isChecked5, isChecked6, isChecked7;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_edit_info);
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        allTimeRanges = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            allTimeRanges.add("off"); //if user not select time, default as off day
        }

        mLaundryViewModel.getShopData(currentUserId).observe(this, shop -> {
            if (shop != null) {
                allTimeRanges = shop.getAllTimeRanges();

                for (int i = 0; i < allTimeRanges.size(); i++) {
                    String timeRange = allTimeRanges.get(i);
                    switch (i) {
                        case 0:
                            switch1.setChecked(!"off".equals(timeRange));
                            timeMonday.setText(timeRange);
                            break;
                        case 1:
                            switch2.setChecked(!"off".equals(timeRange));
                            timeTuesday.setText(timeRange);
                            break;
                        case 2:
                            switch3.setChecked(!"off".equals(timeRange));
                            timeWednesday.setText(timeRange);
                            break;
                        case 3:
                            switch4.setChecked(!"off".equals(timeRange));
                            timeThursday.setText(timeRange);
                            break;
                        case 4:
                            switch5.setChecked(!"off".equals(timeRange));
                            timeFriday.setText(timeRange);
                            break;
                        case 5:
                            switch6.setChecked(!"off".equals(timeRange));
                            timeSaturday.setText(timeRange);
                            break;
                        case 6:
                            switch7.setChecked(!"off".equals(timeRange));
                            timeSunday.setText(timeRange);
                            break;
                    }
                }
            }
        });

        // set each time range
        for (int i = 0; i < 7; i++) {
            startTimes[i] = Calendar.getInstance();
            endTimes[i] = Calendar.getInstance();
        }

        timeMonday = findViewById(R.id.alei_btn_monday_time);
        timeTuesday = findViewById(R.id.alei_btn_tuesday_time);
        timeWednesday = findViewById(R.id.alei_btn_wednesday_time);
        timeThursday = findViewById(R.id.alei_btn_thursday_time);
        timeFriday = findViewById(R.id.alei_btn_friday_time);
        timeSaturday = findViewById(R.id.alei_btn_saturday_time);
        timeSunday = findViewById(R.id.alei_btn_sunday_time);

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        switch4 = findViewById(R.id.switch4);
        switch5 = findViewById(R.id.switch5);
        switch6 = findViewById(R.id.switch6);
        switch7 = findViewById(R.id.switch7);

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isChecked1 = isChecked;
            timeMonday.setEnabled(isChecked);
            if (!isChecked) {
                timeMonday.setText("OFF");
                allTimeRanges.set(0, "off");
            } else {
                timeMonday.setText("Select");
            }
        });

        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isChecked2 = isChecked;
            timeTuesday.setEnabled(isChecked);
            if (!isChecked) {
                timeTuesday.setText("OFF");
                allTimeRanges.set(1, "off");
            } else {
                timeTuesday.setText("Select");
            }
        });

        switch3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isChecked3 = isChecked;
            timeWednesday.setEnabled(isChecked);
            if (!isChecked) {
                timeWednesday.setText("OFF");
                allTimeRanges.set(2, "off");
            } else {
                timeWednesday.setText("Select");
            }
        });

        switch4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isChecked4 = isChecked;
            timeThursday.setEnabled(isChecked);
            if (!isChecked) {
                timeThursday.setText("OFF");
                allTimeRanges.set(3, "off");
            } else {
                timeThursday.setText("Select");
            }
        });

        switch5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isChecked5 = isChecked;
            timeFriday.setEnabled(isChecked);
            if (!isChecked) {
                timeFriday.setText("OFF");
                allTimeRanges.set(4, "off");
            } else {
                timeFriday.setText("Select");
            }
        });

        switch6.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isChecked6 = isChecked;
            timeSaturday.setEnabled(isChecked);
            if (!isChecked) {
                timeSaturday.setText("OFF");
                allTimeRanges.set(5, "off");
            } else {
                timeSaturday.setText("Select");
            }
        });

        switch7.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isChecked7 = isChecked;
            timeSunday.setEnabled(isChecked);
            if (!isChecked) {
                timeSunday.setText("OFF");
                allTimeRanges.set(6, "off");
            } else {
                timeSunday.setText("Select");
            }
        });

        // Set click listeners for each button
        timeMonday.setOnClickListener(v -> showTimePickerDialog(0));
        timeTuesday.setOnClickListener(v -> showTimePickerDialog(1));
        timeWednesday.setOnClickListener(v -> showTimePickerDialog(2));
        timeThursday.setOnClickListener(v -> showTimePickerDialog(3));
        timeFriday.setOnClickListener(v -> showTimePickerDialog(4));
        timeSaturday.setOnClickListener(v -> showTimePickerDialog(5));
        timeSunday.setOnClickListener(v -> showTimePickerDialog(6));

        ImageView doneImageView = findViewById(R.id.alei_iv_done);
        doneImageView.setOnClickListener(v -> updateInfo());
    }

    private void updateInfo() {
        if (allTimeRanges == null) {
            Toast.makeText(this, "Please set the opening hours", Toast.LENGTH_SHORT).show();
        } else {
            mLaundryViewModel.updateOpeningHoursData(currentUserId, allTimeRanges).observe(this, timeRangesStatus -> {
                if (timeRangesStatus != null && timeRangesStatus) {
                    Toast.makeText(this, "Opening hours updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Opening hours update failed!", Toast.LENGTH_SHORT).show();
                }
                ;
            });
        }
    }

    // start time picker dialog
    private void showTimePickerDialog(int dayIndex) {

        Calendar startTime = startTimes[dayIndex];

        int hour = startTime.get(Calendar.HOUR_OF_DAY);
        int minute = startTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.CustomTimePickerDialog,
                (view, hourOfDay, minute1) -> {
                    startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startTime.set(Calendar.MINUTE, minute1);

                    showEndTimePickerDialog(dayIndex);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    //end time picker dialog
    private void showEndTimePickerDialog(int dayIndex) {

        Calendar endTime = endTimes[dayIndex];

        int hour = endTime.get(Calendar.HOUR_OF_DAY);
        int minute = endTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.CustomTimePickerDialog,
                (view, hourOfDay, minute1) -> {
                    endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    endTime.set(Calendar.MINUTE, minute1);

                    updateTimeRangeButton(dayIndex);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    //update button text, add all inside list
    @SuppressLint("SetTextI18n")
    private void updateTimeRangeButton(int dayIndex) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        Calendar start = startTimes[dayIndex];
        Calendar end = endTimes[dayIndex];

        //if the selected times are within the same day
        if (end.before(start)) {
            Toast.makeText(this, "Time range should be within the same day", Toast.LENGTH_SHORT).show();
            return;
        }

        long differenceInMillis = end.getTimeInMillis() - start.getTimeInMillis();
        boolean isLessThanOneHour = differenceInMillis < 3600000;

        String startTimeText;
        String endTimeText;

        if (isLessThanOneHour) {
            startTimeText = "00:00";
            endTimeText = "00:00";
            Toast.makeText(this, "Opening hour should not be less than one hour", Toast.LENGTH_SHORT).show();
        } else {
            startTimeText = sdf.format(start.getTime());
            endTimeText = sdf.format(end.getTime());
        }

        String time = startTimeText + " - " + endTimeText;

        switch (dayIndex) {
            case 0:
                timeMonday.setText(time);
                if (isChecked1) {
                    allTimeRanges.set(dayIndex, time);
                }
                break;
            case 1:
                timeTuesday.setText(time);
                if (isChecked2) {
                    allTimeRanges.set(dayIndex, time);
                }
                break;
            case 2:
                timeWednesday.setText(time);
                if (isChecked3) {
                    allTimeRanges.set(dayIndex, time);
                }
                break;
            case 3:
                timeThursday.setText(time);
                if (isChecked4) {
                    allTimeRanges.set(dayIndex, time);
                }
                break;
            case 4:
                timeFriday.setText(time);
                if (isChecked5) {
                    allTimeRanges.set(dayIndex, time);
                }
                break;
            case 5:
                timeSaturday.setText(time);
                if (isChecked6) {
                    allTimeRanges.set(dayIndex, time);
                }
                break;
            case 6:
                timeSunday.setText(time);
                if (isChecked7) {
                    allTimeRanges.set(dayIndex, time);
                }
                break;
        }
    }


}
