package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.UserAddressModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.Objects;

public class OrderLocationActivity extends AppCompatActivity {

    UserViewModel mUserViewModel;
    String currentUserId;

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_location);

        Toolbar toolbar = findViewById(R.id.ola_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        TextView addressTextView = findViewById(R.id.ol_tv_address);
        TextView addressNameTextView = findViewById(R.id.ol_tv_default_location);
        TextView membershipRateTextView = findViewById(R.id.order_tv_member_discount_rm);
        TextView membershipRateAmountTextView = findViewById(R.id.order_tv_member_discount_amount);
        TextView laundryFeeAmountTextView = findViewById(R.id.order_tv_laundry_fee_amount);
        TextView deliveryFeeAmountTextView = findViewById(R.id.order_tv_delivery_fee_amount);
        TextView totalAmountTextView = findViewById(R.id.order_tv_total_amount);

        mUserViewModel.getUserDefaultAddress(currentUserId).observe(this, defaultAddress -> {
            if (defaultAddress == null) {
                addressNameTextView.setText("Add Address");
                addressTextView.setText("No address found");
            } else {
                if (defaultAddress.isDefaultAddress()) {
                    String name = defaultAddress.getName();
                    addressNameTextView.setText(name);
                    String address = defaultAddress.getAddressDetails() + ", " + defaultAddress.getAddress();
                    addressTextView.setText(address);
                }
            }
        });

        OrderModel mOrderModel = (OrderModel) getIntent().getSerializableExtra("orderData");
        if (mOrderModel != null) {
            String membershipRate = mOrderModel.getMembershipDiscount();
            double membershipDiscount = 0.0;
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            if (Objects.equals(membershipRate, "None")) {
                membershipRateTextView.setText("No membership rate: -RM");
                membershipRateAmountTextView.setText("0.00");
            } else if (membershipRate.equals("GL05")) {
                membershipRateTextView.setText("Membership Rate(" + membershipRate + "): -5%: -RM");
                membershipDiscount = 0.05 * mOrderModel.getLaundryFee();
                String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                membershipRateAmountTextView.setText(formattedMembershipDiscount);
            } else if (membershipRate.equals("GL10")) {
                membershipRateTextView.setText("Membership Rate(" + membershipRate + "): -10%: -RM");
                membershipDiscount = 0.1 * mOrderModel.getLaundryFee();
                String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                membershipRateAmountTextView.setText(formattedMembershipDiscount);
            } else if (membershipRate.equals("GL20")) {
                membershipRateTextView.setText("Membership Rate(" + membershipRate + "): -20%: -RM");
                membershipDiscount = 0.2 * mOrderModel.getLaundryFee();
                String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                membershipRateAmountTextView.setText(formattedMembershipDiscount);
            } else { //gl30
                membershipRateTextView.setText("Membership Rate(" + membershipRate + "): -30%: -RM");
                membershipDiscount = 0.3 * mOrderModel.getLaundryFee();
                String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                membershipRateAmountTextView.setText(formattedMembershipDiscount);
            }
            String formattedLaundryFeeAmount = decimalFormat.format(mOrderModel.getLaundryFee());
            laundryFeeAmountTextView.setText(formattedLaundryFeeAmount);
            String formattedDeliveryFeeAmount = decimalFormat.format(mOrderModel.getDeliveryFee());
            deliveryFeeAmountTextView.setText(formattedDeliveryFeeAmount);

            double laundryFee = Double.parseDouble(formattedLaundryFeeAmount);
            double deliveryFee = Double.parseDouble(formattedDeliveryFeeAmount);
            double total = laundryFee - membershipDiscount + deliveryFee;
            String formattedTotal = decimalFormat.format(total);
            totalAmountTextView.setText(formattedTotal);
        }

//        Button placeButton = findViewById(R.id.ola_btn_place_order);
//        placeButton.setOnClickListener(view -> {
//
//        });
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