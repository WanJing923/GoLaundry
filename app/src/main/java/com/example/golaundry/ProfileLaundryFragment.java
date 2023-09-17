package com.example.golaundry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golaundry.viewModel.LaundryViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ProfileLaundryFragment extends Fragment {

    LaundryViewModel mLaundryViewModel;
    boolean notificationValue;
    boolean laundryIsBreak;

    public ProfileLaundryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_laundry, container, false);

        //toolbar and back button
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.plf_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        //get current user id
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        TextView shopNameTextView = view.findViewById(R.id.plf_tv_laundry_name);
        TextView ratingNumberTextView = view.findViewById(R.id.plf_tv_rating_number);
        RatingBar ratingstarRatingBar = view.findViewById(R.id.plf_rating_star);
        TextView viewRatingsTextView = view.findViewById(R.id.plf_view_ratings);
        TextView fullNameTextView = view.findViewById(R.id.plf_tv_full_name);
        TextView phoneNumberTextView = view.findViewById(R.id.plf_tv_phone_number);
        TextView icNoTextView = view.findViewById(R.id.plf_tv_ic_no);
        TextView emailAddressTextView = view.findViewById(R.id.plf_tv_email);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch notificationSwitch = view.findViewById(R.id.plf_switch_notification);
        TextView takeBreakTextView = view.findViewById(R.id.plf_tv_take_break);
        TextView myWalletTextView = view.findViewById(R.id.plf_tv_my_wallet);
        TextView resetPasswordTextView = view.findViewById(R.id.plf_tv_reset_password);
        TextView getHelpTextView = view.findViewById(R.id.plf_tv_get_help);
        TextView logOutTextView = view.findViewById(R.id.plf_tv_log_out);

        //get laundry data
        mLaundryViewModel.getLaundryData(currentUserId).observe(getViewLifecycleOwner(), laundry -> {
            if (laundry != null) {
                shopNameTextView.setText(laundry.getShopName());
                fullNameTextView.setText(laundry.getFullName());
                phoneNumberTextView.setText(laundry.getPhoneNo());
                icNoTextView.setText(laundry.getIcNo());
                emailAddressTextView.setText(laundry.getEmailAddress());

                //notification switch
                notificationSwitch.setChecked(laundry.getNotification());
                notificationValue = laundry.getNotification();

                if (!laundry.getIsBreak()){
                    takeBreakTextView.setText("Take a break");
                } else {
                    takeBreakTextView.setText("End break");
                }
                laundryIsBreak = laundry.getIsBreak();

            }
        });

        //switch turn on or off
        notificationSwitch.setOnClickListener(view1 -> {
            boolean updatedValue = !notificationValue;
            //update notification data
            mLaundryViewModel.updateNotificationData(currentUserId, updatedValue).observe(getViewLifecycleOwner(), notificationStatusData -> {
                if (notificationStatusData != null && notificationStatusData) {
                    notificationSwitch.setChecked(updatedValue);
                    Toast.makeText(requireContext(), "Notification updated", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(requireContext(), "Notification update failed!", Toast.LENGTH_SHORT).show();
                };
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

        takeBreakTextView.setOnClickListener(view1 -> {
            if (laundryIsBreak){
                LaundryEndBreakDialog dialogFragment = new LaundryEndBreakDialog();
                dialogFragment.setTargetFragment(this, 0);
                assert getFragmentManager() != null;
                dialogFragment.show(getFragmentManager(), "break_end_dialog");
            } else {
                LaundryTakeBreakDialog dialogFragment = new LaundryTakeBreakDialog();
                dialogFragment.setTargetFragment(this, 0);
                assert getFragmentManager() != null;
                dialogFragment.show(getFragmentManager(), "break_confirmation_dialog");
            }
        });

        return view;
    }

    //call dialog
    private void showLogoutConfirmationDialog() {
        LogoutConfirmationDialogFragmentLaundry dialogFragment = new LogoutConfirmationDialogFragmentLaundry();
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