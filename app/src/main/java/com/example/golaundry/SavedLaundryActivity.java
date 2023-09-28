package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.golaundry.adapter.SavedLaundryAdapter;
import com.example.golaundry.adapter.UserOrderLaundryServicesAdapter;
import com.example.golaundry.adapter.UserOrderShowLaundryAdapter;
import com.example.golaundry.model.CombineLaundryData;
import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.LaundryServiceModel;
import com.example.golaundry.model.LaundryShopModel;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.SaveLaundryViewModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SavedLaundryActivity extends AppCompatActivity {

    LaundryViewModel mLaundryViewModel;
    SaveLaundryViewModel mSaveLaundryViewModel;
    String currentUserId;
    UserViewModel mUserViewModel;
    RecyclerView laundryRecyclerView;
    ArrayList<CombineLaundryData> laundryList;
    SavedLaundryAdapter mSavedLaundryAdapter;
    List<LaundryModel> allLaundryData;
    List<LaundryShopModel> allShopData;


    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_laundry);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mSaveLaundryViewModel = new ViewModelProvider(this).get(SaveLaundryViewModel.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.asl_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        laundryRecyclerView = findViewById(R.id.asl_rv_saved_laundry);
        TextView noLaundryTextView = findViewById(R.id.asl_tv_no_laundry);

        //initialize adapter
        laundryList = new ArrayList<>();
        mSavedLaundryAdapter = new SavedLaundryAdapter(laundryList, SavedLaundryActivity.this);
        laundryRecyclerView.setAdapter(mSavedLaundryAdapter);
        laundryRecyclerView.setLayoutManager(new LinearLayoutManager(SavedLaundryActivity.this));

        List<LaundryModel> allLaundryData = new ArrayList<>();
        List<LaundryShopModel> allShopData = new ArrayList<>();
        MediatorLiveData<List<CombineLaundryData>> combinedLiveData = new MediatorLiveData<>();

        mSaveLaundryViewModel.getSavedLaundryId(currentUserId).observe(SavedLaundryActivity.this, allLaundryId -> {
            if (allLaundryId != null) {
                for (String laundryId : allLaundryId) {
                    LiveData<LaundryModel> laundryLiveData = mLaundryViewModel.getLaundryData(laundryId);
                    LiveData<LaundryShopModel> shopLiveData = mLaundryViewModel.getShopData(laundryId);

                    combinedLiveData.addSource(laundryLiveData, laundryDataStatus -> {
                        combinedLiveData.addSource(shopLiveData, shopData -> {
                            if (laundryDataStatus != null && shopData != null) {
                                allLaundryData.add(laundryDataStatus);
                                allShopData.add(shopData);

                                List<CombineLaundryData> combinedDataList = mLaundryViewModel.combineAndNotifyData(allLaundryData, allShopData).getValue();
                                combinedLiveData.setValue(combinedDataList);
                            }
                        });
                    });
                }
            } else {
                noLaundryTextView.setVisibility(View.VISIBLE);
            }
        });

        combinedLiveData.observe(SavedLaundryActivity.this, combinedDataList -> {
            if (combinedDataList != null) {
                laundryList.clear();
                laundryList.addAll(combinedDataList);
                mSavedLaundryAdapter.notifyDataSetChanged();
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