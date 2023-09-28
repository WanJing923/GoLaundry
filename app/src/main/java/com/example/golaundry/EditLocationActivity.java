package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.golaundry.adapter.AddressAdapter;
import com.example.golaundry.adapter.UserOrderLaundryServicesAdapter;
import com.example.golaundry.model.AddressModel;
import com.example.golaundry.model.LaundryServiceModel;
import com.example.golaundry.model.OrderDataHolder;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditLocationActivity extends AppCompatActivity {

    UserViewModel mUserViewModel;
    String currentUserId;
    ArrayList<AddressModel> addressList;
    AddressAdapter mAddressAdapter;
    OrderDataHolder mOrderDataHolder;
    OrderModel orderData;

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);

        Toolbar toolbar = findViewById(R.id.ela_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        mOrderDataHolder = new ViewModelProvider(this).get(OrderDataHolder.class);
        orderData = mOrderDataHolder.getOrderData();
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        RecyclerView addressRecyclerView = findViewById(R.id.ela_rv_address);
        addressList = new ArrayList<>();
        mAddressAdapter = new AddressAdapter(addressList, this,mUserViewModel);
        addressRecyclerView.setAdapter(mAddressAdapter);
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(EditLocationActivity.this));

        mUserViewModel.getAllAddressesForUser(currentUserId).observe(this, addresses -> {
            if (addresses != null) {
                addressList.clear();
                addressList.addAll(addresses);
                mAddressAdapter.notifyDataSetChanged();
            }
        });

        OrderModel orderData = (OrderModel) getIntent().getSerializableExtra("orderData");
        if (orderData != null) {
            mOrderDataHolder.setOrderData(orderData);
        }

        Button doneButton = findViewById(R.id.ela_btn_save);
        doneButton.setOnClickListener(v -> {
            AddressModel selectedAddresses = mAddressAdapter.getSelectedAddresses();
            Intent intent = new Intent(this, OrderLocationActivity.class);
            intent.putExtra("selectedAddresses", selectedAddresses);
            intent.putExtra("orderData", orderData);
            startActivity(intent);
        });

        ImageView addAddressButton = findViewById(R.id.ela_btn_add_new_address);
        addAddressButton.setOnClickListener(view -> {
            Intent intent = new Intent(EditLocationActivity.this, NewAddressActivity.class);
            intent.putExtra("orderData", orderData);
            startActivity(intent);
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