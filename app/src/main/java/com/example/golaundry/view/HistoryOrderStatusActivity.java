package com.example.golaundry.view;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.golaundry.R;
import com.example.golaundry.adapter.OrderStatusAdapter;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.OrderStatusModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.RiderViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class HistoryOrderStatusActivity extends AppCompatActivity {

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order_status);
        RiderViewModel mRiderViewModel = new ViewModelProvider(this).get(RiderViewModel.class);
        LaundryViewModel mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);

        Toolbar toolbar = findViewById(R.id.hosa_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        RecyclerView orderStatusRecyclerView = findViewById(R.id.hosa_rv_order);
        TextView riderTextView = findViewById(R.id.hosa_tv_rider);
        TextView riderNameTextView = findViewById(R.id.hosa_tv_rider_name);
        TextView viewRiderTextView = findViewById(R.id.hosa_tv_rider_info);
        TextView currentStatusTextView = findViewById(R.id.hosa_tv_current_status);
        TextView laundryNameTextView = findViewById(R.id.hosa_tv_shopName);
        TextView viewShopTextView = findViewById(R.id.hosa_tv_view_shop);
        TextView orderIdTextView = findViewById(R.id.hosa_tv_order_id_number);
        ImageView helpImageView = findViewById(R.id.hosa_iv_help);
        TextView orderIdWordTextView = findViewById(R.id.hosa_tv_order_id);

        boolean isRider = getIntent().getBooleanExtra("isRider",false);
        if (isRider){
            orderIdTextView.setVisibility(View.GONE);
            orderIdWordTextView.setVisibility(View.GONE);
            viewShopTextView.setVisibility(View.GONE);
        }

        boolean isLaundry = getIntent().getBooleanExtra("isLaundry",false);
        if (isLaundry){
            viewShopTextView.setVisibility(View.GONE);
        }

        OrderModel mOrderModel = (OrderModel) getIntent().getSerializableExtra("HistoryOrderData");
        if (mOrderModel != null) {
            String laundryId = mOrderModel.getLaundryId();
            String riderId = mOrderModel.getRiderId();
            String orderId = mOrderModel.getOrderId();
            currentStatusTextView.setText(mOrderModel.getCurrentStatus());
            orderIdTextView.setText(orderId);

            mLaundryViewModel.getLaundryData(laundryId).observe(this, laundryData -> {
                if (laundryData != null) {
                    laundryNameTextView.setText(laundryData.getShopName());
                    viewShopTextView.setOnClickListener(view -> {
                        Intent intent = new Intent(HistoryOrderStatusActivity.this, HistoryViewLaundryShopActivity.class);
                        intent.putExtra("laundryId", laundryId);
                        startActivity(intent);
                    });
                }
            });

            ArrayList<OrderStatusModel> orderStatusList = new ArrayList<>();
            DatabaseReference orderStatusRef = FirebaseDatabase.getInstance().getReference().child("orderStatus").child(orderId);
            orderStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot orderStatusSnapshot : dataSnapshot.getChildren()) {
                            String dateTime = orderStatusSnapshot.child("dateTime").getValue(String.class);
                            String statusContent = orderStatusSnapshot.child("statusContent").getValue(String.class);
                            OrderStatusModel orderStatus = new OrderStatusModel(dateTime, statusContent);
                            orderStatusList.add(orderStatus);
                            orderStatusList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));
                        }
                        OrderStatusAdapter orderStatusAdapter = new OrderStatusAdapter(orderStatusList, HistoryOrderStatusActivity.this);
                        orderStatusRecyclerView.setAdapter(orderStatusAdapter);
                        orderStatusRecyclerView.setLayoutManager(new LinearLayoutManager(HistoryOrderStatusActivity.this));
                        orderStatusAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            if (Objects.equals(riderId, "None")) {
                riderTextView.setVisibility(View.GONE);
                riderNameTextView.setVisibility(View.GONE);
                viewRiderTextView.setVisibility(View.GONE);
            } else {
                mRiderViewModel.getRiderData(riderId).observe(this, riderData -> {
                    if (riderData != null) {
                        riderNameTextView.setText(riderData.getFullName());
                    }
                });
                viewRiderTextView.setOnClickListener(view -> {
                    //intent to view rider activity
                    Intent intent = new Intent(HistoryOrderStatusActivity.this, RiderInfoActivity.class);
                    intent.putExtra("riderId", riderId);
                    startActivity(intent);
                });
            }

            helpImageView.setOnClickListener(view -> {
                Intent intent = new Intent(HistoryOrderStatusActivity.this, HelpCenterActivity.class);
                startActivity(intent);
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