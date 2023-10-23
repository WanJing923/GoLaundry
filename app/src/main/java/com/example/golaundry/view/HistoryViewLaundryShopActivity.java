package com.example.golaundry.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golaundry.R;
import com.example.golaundry.adapter.UserOrderLaundryServicesAdapter;
import com.example.golaundry.model.LaundryModel;
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
import java.util.List;
import java.util.Objects;

public class HistoryViewLaundryShopActivity extends AppCompatActivity {
    LaundryViewModel mLaundryViewModel;
    SaveLaundryViewModel mSaveLaundryViewModel;
    String currentUserId;
    boolean isSavedLaundry;
    String laundryId;
    UserViewModel mUserViewModel;
    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view_laundry_shop);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mSaveLaundryViewModel = new ViewModelProvider(this).get(SaveLaundryViewModel.class);
        laundryId = getIntent().getStringExtra("laundryId");

        Toolbar toolbar = findViewById(R.id.hvlsa_toolbar);
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

        if (laundryId!=null){
            mLaundryViewModel.getLaundryData(laundryId).observe(this, laundry -> {
                if (laundry != null) {
                    laundryShopNameTextView.setText(laundry.getShopName());
                    ratingsTextView.setText(String.format("%.2f", laundry.getRatingsAverage()));
                    ratingsRatingBar.setRating(laundry.getRatingsAverage());
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
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void saveLaundryShop(String laundryId, ImageView savedImageView) { //have laundry id and user id, add laundry id to that table
        if (isSavedLaundry) {
            mSaveLaundryViewModel.saveLaundryRemove(currentUserId, laundryId).observe(this, unsavedLaundryStatus -> {
                if (unsavedLaundryStatus != null && unsavedLaundryStatus) {
                    isSavedLaundry = false;
                    Toast.makeText(this, "Removed saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love_grey);
                } else {
                    Toast.makeText(this, "Fail to removed saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love);
                }
            });
        } else {
            mSaveLaundryViewModel.saveLaundryAdd(currentUserId, laundryId).observe(this, saveLaundryStatus -> {
                if (saveLaundryStatus != null && saveLaundryStatus) {
                    isSavedLaundry = true;
                    Toast.makeText(this, "Saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love);
                } else {
                    Toast.makeText(this, "Fail to saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love_grey);
                }
            });
        }
    }

    private void isSaveLaundry(String laundryId, ImageView savedLaundryImageView) {
        mSaveLaundryViewModel.isSavedLaundry(laundryId, currentUserId).observe(this, isSavedResult -> {
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
                    Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show());
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