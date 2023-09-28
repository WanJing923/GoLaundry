package com.example.golaundry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.viewModel.UserViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class HomeUserFragment<membershipRate> extends Fragment {
    private LineChart lineChart;
    UserViewModel mUserViewModel;
    double monthlyTopUp;
    double monthlyTopUpAll;

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
        View view = inflater.inflate(R.layout.fragment_home_user, container, false);
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
        ImageView ProfilePictureImageView = view.findViewById(R.id.fhu_civ_profile_pic);

        //get user and membership data
        mUserViewModel.getUserData(currentUserId).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                //show user info and balance
                userNameTextView.setText(user.getFullName());
                membershipRateTextView.setText(user.getMembershipRate());
                @SuppressLint("DefaultLocale")
                String balance = String.format("%.2f", user.getBalance());
                balanceAmountTextView.setText(balance);

                //show image
                String avatarUrl = user.getAvatar();
                if (!Objects.equals(avatarUrl, "")) {
                    setAvatar(avatarUrl,ProfilePictureImageView);
                }

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
            }
        });

        //show current month
        Calendar calendar = Calendar.getInstance();
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String currentMonth = monthName[calendar.get(Calendar.MONTH)];
        currentMonthTextView.setText(currentMonth);

        //show membership and monthly top up data
        mUserViewModel.getCurrentMembershipData(currentUserId).observe(getViewLifecycleOwner(), currentMembership -> {
            if (currentMembership != null) {
                @SuppressLint("DefaultLocale")
                String monthlyTopUpStr = String.format("%.2f", currentMembership.getMonthlyTopUp());
                monthlyAmountTextView.setText(monthlyTopUpStr);
                monthlyTopUp = currentMembership.getMonthlyTopUp();
            }
        });

//        BarChart barChart = view.findViewById(R.id.fhu_spending_chart);
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

        //intent to membership activity, show all memberships
        view.findViewById(R.id.fhu_cv_balance).setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), MembershipActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

//        lineChart = view.findViewById(R.id.fhu_spending_chart);

//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    ArrayList<String> months = new ArrayList<>();
//                    ArrayList<Integer> values = new ArrayList<>();
//
//                    // Define your custom order for months
//                    String[] monthOrder = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
//
//                    // Iterate through the custom order of months
//                    for (String monthName : monthOrder) {
//                        DataSnapshot monthSnapshot = dataSnapshot.child(monthName);
//                        if (monthSnapshot.exists()) {
//                            int spendingAmount = monthSnapshot.getValue(Integer.class);
//
//                            // Add month names to the months list
//                            months.add(monthName);
//
//                            // Add spending amounts to the values list
//                            values.add(spendingAmount);
//                        }
//                    }
//
//                    // Call a method to display the data in a LineChart
//                    displayDataInLineChart(months, values);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle any errors here
//            }
//        });

        return view;
    }
//    private void displayDataInLineChart(ArrayList<String> months, ArrayList<Integer> values) {
//
//        // Populate the entries list with values
//        ArrayList<Entry> entries = new ArrayList<>();
//        for (int i = 0; i < months.size(); i++) {
//            entries.add(new Entry(i, values.get(i)));
//        }
//
//        LineDataSet dataSet = new LineDataSet(entries, "");
//        dataSet.setColor(Color.BLUE);
//        dataSet.setLineWidth(2f);
//        dataSet.setCircleColor(Color.RED);
//
//        LineData lineData = new LineData(dataSet);
//        lineChart.setData(lineData);
//
//        // Configure X-axis (months)
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setGranularity(1f);
//
//        // Configure Y-axis (spending amounts)
//        YAxis yAxisLeft = lineChart.getAxisLeft();
//        yAxisLeft.setGranularity(1f);
//
//        // Refresh the chart
//        lineChart.invalidate();
//        lineChart.setTouchEnabled(false);
//        lineChart.getDescription().setEnabled(false);
//    }

    private void setAvatar(String avatarUrl, ImageView profilePictureImageView) {
        //referenceFromUrl to get StorageReference
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(avatarUrl);

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