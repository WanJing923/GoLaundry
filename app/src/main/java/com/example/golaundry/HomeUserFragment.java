package com.example.golaundry;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.model.CurrentMembershipModel;
import com.example.golaundry.model.UserModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Objects;

public class HomeUserFragment extends Fragment {
    private LineChart lineChart;

    public HomeUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_user, container, false);

        //toolbar and back button
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fhu_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        //user view model
        UserViewModel mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        //get current user id
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        //declare the view id
        TextView userNameTextView = view.findViewById(R.id.fhu_tv_name);
        TextView membershipRateTextView = view.findViewById(R.id.fhu_tv_rate);
        TextView currentMonthTextView = view.findViewById(R.id.fhu_tv_month);
        TextView balanceAmountTextView = view.findViewById(R.id.fhu_tv_balance_amount);
        TextView monthlyAmountTextView = view.findViewById(R.id.fhu_tv_monthly_amount);

        //get user data
        mUserViewModel.getUserData(currentUserId).observe(getViewLifecycleOwner(), new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel user) {
                if (user != null) {
                    userNameTextView.setText(user.getFullName());
                    membershipRateTextView.setText(user.getMembershipRate());
                    balanceAmountTextView.setText(user.getBalance());
                }
            }
        });

        //show current month
        Calendar calendar = Calendar.getInstance();
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String currentMonth = monthName[calendar.get(Calendar.MONTH)];
        currentMonthTextView.setText(currentMonth);

        //show membership and balance info
        mUserViewModel.getCurrentMembershipData(currentUserId).observe(getViewLifecycleOwner(), new Observer<CurrentMembershipModel>() {
            @Override
            public void onChanged(CurrentMembershipModel currentMembership) {
                if (currentMembership != null) {
                    monthlyAmountTextView.setText(currentMembership.getMonthlyTopUp());
                }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // First clear current all the menu items
        menu.clear();
        // Add the new menu items
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