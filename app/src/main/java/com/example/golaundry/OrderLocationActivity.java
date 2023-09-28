package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golaundry.model.AddressModel;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.OrderStatusModel;
import com.example.golaundry.model.UserAddressModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class OrderLocationActivity extends AppCompatActivity {

    UserViewModel mUserViewModel;
    String currentUserId,name,details,address,selectedDate;
    OrderModel orderData;

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

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        TextView addressTextView = findViewById(R.id.ol_tv_address);
        TextView addressNameTextView = findViewById(R.id.ol_tv_default_location);
        TextView membershipRateTextView = findViewById(R.id.order_tv_member_discount_rm);
        TextView membershipRateAmountTextView = findViewById(R.id.order_tv_member_discount_amount);
        TextView laundryFeeAmountTextView = findViewById(R.id.order_tv_laundry_fee_amount);
        TextView deliveryFeeAmountTextView = findViewById(R.id.order_tv_delivery_fee_amount);
        TextView totalAmountTextView = findViewById(R.id.order_tv_total_amount);
        ImageView editAddressImageView = findViewById(R.id.ol_iv_edit);
        EditText dateEditText = findViewById(R.id.order_et_date);
        EditText noteToRiderEditText = findViewById(R.id.order_et_note_rider);

        dateEditText.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            long minDate = calendar.getTimeInMillis();

            DatePickerDialog datePickerDialog = new DatePickerDialog(OrderLocationActivity.this, R.style.CustomDatePickerDialog, (view1, year1, month1, dayOfMonth) -> {
                selectedDate = (month1 + 1) + "/" + dayOfMonth + "/" + year1;
                dateEditText.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.getDatePicker().setMinDate(minDate);
            datePickerDialog.show();
        });

        AddressModel selectedAddress = (AddressModel) getIntent().getSerializableExtra("selectedAddresses");
        if (selectedAddress != null) {
            name = selectedAddress.getName();
            details = selectedAddress.getDetails();
            address = selectedAddress.getAddress();
            addressNameTextView.setText(name);
            String userAddress = details + ", " + address;
            addressTextView.setText(userAddress);

            editAddressImageView.setOnClickListener(view -> {
                Intent intent = new Intent(OrderLocationActivity.this, EditLocationActivity.class);
                startActivity(intent);
            });
        } else {
            mUserViewModel.getAllAddressesForUser(currentUserId).observe(this, addresses -> {
                if (addresses == null || addresses.isEmpty()) {
                    addressNameTextView.setText("Add Default Address");
                    addressTextView.setText("");
                    editAddressImageView.setImageResource(R.drawable.ic_add_address);

                    address = null;

                    editAddressImageView.setOnClickListener(view -> {
                        Intent intent = new Intent(OrderLocationActivity.this, NewAddressActivity.class);
                        intent.putExtra("orderData", orderData);
                        intent.putExtra("defaultAddress", true);
                        startActivity(intent);
                    });
                } else {
                    AddressModel defaultAddress = addresses.stream().filter(AddressModel::isDefaultAddress).findFirst().orElse(null);
                    if (defaultAddress != null) {
                        name = defaultAddress.getName();
                        details = defaultAddress.getDetails();
                        address = defaultAddress.getAddress();
                        addressNameTextView.setText(name);
                        String userAddress = details + ", " + address;
                        addressTextView.setText(userAddress);

                        editAddressImageView.setOnClickListener(view -> {
                            Intent intent = new Intent(OrderLocationActivity.this, EditLocationActivity.class);
                            intent.putExtra("orderData", orderData);
                            intent.putExtra("defaultAddress", defaultAddress.isDefaultAddress());
                            startActivity(intent);
                        });
                    } else {
                        address = null;
                        addressNameTextView.setText("Add Default Address");
                        addressTextView.setText("");
                        editAddressImageView.setImageResource(R.drawable.ic_add_address);
                    }
                }
            });
        }

        OrderModel mOrderModel = (OrderModel) getIntent().getSerializableExtra("orderData");
        orderData = mOrderModel;
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

        Button placeButton = findViewById(R.id.ola_btn_place_order);
        placeButton.setOnClickListener(view -> {

            String noteToRider = noteToRiderEditText.getText().toString();
            ProgressBar mProgressBar = findViewById(R.id.ola_progressbar);
            mProgressBar.setVisibility(View.VISIBLE);

            if (selectedDate==null){
                mProgressBar.setVisibility(View.GONE);
                dateEditText.setError("Pick up date is required!");
                dateEditText.requestFocus();
            } else if (address==null){
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(OrderLocationActivity.this, "Address info is required!", Toast.LENGTH_SHORT).show();
            } else {
                //get the address info
                Map<String, String> addressData = new HashMap<>();
                addressData.put("name", name);
                addressData.put("details", details);
                addressData.put("address", address);
                orderData.setAddressInfo("AddressInfo", addressData);
                orderData.setPickUpDate(selectedDate);
                orderData.setNoteToRider(noteToRider);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String dateTime = sdf.format(new Date());

                OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime,"Order created");

                mUserViewModel.addOrder(currentUserId,orderData,mOrderStatusModel).observe(OrderLocationActivity.this,orderStatus ->{
                    if (orderStatus){
                        Toast.makeText(OrderLocationActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(OrderLocationActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                    mProgressBar.setVisibility(View.GONE);
                });

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