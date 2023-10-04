package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.OrderStatusModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class RiderViewOrderActivity extends AppCompatActivity {

    private OrderModel mOrderModel;
    private double distance;
    private LaundryModel laundryData;
    private UserViewModel mUserViewModel;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_view_order);
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Toolbar toolbar = findViewById(R.id.arvo_toolbar);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        mOrderModel = (OrderModel) getIntent().getSerializableExtra("RiderViewOrderData");
        distance = getIntent().getDoubleExtra("distance", 0.0);
        laundryData = (LaundryModel) getIntent().getSerializableExtra("laundryData");

        TextView userNameTextView = findViewById(R.id.arvo_tv_name);
        TextView userPhoneTextView = findViewById(R.id.arvo_tv_user_number);
        TextView fromAddressTextView = findViewById(R.id.arvo_tv_address_from);
        ImageView copyFromImageView = findViewById(R.id.from_copy);
        TextView laundryNameTextView = findViewById(R.id.arvo_tv_laundry_name);
        TextView laundryPhoneTextView = findViewById(R.id.arvo_tv_laundry_number);
        TextView toAddressTextView = findViewById(R.id.arvo_tv_address_to);
        ImageView copyToImageView = findViewById(R.id.to_copy);
        TextView distanceTextView = findViewById(R.id.arvo_tv_distance_number);
        TextView earnTextView = findViewById(R.id.arvo_tv_money);
        Button acceptButton = findViewById(R.id.arvo_btn_accept);

        if (mOrderModel != null && distance != 0.0 && laundryData != null) {
            //upper part
            String userId = mOrderModel.getUserId();
            mUserViewModel.getUserData(userId).observe(this, user -> {
                if (user != null) {
                    String userName = user.getFullName();
                    userNameTextView.setText(userName);
                    String userPhone = "+60 " + user.getPhoneNo();
                    userPhoneTextView.setText(userPhone);
                }
            });
            String details = mOrderModel.getAddressInfo().get("details");
            String address = mOrderModel.getAddressInfo().get("address");
            String fullAddress = details + ", " + address;
            fromAddressTextView.setText(fullAddress);
            copyFromImageView.setOnClickListener(view -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("user address", address);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "User address copied to clipboard", Toast.LENGTH_SHORT).show();
            });

            //middle part
            laundryNameTextView.setText(laundryData.getShopName());
            laundryPhoneTextView.setText(laundryData.getContactNo());
            toAddressTextView.setText(laundryData.getAddress());
            copyToImageView.setOnClickListener(view -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("laundry address", laundryData.getAddress());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(RiderViewOrderActivity.this, "Laundry address copied to clipboard", Toast.LENGTH_SHORT).show();
            });

            @SuppressLint("DefaultLocale")
            String distanceShow = String.format("%.2f", distance);
            distanceTextView.setText(distanceShow);
            @SuppressLint("DefaultLocale")
            String earnShow = String.format("%.2f", mOrderModel.getDeliveryFee());
            earnTextView.setText(earnShow);

            acceptButton.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(RiderViewOrderActivity.this);
                builder.setTitle("Accept Order Confirmation");
                builder.setMessage("You are unable to cancel once you accept it.");

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    //update order status table ,order table
                    DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder").child(mOrderModel.getOrderId());
                    DatabaseReference orderStatusRef = FirebaseDatabase.getInstance().getReference().child("orderStatus").child(mOrderModel.getOrderId());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String dateTime = sdf.format(new Date());
                    OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime,"Rider accept order");

                    userOrderRef.child("riderId").setValue(currentUserId).addOnSuccessListener(aVoid -> { //update order table
                        userOrderRef.child("currentStatus").setValue("Rider accept order").addOnSuccessListener(aVoid1 -> { //update order table
                            String orderStatusId = String.valueOf(UUID.randomUUID());
                            orderStatusRef.child(orderStatusId).setValue(mOrderStatusModel).addOnSuccessListener(aVoid2 -> { //add order status table
                                Toast.makeText(RiderViewOrderActivity.this, "Order accepted", Toast.LENGTH_SHORT).show();
                                finish();
                            }).addOnFailureListener(e -> {
                                //
                            });
                        }).addOnFailureListener(e -> {
                            //
                        });
                    }).addOnFailureListener(e -> {
                        //
                    });

                    dialog.dismiss();
                });

                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
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