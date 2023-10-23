package com.example.golaundry.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.golaundry.R;
import com.example.golaundry.model.RateLaundryModel;
import com.example.golaundry.model.RateRiderModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class RatingActivity extends AppCompatActivity {

    private String orderId, riderId, laundryId, userId;
    private float riderRatingNumber = 1;
    private float laundryRatingNumber = 1;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Toolbar toolbar = findViewById(R.id.rate_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon((getResources().getDrawable(R.drawable.ic_toolbar_back)));

        RatingBar rb_laundryRating = findViewById(R.id.rate_rate_laundry);
        RatingBar rb_riderRating = findViewById(R.id.rate_rate_rider);
        EditText et_laundryComment = findViewById(R.id.rate_et_laundry_service);
        EditText et_riderComment = findViewById(R.id.rate_et_rider_service_note);
        Button btn_submit_rating = findViewById(R.id.rate_btn_done);

        rb_laundryRating.setOnRatingBarChangeListener((ratingBar, v, b) -> laundryRatingNumber = v);

        rb_riderRating.setOnRatingBarChangeListener((ratingBar, v, b) -> riderRatingNumber = v);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderID");
        riderId = intent.getStringExtra("riderID");
        laundryId = intent.getStringExtra("laundryID");
        userId = intent.getStringExtra("userID");

        if (orderId != null && riderId != null && laundryId != null && userId != null){
            btn_submit_rating.setOnClickListener(view -> {

                String rateLaundryId = String.valueOf(UUID.randomUUID());
                String rateRiderId = String.valueOf(UUID.randomUUID());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String dateTime = sdf.format(new Date());

                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("userOrder");
                DatabaseReference ratingsLaundryRef = FirebaseDatabase.getInstance().getReference().child("ratingsLaundry");
                DatabaseReference ratingsRiderRef = FirebaseDatabase.getInstance().getReference().child("ratingsRider");

                String laundryComment = et_laundryComment.getText().toString();
                String riderComment = et_riderComment.getText().toString();

                if (riderRatingNumber == 0 || laundryRatingNumber == 0) {
                    Toast.makeText(RatingActivity.this, "Ratings should be at least 1 star.", Toast.LENGTH_SHORT).show();
                } else {
                    float riderRating = riderRatingNumber;
                    float laundryRating = laundryRatingNumber;

                    //set userOrder not able to rate
                    orderRef.child(orderId).child("ableToRate").setValue(false);

                    RateLaundryModel newRateLaundryModel = new RateLaundryModel(rateLaundryId, userId, riderId, orderId, dateTime, laundryRating, laundryComment, laundryId);
                    RateRiderModel newRateRiderModel = new RateRiderModel(rateRiderId, userId, riderId, orderId, dateTime, riderRating, riderComment, laundryId);

                    ratingsLaundryRef.child(rateLaundryId).setValue(newRateLaundryModel).addOnCompleteListener(task -> { //rate laundry table
                        if (task.isSuccessful()) {

                            ratingsRiderRef.child(rateRiderId).setValue(newRateRiderModel).addOnCompleteListener(task1 -> { //rate rider table
                                if (task.isSuccessful()) {
                                    //calculate laundry average and update
                                    ratingsLaundryRef.orderByChild("laundryId").equalTo(laundryId).addValueEventListener(new ValueEventListener() { //laundry average
                                        @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                List<RateLaundryModel> ratingsList1 = new ArrayList<>();
                                                for (DataSnapshot rateSnapshot : dataSnapshot.getChildren()) {
                                                    RateLaundryModel ratings = rateSnapshot.getValue(RateLaundryModel.class);
                                                    if (ratings != null) {
                                                        ratingsList1.add(ratings);
                                                    }
                                                }

                                                float totalRating1 = 0;
                                                int numberOfRatings = ratingsList1.size();
                                                for (RateLaundryModel rating : ratingsList1) {
                                                    totalRating1 += rating.getRateToLaundry();
                                                }
                                                float averageRating = totalRating1 / numberOfRatings;

                                                DatabaseReference laundryRef = FirebaseDatabase.getInstance().getReference().child("laundry");
                                                laundryRef.child(laundryId).child("ratingsAverage").setValue(averageRating);

                                                ratingsRiderRef.orderByChild("riderId").equalTo(riderId).addValueEventListener(new ValueEventListener() { //rider average
                                                    @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            List<RateRiderModel> ratingsList = new ArrayList<>();
                                                            for (DataSnapshot rateSnapshot : dataSnapshot.getChildren()) {
                                                                RateRiderModel ratings = rateSnapshot.getValue(RateRiderModel.class);
                                                                if (ratings != null) {
                                                                    ratingsList.add(ratings);
                                                                }
                                                            }

                                                            float totalRating = 0;
                                                            int numberOfRatings = ratingsList.size();
                                                            for (RateRiderModel rating : ratingsList) {
                                                                totalRating += rating.getRateToRider();
                                                            }
                                                            float averageRating = totalRating / numberOfRatings;

                                                            DatabaseReference ridersRef = FirebaseDatabase.getInstance().getReference().child("riders");
                                                            ridersRef.child(riderId).child("ratingsAverage").setValue(averageRating);

                                                            Toast.makeText(RatingActivity.this, "Rate successful", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });

                                } else {
                                    Toast.makeText(RatingActivity.this, "Rate Rider Fail. Please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(RatingActivity.this, "Rate Laundry Fail. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            });
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