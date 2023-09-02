package com.example.golaundry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.viewModel.UserViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProfileUserFragment extends Fragment {

    private LineChart lineChart;
    UserViewModel mUserViewModel;
    double monthlyTopUp;
    double monthlyTopUpAll;

    public ProfileUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @SuppressLint({"SetTextI18n", "UseSwitchCompatOrMaterialCode"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_user, container, false);

        //toolbar and back button
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.puf_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        //get current user id
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        //declare the view id
        //upper card
        TextView userNameTextView = view.findViewById(R.id.puf_tv_user_name);
        TextView membershipRateTextView = view.findViewById(R.id.puf_tv_rate);
        TextView currentMonthTextView = view.findViewById(R.id.puf_tv_month);
        TextView balanceAmountTextView = view.findViewById(R.id.puf_tv_balance_amount);
        TextView monthlyAmountTextView = view.findViewById(R.id.puf_tv_top_up_amount);
        TextView progressBarTextView = view.findViewById(R.id.puf_tv_progressBar);
        ProgressBar membershipProgressBar = view.findViewById(R.id.puf_progressBar);
        TextView messageAmountTextView = view.findViewById(R.id.puf_tv_messageamount);
        TextView messageStartTextView = view.findViewById(R.id.puf_tv_messagestart);
        TextView messageEndTextView = view.findViewById(R.id.puf_tv_messageend);

        //bottom card
        Switch notificationSwitch = view.findViewById(R.id.puf_noti_switch);
        TextView savedLaundryTextView = view.findViewById(R.id.puf_tv_saved_laundry);
        TextView myAddressTextView = view.findViewById(R.id.puf_tv_my_address);
        TextView resetPasswordTextView = view.findViewById(R.id.puf_tv_reset_password);
        TextView getHelpTextView = view.findViewById(R.id.puf_tv_get_help);
        TextView logOutTextView = view.findViewById(R.id.puf_tv_log_out);

        //show current month
        Calendar calendar = Calendar.getInstance();
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String currentMonth = monthName[calendar.get(Calendar.MONTH)];
        currentMonthTextView.setText(currentMonth);

        //get user and membership data
        mUserViewModel.getUserData(currentUserId).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                //show user info and balance
                userNameTextView.setText(user.getFullName());
                membershipRateTextView.setText(user.getMembershipRate());
                @SuppressLint("DefaultLocale")
                String balance = String.format("%.2f", user.getBalance());
                balanceAmountTextView.setText(balance);

                //show membership card data
                if (Objects.equals(user.getMembershipRate(), "None")) {

                    mUserViewModel.getAllMembershipData("GL05").observe(getViewLifecycleOwner(), allMemberships -> {
                        if (allMemberships != null) {

                            //progress bar
                            monthlyTopUpAll = allMemberships.getMonthlyTopUp();
                            int progressEndStrFinal = (int) Math.round(monthlyTopUpAll);
                            membershipProgressBar.setMax(progressEndStrFinal);
                            int progressStartStrFinal = (int) Math.round(monthlyTopUp);
                            membershipProgressBar.setProgress(progressStartStrFinal);

                            //progress bar percentage text view
                            double progressPercentage = ((double) monthlyTopUp / progressEndStrFinal) * 100;
                            int progressTv = (int) Math.round(progressPercentage);
                            String progressTvStr = String.valueOf(progressTv);
                            progressBarTextView.setText(progressTvStr + "%");

                            //left top up how much text view
                            double leftTopUpAmount = monthlyTopUpAll - monthlyTopUp;
                            @SuppressLint("DefaultLocale")
                            String leftTopUpAmountStr = String.format("%.2f", leftTopUpAmount);
                            messageAmountTextView.setText(leftTopUpAmountStr);
                            messageEndTextView.setText(" more to enjoy membership rate");
                        }
                    });

                } else if (user.getMembershipRate().equals("GL05")) { //if membership rate is GL05

                    mUserViewModel.getAllMembershipData("GL10").observe(getViewLifecycleOwner(), allMemberships -> {
                        if (allMemberships != null) {

                            //progress bar
                            monthlyTopUpAll = allMemberships.getMonthlyTopUp();
                            int progressEndStrFinal = (int) Math.round(monthlyTopUpAll);
                            membershipProgressBar.setMax(progressEndStrFinal);
                            int progressStartStrFinal = (int) Math.round(monthlyTopUp);
                            membershipProgressBar.setProgress(progressStartStrFinal);

                            //progress bar percentage text view
                            double progressPercentage = ((double) monthlyTopUp / progressEndStrFinal) * 100;
                            int progressTv = (int) Math.round(progressPercentage);
                            String progressTvStr = String.valueOf(progressTv);
                            progressBarTextView.setText(progressTvStr + "%");

                            //left top up how much text view
                            double leftTopUpAmount = monthlyTopUpAll - monthlyTopUp;
                            @SuppressLint("DefaultLocale")
                            String leftTopUpAmountStr = String.format("%.2f", leftTopUpAmount);
                            messageAmountTextView.setText(leftTopUpAmountStr);

                        }
                    });

                } else if (user.getMembershipRate().equals("GL10")) {

                    mUserViewModel.getAllMembershipData("GL20").observe(getViewLifecycleOwner(), allMemberships -> {
                        if (allMemberships != null) {

                            //progress bar
                            monthlyTopUpAll = allMemberships.getMonthlyTopUp();
                            int progressEndStrFinal = (int) Math.round(monthlyTopUpAll);
                            membershipProgressBar.setMax(progressEndStrFinal);
                            int progressStartStrFinal = (int) Math.round(monthlyTopUp);
                            membershipProgressBar.setProgress(progressStartStrFinal);

                            //progress bar percentage text view
                            double progressPercentage = ((double) monthlyTopUp / progressEndStrFinal) * 100;
                            int progressTv = (int) Math.round(progressPercentage);
                            String progressTvStr = String.valueOf(progressTv);
                            progressBarTextView.setText(progressTvStr + "%");

                            //left top up how much text view
                            double leftTopUpAmount = monthlyTopUpAll - monthlyTopUp;
                            @SuppressLint("DefaultLocale")
                            String leftTopUpAmountStr = String.format("%.2f", leftTopUpAmount);
                            messageAmountTextView.setText(leftTopUpAmountStr);

                        }
                    });

                } else if (user.getMembershipRate().equals("GL20")) {

                    mUserViewModel.getAllMembershipData("GL30").observe(getViewLifecycleOwner(), allMemberships -> {
                        if (allMemberships != null) {

                            //progress bar
                            monthlyTopUpAll = allMemberships.getMonthlyTopUp();
                            int progressEndStrFinal = (int) Math.round(monthlyTopUpAll);
                            membershipProgressBar.setMax(progressEndStrFinal);
                            int progressStartStrFinal = (int) Math.round(monthlyTopUp);
                            membershipProgressBar.setProgress(progressStartStrFinal);

                            //progress bar percentage text view
                            double progressPercentage = ((double) monthlyTopUp / progressEndStrFinal) * 100;
                            int progressTv = (int) Math.round(progressPercentage);
                            String progressTvStr = String.valueOf(progressTv);
                            progressBarTextView.setText(progressTvStr + "%");

                            //left top up how much text view
                            double leftTopUpAmount = monthlyTopUpAll - monthlyTopUp;
                            @SuppressLint("DefaultLocale")
                            String leftTopUpAmountStr = String.format("%.2f", leftTopUpAmount);
                            messageAmountTextView.setText(leftTopUpAmountStr);

                        }
                    });

                } else {

                    membershipProgressBar.setProgress(100);
                    progressBarTextView.setText("100%");
                    messageStartTextView.setText("You have reach the highest member rate");
                    messageAmountTextView.setVisibility(View.GONE);
                    messageEndTextView.setVisibility(View.GONE);

                }

                //notification switch
                notificationSwitch.setChecked(user.getNotification());

            }
        });



        notificationSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            //get latest user notification data
            AtomicBoolean latestValue = new AtomicBoolean(false);

            mUserViewModel.getUserData(currentUserId).observe(getViewLifecycleOwner(), user -> {
                boolean value = user.getNotification();
                boolean updatedValue = !value;
                latestValue.set(updatedValue);
            });

            mUserViewModel.updateNotificationData(currentUserId, latestValue.get()).observe(getViewLifecycleOwner(), notificationStatusData -> {
                if (notificationStatusData != null) {
                    assert getFragmentManager() != null;
                    getFragmentManager().beginTransaction().detach(this).attach(this).commit();
                    Toast.makeText(requireContext(), "Notification updated", Toast.LENGTH_SHORT).show();
                };

            });
        });

        //intent to top up
        view.findViewById(R.id.puf_iv_topup).setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), MembershipActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        //intent to membership activity, show all memberships
        view.findViewById(R.id.puf_cv_membership).setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), MembershipActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        //saved laundry
        view.findViewById(R.id.puf_tv_saved_laundry).setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), SavedLaundryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        //my addresses
//        view.findViewById(R.id.puf_tv_my_address).setOnClickListener(view1 -> {
//            Intent intent = new Intent(getContext(), MyAddressesActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        });

        //reset password
        view.findViewById(R.id.puf_tv_reset_password).setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), ResetPasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        //helpcenter
        view.findViewById(R.id.puf_tv_get_help).setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), HelpCenterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        //logout
        view.findViewById(R.id.puf_tv_log_out).setOnClickListener(view1 -> {
            showLogoutConfirmationDialog();
        });

        //show membership and monthly top up data
        mUserViewModel.getCurrentMembershipData(currentUserId).observe(getViewLifecycleOwner(), currentMembership -> {
            if (currentMembership != null) {
                @SuppressLint("DefaultLocale")
                String monthlyTopUpStr = String.format("%.2f", currentMembership.getMonthlyTopUp());
                monthlyAmountTextView.setText(monthlyTopUpStr);
                monthlyTopUp = currentMembership.getMonthlyTopUp();
            }
        });

        return view;
    }

    //call dialog
    private void showLogoutConfirmationDialog() {
        LogoutConfirmationDialogFragment dialogFragment = new LogoutConfirmationDialogFragment();
        dialogFragment.setTargetFragment(this, 0);
        assert getFragmentManager() != null;
        dialogFragment.show(getFragmentManager(), "logout_confirmation_dialog");
    }

    //logout user
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginFragment.class);
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