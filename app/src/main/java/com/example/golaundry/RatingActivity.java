package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.golaundry.model.RateModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class RatingActivity extends AppCompatActivity {

    private String orderId, riderId, laundryId, userId = "";
    private float riderRatingNumber = 1;
    private float laundryRatingNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

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
        String ratingsId = String.valueOf(UUID.randomUUID());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String dateTime = sdf.format(new Date());

        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference().child("ratings");
        btn_submit_rating.setOnClickListener(view -> {
            String laundryComment = et_laundryComment.getText().toString();
            String riderComment = et_riderComment.getText().toString();
            float riderRating = riderRatingNumber;
            float laundryRating = laundryRatingNumber;
            ratingRef.child(ratingsId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        RateModel newRate = new RateModel(ratingsId, userId, riderId, orderId, dateTime, laundryRating, laundryComment, riderRating, riderComment, laundryId);

                        ratingRef.child(ratingsId).setValue(newRate).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                //set userOrder not able to rate
                                DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder");
                                userOrderRef.child(orderId).child("ableToRate").setValue(false);

                                Toast.makeText(RatingActivity.this, "Rate Successful", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                Toast.makeText(RatingActivity.this, "Rate Fail. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }
}