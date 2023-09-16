package com.example.golaundry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.viewModel.RiderViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ProfileRiderFragment extends Fragment {

    RiderViewModel mRiderViewModel;
    boolean notificationValue;

    public ProfileRiderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRiderViewModel = new ViewModelProvider(this).get(RiderViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_rider, container, false);

        //toolbar and back button
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.prf_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        //get current user id
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        TextView riderNameTextView = view.findViewById(R.id.prf_tv_rider_name);
        TextView ratingNumberTextView = view.findViewById(R.id.prf_tv_rating_number);
        RatingBar ratingstarRatingBar = view.findViewById(R.id.prf_rating_star);
        TextView viewRatingsTextView = view.findViewById(R.id.prf_view_ratings);
        TextView fullNameTextView = view.findViewById(R.id.prf_tv_full_name);
        TextView phoneNumberTextView = view.findViewById(R.id.prf_tv_phone_number);
        TextView icNoTextView = view.findViewById(R.id.prf_tv_ic);
        TextView emailAddressTextView = view.findViewById(R.id.prf_tv_email);
        TextView plateNumberTextView = view.findViewById(R.id.prf_tv_plate_number);
        ImageView profilePictureImageView = view.findViewById(R.id.prf_profile_image);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch notificationSwitch = view.findViewById(R.id.prf_switch_notification);
        TextView myWalletTextView = view.findViewById(R.id.prf_tv_my_wallet);
        TextView resetPasswordTextView = view.findViewById(R.id.prf_tv_reset_password);
        TextView getHelpTextView = view.findViewById(R.id.prf_tv_get_help);
        TextView logOutTextView = view.findViewById(R.id.prf_tv_log_out);

        //get laundry data
        mRiderViewModel.getRiderData(currentUserId).observe(getViewLifecycleOwner(), rider -> {
            if (rider != null) {
                riderNameTextView.setText(rider.getFullName());
                fullNameTextView.setText(rider.getFullName());
                phoneNumberTextView.setText(rider.getContactNo());
                icNoTextView.setText(rider.getIcNo());
                emailAddressTextView.setText(rider.getEmailAddress());
                plateNumberTextView.setText(rider.getPlateNumber());

                //notification switch
                notificationSwitch.setChecked(rider.getNotification());
                notificationValue = rider.getNotification();

                //show image
                String facePhotoUrl = rider.getFacePhoto();
                if (!Objects.equals(facePhotoUrl, "")) {
                    setAvatar(facePhotoUrl, profilePictureImageView);
                }
            }
        });

        //switch turn on or off
        notificationSwitch.setOnClickListener(view1 -> {
            boolean updatedValue = !notificationValue;

            //update notification data
            mRiderViewModel.updateNotificationData(currentUserId, updatedValue).observe(getViewLifecycleOwner(), notificationStatusData -> {
                if (notificationStatusData != null && notificationStatusData) {
                    notificationSwitch.setChecked(updatedValue);
                    Toast.makeText(requireContext(), "Notification updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Notification update failed!", Toast.LENGTH_SHORT).show();
                }
                ;
            });
        });

        //intent to wallet
        myWalletTextView.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), WalletActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        //intent to reset password
        resetPasswordTextView.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), ResetPasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        //intent to help center
        getHelpTextView.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), HelpCenterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        //logout
        logOutTextView.setOnClickListener(view1 -> {
            showLogoutConfirmationDialog();
        });

        return view;
    }

    private void setAvatar(String facePhotoUrl, ImageView profilePictureImageView) {
        //referenceFromUrl to get StorageReference
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(facePhotoUrl);

        try {
            File localFile = File.createTempFile("tempfile", ".jpg");

            mStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                //show
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                profilePictureImageView.setImageBitmap(bitmap);

            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to retrieve image", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //call dialog
    private void showLogoutConfirmationDialog() {
        LogoutConfirmationDialogFragmentRider dialogFragment = new LogoutConfirmationDialogFragmentRider();
        dialogFragment.setTargetFragment(this, 0);
        assert getFragmentManager() != null;
        dialogFragment.show(getFragmentManager(), "logout_confirmation_dialog");
    }

    //logout user
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginFragment.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //first clear current all the menu items
        menu.clear();
        //add the new menu items
        inflater.inflate(R.menu.menu_top, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //intent to notification
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