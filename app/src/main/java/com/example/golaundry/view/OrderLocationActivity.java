package com.example.golaundry.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golaundry.R;
import com.example.golaundry.model.AddressModel;
import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.viewModel.OrderLocationDataHolder;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.OrderStatusModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class OrderLocationActivity extends AppCompatActivity {

    UserViewModel mUserViewModel;
    String currentUserId, name, details, address, selectedDate, currentMembershipUser;
    OrderModel orderData;
    String laundryIdLRO, currentStatusLRO, noteToLaundryLRO, noteToRiderLRO, membershipRateLRO, finalDistance,addressLaundryLRO;
    double laundryFeeLRO, deliveryFeeLRO, totalFeeLRO, currentBalanceUser, distance;
    Map<String, Integer> selectedServicesLRO;
    Map<String, String> addressInfoLRO;
    OrderModel latestOrderData;
    LaundryViewModel mLaundryViewModel;
    SimpleDateFormat dayFormat, timeFormat;
    boolean laundryIsOpening = false;

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_location);
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
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
        TextView noteToLaundryTextView = findViewById(R.id.order_tv_note_laundry);
        EditText noteToLaundryEditText = findViewById(R.id.order_et_note_laundry);

        mUserViewModel.getUserData(currentUserId).observe(OrderLocationActivity.this, user -> {
            if (user != null) {
                currentMembershipUser = user.getMembershipRate();
                currentBalanceUser = user.getBalance();
            }
        });

        Intent intentLRO = getIntent();
        OrderLocationDataHolder mOrderLocationDataHolder = OrderLocationDataHolder.getInstance();

        orderData = (OrderModel) getIntent().getSerializableExtra("orderData");
        if (orderData != null) {
            mOrderLocationDataHolder.setOrderData(orderData);
        }

        LaundryModel laundryModel = (LaundryModel) getIntent().getSerializableExtra("laundryData");
        if (laundryModel != null) {
            mOrderLocationDataHolder.setLaundryData(laundryModel);
        }

        if (intentLRO.hasExtra("latestOrderData")) {
            latestOrderData = (OrderModel) intentLRO.getSerializableExtra("latestOrderData");
            assert latestOrderData != null;
            mOrderLocationDataHolder.setOrderData(latestOrderData);
            laundryIdLRO = mOrderLocationDataHolder.getOrderData().getLaundryId();
            laundryFeeLRO = mOrderLocationDataHolder.getOrderData().getLaundryFee();
            membershipRateLRO = mOrderLocationDataHolder.getOrderData().getMembershipDiscount();
            selectedServicesLRO = mOrderLocationDataHolder.getOrderData().getSelectedServices();
            addressInfoLRO = mOrderLocationDataHolder.getOrderData().getAddressInfo();
            currentStatusLRO = mOrderLocationDataHolder.getOrderData().getCurrentStatus();
            deliveryFeeLRO = mOrderLocationDataHolder.getOrderData().getDeliveryFee();
            noteToLaundryLRO = mOrderLocationDataHolder.getOrderData().getNoteToLaundry();
            noteToRiderLRO = mOrderLocationDataHolder.getOrderData().getNoteToRider();
            totalFeeLRO = mOrderLocationDataHolder.getOrderData().getTotalFee();

            Date currentDate = new Date();
            dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String currentDay = dayFormat.format(currentDate);
            String currentTime = timeFormat.format(currentDate);

            mLaundryViewModel.getLaundryData(laundryIdLRO).observe(this, laundryData -> {
                if (laundryData!= null){
                    mOrderLocationDataHolder.setLaundryData(laundryData);
                    if (laundryData.getIsBreak()){
                        finish();
                        Toast.makeText(OrderLocationActivity.this, "Laundry shop is having break", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mLaundryViewModel.getShopData(laundryIdLRO).observe(this, shop -> {
                if (shop != null) {
                    List<String> allTimeRanges = shop.getAllTimeRanges();
                    String mappedDay = "";
                    switch (currentDay) {
                        case "Monday":
                            mappedDay = allTimeRanges.get(0);
                            break;
                        case "Tuesday":
                            mappedDay = allTimeRanges.get(1);
                            break;
                        case "Wednesday":
                            mappedDay = allTimeRanges.get(2);
                            break;
                        case "Thursday":
                            mappedDay = allTimeRanges.get(3);
                            break;
                        case "Friday":
                            mappedDay = allTimeRanges.get(4);
                            break;
                        case "Saturday":
                            mappedDay = allTimeRanges.get(5);
                            break;
                        case "Sunday":
                            mappedDay = allTimeRanges.get(6);
                            break;
                    }

                    if (!"off".equals(mappedDay)) {
                        if (isTimeInRange(currentTime, mappedDay)) {
                            laundryIsOpening = true;
                        } else {
                            laundryIsOpening = false;
                            finish();
                            Toast.makeText(OrderLocationActivity.this, "Laundry shop is currently closed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        laundryIsOpening = false;
                        finish();
                        Toast.makeText(OrderLocationActivity.this, "Laundry shop is currently closed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (mOrderLocationDataHolder.getSelectedDate() != null) {
            dateEditText.setText(mOrderLocationDataHolder.getSelectedDate());
        }

        dateEditText.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            long minDate = calendar.getTimeInMillis();

            DatePickerDialog datePickerDialog = new DatePickerDialog(OrderLocationActivity.this, R.style.CustomDatePickerDialog, (view1, year1, month1, dayOfMonth) -> {
                selectedDate = (month1 + 1) + "/" + dayOfMonth + "/" + year1;
                mOrderLocationDataHolder.setSelectedDate(selectedDate);
                dateEditText.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.getDatePicker().setMinDate(minDate);
            datePickerDialog.show();
        });

        if (addressInfoLRO != null) {
            name = addressInfoLRO.get("name");
            details = addressInfoLRO.get("details");
            address = addressInfoLRO.get("address");
            addressNameTextView.setText(name);
            String userAddress = details + ", " + address;
            addressTextView.setText(userAddress);

            editAddressImageView.setOnClickListener(view -> {
                Intent intent = new Intent(OrderLocationActivity.this, EditLocationActivity.class);
                startActivity(intent);
            });

            noteToRiderEditText.setText(noteToRiderLRO);
            noteToLaundryTextView.setVisibility(View.VISIBLE);
            noteToLaundryEditText.setVisibility(View.VISIBLE);
            noteToLaundryEditText.setText(noteToLaundryLRO);

        } else {
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

                            String laundryAddress = mOrderLocationDataHolder.getLaundryData().getAddress();
                            String userSelectAddress = address;

                            LatLng laundryLatLng = getLocationFromAddress(this, laundryAddress);
                            LatLng userLatLng = getLocationFromAddress(this, userSelectAddress);
                            if (laundryLatLng != null && userLatLng != null) {
                                double dis = SphericalUtil.computeDistanceBetween(laundryLatLng, userLatLng);
                                distance = dis / 1000;
                                finalDistance = String.format("%.2f", distance);
                                deliveryFeeAmountTextView.setText(finalDistance);
                                orderData.setDeliveryFee(distance);
                                mOrderLocationDataHolder.getOrderData().setDeliveryFee(distance);
                            }

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
        }

        if (orderData != null) {
            mOrderLocationDataHolder.setOrderData(orderData);
            String membershipRate = orderData.getMembershipDiscount();
            double membershipDiscount = 0.0;
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            if (Objects.equals(membershipRate, "None")) {
                membershipRateTextView.setText("No membership rate: -RM");
                membershipRateAmountTextView.setText("0.00");
            } else if (membershipRate.equals("GL05")) {
                membershipRateTextView.setText("Membership Rate(" + membershipRate + "): -5%: -RM");
                membershipDiscount = 0.05 * mOrderLocationDataHolder.getOrderData().getLaundryFee();
                String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                membershipRateAmountTextView.setText(formattedMembershipDiscount);
            } else if (membershipRate.equals("GL10")) {
                membershipRateTextView.setText("Membership Rate(" + membershipRate + "): -10%: -RM");
                membershipDiscount = 0.1 * mOrderLocationDataHolder.getOrderData().getLaundryFee();
                String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                membershipRateAmountTextView.setText(formattedMembershipDiscount);
            } else if (membershipRate.equals("GL20")) {
                membershipRateTextView.setText("Membership Rate(" + membershipRate + "): -20%: -RM");
                membershipDiscount = 0.2 * mOrderLocationDataHolder.getOrderData().getLaundryFee();
                String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                membershipRateAmountTextView.setText(formattedMembershipDiscount);
            } else { //gl30
                membershipRateTextView.setText("Membership Rate(" + membershipRate + "): -30%: -RM");
                membershipDiscount = 0.3 * mOrderLocationDataHolder.getOrderData().getLaundryFee();
                String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                membershipRateAmountTextView.setText(formattedMembershipDiscount);
            }
            String formattedLaundryFeeAmount = decimalFormat.format(mOrderLocationDataHolder.getOrderData().getLaundryFee());
            laundryFeeAmountTextView.setText(formattedLaundryFeeAmount);
            String formattedDeliveryFeeAmount = decimalFormat.format(mOrderLocationDataHolder.getOrderData().getDeliveryFee());
            deliveryFeeAmountTextView.setText(formattedDeliveryFeeAmount);

            double laundryFee = Double.parseDouble(formattedLaundryFeeAmount);
            double deliveryFee = Double.parseDouble(formattedDeliveryFeeAmount);
            double total = laundryFee - membershipDiscount + deliveryFee;
            String formattedTotal = decimalFormat.format(total);
            totalAmountTextView.setText(formattedTotal);
            orderData.setTotalFee(total);
            mOrderLocationDataHolder.getOrderData().setTotalFee(total);
        } else {
            orderData = mOrderLocationDataHolder.getOrderData();
            mUserViewModel.getUserData(currentUserId).observe(OrderLocationActivity.this, user -> {
                if (user != null) {
                    currentMembershipUser = user.getMembershipRate();
                    currentBalanceUser = user.getBalance();
                    if (currentMembershipUser != null) {
                        String membershipRate = currentMembershipUser;
                        double membershipDiscount = 0.0;
                        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                        if (Objects.equals(membershipRate, "None")) {
                            membershipRateTextView.setText("No membership rate: -RM");
                            membershipRateAmountTextView.setText("0.00");
                        } else if (membershipRate.equals("GL05")) {
                            membershipRateTextView.setText("Membership Rate(" + membershipRate + "): -5%: -RM");
                            membershipDiscount = 0.05 * mOrderLocationDataHolder.getOrderData().getLaundryFee();
                            String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                            membershipRateAmountTextView.setText(formattedMembershipDiscount);
                        } else if (membershipRate.equals("GL10")) {
                            membershipRateTextView.setText("Membership Rate(" + membershipRate + "): -10%: -RM");
                            membershipDiscount = 0.1 * mOrderLocationDataHolder.getOrderData().getLaundryFee();
                            String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                            membershipRateAmountTextView.setText(formattedMembershipDiscount);
                        } else if (membershipRate.equals("GL20")) {
                            membershipRateTextView.setText("Membership Rate(" + membershipRate + "): -20%: -RM");
                            membershipDiscount = 0.2 * mOrderLocationDataHolder.getOrderData().getLaundryFee();
                            String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                            membershipRateAmountTextView.setText(formattedMembershipDiscount);
                        } else { //gl30
                            membershipRateTextView.setText("Membership Rate(" + membershipRate + "): -30%: -RM");
                            membershipDiscount = 0.3 * mOrderLocationDataHolder.getOrderData().getLaundryFee();
                            String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                            membershipRateAmountTextView.setText(formattedMembershipDiscount);
                        }
                        String formattedLaundryFeeAmount = decimalFormat.format(mOrderLocationDataHolder.getOrderData().getLaundryFee());
                        laundryFeeAmountTextView.setText(formattedLaundryFeeAmount);
                        String formattedDeliveryFeeAmount = decimalFormat.format(mOrderLocationDataHolder.getOrderData().getDeliveryFee());
                        deliveryFeeAmountTextView.setText(formattedDeliveryFeeAmount);

                        double laundryFee = Double.parseDouble(formattedLaundryFeeAmount);
                        double deliveryFee = Double.parseDouble(formattedDeliveryFeeAmount);
                        double total = laundryFee - membershipDiscount + deliveryFee;
                        String formattedTotal = decimalFormat.format(total);
                        totalAmountTextView.setText(formattedTotal);
                        orderData.setTotalFee(total);
                        mOrderLocationDataHolder.getOrderData().setTotalFee(total);
                    }
                }
            });
        }

        if (mOrderLocationDataHolder.getLaundryData() != null) {
            String laundryAddress = mOrderLocationDataHolder.getLaundryData().getAddress();
            String userSelectAddress = address;

            LatLng laundryLatLng = getLocationFromAddress(this, laundryAddress);
            LatLng userLatLng = getLocationFromAddress(this, userSelectAddress);
            if (laundryLatLng != null && userLatLng != null) {
                double dis = SphericalUtil.computeDistanceBetween(laundryLatLng, userLatLng);
                distance = dis / 1000;
                finalDistance = String.format("%.2f", distance);
                deliveryFeeAmountTextView.setText(finalDistance);
                orderData.setDeliveryFee(distance);
                mOrderLocationDataHolder.getOrderData().setDeliveryFee(distance);
            }
        }

        Button placeButton = findViewById(R.id.ola_btn_place_order);
        placeButton.setOnClickListener(view -> {
            String noteToRider = noteToRiderEditText.getText().toString();
            ProgressBar mProgressBar = findViewById(R.id.ola_progressbar);
            mProgressBar.setVisibility(View.VISIBLE);

            if (mOrderLocationDataHolder.getSelectedDate() == null) {
                mProgressBar.setVisibility(View.GONE);
                dateEditText.setError("Pick up date is required!");
                dateEditText.requestFocus();
            } else if (address == null) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(OrderLocationActivity.this, "Address info is required!", Toast.LENGTH_SHORT).show();
            } else {
                //get the address info
                Map<String, String> addressData = new HashMap<>();
                addressData.put("name", name);
                addressData.put("details", details);
                addressData.put("address", address);
                mOrderLocationDataHolder.getOrderData().setAddressInfo("AddressInfo", addressData);
                mOrderLocationDataHolder.getOrderData().setPickUpDate(mOrderLocationDataHolder.getSelectedDate());
                mOrderLocationDataHolder.getOrderData().setNoteToRider(noteToRider);

                if (noteToLaundryLRO != null) {
                    String noteToLaundry = noteToLaundryEditText.getText().toString();
                    mOrderLocationDataHolder.getOrderData().setNoteToLaundry(noteToLaundry);
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String dateTime = sdf.format(new Date());
                mOrderLocationDataHolder.getOrderData().setDateTime(dateTime);

                OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime, "Order created");

                //deduct balance checking
                double newBalance = currentBalanceUser - mOrderLocationDataHolder.getOrderData().getTotalFee();
                if (newBalance >= 0) { //enough balance
                    //confirm place order dialog
                    mProgressBar.setVisibility(View.GONE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Place Order Confirmation");
                    builder.setMessage("Your account balance will be deducted. Your order cannot be cancelled once the order has been picked up. Please provide the order ID to the rider.");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mUserViewModel.addOrder(currentUserId, mOrderLocationDataHolder.getOrderData(), mOrderStatusModel, mOrderLocationDataHolder.getOrderData().getTotalFee(), newBalance).observe(OrderLocationActivity.this, orderStatus -> {
                            if (orderStatus) {
                                Toast.makeText(OrderLocationActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                Toast.makeText(OrderLocationActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                            }
                            mProgressBar.setVisibility(View.GONE);
                        });
                        dialog.dismiss();
                        mProgressBar.setVisibility(View.GONE);
                    });
                    builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);

                } else {
                    Toast.makeText(OrderLocationActivity.this, "Insufficient balance! Please reload.", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    public LatLng getLocationFromAddress(Context context, String theAddress) {

        if (theAddress == null || theAddress.isEmpty()) {
            return null;
        }

        if (!Geocoder.isPresent()) {
            return null;
        }
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(theAddress, 5);
            if (address == null || address.isEmpty()) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    private boolean isTimeInRange(String currentTime, String timeRange) {
        try {
            String[] parts = timeRange.split(" - ");
            String startTime = parts[0];
            String endTime = parts[1];

            Date currentTimeDate = timeFormat.parse(currentTime);
            Date startTimeDate = timeFormat.parse(startTime);
            Date endTimeDate = timeFormat.parse(endTime);

            assert currentTimeDate != null;
            return currentTimeDate.after(startTimeDate) && currentTimeDate.before(endTimeDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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