package com.example.golaundry.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.golaundry.R;
import com.example.golaundry.adapter.SavedLaundryAdapter;
import com.example.golaundry.model.CombineLaundryData;
import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.LaundryShopModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.SaveLaundryViewModel;
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
import java.util.concurrent.atomic.AtomicInteger;

public class SavedLaundryActivity extends AppCompatActivity {

    LaundryViewModel mLaundryViewModel;
    SaveLaundryViewModel mSaveLaundryViewModel;
    String currentUserId;
    UserViewModel mUserViewModel;
    RecyclerView laundryRecyclerView;
    ArrayList<CombineLaundryData> laundryList;
    SavedLaundryAdapter mSavedLaundryAdapter;
    TextView noLaundryTextView;

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_laundry);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mSaveLaundryViewModel = new ViewModelProvider(this).get(SaveLaundryViewModel.class);

        Toolbar toolbar = findViewById(R.id.asl_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        laundryRecyclerView = findViewById(R.id.asl_rv_saved_laundry);
        noLaundryTextView = findViewById(R.id.asl_tv_no_laundry);

        //initialize adapter
        laundryList = new ArrayList<>();
        mSavedLaundryAdapter = new SavedLaundryAdapter(laundryList, SavedLaundryActivity.this);
        laundryRecyclerView.setAdapter(mSavedLaundryAdapter);
        laundryRecyclerView.setLayoutManager(new LinearLayoutManager(SavedLaundryActivity.this));

        mSaveLaundryViewModel.getSavedLaundryId(currentUserId).observe(SavedLaundryActivity.this, allLaundryId -> {
            if (allLaundryId.size() != 0) {
                List<LaundryModel> laundryLiveData = new ArrayList<>();
                List<LaundryShopModel> shopLiveData = new ArrayList<>();

                AtomicInteger laundryTaskCounter = new AtomicInteger(allLaundryId.size());
                AtomicInteger shopTaskCounter = new AtomicInteger(allLaundryId.size());

                for (String laundryId : allLaundryId) {
                    DatabaseReference laundryReference = FirebaseDatabase.getInstance().getReference().child("laundry").child(laundryId);
                    DatabaseReference shopReference = FirebaseDatabase.getInstance().getReference().child("laundryShop").child(laundryId);

                    laundryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot laundryDataSnapshot) {
                            if (laundryDataSnapshot.exists()) {
                                LaundryModel laundry = laundryDataSnapshot.getValue(LaundryModel.class);
                                laundryLiveData.add(laundry);
                            }
                            if (laundryTaskCounter.decrementAndGet() == 0) {
                                LiveData<List<CombineLaundryData>> combinedDataList = mLaundryViewModel.combineAndNotifyData(laundryLiveData, shopLiveData);
                                combinedDataList.observe(SavedLaundryActivity.this, combinedData -> {
                                    laundryList.clear();
                                    laundryList.addAll(combinedData);
                                    mSavedLaundryAdapter.notifyDataSetChanged();
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    shopReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot shopDataSnapshot) {
                            if (shopDataSnapshot.exists()) {
                                LaundryShopModel shop = shopDataSnapshot.getValue(LaundryShopModel.class);
                                shopLiveData.add(shop);
                            }
                            if (shopTaskCounter.decrementAndGet() == 0) {
                                LiveData<List<CombineLaundryData>> combinedDataList = mLaundryViewModel.combineAndNotifyData(laundryLiveData, shopLiveData);
                                combinedDataList.observe(SavedLaundryActivity.this, combinedData -> {
                                    laundryList.clear();
                                    laundryList.addAll(combinedData);
                                    mSavedLaundryAdapter.notifyDataSetChanged();
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle onCancelled event if needed
                        }
                    });
                }
            } else {
                noLaundryTextView.setVisibility(View.VISIBLE);
            }
        });



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