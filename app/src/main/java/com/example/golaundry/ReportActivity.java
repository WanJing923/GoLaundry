package com.example.golaundry;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.model.RateLaundryModel;
import com.example.golaundry.model.RateRiderModel;
import com.example.golaundry.model.ReportModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.RiderViewModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class ReportActivity extends AppCompatActivity {
    RateLaundryModel laundryRateData;
    RateRiderModel riderRateData;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = findViewById(R.id.act_report_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon((getResources().getDrawable(R.drawable.ic_toolbar_back)));
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        LaundryViewModel mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        UserViewModel mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        RiderViewModel mRiderViewModel = new ViewModelProvider(this).get(RiderViewModel.class);

        TextView userNameTextView = findViewById(R.id.act_report_tv_from_name);
        TextView orderIdTextView = findViewById(R.id.act_report_tv_order_id);
        TextView commentTextView = findViewById(R.id.act_report_comment);
        RatingBar RatingBar = findViewById(R.id.act_report_rating_star);
        TextView dateTimeTextView = findViewById(R.id.act_report_tv_date);
        EditText msgEditText = findViewById(R.id.act_report_et_msg_to_admin);
        Button submitButton = findViewById(R.id.act_report_btn_submit);
        ProgressBar mProgressBar = findViewById(R.id.act_report_progressBar);

        boolean isRider = getIntent().getBooleanExtra("isRider", false);
        boolean isLaundry = getIntent().getBooleanExtra("isLaundry", false);

        laundryRateData = (RateLaundryModel) getIntent().getSerializableExtra("LaundryRateData");
        if (laundryRateData != null) {
            mUserViewModel.getUserData(laundryRateData.getUserId()).observe(this, userModel -> {
                if (userModel != null) {
                    userNameTextView.setText(userModel.getFullName());
                }
            });
            orderIdTextView.setText(laundryRateData.getOrderId());

            if (isLaundry) {
                commentTextView.setText(laundryRateData.getCommentToLaundry());
                float rateAmount = laundryRateData.getRateToLaundry();
                RatingBar.setRating(rateAmount);
            }

            String date = laundryRateData.getDateTime();
            String formattedDate = formatDateTimeReport(date);
            dateTimeTextView.setText(formattedDate);

            submitButton.setOnClickListener(view -> {
                mProgressBar.setVisibility(View.VISIBLE);
                String message = msgEditText.getText().toString().trim();

                if (message.equals("")) {
                    Toast.makeText(this, "Message is required", Toast.LENGTH_SHORT).show();
                    msgEditText.requestFocus();
                    msgEditText.setError("Required");
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference().child("reports");
                    String reportId = String.valueOf(UUID.randomUUID());
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String currentDateTime = sdf2.format(new Date());

                    if (isLaundry) {
                        ReportModel mReportModel = new ReportModel(reportId, "laundry", laundryRateData.getLaundryId(), laundryRateData.getUserId(), laundryRateData.getOrderId(), laundryRateData.getRateLaundryId(), currentDateTime, message, false);
                        reportsRef.child(reportId).setValue(mReportModel);
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Reported successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

            });
        }

        riderRateData = (RateRiderModel) getIntent().getSerializableExtra("RiderRateData");
        if (riderRateData != null) {
            mUserViewModel.getUserData(riderRateData.getUserId()).observe(this, userModel -> {
                if (userModel != null) {
                    userNameTextView.setText(userModel.getFullName());
                }
            });
            orderIdTextView.setText(riderRateData.getOrderId());

            if (isRider) {
                commentTextView.setText(riderRateData.getCommentToRider());
                float rateAmount = riderRateData.getRateToRider();
                RatingBar.setRating(rateAmount);
            }

            String date = riderRateData.getDateTime();
            String formattedDate = formatDateTimeReport(date);
            dateTimeTextView.setText(formattedDate);

            submitButton.setOnClickListener(view -> {
                mProgressBar.setVisibility(View.VISIBLE);
                String message = msgEditText.getText().toString().trim();

                if (message.equals("")) {
                    Toast.makeText(this, "Message is required", Toast.LENGTH_SHORT).show();
                    msgEditText.requestFocus();
                    msgEditText.setError("Required");
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference().child("reports");
                    String reportId = String.valueOf(UUID.randomUUID());
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String currentDateTime = sdf2.format(new Date());

                    if (isRider) {
                        ReportModel mReportModel = new ReportModel(reportId, "rider", riderRateData.getLaundryId(), riderRateData.getUserId(), riderRateData.getOrderId(), riderRateData.getRateRiderId(), currentDateTime, message, false);
                        reportsRef.child(reportId).setValue(mReportModel);
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Reported successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

            });
        }


    }

    public String formatDateTimeReport(String dateTime) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date date = originalFormat.parse(dateTime);

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            assert date != null;
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}