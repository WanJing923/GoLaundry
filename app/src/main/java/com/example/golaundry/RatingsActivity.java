package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.golaundry.adapter.RatingsLaundryAdapter;
import com.example.golaundry.adapter.RatingsRiderAdapter;
import com.example.golaundry.model.RateModel;
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


    @SuppressLint("UseCompatLoadingForDrawables")
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

        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference().child("ratings");

        boolean isLaundry = getIntent().getBooleanExtra("isLaundry", false);
        if (isLaundry) {
            ratingsRef.orderByChild("laundryId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
                @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<RateModel> ratingsList = new ArrayList<>();
                        for (DataSnapshot rateSnapshot : dataSnapshot.getChildren()) {
                            RateModel ratings = rateSnapshot.getValue(RateModel.class);
                            if (ratings != null) {
                                ratingsList.add(ratings);
                            }
                        }
                        //sort latest
                        ratingsList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

                        RecyclerView recyclerView = findViewById(R.id.ar_rv_reviews);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(new RatingsLaundryAdapter(RatingsActivity.this,ratingsList,RatingsActivity.this,mLaundryViewModel,mUserViewModel,mRiderViewModel));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        boolean isRider = getIntent().getBooleanExtra("isRider", false);
        if (isRider) {
            ratingsRef.orderByChild("riderId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
                @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<RateModel> ratingsList = new ArrayList<>();
                        for (DataSnapshot rateSnapshot : dataSnapshot.getChildren()) {
                            RateModel ratings = rateSnapshot.getValue(RateModel.class);
                            if (ratings != null) {
                                ratingsList.add(ratings);
                            }
                        }
                        //sort latest
                        ratingsList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

                        RecyclerView recyclerView = findViewById(R.id.ar_rv_reviews);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(new RatingsRiderAdapter(RatingsActivity.this, ratingsList,RatingsActivity.this,mLaundryViewModel,mUserViewModel,mRiderViewModel));
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