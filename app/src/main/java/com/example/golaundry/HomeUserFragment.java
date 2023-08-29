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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.viewModel.UserViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Objects;

public class HomeUserFragment<membershipRate> extends Fragment {
    private LineChart lineChart;
    String membershipRate;
    UserViewModel mUserViewModel;
    String monthlyTopUp;
    private View view;

    public HomeUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_user, container, false);
        //toolbar and back button
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fhu_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        //get current user id
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        //declare the view id
        TextView userNameTextView = view.findViewById(R.id.fhu_tv_name);
        TextView membershipRateTextView = view.findViewById(R.id.fhu_tv_rate);
        TextView currentMonthTextView = view.findViewById(R.id.fhu_tv_month);
        TextView balanceAmountTextView = view.findViewById(R.id.fhu_tv_balance_amount);
        TextView monthlyAmountTextView = view.findViewById(R.id.fhu_tv_monthly_amount);
        TextView progressBarTextView = view.findViewById(R.id.fhu_tv_progress);
        ProgressBar membershipProgressBar = view.findViewById(R.id.fhu_progressBar);
        TextView messageAmountTextView = view.findViewById(R.id.fhu_tv_messageamount);
        TextView messageStartTextView = view.findViewById(R.id.fhu_tv_messagestart);
        TextView messageEndTextView = view.findViewById(R.id.fhu_tv_messageend);

        //get user data
        mUserViewModel.getUserData(currentUserId).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                userNameTextView.setText(user.getFullName());
                membershipRateTextView.setText(user.getMembershipRate());
                balanceAmountTextView.setText(user.getBalance());

                if (Objects.equals(user.getMembershipRate(), "None")) {
                    membershipProgressBar.setProgress(0);
                    progressBarTextView.setText("0%");
                    messageAmountTextView.setText("5.00");
                    messageEndTextView.setText(" to enjoy membership rate");

                } else if (user.getMembershipRate().equals("GL05") || user.getMembershipRate().equals("GL10") || user.getMembershipRate().equals("GL20")) {
//                    membershipProgressBar.setProgress(100);
//                    progressBarTextView.setText("100%");
//                    messageStartTextView.setText("You have reach the highest member rate");
//                    messageAmountTextView.setVisibility(View.GONE);
//                    messageEndTextView.setVisibility(View.GONE);

//                    getAllMemberships(membershipRate)
                    mUserViewModel.getAllMembershipData(user.getMembershipRate()).observe(getViewLifecycleOwner(), allMemberships -> {
                        if (allMemberships != null) {
//                              int discountAll = allMemberships.getDiscount();
                            double monthlyTopUpAll = allMemberships.getMonthlyTopUp();
                            double monthlyTopUpCurrent = Double.parseDouble(monthlyTopUp);

//                                            TextView messageAmountTextView = view.findViewById(R.id.fhu_tv_messageamount);
//                                            TextView messageStartTextView = view.findViewById(R.id.fhu_tv_messagestart);
//                                            TextView messageEndTextView = view.findViewById(R.id.fhu_tv_messageend);

//                if (monthlyTopUpCurrent >= monthlyTopUpAll){
//                    messageStartTextView.setText("You have reach the highest member rate!");
//                    messageAmountTextView.setVisibility(View.GONE);
//                    messageEndTextView.setVisibility(View.GONE);
//                }

                            double leftTopUpAmount = monthlyTopUpAll - monthlyTopUpCurrent;
                            String leftTopUpAmountString = String.valueOf(leftTopUpAmount);
                            messageAmountTextView.setText(leftTopUpAmountString);

                        }
                    });

                } else {
                    membershipProgressBar.setProgress(100);
                    progressBarTextView.setText("100%");
                    messageStartTextView.setText("You have reach the highest member rate");
                    messageAmountTextView.setVisibility(View.GONE);
                    messageEndTextView.setVisibility(View.GONE);
                }
            }
        });

        //show current month
        Calendar calendar = Calendar.getInstance();
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String currentMonth = monthName[calendar.get(Calendar.MONTH)];
        currentMonthTextView.setText(currentMonth);

        //show membership and balance info
        mUserViewModel.getCurrentMembershipData(currentUserId).observe(getViewLifecycleOwner(), currentMembership -> {
            if (currentMembership != null) {
                monthlyAmountTextView.setText(currentMembership.getMonthlyTopUp());
                monthlyTopUp = currentMembership.getMonthlyTopUp();
            }
        });

//        BarChart barChart = view.findViewById(R.id.fhl_order_chart);
//        ArrayList<BarEntry> barEntries = new ArrayList<>();
//        barEntries.add(new BarEntry(0f, 44f));
//        barEntries.add(new BarEntry(1f, 88f));
//        barEntries.add(new BarEntry(2f, 41f));
//        barEntries.add(new BarEntry(3f, 85f));
//        barEntries.add(new BarEntry(4f, 96f));
//        barEntries.add(new BarEntry(5f, 25f));
//        barEntries.add(new BarEntry(6f, 10f));
//        BarDataSet barDataSet = new BarDataSet(barEntries, "Dates");
//        ArrayList<String> theDates = new ArrayList<>();
//        theDates.add("Mars");
//        theDates.add("Avril");
//        theDates.add("Dec");
//        theDates.add("May");
//        theDates.add("OCt");
//        theDates.add("Nov");
//        theDates.add("Fir");
//        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(theDates));
//        BarData theData = new BarData(barDataSet);//----Line of error
//        barChart.setData(theData);
//        barChart.setTouchEnabled(false);
//        barChart.setDragEnabled(false);
//        barChart.setScaleEnabled(false);

        return view;
    }

    //show membership message
//    @SuppressLint("SetTextI18n")
//    private void getAllMemberships(membershipRate) {
//        mUserViewModel.getAllMembershipData(membershipRate).observe(getViewLifecycleOwner(), allMemberships -> {
//            if (allMemberships != null) {
////                int discountAll = allMemberships.getDiscount();
//                int monthlyTopUpAll = allMemberships.getMonthlyTopUp();
//                int monthlyTopUpCurrent = Integer.parseInt(monthlyTopUp);
//
//                TextView messageAmountTextView = view.findViewById(R.id.fhu_tv_messageamount);
//                TextView messageStartTextView = view.findViewById(R.id.fhu_tv_messagestart);
//                TextView messageEndTextView = view.findViewById(R.id.fhu_tv_messageend);
//
////                if (monthlyTopUpCurrent >= monthlyTopUpAll){
////                    messageStartTextView.setText("You have reach the highest member rate!");
////                    messageAmountTextView.setVisibility(View.GONE);
////                    messageEndTextView.setVisibility(View.GONE);
////                }
//
//                int leftTopUpAmount = monthlyTopUpAll - monthlyTopUpCurrent;
//                String leftTopUpAmountString = String.valueOf(leftTopUpAmount);
//                messageAmountTextView.setText(leftTopUpAmountString);
//
//            }
//        });
//}

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