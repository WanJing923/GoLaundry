package com.example.golaundry;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
import java.util.List;
import java.util.Objects;

public class LaundryEditServicesActivity extends AppCompatActivity {

    String currentUserId;
    LaundryViewModel mLaundryViewModel;
    ArrayList<LaundryServiceModel> serviceList;
    ServiceAdapter mServiceAdapter;
    RecyclerView servicesRecyclerView;
    EditText serviceNameEditText, serviceDescriptionEditText, servicePriceEditText, servicePriceForEachEditText;

    @SuppressLint("UseCompatLoadingForDrawables")
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

        // Initialize the serviceList
        serviceList = new ArrayList<>();

        addImageView.setOnClickListener(v -> {
            String name = serviceNameEditText.getText().toString();
            String description = serviceDescriptionEditText.getText().toString();

            // Convert the price and priceForEach strings to their respective numeric types
            String priceText = servicePriceEditText.getText().toString();
            double price = Double.parseDouble(priceText);

            String priceForEachText = servicePriceForEachEditText.getText().toString();
            int priceForEach = Integer.parseInt(priceForEachText);

            LaundryServiceModel service = new LaundryServiceModel(name, description, price, priceForEach);

            addItem(service);

            // Clear the input fields
            serviceNameEditText.setText("");
            serviceDescriptionEditText.setText("");
            servicePriceEditText.setText("");
            servicePriceForEachEditText.setText("");
        });

        servicesRecyclerView = findViewById(R.id.als_rv_service);
        mServiceAdapter = new ServiceAdapter(serviceList);
        servicesRecyclerView.setAdapter(mServiceAdapter);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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