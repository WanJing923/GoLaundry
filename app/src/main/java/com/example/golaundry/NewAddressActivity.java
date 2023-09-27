package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.golaundry.model.AddressModel;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class NewAddressActivity extends AppCompatActivity {

    UserViewModel mUserViewModel;
    String currentUserId;
    boolean defaultAddress;
    private static final int REQUEST_CODE_MAP = 8;
    String formattedAddress;


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);

        Toolbar toolbar = findViewById(R.id.nwa_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        defaultAddress = getIntent().getBooleanExtra("defaultAddress", false);

        EditText nameEditText = findViewById(R.id.nwa_et_name);
        EditText addressEditText = findViewById(R.id.nwa_et_address);
        EditText detailsEditText = findViewById(R.id.nwa_et_address_details);
        Button saveButton = findViewById(R.id.nwa_btn_save);
        addressEditText.setOnClickListener(view -> {
            Intent intent = new Intent(NewAddressActivity.this, MapsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_MAP);
        });

        OrderModel orderData = (OrderModel) getIntent().getSerializableExtra("orderData");

        saveButton.setOnClickListener(view -> {
            ProgressBar mProgressBar = findViewById(R.id.nwa_progressbar);
            mProgressBar.setVisibility(View.VISIBLE);

            String name = nameEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String details = detailsEditText.getText().toString();

            if (name.isEmpty()) {
                mProgressBar.setVisibility(View.GONE);
                nameEditText.setError("Name is required!");
                nameEditText.requestFocus();
            } else if (address.isEmpty()) {
                mProgressBar.setVisibility(View.GONE);
                addressEditText.setError("Address is required!");
                addressEditText.requestFocus();
            } else if (details.isEmpty()) {
                mProgressBar.setVisibility(View.GONE);
                detailsEditText.setError("Address details is required!");
                detailsEditText.requestFocus();
            } else {
                AddressModel mAddressModel = new AddressModel("", name, address, details, defaultAddress);
                mUserViewModel.addUserAddress(currentUserId, mAddressModel).observe(NewAddressActivity.this, addressStatus -> {
                    if (addressStatus) {
                        if (orderData != null) {
                            Intent intent = new Intent(NewAddressActivity.this, EditLocationActivity.class);
                            intent.putExtra("orderData", orderData);
                            startActivity(intent);
                            Toast.makeText(NewAddressActivity.this, "New address added", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(NewAddressActivity.this, "New address added", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(NewAddressActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                    mProgressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_MAP && data != null) {
                //get return intent
                formattedAddress = data.getStringExtra("formattedAddress");
                EditText addressEditText = findViewById(R.id.nwa_et_address);
                addressEditText.setText(formattedAddress);
            }
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