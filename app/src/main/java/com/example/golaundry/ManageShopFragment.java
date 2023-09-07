package com.example.golaundry;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

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

        //id before setup
        TextView msgTextView = view.findViewById(R.id.fms_tv_msg);
        CardView beforeSetupCardView = view.findViewById(R.id.fms_card_shop_info);
        TextView locationTextView = view.findViewById(R.id.fms_tv_location);
        TextView phoneTextView = view.findViewById(R.id.fms_tv_phone_number);
        ImageView editImageView = view.findViewById(R.id.fms_iv_edit);

        //card after setup
        CardView afterSetupCardView = view.findViewById(R.id.fms_cv_show_laundry_details);
        ImageView laundryImagesImageView = view.findViewById(R.id.fms_iv_image);
        TextView addressTextView = view.findViewById(R.id.fms_tv_address);
        TextView phoneNoTextView = view.findViewById(R.id.fms_tv_phone);
        TextView mondayTextView = view.findViewById(R.id.fms_tv_monday_time);
        TextView tuesdayTextView = view.findViewById(R.id.fms_tv_tuesday_time);
        TextView wednesdayTextView = view.findViewById(R.id.fms_tv_wednesday_time);
        TextView thursdayTextView = view.findViewById(R.id.fms_tv_thursday_time);
        TextView fridayTextView = view.findViewById(R.id.fms_tv_friday_time);
        TextView saturdayTextView = view.findViewById(R.id.fms_tv_saturday_time);
        TextView sundayTextView = view.findViewById(R.id.fms_tv_sunday_time);

        //manage services
        ImageView editServicesImageView = view.findViewById(R.id.fms_iv_edit_services);
        RecyclerView servicesRecyclerView = view.findViewById(R.id.fms_rv_service);

        //get laundry data
        mLaundryViewModel.getLaundryData(currentUserId).observe(getViewLifecycleOwner(), laundry -> {
            if (laundry != null) {
                String address = laundry.getAddressDetails() + ", " + laundry.getAddress();
                locationTextView.setText(address);
                phoneTextView.setText(laundry.getPhoneNo());

            }
        });

        //intent to edit info
        editImageView.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), LaundryEditInfoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        return view;
    }
}