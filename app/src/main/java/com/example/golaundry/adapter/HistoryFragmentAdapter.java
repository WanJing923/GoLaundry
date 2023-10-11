package com.example.golaundry.adapter;

import static android.content.Context.WINDOW_SERVICE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.golaundry.HistoryOrderStatusActivity;
import com.example.golaundry.model.OrderStatusModel;
import com.example.golaundry.viewModel.RiderViewModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.R;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.ServiceItem;
import com.example.golaundry.viewModel.LaundryViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HistoryFragmentAdapter extends RecyclerView.Adapter<HistoryFragmentAdapter.ViewHolder> {
    private final List<OrderModel> orderList;
    private final Context context;
    private final LaundryViewModel mLaundryViewModel;
    private final UserViewModel mUserViewModel;
    private final RiderViewModel mRiderViewModel;

    QRGEncoder qrgEncoder;
    Bitmap bitmap;
    String TAG = "GenerateQRCODE";
    private double cancelBalance;

    public HistoryFragmentAdapter(List<OrderModel> orderList, Context context, LaundryViewModel mLaundryViewModel, UserViewModel mUserViewModel, RiderViewModel mRiderViewModel) {
        this.orderList = orderList;
        this.context = context;
        this.mLaundryViewModel = mLaundryViewModel;
        this.mUserViewModel = mUserViewModel;
        this.mRiderViewModel = mRiderViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        holder.orderIdTextView.setText(order.getOrderId());
        holder.statusTextView.setText(order.getCurrentStatus());

        double distance = order.getDeliveryFee();
        @SuppressLint("DefaultLocale")
        String distanceShow = String.format("%.2f", distance);
        holder.deliveryAmountTextView.setText(distanceShow);

        double total = order.getTotalFee();
        @SuppressLint("DefaultLocale")
        String totalShow = String.format("%.2f", total);
        holder.totalAmountTextView.setText(totalShow);

        String date = order.getDateTime();
        String formattedDate = formatDateTime(date);
        holder.dateTextView.setText("Order by " + formattedDate);

        mLaundryViewModel.getLaundryData(order.getLaundryId()).observe((LifecycleOwner) context, laundryModel -> {
            if (laundryModel != null) {
                String shopName = laundryModel.getShopName();
                holder.laundryShopNameTextView.setText(shopName);
            }
        });

        mUserViewModel.getUserData(order.getUserId()).observe((LifecycleOwner) context, userModel -> {
            if (userModel != null) {
                double currentBalance = userModel.getBalance();
                cancelBalance = currentBalance + order.getTotalFee();
            }
        });

        //Pending collection
        if (Objects.equals(order.getCurrentStatus(), "Order created")) {
            holder.currentStatusTextView.setText("Pending Collection");
            holder.actionButton.setText("CANCEL");
            holder.actionButton.setOnClickListener(view -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Cancel Order Confirmation");
                builder.setMessage("Your fees will be refund.");

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                    holder.actionButton.setText("LOADING");
                    // update order status history, order current status, user balance, user spending, user total order
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(order.getUserId());
                    DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder").child(order.getOrderId());
                    DatabaseReference orderStatusRef = FirebaseDatabase.getInstance().getReference().child("orderStatus").child(order.getOrderId());
                    DatabaseReference userSpendingRef = FirebaseDatabase.getInstance().getReference().child("userSpending").child(order.getUserId());
                    DatabaseReference userTotalOrderRef = FirebaseDatabase.getInstance().getReference().child("userTotalOrder").child(order.getUserId());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String dateTime = sdf.format(new Date());
                    OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime, "Order cancelled by customer");
                    String orderStatusId = String.valueOf(UUID.randomUUID());

                    orderStatusRef.child(orderStatusId).setValue(mOrderStatusModel).addOnSuccessListener(aVoid -> { //order status history
                        userOrderRef.child("currentStatus").setValue("Order cancelled by customer").addOnSuccessListener(aVoid1 -> { //order current status
                            userRef.child("balance").setValue(cancelBalance).addOnSuccessListener(aVoid2 -> { //add back user current balance
                                userSpendingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String latestYear = "";
                                            String latestMonth = "";
                                            Double currentSpending = null;
                                            for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) {
                                                String year = yearSnapshot.getKey();
                                                for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                                                    String month = monthSnapshot.getKey();
                                                    assert latestYear != null;
                                                    if (latestYear.equals("") || Objects.equals(latestMonth, "") || (year + month).compareTo(latestYear + latestMonth) > 0) {
                                                        latestYear = year;
                                                        latestMonth = month;
                                                        currentSpending = monthSnapshot.getValue(Double.class);
                                                    }
                                                }
                                            }
                                            if (currentSpending != null) {
                                                double updatedSpending = currentSpending - order.getTotalFee();
                                                assert latestYear != null;
                                                if (!latestYear.isEmpty() && !Objects.requireNonNull(latestMonth).isEmpty()) {
                                                    userSpendingRef.child(latestYear).child(latestMonth).setValue(updatedSpending).addOnSuccessListener(aVoid3 -> {  //deduct the order total fee
                                                        userTotalOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    String latestYear1 = "";
                                                                    String latestMonth1 = "";
                                                                    int currentTotalNumber = 0;
                                                                    for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) {
                                                                        String year = yearSnapshot.getKey();
                                                                        for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                                                                            String month = monthSnapshot.getKey();
                                                                            assert latestYear1 != null;
                                                                            if (latestYear1.equals("") || Objects.equals(latestMonth1, "") || (year + month).compareTo(latestYear1 + latestMonth1) > 0) {
                                                                                latestYear1 = year;
                                                                                latestMonth1 = month;
                                                                                Integer totalNumber = monthSnapshot.getValue(Integer.class);
                                                                                if (totalNumber != null) {
                                                                                    currentTotalNumber = totalNumber;
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    if (currentTotalNumber != 0) {
                                                                        int updatedNumber = currentTotalNumber - 1;
                                                                        assert latestYear1 != null;
                                                                        assert latestMonth1 != null;
                                                                        if (!latestYear1.equals("") && !Objects.equals(latestMonth1, "")) {
                                                                            userTotalOrderRef.child(latestYear1).child(latestMonth1).setValue(updatedNumber).addOnSuccessListener(aVoid4 -> {//deduct order total number
                                                                                Toast.makeText(context, "Order cancelled, please refresh.", Toast.LENGTH_SHORT).show();
                                                                                holder.actionButton.setText("CANCELLED");
                                                                                holder.actionButton.setEnabled(false);
                                                                                holder.mProgressBar.setVisibility(View.GONE);
                                                                            }).addOnFailureListener(e -> Toast.makeText(context, "Order cancelling process failed.", Toast.LENGTH_SHORT).show());
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            }
                                                        });
                                                    }).addOnFailureListener(e -> {
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        holder.mProgressBar.setVisibility(View.GONE);
                                    }
                                });
                            }).addOnFailureListener(e -> holder.mProgressBar.setVisibility(View.GONE));
                        }).addOnFailureListener(e -> holder.mProgressBar.setVisibility(View.GONE));
                    }).addOnFailureListener(e -> holder.mProgressBar.setVisibility(View.GONE));
                    dialog.dismiss();
                });

                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
            });
        }
        //rider accept order, cant cancel order
        else if (Objects.equals(order.getCurrentStatus(), "Rider accept order")) {
            holder.actionButton.setVisibility(View.GONE);
        }
        //pending receiving
        else if (Objects.equals(order.getCurrentStatus(), "Rider pick up") || Objects.equals(order.getCurrentStatus(), "Order reached laundry shop")
                || Objects.equals(order.getCurrentStatus(), "Laundry done process") || Objects.equals(order.getCurrentStatus(), "Order out of delivery")) {
            holder.currentStatusTextView.setText("Pending Receiving");
            holder.actionButton.setVisibility(View.GONE);
        } else if (Objects.equals(order.getCurrentStatus(), "Order delivered")) { //confirm complete order
            holder.currentStatusTextView.setText("Pending Receiving");
            holder.actionButton.setText("RECEIVE");
            holder.actionButton.setOnClickListener(view -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Received and Confirm Order Confirmation");
                builder.setMessage("Release money to laundry shop and rider.");

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    holder.actionButton.setText("LOADING");
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                    // update order status history, order current status, laundry earnings&total order, rider earnings&total order, rider and laundry balance
                    DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder").child(order.getOrderId());
                    DatabaseReference orderStatusRef = FirebaseDatabase.getInstance().getReference().child("orderStatus").child(order.getOrderId());
                    DatabaseReference laundryEarningsRef = FirebaseDatabase.getInstance().getReference().child("laundryEarnings").child(order.getLaundryId());
                    DatabaseReference laundryTotalOrderRef = FirebaseDatabase.getInstance().getReference().child("laundryTotalOrder").child(order.getLaundryId());
                    DatabaseReference riderEarningsRef = FirebaseDatabase.getInstance().getReference().child("riderEarnings").child(order.getRiderId());
                    DatabaseReference riderTotalOrderRef = FirebaseDatabase.getInstance().getReference().child("riderTotalOrder").child(order.getRiderId());
                    DatabaseReference ridersRef = FirebaseDatabase.getInstance().getReference().child("riders").child(order.getRiderId());
                    DatabaseReference laundryRef = FirebaseDatabase.getInstance().getReference().child("laundry").child(order.getLaundryId());
                    DatabaseReference appEarningsRef = FirebaseDatabase.getInstance().getReference().child("appEarnings").child(order.getOrderId());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String dateTime = sdf.format(new Date());
                    OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime, "Order completed");
                    String orderStatusId = String.valueOf(UUID.randomUUID());

                    orderStatusRef.child(orderStatusId).setValue(mOrderStatusModel).addOnSuccessListener(aVoid -> { //order status history, done
                        userOrderRef.child("currentStatus").setValue("Order completed").addOnSuccessListener(aVoid1 -> { //order current status, done
                            laundryEarningsRef.addListenerForSingleValueEvent(new ValueEventListener() { //laundry dashboard,  done
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String latestYear = "";
                                        String latestMonth = "";
                                        Double currentEarningsLaundry = null;
                                        for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) {
                                            String year = yearSnapshot.getKey();
                                            for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                                                String month = monthSnapshot.getKey();
                                                assert latestYear != null;
                                                if (latestYear.equals("") || Objects.equals(latestMonth, "") || (year + month).compareTo(latestYear + latestMonth) > 0) {
                                                    latestYear = year;
                                                    latestMonth = month;
                                                    currentEarningsLaundry = monthSnapshot.getValue(Double.class);
                                                }
                                            }
                                        }
                                        if (currentEarningsLaundry != null) {
                                            double updatedEarningsLaundry = currentEarningsLaundry + (order.getLaundryFee() / 2);
                                            assert latestYear != null;
                                            if (!latestYear.isEmpty() && !Objects.requireNonNull(latestMonth).isEmpty()) {
                                                laundryEarningsRef.child(latestYear).child(latestMonth).setValue(updatedEarningsLaundry).addOnSuccessListener(aVoid3 -> {  //add the laundry earnings, done
                                                    laundryTotalOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                String latestYear1 = "";
                                                                String latestMonth1 = "";
                                                                int currentTotalNumberLaundry = 100000;
                                                                for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) {
                                                                    String year = yearSnapshot.getKey();
                                                                    for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                                                                        String month = monthSnapshot.getKey();
                                                                        assert latestYear1 != null;
                                                                        if (latestYear1.equals("") || Objects.equals(latestMonth1, "") || (year + month).compareTo(latestYear1 + latestMonth1) > 0) {
                                                                            latestYear1 = year;
                                                                            latestMonth1 = month;
                                                                            Integer totalNumber = monthSnapshot.getValue(Integer.class);
                                                                            if (totalNumber != null) {
                                                                                currentTotalNumberLaundry = totalNumber;
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                if (currentTotalNumberLaundry != 100000) {
                                                                    int updatedNumberLaundry = currentTotalNumberLaundry + 1;
                                                                    assert latestYear1 != null;
                                                                    assert latestMonth1 != null;
                                                                    if (!latestYear1.equals("") && !Objects.equals(latestMonth1, "")) {
                                                                        laundryTotalOrderRef.child(latestYear1).child(latestMonth1).setValue(updatedNumberLaundry).addOnSuccessListener(aVoid4 -> {  //add laundry order total number

                                                                            //rider
                                                                            riderEarningsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    if (dataSnapshot.exists()) {
                                                                                        String latestYear3 = "";
                                                                                        String latestMonth3 = "";
                                                                                        Double currentEarningsRider = null;
                                                                                        for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) {
                                                                                            String year = yearSnapshot.getKey();
                                                                                            for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                                                                                                String month = monthSnapshot.getKey();
                                                                                                assert latestYear3 != null;
                                                                                                if (latestYear3.equals("") || Objects.equals(latestMonth3, "") || (year + month).compareTo(latestYear3 + latestMonth3) > 0) {
                                                                                                    latestYear3 = year;
                                                                                                    latestMonth3 = month;
                                                                                                    currentEarningsRider = monthSnapshot.getValue(Double.class);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                        if (currentEarningsRider != null) { //2f format
                                                                                            double updatedEarningsRider = currentEarningsRider + (order.getDeliveryFee());
                                                                                            assert latestYear3 != null;
                                                                                            if (!latestYear3.isEmpty() && !Objects.requireNonNull(latestMonth3).isEmpty()) {
                                                                                                riderEarningsRef.child(latestYear3).child(latestMonth3).setValue(updatedEarningsRider).addOnSuccessListener(aVoid3 -> {  //add the rider earnings
                                                                                                    riderTotalOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                            if (dataSnapshot.exists()) {
                                                                                                                String latestYear4 = "";
                                                                                                                String latestMonth4 = "";
                                                                                                                int currentTotalNumberRider = 100000000;
                                                                                                                for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) {
                                                                                                                    String year = yearSnapshot.getKey();
                                                                                                                    for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                                                                                                                        String month = monthSnapshot.getKey();
                                                                                                                        assert latestYear4 != null;
                                                                                                                        if (latestYear4.equals("") || Objects.equals(latestMonth4, "") || (year + month).compareTo(latestYear4 + latestMonth4) > 0) {
                                                                                                                            latestYear4 = year;
                                                                                                                            latestMonth4 = month;
                                                                                                                            Integer totalNumber = monthSnapshot.getValue(Integer.class);
                                                                                                                            if (totalNumber != null) {
                                                                                                                                currentTotalNumberRider = totalNumber;
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                                if (currentTotalNumberRider != 100000000) {
                                                                                                                    int updatedNumberLaundry = currentTotalNumberRider + 1;
                                                                                                                    assert latestYear4 != null;
                                                                                                                    assert latestMonth4 != null;
                                                                                                                    if (!latestYear4.equals("") && !Objects.equals(latestMonth4, "")) {
                                                                                                                        riderTotalOrderRef.child(latestYear4).child(latestMonth4).setValue(updatedNumberLaundry).addOnSuccessListener(aVoid4 -> {//add order total number

                                                                                                                            //laundry balance
                                                                                                                            laundryRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                                @Override
                                                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                                    if (dataSnapshot.exists()) {
                                                                                                                                        Double currentBalanceLaundry = dataSnapshot.getValue(Double.class);
                                                                                                                                        if (currentBalanceLaundry != null) {
                                                                                                                                            double updatedBalanceLaundry = currentBalanceLaundry + (order.getLaundryFee() / 2);

                                                                                                                                            laundryRef.child("balance").setValue(updatedBalanceLaundry).addOnSuccessListener(aVoid5 -> { //update laundry balance
                                                                                                                                                //rider balance
                                                                                                                                                ridersRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                                                        if (dataSnapshot.exists()) {
                                                                                                                                                            Double currentBalanceRider = dataSnapshot.getValue(Double.class);
                                                                                                                                                            if (currentBalanceRider != null) {
                                                                                                                                                                double updatedBalanceRider = currentBalanceRider + (order.getDeliveryFee());

                                                                                                                                                                ridersRef.child("balance").setValue(updatedBalanceRider).addOnSuccessListener(aVoid5 -> { //update rider balance

                                                                                                                                                                    appEarningsRef.child("dateTime").setValue(dateTime).addOnSuccessListener(aVoid6 -> { //update app earnings datetime

                                                                                                                                                                        double appEarningsWithoutDeductMembership = order.getTotalFee() - order.getDeliveryFee() - (order.getLaundryFee() / 2);
                                                                                                                                                                        double appEarnings;

                                                                                                                                                                        if (Objects.equals(order.getMembershipDiscount(), "None")) {
                                                                                                                                                                            appEarnings = appEarningsWithoutDeductMembership;
                                                                                                                                                                        } else if (Objects.equals(order.getMembershipDiscount(), "GL05")) {
                                                                                                                                                                            appEarnings = 0.95 * appEarningsWithoutDeductMembership;
                                                                                                                                                                        } else if (Objects.equals(order.getMembershipDiscount(), "GL10")) {
                                                                                                                                                                            appEarnings = 0.9 * appEarningsWithoutDeductMembership;
                                                                                                                                                                        } else if (Objects.equals(order.getMembershipDiscount(), "GL20")) {
                                                                                                                                                                            appEarnings = 0.8 * appEarningsWithoutDeductMembership;
                                                                                                                                                                        } else {
                                                                                                                                                                            appEarnings = 0.7 * appEarningsWithoutDeductMembership;
                                                                                                                                                                        }

                                                                                                                                                                        appEarningsRef.child("earnings").setValue(appEarnings).addOnSuccessListener(aVoid7 -> { //update app earnings amount

                                                                                                                                                                            appEarningsRef.child("orderId").setValue(order.getOrderId()).addOnSuccessListener(aVoid8 -> { //update app earnings orderID

                                                                                                                                                                                holder.mProgressBar.setVisibility(View.GONE);
                                                                                                                                                                                Toast.makeText(context, "Order has been completed, please refresh.", Toast.LENGTH_SHORT).show();
                                                                                                                                                                                holder.actionButton.setText("Done");
                                                                                                                                                                                holder.actionButton.setEnabled(false);

                                                                                                                                                                            }).addOnFailureListener(e -> {
                                                                                                                                                                                holder.mProgressBar.setVisibility(View.GONE);
                                                                                                                                                                                Toast.makeText(context, "Order confirming process failed.", Toast.LENGTH_SHORT).show();
                                                                                                                                                                            });

                                                                                                                                                                        }).addOnFailureListener(e -> holder.mProgressBar.setVisibility(View.GONE));

                                                                                                                                                                    }).addOnFailureListener(e -> holder.mProgressBar.setVisibility(View.GONE));

                                                                                                                                                                }).addOnFailureListener(e -> holder.mProgressBar.setVisibility(View.GONE));
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    }

                                                                                                                                                    @Override
                                                                                                                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                                                                                                                        holder.mProgressBar.setVisibility(View.GONE);
                                                                                                                                                    }
                                                                                                                                                });
                                                                                                                                            });
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }

                                                                                                                                @Override
                                                                                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                                                                                    holder.mProgressBar.setVisibility(View.GONE);
                                                                                                                                }
                                                                                                                            });
                                                                                                                        }).addOnFailureListener(e -> holder.mProgressBar.setVisibility(View.GONE));
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                            holder.mProgressBar.setVisibility(View.GONE);
                                                                                                        }
                                                                                                    });
                                                                                                }).addOnFailureListener(e -> holder.mProgressBar.setVisibility(View.GONE));
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                    holder.mProgressBar.setVisibility(View.GONE);
                                                                                }
                                                                            });
                                                                        }).addOnFailureListener(e -> holder.mProgressBar.setVisibility(View.GONE));
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            holder.mProgressBar.setVisibility(View.GONE);
                                                        }
                                                    });
                                                }).addOnFailureListener(e -> holder.mProgressBar.setVisibility(View.GONE));
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    holder.mProgressBar.setVisibility(View.GONE);
                                }
                            });

                        }).addOnFailureListener(e -> holder.mProgressBar.setVisibility(View.GONE));
                    }).addOnFailureListener(e -> holder.mProgressBar.setVisibility(View.GONE));

                    dialog.dismiss();
                });

                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
            });
        }
        //completed
        else if (Objects.equals(order.getCurrentStatus(), "Order completed")) {
            holder.currentStatusTextView.setText("Completed");

            if (!order.isAbleToRate()){
                holder.actionButton.setEnabled(false);
                holder.actionButton.setVisibility(View.GONE);
            } else {
                holder.actionButton.setText("RATE");
                holder.actionButton.setOnClickListener(view -> {
                    // go ratings feature
                    //intent and implement rate laundry and rider function, pass the orderId, userId, laundryId, riderId
                    //example: order.getOrderId(), order.getUserId(), order.getLaundryId(), order.getRiderId put extra to intent

                    //set userOrder not able to rate
                });
            }

        }
        //cancelled
        else if (Objects.equals(order.getCurrentStatus(), "Order cancelled by customer") || (Objects.equals(order.getCurrentStatus(), "Order cancelled by laundry shop"))) {
            holder.currentStatusTextView.setText("Cancelled");
            holder.actionButton.setVisibility(View.GONE);
        } else if (Objects.equals(order.getCurrentStatus(), "Order cancelled due to rider missed pick up") || Objects.equals(order.getCurrentStatus(), "Order cancelled due to no rider accept order")) {
            holder.currentStatusTextView.setText("Cancelled");
            holder.actionButton.setText("Reschedule");
            holder.actionButton.setOnClickListener(view -> {
                final String[] selectedDate = {null};
                //rescheduling pick up time, show dialog, new rider pick up
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                calendar.add(Calendar.DAY_OF_MONTH, 1);
                long minDate = calendar.getTimeInMillis();

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.CustomDatePickerDialog, (view1, year1, month1, dayOfMonth) -> {
                    selectedDate[0] = (month1 + 1) + "/" + dayOfMonth + "/" + year1;
                    holder.currentStatusTextView.setText(selectedDate[0]);
                    holder.actionButton.setText("Submit");
                }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(minDate);
                datePickerDialog.show();

                String orderStatusId = String.valueOf(UUID.randomUUID());
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String dateTime = sdf2.format(new Date());

                holder.actionButton.setOnClickListener(view12 -> {
                    holder.mProgressBar.setVisibility(View.VISIBLE);

                    if (selectedDate[0]!=null) {
                        //update order pick up date, order current status and another table, order status add one
                        DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder");
                        DatabaseReference orderStatusRef = FirebaseDatabase.getInstance().getReference().child("orderStatus");

                        userOrderRef.child(order.getOrderId()).child("pickUpDate").setValue(selectedDate[0]);
                        userOrderRef.child(order.getOrderId()).child("currentStatus").setValue("Order created");
                        userOrderRef.child(order.getOrderId()).child("riderId").setValue("None");

                        OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime, "Order created");
                        orderStatusRef.child(order.getOrderId()).child(orderStatusId).setValue(mOrderStatusModel);

                        Toast.makeText(context, "Order created again, please refresh", Toast.LENGTH_SHORT).show();
                        holder.mProgressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(context, "Please select a new pick up date", Toast.LENGTH_SHORT).show();
                        holder.mProgressBar.setVisibility(View.GONE);
                    }

                });

            });
        }

        //show services list view
        List<ServiceItem> servicesList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : order.getSelectedServices().entrySet()) {
            servicesList.add(new ServiceItem(entry.getKey(), entry.getValue()));
        }

        mLaundryViewModel.getServiceData(order.getLaundryId()).observe((LifecycleOwner) context, service -> {
            if (service != null) {
                HistoryFragmentServicesAdapter mHistoryFragmentServicesAdapter = new HistoryFragmentServicesAdapter(servicesList, service, context, order.getLaundryId());
                holder.servicesRecyclerView.setAdapter(mHistoryFragmentServicesAdapter);
                holder.servicesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
        });

        holder.qrImageView.setOnClickListener(view -> {
            holder.mProgressBar.setVisibility(View.VISIBLE);
            String orderId = order.getOrderId();

            WindowManager manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = Math.min(width, height);
            smallerDimension = smallerDimension * 3 / 4;

            qrgEncoder = new QRGEncoder(orderId, null, QRGContents.Type.TEXT, smallerDimension);

            try {
                bitmap = qrgEncoder.encodeAsBitmap();
                Dialog qrCodeDialog = new Dialog(context);
                qrCodeDialog.setContentView(R.layout.dialog_qr_code);
                ImageView qrCodeImageView = qrCodeDialog.findViewById(R.id.qr_show);
                qrCodeImageView.setImageBitmap(bitmap);
                qrCodeDialog.show();
                holder.mProgressBar.setVisibility(View.GONE);
            } catch (WriterException e) {
                Log.v(TAG, e.toString());
                holder.mProgressBar.setVisibility(View.GONE);
            }

        });

        holder.moreImageView.setOnClickListener(view -> {
            //intent to order status history
            Intent intent = new Intent(context, HistoryOrderStatusActivity.class);
            intent.putExtra("HistoryOrderData", order);
            context.startActivity(intent);
        });
    }

    public String formatDateTime(String dateTime) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date date = originalFormat.parse(dateTime);

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            assert date != null;
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView laundryShopNameTextView, orderIdTextView, statusTextView, deliveryAmountTextView, totalAmountTextView, dateTextView, currentStatusTextView;
        RecyclerView servicesRecyclerView;
        ImageView moreImageView, qrImageView;
        Button actionButton;
        ProgressBar mProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            laundryShopNameTextView = itemView.findViewById(R.id.huli_laundry_shop_name);
            orderIdTextView = itemView.findViewById(R.id.huli_tv_show_order_id);
            servicesRecyclerView = itemView.findViewById(R.id.huli_lv_services);
            statusTextView = itemView.findViewById(R.id.huli_tv_status_content);
            moreImageView = itemView.findViewById(R.id.huli_iv_more_icon);
            deliveryAmountTextView = itemView.findViewById(R.id.huli_tv_delivery_fee_amount);
            totalAmountTextView = itemView.findViewById(R.id.huli_tv_total_amount);
            dateTextView = itemView.findViewById(R.id.huli_tv_date);
            currentStatusTextView = itemView.findViewById(R.id.huli_tv_status);
            actionButton = itemView.findViewById(R.id.huli_button);
            qrImageView = itemView.findViewById(R.id.huli_iv_qr);
            mProgressBar = itemView.findViewById(R.id.huli_progressBar);
        }
    }
}
