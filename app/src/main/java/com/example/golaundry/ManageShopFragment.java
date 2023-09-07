package com.example.golaundry;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.golaundry.viewModel.LaundryViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ManageShopFragment extends Fragment {

    LaundryViewModel mLaundryViewModel;

    public ManageShopFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_shop, container, false);

        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        TextView locationTextView = view.findViewById(R.id.ams_tv_location);
        TextView phoneTextView = view.findViewById(R.id.ams_tv_phone_number);
        ImageView editImageView = view.findViewById(R.id.ams_iv_edit);

        //get laundry data
        mLaundryViewModel.getLaundryData(currentUserId).observe(getViewLifecycleOwner(), laundry -> {
            if (laundry != null) {
                String address = laundry.getAddressDetails() + ", " + laundry.getAddress();
                locationTextView.setText(address);
                phoneTextView.setText(laundry.getPhoneNo());

            }
        });

        //intent to manage shop
        editImageView.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), EditLocationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        return view;
    }
}