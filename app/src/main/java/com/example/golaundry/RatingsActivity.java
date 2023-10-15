package com.example.golaundry;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.adapter.RatingsLaundryAdapter;
import com.example.golaundry.adapter.RatingsRiderAdapter;
import com.example.golaundry.model.RateLaundryModel;
import com.example.golaundry.model.RateRiderModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.RiderViewModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RatingsActivity extends AppCompatActivity {

    String currentUserId;

    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);

        Toolbar toolbar = findViewById(R.id.ar_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon((getResources().getDrawable(R.drawable.ic_toolbar_back)));
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        LaundryViewModel mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        UserViewModel mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        RiderViewModel mRiderViewModel = new ViewModelProvider(this).get(RiderViewModel.class);

        TextView ratingsNumberTextView = findViewById(R.id.ar_tv_rating_number);
        RatingBar RatingBar = findViewById(R.id.ar_rating_star);

        DatabaseReference ratingsLaundryRef = FirebaseDatabase.getInstance().getReference().child("ratingsLaundry");
        DatabaseReference ratingsRiderRef = FirebaseDatabase.getInstance().getReference().child("ratingsRider");

        boolean isLaundry = getIntent().getBooleanExtra("isLaundry", false);
        if (isLaundry) {
            mLaundryViewModel.getLaundryData(currentUserId).observe(this, laundry -> {
                if (laundry != null) {
                    ratingsNumberTextView.setText(String.format("%.2f", laundry.getRatingsAverage()));
                    RatingBar.setRating(laundry.getRatingsAverage());
                }
            });

            ratingsLaundryRef.orderByChild("laundryId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
                @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<RateLaundryModel> ratingsList = new ArrayList<>();
                        for (DataSnapshot rateSnapshot : dataSnapshot.getChildren()) {
                            RateLaundryModel ratings = rateSnapshot.getValue(RateLaundryModel.class);
                            if (ratings != null) {
                                ratingsList.add(ratings);
                            }
                        }
                        //sort latest
                        ratingsList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

                        RecyclerView recyclerView = findViewById(R.id.ar_rv_reviews);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(new RatingsLaundryAdapter(RatingsActivity.this, ratingsList, RatingsActivity.this, mLaundryViewModel, mUserViewModel, mRiderViewModel));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        boolean isRider = getIntent().getBooleanExtra("isRider", false);
        if (isRider) {
            mRiderViewModel.getRiderData(currentUserId).observe(this, rider -> {
                if (rider != null) {
                    ratingsNumberTextView.setText(String.format("%.2f", rider.getRatingsAverage()));
                    RatingBar.setRating(rider.getRatingsAverage());
                }
            });

            ratingsRiderRef.orderByChild("riderId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
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
                        //sort latest
                        ratingsList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

                        RecyclerView recyclerView = findViewById(R.id.ar_rv_reviews);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(new RatingsRiderAdapter(RatingsActivity.this, ratingsList, RatingsActivity.this, mLaundryViewModel, mUserViewModel, mRiderViewModel));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
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