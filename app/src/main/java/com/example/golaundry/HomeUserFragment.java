package com.example.golaundry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.model.CurrentMembershipModel;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.viewModel.UserViewModel;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeUserFragment extends Fragment {
    private LineChart orderLineChart;
    private BarChart spendingBarChart;
    UserViewModel mUserViewModel;
    String currentUserId;
    double monthlyTopUp;
    double monthlyTopUpAll;
    String[] monthName;
    List<OrderModel> toCollectListUser = new ArrayList<>();
    List<OrderModel> toReceiveListUser = new ArrayList<>();

    public HomeUserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_user, container, false);
        //toolbar and back button
        Toolbar toolbar = view.findViewById(R.id.fhu_toolbar);
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
        TextView numOfPendingCollectionTextView = view.findViewById(R.id.fhu_tv_number_pending_collection);
        TextView numOfPendingReceivingTextView = view.findViewById(R.id.fhu_tv_number_pending_receiving);

        //show current month
        Calendar calendar = Calendar.getInstance();
        monthName = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String currentMonth = monthName[calendar.get(Calendar.MONTH)];
        currentMonthTextView.setText(currentMonth);

        String currentMonthYear = currentMonthYear();

        //show membership and monthly top up data
        mUserViewModel.getCurrentMembershipData(currentUserId).observe(getViewLifecycleOwner(), currentMembership -> {
            if (currentMembership != null) {
                if (Objects.equals(currentMembership.getMonthYear(), currentMonthYear)) {
                    @SuppressLint("DefaultLocale")
                    String monthlyTopUpStr = String.format("%.2f", currentMembership.getMonthlyTopUp());
                    monthlyAmountTextView.setText(monthlyTopUpStr);
                    monthlyTopUp = currentMembership.getMonthlyTopUp();
                } else {
                    CurrentMembershipModel currentMembershipModel = new CurrentMembershipModel(currentMonthYear, 0);
                    mUserViewModel.updateCurrentMembership(currentUserId, currentMembershipModel).observe(getViewLifecycleOwner(), updateStatus -> {
                        if (updateStatus) {
                            //update membership history
                            mUserViewModel.updateMembershipHistory(currentUserId, currentMonthYear, 0).observe(getViewLifecycleOwner(), updateMembershipHistoryStatus -> {
                                if (updateMembershipHistoryStatus) {

                                    //update user table current membership rate
                                    String newMembershipRate = "None";

                                    mUserViewModel.updateUserMembership(currentUserId, newMembershipRate).observe(getViewLifecycleOwner(), updateUserStatus -> {
                                        if (updateUserStatus) {
                                            @SuppressLint("DefaultLocale")
                                            String monthlyTopUpStr = String.format("%.2f", currentMembership.getMonthlyTopUp());
                                            monthlyAmountTextView.setText(monthlyTopUpStr);
                                            monthlyTopUp = currentMembership.getMonthlyTopUp();

                                        } else {
                                            Toast.makeText(getContext(), "Update user membership history failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    Toast.makeText(getContext(), "Update user membership history failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Toast.makeText(getContext(), "Update current membership failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            } else {
                Toast.makeText(getContext(), "Null current membership", Toast.LENGTH_SHORT).show();
            }
        });

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
//                    setAvatar(avatarUrl, ProfilePictureImageView);
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
                            double progressPercentage = (monthlyTopUp / progressEndStrFinal) * 100;
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

        view.findViewById(R.id.fhu_iv_topup).setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), TopUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        view.findViewById(R.id.fhu_cv_balance).setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), MembershipActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String currentYear = sdf.format(new Date());

        orderLineChart = view.findViewById(R.id.fhu_order_chart);
        spendingBarChart = view.findViewById(R.id.fhu_spending_chart);
        DatabaseReference userTotalOrderRef = FirebaseDatabase.getInstance().getReference().child("userTotalOrder").child(currentUserId).child(currentYear);
        DatabaseReference userSpendingRef = FirebaseDatabase.getInstance().getReference().child("userSpending").child(currentUserId).child(currentYear);

        ArrayList<String> months = new ArrayList<>(Arrays.asList(monthName));
        showCharts(currentUserId, currentYear, months);

        userSpendingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // current year node doesn't exist, create it
                    userSpendingRef.child(currentMonth).setValue(0);
                } else {
                    if (!dataSnapshot.hasChild(currentMonth)) {
                        userSpendingRef.child(currentMonth).setValue(0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        userTotalOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // current year node doesn't exist, create it
                    userTotalOrderRef.child(currentMonth).setValue(0);
                } else {
                    if (!dataSnapshot.hasChild(currentMonth)) {
                        userTotalOrderRef.child(currentMonth).setValue(0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        getUserOrderDataForToCollect(pendingCollectionNum1 -> {
            if (pendingCollectionNum1 == 0) {
                numOfPendingCollectionTextView.setText("0");
            } else {
                numOfPendingCollectionTextView.setText(String.valueOf(pendingCollectionNum1));
            }
        });

        getUserOrderDataForToReceive(pendingReceiveNum1 -> {
            if (pendingReceiveNum1 == 0) {
                numOfPendingReceivingTextView.setText("0");
            } else {
                numOfPendingReceivingTextView.setText(String.valueOf(pendingReceiveNum1));
            }
        });

        CardView pendingCollectionCV = view.findViewById(R.id.fhu_cv_pending_collection);
        CardView pendingReceivingCV = view.findViewById(R.id.fhu_cv_pending_receiving);

        pendingCollectionCV.setOnClickListener(view12 -> {
            HomeActivity activity = (HomeActivity) requireActivity();
            activity.navigateToHistoryFragment();
        });

        pendingReceivingCV.setOnClickListener(view13 -> {
            HomeActivity activity = (HomeActivity) requireActivity();
            activity.navigateToHistoryFragment();
        });

        return view;
    }

    public interface DataCallback<T> {
        void onDataLoaded(T data);
    }

    private void getUserOrderDataForToCollect(DataCallback<Integer> callback) {
        DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder");
        AtomicInteger pendingCollectionNumber = new AtomicInteger(0);
        userOrderRef.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            String currentStatus = order.getCurrentStatus();
                            if ("Order created".equals(currentStatus) || "Rider accept order".equals(currentStatus)) {
                                toCollectListUser.add(order);
                                pendingCollectionNumber.set(toCollectListUser.size());
                            }
                        }
                    }
                    callback.onDataLoaded(pendingCollectionNumber.get());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        pendingCollectionNumber.get();
    }

    private void getUserOrderDataForToReceive(DataCallback<Integer> callback) {
        DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder");
        AtomicInteger pendingReceivingNumber = new AtomicInteger(0);
        userOrderRef.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            String currentStatus = order.getCurrentStatus();
                            if ("Rider pick up".equals(currentStatus) || "Order reached laundry shop".equals(currentStatus)
                                    || "Laundry done process".equals(currentStatus) || "Order out of delivery".equals(currentStatus)
                                    || "Order delivered".equals(currentStatus)) {
                                toReceiveListUser.add(order);
                                pendingReceivingNumber.set(toReceiveListUser.size());
                            }
                        }
                    }
                    callback.onDataLoaded(pendingReceivingNumber.get());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        pendingReceivingNumber.get();
    }

    private void showCharts(String currentUserId, String currentYear, ArrayList<String> months) {
        DatabaseReference userSpendingRef = FirebaseDatabase.getInstance().getReference().child("userSpending").child(currentUserId).child(currentYear);
        DatabaseReference userTotalOrderRef = FirebaseDatabase.getInstance().getReference().child("userTotalOrder").child(currentUserId).child(currentYear);

        ArrayList<Double> spendingValues = new ArrayList<>();
        ArrayList<Integer> orderValues = new ArrayList<>();

        for (int i = 0; i < months.size(); i++) {
            spendingValues.add(0.0);
            orderValues.add(0);
        }

        userSpendingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (String month : months) {
                        DataSnapshot monthSnapshot = dataSnapshot.child(month);
                        if (monthSnapshot.exists()) {
                            Double spendingValue = monthSnapshot.getValue(Double.class);
                            spendingValues.set(months.indexOf(month), spendingValue != null ? spendingValue : 0.0);
                        }
                    }
                    displaySpendingBarChart(months, spendingValues);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        userTotalOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    displayOrderLineChart(months, orderValues);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void displayOrderLineChart(ArrayList<String> months, ArrayList<Integer> values) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < months.size(); i++) {
            // the month name to its 1 to 12
            int monthNumber = Arrays.asList(monthName).indexOf(months.get(i)) + 1;
            entries.add(new Entry(monthNumber, values.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Total Number of Orders");
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.RED);

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

    private void displaySpendingBarChart(ArrayList<String> months, ArrayList<Double> values) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < months.size(); i++) {
            int monthNumber = Arrays.asList(monthName).indexOf(months.get(i)) + 1;
            float floatValue = values.get(i).floatValue();
            barEntries.add(new BarEntry(monthNumber, floatValue));
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Total Spending");
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

    public String currentMonthYear() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

//    private void setAvatar(String avatarUrl, ImageView profilePictureImageView) {
//        //referenceFromUrl to get StorageReference
//        StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(avatarUrl);
//
//        try {
//            File localFile = File.createTempFile("tempfile", ".jpg");
//
//            mStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
//                //show
//                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                profilePictureImageView.setImageBitmap(bitmap);
//
//            }).addOnFailureListener(e -> {
//                Toast.makeText(getContext(), "Failed to retrieve image", Toast.LENGTH_SHORT).show();
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}