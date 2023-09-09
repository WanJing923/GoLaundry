package com.example.golaundry;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
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

    private Button timeMonday;
    private Button timeTuesday;
    private Button timeWednesday;
    private Button timeThursday;
    private Button timeFriday;
    private Button timeSaturday;
    private Button timeSunday;
    List<String> allTimeRanges; //store all time ranges
    String currentUserId;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switch1,switch2,switch3,switch4,switch5,switch6,switch7;

    private final Calendar[] startTimes = new Calendar[7];
    private final Calendar[] endTimes = new Calendar[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_edit_info);
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);

        allTimeRanges = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            allTimeRanges.add("off"); //if user not select time, default as off day
        }

        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

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
            timeMonday.setEnabled(isChecked);
        });
        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timeTuesday.setEnabled(isChecked);
        });
        switch3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timeWednesday.setEnabled(isChecked);
        });
        switch4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timeThursday.setEnabled(isChecked);
        });
        switch5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timeFriday.setEnabled(isChecked);
        });
        switch6.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timeSaturday.setEnabled(isChecked);
        });
        switch7.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timeSunday.setEnabled(isChecked);
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
                break;
            case 1:
                timeTuesday.setText(time);
                break;
            case 2:
                timeWednesday.setText(time);
                break;
            case 3:
                timeThursday.setText(time);
                break;
            case 4:
                timeFriday.setText(time);
                break;
            case 5:
                timeSaturday.setText(time);
                break;
            case 6:
                timeSunday.setText(time);
                break;
        }

        //update the allTimeRanges list
        if (dayIndex < allTimeRanges.size()) {
            if (dayIndex == 0 && !switch1.isChecked()) {
                allTimeRanges.set(dayIndex, "off");
            } else if (dayIndex == 1 && !switch2.isChecked()) {
                allTimeRanges.set(dayIndex, "off");
            } else if (dayIndex == 2 && !switch3.isChecked()) {
                allTimeRanges.set(dayIndex, "off");
            } else if (dayIndex == 3 && !switch4.isChecked()) {
                allTimeRanges.set(dayIndex, "off");
            } else if (dayIndex == 4 && !switch5.isChecked()) {
                allTimeRanges.set(dayIndex, "off");
            } else if (dayIndex == 5 && !switch6.isChecked()) {
                allTimeRanges.set(dayIndex, "off");
            } else if (dayIndex == 6 && !switch7.isChecked()) {
                allTimeRanges.set(dayIndex, "off");
            } else {
                allTimeRanges.set(dayIndex, time);
            }
        }
    }


}
