package com.example.golaundry.view;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golaundry.R;
import com.example.golaundry.viewModel.RiderViewModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class RiderInfoActivity extends AppCompatActivity {

    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_info);
        Toolbar toolbar = findViewById(R.id.ria_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));
        RiderViewModel mRiderViewModel = new ViewModelProvider(this).get(RiderViewModel.class);

        ImageView profilePicImageView = findViewById(R.id.ria_iv_profile);
        TextView riderNameTextView = findViewById(R.id.ria_tv_name);
        TextView phoneNumberTextView = findViewById(R.id.ria_tv_phone_number);
        TextView plateNumberTextView = findViewById(R.id.ria_tv_plate_number);
        TextView ratingsNumberTextView = findViewById(R.id.ria_tv_rating_number);
        RatingBar ratingsBar = findViewById(R.id.ria_rating_star);

        Intent intent = getIntent();
        if (intent.hasExtra("riderId")) {
            String riderId = intent.getStringExtra("riderId");
            mRiderViewModel.getRiderData(riderId).observe(this, riderModel -> {
                if (riderModel != null) {
                    riderNameTextView.setText(riderModel.getFullName());
                    phoneNumberTextView.setText(riderModel.getContactNo());
                    plateNumberTextView.setText(riderModel.getPlateNumber());
                    ratingsNumberTextView.setText(String.format("%.2f", riderModel.getRatingsAverage()));
                    ratingsBar.setRating(riderModel.getRatingsAverage());

                    String facePhotoUrl = riderModel.getFacePhoto();
                    if (!Objects.equals(facePhotoUrl, "")) {
                        setAvatar(facePhotoUrl, profilePicImageView);
                    }
                }
            });
        }
    }

    private void setAvatar(String facePhotoUrl, ImageView profilePicImageView) {
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(facePhotoUrl);
        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            mStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                profilePicImageView.setImageBitmap(bitmap);
            }).addOnFailureListener(e -> {
            });
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