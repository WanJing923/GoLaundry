package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.OrderStatusModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class RiderOrderDetailsActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private CodeScannerView qRCodeScannerView;
    private CardView enterOrderIdCardView;
    private EditText orderIdEditText;

    private boolean isScannerEnabled = false;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_order_details);
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Toolbar toolbar = findViewById(R.id.arvd_toolbar);
        UserViewModel mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        LaundryViewModel mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        OrderModel mOrderModel = (OrderModel) getIntent().getSerializableExtra("RiderViewOrderDetailsData");

        TextView userNameTextView = findViewById(R.id.arvd_tv_customer_name);
        TextView userPhoneTextView = findViewById(R.id.arvd_tv_customer_number);
        TextView userAddressTextView = findViewById(R.id.arvd_tv_address_customer);
        ImageView copyUserImageView = findViewById(R.id.arvd_copy1);
        TextView laundryNameTextView = findViewById(R.id.arvd_tv_laundry_name);
        TextView laundryPhoneTextView = findViewById(R.id.arvd_tv_laundry_number);
        TextView laundryAddressTextView = findViewById(R.id.arvd_tv_address_laundry);
        ImageView copyLaundryImageView = findViewById(R.id.arvd_copy2);
        TextView distanceTextView = findViewById(R.id.arvd_tv_distance_number);
        TextView earnTextView = findViewById(R.id.arvd_tv_money);
        Button pickUpButton = findViewById(R.id.arvd_btn_pick_up);
        enterOrderIdCardView = findViewById(R.id.arvd_cv_enter_order_id);
        CardView orderDetailsCardView = findViewById(R.id.arvd_cv_order_details);
        orderIdEditText = findViewById(R.id.arvd_et_order_id);
        ImageView scanImageView = findViewById(R.id.arvd_qr_scan);
        TextView okTextView = findViewById(R.id.arvd_tv_ok);
        TextView cancelTextView = findViewById(R.id.arvd_tv_cancel);
        qRCodeScannerView = findViewById(R.id.arvd_scanner);

        if (mOrderModel != null) {
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
            userAddressTextView.setText(fullAddress);
            copyUserImageView.setOnClickListener(view -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("user address", address);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "User address copied to clipboard", Toast.LENGTH_SHORT).show();
            });

            //middle part
            mLaundryViewModel.getLaundryData(mOrderModel.getLaundryId()).observe(this, laundryModel -> {
                if (laundryModel != null) {
                    laundryNameTextView.setText(laundryModel.getShopName());
                    laundryPhoneTextView.setText(laundryModel.getContactNo());
                    laundryAddressTextView.setText(laundryModel.getAddress());
                    copyLaundryImageView.setOnClickListener(view -> {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("laundry address", laundryModel.getAddress());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(RiderOrderDetailsActivity.this, "Laundry address copied to clipboard", Toast.LENGTH_SHORT).show();
                    });
                }
            });
            @SuppressLint("DefaultLocale")
            String distanceShow = String.format("%.2f", mOrderModel.getDistanceBetweenUserLaundry());
            distanceTextView.setText(distanceShow);
            @SuppressLint("DefaultLocale")
            String earnShow = String.format("%.2f", mOrderModel.getDeliveryFee());
            earnTextView.setText(earnShow);

            //scan order ID to match
            pickUpButton.setOnClickListener(view -> {

                //show 2nd card, hide 1st card
                enterOrderIdCardView.setVisibility(View.VISIBLE);
                orderDetailsCardView.setVisibility(View.GONE);
//                qRCodeScannerView.setVisibility(View.GONE);
                disableScanner();


                scanImageView.setOnClickListener(view1 -> {
                    qRCodeScannerView.setVisibility(View.VISIBLE);
                    enterOrderIdCardView.setVisibility(View.GONE);
                    orderDetailsCardView.setVisibility(View.GONE);
                    enableScanner();
                    startScanning();
                });

                cancelTextView.setOnClickListener(view1 -> {
                    disableScanner();
//                    qRCodeScannerView.setVisibility(View.GONE);
                    enterOrderIdCardView.setVisibility(View.GONE);
                    orderDetailsCardView.setVisibility(View.VISIBLE);
                });

                okTextView.setOnClickListener(view1 -> {
                    disableScanner();
                    String orderIdString = orderIdEditText.getText().toString();
                    if (!orderIdString.equals("")) {
                        if (orderIdString.equals(mOrderModel.getOrderId())) {
                            //update order status table ,order table
                            DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder").child(mOrderModel.getOrderId());
                            DatabaseReference orderStatusRef = FirebaseDatabase.getInstance().getReference().child("orderStatus").child(mOrderModel.getOrderId());

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                            String dateTime = sdf.format(new Date());
                            OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime, "Rider pick up");

                            userOrderRef.child("riderId").setValue(currentUserId).addOnSuccessListener(aVoid -> { //update order table
                                userOrderRef.child("currentStatus").setValue("Rider pick up").addOnSuccessListener(aVoid1 -> { //update order table
                                    String orderStatusId = String.valueOf(UUID.randomUUID());
                                    orderStatusRef.child(orderStatusId).setValue(mOrderStatusModel).addOnSuccessListener(aVoid2 -> { //add order status table
                                        Toast.makeText(RiderOrderDetailsActivity.this, "Rider pick up", Toast.LENGTH_SHORT).show();
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

                            qRCodeScannerView.setVisibility(View.GONE);
                            disableScanner();
                            enterOrderIdCardView.setVisibility(View.GONE);
                            orderDetailsCardView.setVisibility(View.VISIBLE);
                            finish();
                        } else {
                            Toast.makeText(RiderOrderDetailsActivity.this, "Order ID is not match. Please retry.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RiderOrderDetailsActivity.this, "Order ID is required!", Toast.LENGTH_SHORT).show();
                        orderIdEditText.requestFocus();
                        qRCodeScannerView.setVisibility(View.GONE);
                        enterOrderIdCardView.setVisibility(View.VISIBLE);
                        orderDetailsCardView.setVisibility(View.GONE);
                    }
                });
            });
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(RiderOrderDetailsActivity.this, new String[]{android.Manifest.permission.CAMERA}, 123);
        } else {
            startScanning();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
                startScanning();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startScanning() {
        if (!isScannerEnabled) {
            mCodeScanner = new CodeScanner(RiderOrderDetailsActivity.this, qRCodeScannerView);
            mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
                String text = String.valueOf(result);
                Toast.makeText(RiderOrderDetailsActivity.this, text, Toast.LENGTH_SHORT).show();
                qRCodeScannerView.setVisibility(View.INVISIBLE);
                enterOrderIdCardView.setVisibility(View.VISIBLE);
                orderIdEditText.setText(text);
            }));
            mCodeScanner.setErrorCallback(error -> Toast.makeText(getApplicationContext(), "Unable to Read QR.", Toast.LENGTH_SHORT).show());

            qRCodeScannerView.setOnClickListener(view -> {
                if (isScannerEnabled) {
                    mCodeScanner.startPreview(); // Start scanning when the view is clicked
                }
            });

            // Initially, the scanner is enabled
            enableScanner();
        }
    }

    private void disableScanner() {
        if (mCodeScanner != null) {
            mCodeScanner.stopPreview();
            isScannerEnabled = false;
        }
    }

    private void enableScanner() {
        if (mCodeScanner != null) {
            mCodeScanner.startPreview();
            isScannerEnabled = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isScannerEnabled) {
            enableScanner(); // Enable the scanner when the activity is resumed
        }
    }

    @Override
    protected void onPause() {
        disableScanner(); // Disable the scanner when the activity is paused
        super.onPause();
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