package com.example.golaundry;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.TextView;

import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.OrderStatusModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class HomeLaundryFragment extends Fragment {
    LaundryViewModel mLaundryViewModel;
    private LineChart orderLineChart;
    private BarChart spendingBarChart;
    private String[] monthName;

    public HomeLaundryFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_home_laundry, container, false);

        Toolbar toolbar = view.findViewById(R.id.fhl_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        //get current user id
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        TextView shopNameTextView = view.findViewById(R.id.fhl_tv_name);
        TextView ratingNumberTextView = view.findViewById(R.id.fhl_tv_rating_num);
        RatingBar ratingstarRatingBar = view.findViewById(R.id.fhl_tv_rating_star);
        TextView viewRatingsTextView = view.findViewById(R.id.fhl_tv_view_ratings);

        //get laundry data
        mLaundryViewModel.getLaundryData(currentUserId).observe(getViewLifecycleOwner(), laundry -> {
            if (laundry != null) {
                shopNameTextView.setText(laundry.getShopName());
            }
        });

        viewRatingsTextView.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), RatingsActivity.class);
            intent.putExtra("isLaundry", true);
            startActivity(intent);
        });

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String currentYear = sdf.format(new Date());
        orderLineChart = view.findViewById(R.id.fhl_order_chart);
        spendingBarChart = view.findViewById(R.id.fhl_earnings_chart);
        DatabaseReference laundryTotalOrderRef = FirebaseDatabase.getInstance().getReference().child("laundryTotalOrder").child(currentUserId).child(currentYear);
        DatabaseReference laundryEarningsRef = FirebaseDatabase.getInstance().getReference().child("laundryEarnings").child(currentUserId).child(currentYear);

        Calendar calendar = Calendar.getInstance();
        monthName = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String currentMonth = monthName[calendar.get(Calendar.MONTH)];

        ArrayList<String> months = new ArrayList<>(Arrays.asList(monthName));
        showCharts(currentUserId, currentYear, months);

        laundryEarningsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // current year node doesn't exist, create it
                    laundryEarningsRef.child(currentMonth).setValue(0);
                } else {
                    if (!dataSnapshot.hasChild(currentMonth)) {
                        laundryEarningsRef.child(currentMonth).setValue(0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        laundryTotalOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // current year node doesn't exist, create it
                    laundryTotalOrderRef.child(currentMonth).setValue(0);
                } else {
                    if (!dataSnapshot.hasChild(currentMonth)) {
                        laundryTotalOrderRef.child(currentMonth).setValue(0);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //update order status
        SimpleDateFormat sdf1 = new SimpleDateFormat("M/d/yyyy", Locale.getDefault());
        String currentDateFromDb = sdf1.format(new Date());
        DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder");
        DatabaseReference orderStatusRef = FirebaseDatabase.getInstance().getReference().child("orderStatus");
        DatabaseReference ridersRef = FirebaseDatabase.getInstance().getReference().child("riders");
        String orderStatusId = String.valueOf(UUID.randomUUID());
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String dateTime = sdf2.format(new Date());

        userOrderRef.orderByChild("laundryId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            String currentStatus = order.getCurrentStatus();
                            if ("Order created".equals(currentStatus) || "Rider accept order".equals(currentStatus)) {
                                try {
                                    Date currentDate = sdf1.parse(currentDateFromDb);
                                    Date dbDate = sdf1.parse(order.getPickUpDate());

                                    assert currentDate != null;
                                    if (currentDate.after(dbDate)) {
                                        //check these order.getRiderId is "None" or not, if yes,update the table child(currentStatus) to "Order cancelled due to no rider accept order"
                                        if ("None".equals(order.getRiderId())) {
                                            userOrderRef.child(order.getOrderId()).child("currentStatus").setValue("Order cancelled due to no rider accept order");
                                            //order status
                                            OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime, "Order cancelled due to no rider accept order");
                                            orderStatusRef.child(order.getOrderId()).child(orderStatusId).setValue(mOrderStatusModel);
                                        } else {
                                            userOrderRef.child(order.getOrderId()).child("currentStatus").setValue("Order cancelled due to rider missed pick up");
                                            //order status
                                            OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime, "Order cancelled due to rider missed pick up");
                                            orderStatusRef.child(order.getOrderId()).child(orderStatusId).setValue(mOrderStatusModel);
                                            //terminate rider due to missed pick up
                                            ridersRef.child(order.getRiderId()).child("status").setValue("terminated");

                                            //show dialog
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Rider's account has been terminated" );
                                            builder.setMessage("Rider has missed pick up the order " + order.getOrderId());
                                            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return view;
    }

    private void showCharts(String currentUserId, String currentYear, ArrayList<String> months) {
        DatabaseReference laundryEarningsRef = FirebaseDatabase.getInstance().getReference().child("laundryEarnings").child(currentUserId).child(currentYear);
        DatabaseReference laundryTotalOrderRef = FirebaseDatabase.getInstance().getReference().child("laundryTotalOrder").child(currentUserId).child(currentYear);

        ArrayList<Double> earningsValues = new ArrayList<>();
        ArrayList<Integer> orderValues = new ArrayList<>();

        for (int i = 0; i < months.size(); i++) {
            earningsValues.add(0.0);
            orderValues.add(0);
        }

        laundryEarningsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (String month : months) {
                        DataSnapshot monthSnapshot = dataSnapshot.child(month);
                        if (monthSnapshot.exists()) {
                            Double earningsValue = monthSnapshot.getValue(Double.class);
                            earningsValues.set(months.indexOf(month), earningsValue != null ? earningsValue : 0.0);
                        }
                    }
                    displayRiderEarningsBarChart(months, earningsValues);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        laundryTotalOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (String month : months) {
                        DataSnapshot monthSnapshot = dataSnapshot.child(month);
                        if (monthSnapshot.exists()) {
                            Integer orderValue = monthSnapshot.getValue(Integer.class);
                            orderValues.set(months.indexOf(month), orderValue != null ? orderValue : 0);
                        }
                    }
                    displayRiderOrderLineChart(months, orderValues);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void displayRiderOrderLineChart(ArrayList<String> months, ArrayList<Integer> values) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < months.size(); i++) {
            // the month name to its 1 to 12
            int monthNumber = Arrays.asList(monthName).indexOf(months.get(i)) + 1;
            entries.add(new Entry(monthNumber, values.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Total Number of Orders");
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.GREEN);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return String.valueOf((int) entry.getY());
            }
        });

        LineData lineData = new LineData(dataSet);
        orderLineChart.setData(lineData);

        YAxis yAxis = orderLineChart.getAxisLeft();
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });

        //months as numbers
        XAxis xAxis = orderLineChart.getXAxis();
        xAxis.setValueFormatter(null);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        //refresh
        orderLineChart.invalidate();
        orderLineChart.setTouchEnabled(false);
        orderLineChart.getDescription().setEnabled(false);
    }

    private void displayRiderEarningsBarChart(ArrayList<String> months, ArrayList<Double> values) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < months.size(); i++) {
            int monthNumber = Arrays.asList(monthName).indexOf(months.get(i)) + 1;
            float floatValue = values.get(i).floatValue();
            barEntries.add(new BarEntry(monthNumber, floatValue));
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Total Earnings");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        XAxis xAxis = spendingBarChart.getXAxis();
        xAxis.setValueFormatter(null);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(months.size());

        spendingBarChart.setData(data);
        spendingBarChart.setFitBars(true);
        spendingBarChart.invalidate();
        spendingBarChart.setTouchEnabled(false);
        spendingBarChart.getDescription().setEnabled(false);
    }

}