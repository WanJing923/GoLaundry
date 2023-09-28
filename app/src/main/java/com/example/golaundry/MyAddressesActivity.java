package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.golaundry.adapter.AddressAdapter;
import com.example.golaundry.adapter.MyAddressesAdapter;
import com.example.golaundry.model.AddressModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class MyAddressesActivity extends AppCompatActivity {

    UserViewModel mUserViewModel;
    String currentUserId;
    ArrayList<AddressModel> addressList;
    MyAddressesAdapter mMyAddressesAdapter;

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        Toolbar toolbar = findViewById(R.id.maa_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        RecyclerView addressRecyclerView = findViewById(R.id.maa_rv_address);
        addressList = new ArrayList<>();
        mMyAddressesAdapter = new MyAddressesAdapter(addressList, this,mUserViewModel);
        addressRecyclerView.setAdapter(mMyAddressesAdapter);
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(MyAddressesActivity.this));

        mUserViewModel.getAllAddressesForUser(currentUserId).observe(this, addresses -> {
            if (addresses != null) {
                addressList.clear();
                addressList.addAll(addresses);
                mMyAddressesAdapter.notifyDataSetChanged();
            }
        });

        ImageView addAddressButton = findViewById(R.id.maa_btn_add_new_address);
        addAddressButton.setOnClickListener(view -> {
            Intent intent = new Intent(MyAddressesActivity.this, NewAddressActivity.class);
            startActivity(intent);
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