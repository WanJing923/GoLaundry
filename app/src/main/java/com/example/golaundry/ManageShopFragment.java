package com.example.golaundry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golaundry.adapter.ServiceAdapter;
import com.example.golaundry.adapter.ShowServiceAdapter;
import com.example.golaundry.model.LaundryServiceModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManageShopFragment extends Fragment {

    LaundryViewModel mLaundryViewModel;
    String currentUserId;
    List<String> allTimeRanges;
    ArrayList<LaundryServiceModel> laundryServiceList;
    ShowServiceAdapter mShowServiceAdapter;
    RecyclerView servicesRecyclerView;
    String imageUrl;
    ImageView laundryImagesImageView;

    public ManageShopFragment() {
        // Required empty public constructor
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        //initialize
        laundryServiceList = new ArrayList<>();
        mShowServiceAdapter = new ShowServiceAdapter(laundryServiceList, getContext());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_shop, container, false);

        //id before setup
        TextView msgTextView = view.findViewById(R.id.fms_tv_msg);
        CardView beforeSetupCardView = view.findViewById(R.id.fms_card_shop_info);
        TextView locationTextView = view.findViewById(R.id.fms_tv_location);
        TextView phoneTextView = view.findViewById(R.id.fms_tv_phone_number);
        ImageView editImageView = view.findViewById(R.id.fms_iv_edit);

        //card after setup
        CardView afterSetupCardView = view.findViewById(R.id.fms_cv_show_laundry_details);
        laundryImagesImageView = view.findViewById(R.id.fms_iv_image);
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
        servicesRecyclerView = view.findViewById(R.id.fms_rv_service);
        RelativeLayout manageServicesLayout = view.findViewById(R.id.fms_manage_services_layout);

        mLaundryViewModel.getServiceData(currentUserId).observe(getViewLifecycleOwner(), services -> {
            if (services != null) {
                laundryServiceList.clear();
                laundryServiceList.addAll(services);
                mShowServiceAdapter.notifyDataSetChanged();
            }
        });

        servicesRecyclerView.setAdapter(mShowServiceAdapter);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //get laundry data
        mLaundryViewModel.getLaundryData(currentUserId).observe(getViewLifecycleOwner(), laundry -> {
            if (laundry != null) {
                if (!laundry.getSetup()) {
                    //show item that before laundry setup, hide others
                    beforeSetupCardView.setVisibility(View.VISIBLE);
                    msgTextView.setVisibility(View.VISIBLE);
                    afterSetupCardView.setVisibility(View.INVISIBLE);
                    manageServicesLayout.setVisibility(View.INVISIBLE);

                    String address = laundry.getAddressDetails() + ", " + laundry.getAddress();
                    locationTextView.setText(address);
                    phoneTextView.setText(laundry.getPhoneNo());

                } else {
                    beforeSetupCardView.setVisibility(View.INVISIBLE);
                    msgTextView.setVisibility(View.INVISIBLE);
                    afterSetupCardView.setVisibility(View.VISIBLE);
                    manageServicesLayout.setVisibility(View.VISIBLE);

                    String address = laundry.getAddressDetails() + ", " + laundry.getAddress();
                    addressTextView.setText(address);
                    phoneNoTextView.setText(laundry.getPhoneNo());
                }
            }
        });

        mLaundryViewModel.getShopData(currentUserId).observe(getViewLifecycleOwner(), shop -> {
            if (shop != null) {
                allTimeRanges = shop.getAllTimeRanges();
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
                imageUrl = shop.getImages();
                if (!Objects.equals(imageUrl, "")) {
                    setImages(imageUrl, laundryImagesImageView);
                }
            }
        });

        //intent to edit info
        editImageView.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), LaundryEditInfoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        //intent to edit services
        editServicesImageView.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), LaundryEditServicesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        return view;
    }

    private void setImages(String imageUrl, ImageView laundryImagesImageView) {
        //referenceFromUrl to get StorageReference
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        try {
            File localFile = File.createTempFile("tempfile", ".jpg");

            mStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                //show
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                laundryImagesImageView.setImageBitmap(bitmap);

            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to retrieve image", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // First clear current all the menu items
        menu.clear();
        inflater.inflate(R.menu.menu_top, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.tm_btn_notification) {
            //intent notification
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}