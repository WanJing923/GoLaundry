package com.example.golaundry.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.R;
import com.example.golaundry.adapter.ServiceAdapter;
import com.example.golaundry.model.LaundryServiceModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class LaundryEditServicesActivity extends AppCompatActivity {

    String currentUserId;
    LaundryViewModel mLaundryViewModel;
    ArrayList<LaundryServiceModel> serviceList;
    ServiceAdapter mServiceAdapter;
    RecyclerView servicesRecyclerView;
    EditText serviceNameEditText, serviceDescriptionEditText, servicePriceEditText, servicePriceForEachEditText;
    boolean laundryIsSetup;

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_edit_services);
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Toolbar toolbar = findViewById(R.id.als_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left));

        serviceNameEditText = findViewById(R.id.als_service_et_name);
        serviceDescriptionEditText = findViewById(R.id.als_service_et_desc);
        servicePriceEditText = findViewById(R.id.als_service_et_price);
        servicePriceForEachEditText = findViewById(R.id.als_service_et_price_for_each);

        ImageView doneImageView = findViewById(R.id.als_iv_done);
        ImageView addImageView = findViewById(R.id.als_iv_add);

        //initialize the serviceList
        serviceList = new ArrayList<>();

        mLaundryViewModel.getServiceData(currentUserId).observe(LaundryEditServicesActivity.this, services -> {
            if (services != null) {
                serviceList.clear();
                serviceList.addAll(services);
                mServiceAdapter.notifyDataSetChanged();
            }
        });

        addImageView.setOnClickListener(v -> {
            String name = serviceNameEditText.getText().toString();
            String description = serviceDescriptionEditText.getText().toString();
            String priceText = servicePriceEditText.getText().toString();
            String priceForEachText = servicePriceForEachEditText.getText().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) ||
                    TextUtils.isEmpty(priceText) || TextUtils.isEmpty(priceForEachText)) {
                Toast.makeText(this, "Please enter all information", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    double price = Double.parseDouble(priceText);

                    LaundryServiceModel service = new LaundryServiceModel(name, description, price, priceForEachText);
                    addItem(service);

                    //clear
                    serviceNameEditText.setText("");
                    serviceDescriptionEditText.setText("");
                    servicePriceEditText.setText("");
                    servicePriceForEachEditText.setText("");

                    // refresh recycler view
                    mServiceAdapter.notifyDataSetChanged();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid numeric input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        servicesRecyclerView = findViewById(R.id.als_rv_service);
        mServiceAdapter = new ServiceAdapter(serviceList, this);
        servicesRecyclerView.setAdapter(mServiceAdapter);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mLaundryViewModel.getLaundryData(currentUserId).observe(this, laundry -> {
            if (laundry != null) {
                laundryIsSetup = laundry.getSetup();
            }
        });

        doneImageView.setOnClickListener(v -> {
            if (serviceList.isEmpty()) {
                Toast.makeText(this, "Service list is empty. Add services before uploading.", Toast.LENGTH_SHORT).show();
            } else {
                mLaundryViewModel.uploadServiceData(currentUserId, serviceList).observe(LaundryEditServicesActivity.this, uploadServiceStatus -> {
                    if (uploadServiceStatus) {
                        Toast.makeText(this, "Shop services updated", Toast.LENGTH_SHORT).show();
                        if (laundryIsSetup) {
                            finish();
                        } else {
                            //already check laundry image, opening hrs, and services not null, so just update db setup to true
                            mLaundryViewModel.updateSetupData(currentUserId, true).observe(this, updateSetupStatus -> {

                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setTitle("Thank you for joining us!")
                                        .setMessage("Your laundry shop account has been setup and successfully uploaded to be discovered by the customers.");

                                SpannableString spannableString = new SpannableString("OK");
                                spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableString.length(), 0);

                                builder.setPositiveButton(spannableString, (dialog, which) -> {
                                    dialog.dismiss();
                                    //intent to home activity
                                    Intent intent = new Intent(this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }).show();

                            });
                        }
                    } else {
                        Toast.makeText(this, "Shop opening hours update failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addItem(LaundryServiceModel service) {
        serviceList.add(service);
        mServiceAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}