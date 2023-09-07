package com.example.golaundry;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.golaundry.model.RiderModel;
import com.example.golaundry.model.UserModel;
import com.example.golaundry.viewModel.RiderViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class HomeRiderFragment extends Fragment {
    private LineChart lineChart;

    public HomeRiderFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_home_rider, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fhr_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        //user view model
        RiderViewModel mRiderViewModel = new ViewModelProvider(this).get(RiderViewModel.class);
        //get current rider id
        String currentRiderId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        TextView riderNameTextView = view.findViewById(R.id.fhr_tv_name);
        TextView ratingsTextView = view.findViewById(R.id.fhr_tv_rating_num);
        TextView viewRatingsTextView = view.findViewById(R.id.fhr_tv_view_ratings);

        mRiderViewModel.getRiderData(currentRiderId).observe(getViewLifecycleOwner(), rider -> {
            if (rider != null) {
                riderNameTextView.setText(rider.getFullName());
            }
        });

        viewRatingsTextView.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), RatingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });




//        BarChart barChart = view.findViewById(R.id.fh_order_chart);
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