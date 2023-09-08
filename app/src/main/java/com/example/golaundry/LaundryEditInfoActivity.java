package com.example.golaundry;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
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
    List<String> allTimeRanges; // List to store all time ranges

    private final Calendar[] startTimes = new Calendar[7];
    private final Calendar[] endTimes = new Calendar[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_edit_info);
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);

        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Set each time range for 7 days
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
//insert to db
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

        //create a new list
        if (allTimeRanges == null) {
            allTimeRanges = new ArrayList<>();
        } else {
            allTimeRanges.clear();
        }

        //add into list for all days
        for (int i = 0; i < 7; i++) {
            String timeRange = sdf.format(startTimes[i].getTime()) + " - " + sdf.format(endTimes[i].getTime());
            allTimeRanges.add(timeRange);
        }
    }
}
