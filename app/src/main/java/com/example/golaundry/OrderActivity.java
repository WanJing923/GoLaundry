package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golaundry.adapter.UserOrderLaundryServicesAdapter;
import com.example.golaundry.holder.OrderServicesHolder;
import com.example.golaundry.model.LaundryServiceModel;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.SaveLaundryViewModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class OrderActivity extends AppCompatActivity {

    LaundryViewModel mLaundryViewModel;
    SaveLaundryViewModel mSaveLaundryViewModel;
    String currentUserId;
    boolean isSavedLaundry;
    ArrayList<LaundryServiceModel> laundryServiceList;
    UserOrderLaundryServicesAdapter mUserOrderLaundryServicesAdapter;
    String laundryId, note;
    OrderModel mOrderModel;
    double distance,deliveryFee;
    double totalLaundryFee;
    UserViewModel mUserViewModel;
    String membershipRate;

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mSaveLaundryViewModel = new ViewModelProvider(this).get(SaveLaundryViewModel.class);
        laundryId = getIntent().getStringExtra("laundryId");
        distance = getIntent().getDoubleExtra("distance", 0.0);

        Toolbar toolbar = findViewById(R.id.oa_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        TextView laundryShopNameTextView = findViewById(R.id.ol_tv_laundry_name);
        TextView ratingsTextView = findViewById(R.id.ol_tv_rating);
        RatingBar ratingsRatingBar = findViewById(R.id.ol_rb_star);
        ImageView savedLaundryImageView = findViewById(R.id.ol_iv_save);
        ImageView laundryImageView = findViewById(R.id.ol_iv_image);
        TextView locationTextView = findViewById(R.id.ol_tv_location);
        TextView phoneNoTextView = findViewById(R.id.ol_tv_phone);
        TextView mondayTextView = findViewById(R.id.ol_tv_monday_time);
        TextView tuesdayTextView = findViewById(R.id.ol_tv_tuesday_time);
        TextView wednesdayTextView = findViewById(R.id.ol_tv_wednesday_time);
        TextView thursdayTextView = findViewById(R.id.ol_tv_thursday_time);
        TextView fridayTextView = findViewById(R.id.ol_tv_friday_time);
        TextView saturdayTextView = findViewById(R.id.ol_tv_saturday_time);
        TextView sundayTextView = findViewById(R.id.ol_tv_sunday_time);

        //get laundry data
        mLaundryViewModel.getLaundryData(laundryId).observe(OrderActivity.this, laundry -> {
            if (laundry != null) {
                laundryShopNameTextView.setText(laundry.getShopName());
                ratingsTextView.setText("0");
                ratingsRatingBar.setRating(0);
                String address = laundry.getAddressDetails() + ", " + laundry.getAddress();
                locationTextView.setText(address);
                phoneNoTextView.setText(laundry.getPhoneNo());
            }
        });

        mLaundryViewModel.getShopData(laundryId).observe(this, shop -> {
            if (shop != null) {
                List<String> allTimeRanges = shop.getAllTimeRanges();
                for (int i = 0; i < allTimeRanges.size(); i++) {
                    String timeRange = allTimeRanges.get(i);
                    switch (i) {
                        case 0:
                            mondayTextView.setText(timeRange);
                            break;
                        case 1:
                            tuesdayTextView.setText(timeRange);
                            break;
                        case 2:
                            wednesdayTextView.setText(timeRange);
                            break;
                        case 3:
                            thursdayTextView.setText(timeRange);
                            break;
                        case 4:
                            fridayTextView.setText(timeRange);
                            break;
                        case 5:
                            saturdayTextView.setText(timeRange);
                            break;
                        case 6:
                            sundayTextView.setText(timeRange);
                            break;
                    }
                }
                //show image
                String imageUrl = shop.getImages();
                if (!Objects.equals(imageUrl, "")) {
                    setImages(imageUrl, laundryImageView);
                }
            }
        });

        isSaveLaundry(laundryId, savedLaundryImageView);
        //save and unsaved laundry shop
        savedLaundryImageView.setOnClickListener(v -> saveLaundryShop(laundryId, savedLaundryImageView));

        //show services
        RecyclerView servicesRecyclerView = findViewById(R.id.ao_laundry_service);
        laundryServiceList = new ArrayList<>();
        mUserOrderLaundryServicesAdapter = new UserOrderLaundryServicesAdapter(laundryServiceList, this);
        mLaundryViewModel.getServiceData(laundryId).observe(OrderActivity.this, services -> {
            if (services != null) {
                laundryServiceList.clear();
                laundryServiceList.addAll(services);
                mUserOrderLaundryServicesAdapter.notifyDataSetChanged();
            }
        });
        servicesRecyclerView.setAdapter(mUserOrderLaundryServicesAdapter);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(OrderActivity.this));

        //extra note
        EditText noteEditText = findViewById(R.id.os_et_notes);
        note = noteEditText.getText().toString();

        Button nextButton = findViewById(R.id.order_btn_next);
        nextButton.setOnClickListener(view -> {
            Map<String, Integer> selectedServices = mUserOrderLaundryServicesAdapter.getSelectedServices();
            if (selectedServices != null ){
                createOrderModel();
                Intent intent = new Intent(OrderActivity.this, OrderLocationActivity.class);
                intent.putExtra("orderData", mOrderModel);
                startActivity(intent);
                finish();
            }
        });

        mUserViewModel.getUserData(currentUserId).observe(this, user -> {
            if (user != null) {
                membershipRate = user.getMembershipRate();
            }
        });

        deliveryFee = distance * 0.5;
    }

    public void createOrderModel() {
        Map<String, Integer> selectedServices = mUserOrderLaundryServicesAdapter.getSelectedServices();
        Map<String, String> addressInfo = new HashMap<>();

        //calculate laundry fees
        Map<String, OrderServicesHolder> servicesInfo = mUserOrderLaundryServicesAdapter.getServicesInfo();
        totalLaundryFee = 0.0;
        for (String serviceName : servicesInfo.keySet()) {
            OrderServicesHolder orderServicesHolder = servicesInfo.get(serviceName);
            assert orderServicesHolder != null;
            double price = orderServicesHolder.getPrice();
            int userQty = orderServicesHolder.getUserSelected();
            double totalPrice = price * userQty;
            totalLaundryFee += totalPrice;
        }

        if (note.isEmpty()) {
            note = "";
        }

        mOrderModel = new OrderModel(laundryId, currentUserId, selectedServices, note, "None", addressInfo, "", "Order created", totalLaundryFee, membershipRate, deliveryFee, 0, "","");
    }


    @SuppressLint("NotifyDataSetChanged")
    public void saveLaundryShop(String laundryId, ImageView savedImageView) { //have laundry id and user id, add laundry id to that table
        if (isSavedLaundry) {
            mSaveLaundryViewModel.saveLaundryRemove(currentUserId, laundryId).observe(OrderActivity.this, unsavedLaundryStatus -> {
                if (unsavedLaundryStatus != null && unsavedLaundryStatus) {
                    isSavedLaundry = false;
                    Toast.makeText(OrderActivity.this, "Removed saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love_grey);
                } else {
                    Toast.makeText(OrderActivity.this, "Fail to removed saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love);
                }
            });
        } else {
            mSaveLaundryViewModel.saveLaundryAdd(currentUserId, laundryId).observe(OrderActivity.this, saveLaundryStatus -> {
                if (saveLaundryStatus != null && saveLaundryStatus) {
                    isSavedLaundry = true;
                    Toast.makeText(OrderActivity.this, "Saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love);
                } else {
                    Toast.makeText(OrderActivity.this, "Fail to saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love_grey);
                }
            });
        }
    }

    private void isSaveLaundry(String laundryId, ImageView savedLaundryImageView) {
        mSaveLaundryViewModel.isSavedLaundry(laundryId, currentUserId).observe(OrderActivity.this, isSavedResult -> {
            if (isSavedResult != null && isSavedResult) {
                isSavedLaundry = true;
                savedLaundryImageView.setImageResource(R.drawable.ic_love);
            } else {
                isSavedLaundry = false;
                savedLaundryImageView.setImageResource(R.drawable.ic_love_grey);
            }
        });
    }

    private void setImages(String imageUrl, ImageView LaundryPictureImageView) {
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            mStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                //show
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                LaundryPictureImageView.setImageBitmap(bitmap);
            }).addOnFailureListener(e ->
                    Toast.makeText(OrderActivity.this, "Failed to retrieve image", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            e.printStackTrace();
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