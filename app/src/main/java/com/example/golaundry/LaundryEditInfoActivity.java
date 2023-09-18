package com.example.golaundry;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.LaundryShopModel;
import com.example.golaundry.model.UserModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class LaundryEditInfoActivity extends AppCompatActivity {

    LaundryViewModel mLaundryViewModel;
    private Button timeMonday, timeTuesday, timeWednesday, timeThursday, timeFriday, timeSaturday, timeSunday;
    List<String> allTimeRanges;
    String currentUserId;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switch1, switch2, switch3, switch4, switch5, switch6, switch7;
    private final Calendar[] startTimes = new Calendar[7];
    private final Calendar[] endTimes = new Calendar[7];
    boolean isChecked1, isChecked2, isChecked3, isChecked4, isChecked5, isChecked6, isChecked7;
    private final int SELECT_LAUNDRY_IMAGE = 6;
    private Uri LaundryPicUri;
    ImageView LaundryPictureImageView;
    String laundryPicUriString, imageUrl;
    boolean laundryIsSetup;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_edit_info);

        Toolbar toolbar = findViewById(R.id.alei_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left));

        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        allTimeRanges = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            allTimeRanges.add("off"); //if user not select time, default as off day
        }

        LaundryPictureImageView = findViewById(R.id.alei_laundry_image);
        TextView addressTextView = findViewById(R.id.alei_tv_address);
        TextView phoneNoTextView = findViewById(R.id.alei_tv_phone);

        mLaundryViewModel.getLaundryData(currentUserId).observe(this, laundry -> {
            if (laundry != null) {
                String address = laundry.getAddressDetails() + ", " + laundry.getAddress();
                addressTextView.setText(address);
                phoneNoTextView.setText(laundry.getPhoneNo());
                laundryIsSetup = laundry.getSetup();
            }
        });

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
                //show image
                imageUrl = shop.getImages();
                if (!Objects.equals(imageUrl, "")) {
                    setImages(imageUrl, LaundryPictureImageView);
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

        //click listeners for each button
        timeMonday.setOnClickListener(v -> showTimePickerDialog(0));
        timeTuesday.setOnClickListener(v -> showTimePickerDialog(1));
        timeWednesday.setOnClickListener(v -> showTimePickerDialog(2));
        timeThursday.setOnClickListener(v -> showTimePickerDialog(3));
        timeFriday.setOnClickListener(v -> showTimePickerDialog(4));
        timeSaturday.setOnClickListener(v -> showTimePickerDialog(5));
        timeSunday.setOnClickListener(v -> showTimePickerDialog(6));

        ImageView doneImageView = findViewById(R.id.alei_iv_done);
        doneImageView.setOnClickListener(v -> updateInfo());

        //let laundry select image
        LaundryPictureImageView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Laundry Shop Picture"), SELECT_LAUNDRY_IMAGE);
        });
    }

    private void setImages(String imageUrl, ImageView LaundryPictureImageView) {
        //referenceFromUrl to get StorageReference
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        try {
            File localFile = File.createTempFile("tempfile", ".jpg");

            mStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                //show
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                LaundryPictureImageView.setImageBitmap(bitmap);

            }).addOnFailureListener(e -> {
                Toast.makeText(LaundryEditInfoActivity.this, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //process images
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_LAUNDRY_IMAGE && data != null && data.getData() != null) {
                //handle selected laundry image
                LaundryPicUri = data.getData();
                laundryPicUriString = String.valueOf(LaundryPicUri);
                //show what user select
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), LaundryPicUri);
                    LaundryPictureImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean areAllElementsOff(List<String> allTimeRanges) {
        for (String timeRange : allTimeRanges) {
            if (!timeRange.equals("off")) {
                return false;
            }
        }
        return true;
    }

    private void updateInfo() {

        ProgressBar mProgressBar = findViewById(R.id.alei_progressbar);
        // show the visibility of progress bar to show loading
        mProgressBar.setVisibility(View.VISIBLE);

        if (Objects.equals(imageUrl, "")){
            if (LaundryPicUri == null) {
                Toast.makeText(this, "Please upload laundry image", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                return;
            }
        }

        if (areAllElementsOff(allTimeRanges)) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please set the opening hours", Toast.LENGTH_SHORT).show();
            return;
        }

        if (allTimeRanges == null) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please set the opening hours", Toast.LENGTH_SHORT).show();
        } else if (LaundryPicUri == null) {
            mLaundryViewModel.updateShopInfoNoImage(currentUserId, allTimeRanges).observe(this, timeRangesStatus -> {
                if (timeRangesStatus != null && timeRangesStatus) {
                    Toast.makeText(this, "Shop opening hours updated", Toast.LENGTH_SHORT).show();
                    if (laundryIsSetup) {
                        mProgressBar.setVisibility(View.GONE);
                        finish();
                    } else {
                        Intent intent = new Intent(LaundryEditInfoActivity.this, LaundryEditServicesActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        mProgressBar.setVisibility(View.GONE);
                        finish();
                    }
                } else {
                    Toast.makeText(this, "Shop opening hours update failed!", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            });

        } else {
            LaundryShopModel shopInfo = new LaundryShopModel(currentUserId, laundryPicUriString, allTimeRanges);
            mLaundryViewModel.updateShopInfo(currentUserId, shopInfo).observe(this, timeRangesStatus -> {
                if (timeRangesStatus != null && timeRangesStatus) {
                    Toast.makeText(this, "Shop info updated", Toast.LENGTH_SHORT).show();
                    finish();
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Shop info update failed!", Toast.LENGTH_SHORT).show();
                }
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

        if (time.equals("00:00 - 00:00")){
            time = "off";
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //close the current activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
