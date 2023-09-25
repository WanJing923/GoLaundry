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

import com.example.golaundry.model.UserAddressModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class OrderLocationActivity extends AppCompatActivity {

    private UserViewModel mUserViewModel;
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