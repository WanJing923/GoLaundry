package com.example.golaundry.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.view.HistoryOrderStatusActivity;
import com.example.golaundry.R;
import com.example.golaundry.view.ViewNotesActivity;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.OrderStatusModel;
import com.example.golaundry.model.ServiceItem;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.RiderViewModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HistoryLaundryFragmentAdapter extends RecyclerView.Adapter<HistoryLaundryFragmentAdapter.ViewHolder> {
    private final List<OrderModel> orderList;
    private final Context context;
    private final LaundryViewModel mLaundryViewModel;
    private final UserViewModel mUserViewModel;
    private double cancelBalance;
    private String currentUserId;

    public HistoryLaundryFragmentAdapter(List<OrderModel> orderList, Context context, LaundryViewModel mLaundryViewModel, UserViewModel mUserViewModel, RiderViewModel mRiderViewModel) {
        this.orderList = orderList;
        this.context = context;
        this.mLaundryViewModel = mLaundryViewModel;
        this.mUserViewModel = mUserViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_user_list_item, parent, false);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        return new ViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        holder.orderIdTextView.setText(order.getOrderId());
        holder.statusTextView.setText(order.getCurrentStatus());
        holder.deliveryAmountTextView.setVisibility(View.GONE);
        holder.deliveryFeeTextView.setVisibility(View.GONE);
        holder.deliveryFeeRMTextView.setVisibility(View.GONE);
        holder.qrImageView.setVisibility(View.GONE);
        holder.userRoleImageView.setImageResource(R.drawable.ic_user);

        double total = order.getLaundryFee() / 2;
        @SuppressLint("DefaultLocale")
        String totalShow = String.format("%.2f", total);
        holder.totalAmountTextView.setText(totalShow);

        String datePickUp = order.getPickUpDate();
        String formattedDatePick = formatDateTimeLaundryPickUp(datePickUp);
        holder.pickUpDateTextView.setText("Pick up on " + formattedDatePick);

        String date = order.getDateTime();
        String formattedDate = formatDateTimeLaundry(date);
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
                holder.laundryShopNameTextView.setText(userModel.getFullName());
            }
        });

        //Pending process
        if (Objects.equals(order.getCurrentStatus(), "Order created")) {
            holder.currentStatusTextView.setText("Pending Process");
            holder.actionButton.setText("CANCEL");
            holder.actionButton.setOnClickListener(view -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Cancel Order Confirmation");
                builder.setMessage("Are you sure you want to cancel this order?");

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    holder.actionButton.setText("LOADING");
                    // update order status history, order current status, user balance, user spending, user total order
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(order.getUserId());
                    DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder").child(order.getOrderId());
                    DatabaseReference orderStatusRef = FirebaseDatabase.getInstance().getReference().child("orderStatus").child(order.getOrderId());
                    DatabaseReference userSpendingRef = FirebaseDatabase.getInstance().getReference().child("userSpending").child(order.getUserId());
                    DatabaseReference userTotalOrderRef = FirebaseDatabase.getInstance().getReference().child("userTotalOrder").child(order.getUserId());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String dateTime = sdf.format(new Date());
                    OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime, "Order cancelled by laundry shop");
                    String orderStatusId = String.valueOf(UUID.randomUUID());

                    orderStatusRef.child(orderStatusId).setValue(mOrderStatusModel).addOnSuccessListener(aVoid -> { //order status history
                        userOrderRef.child("currentStatus").setValue("Order cancelled by laundry shop").addOnSuccessListener(aVoid1 -> { //order current status
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
                                    }
                                });
                            }).addOnFailureListener(e -> {
                            });
                        }).addOnFailureListener(e -> {
                        });
                    }).addOnFailureListener(e -> {
                    });

                    dialog.dismiss();
                });

                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
            });
        } else if (Objects.equals(order.getCurrentStatus(), "Rider accept order")) {
            holder.currentStatusTextView.setText("Pending Process");
            holder.actionButton.setVisibility(View.GONE);
        } else if (Objects.equals(order.getCurrentStatus(), "Rider pick up")) {
            holder.currentStatusTextView.setText("Pending Process");
            holder.actionButton.setText("Received and proceed");
            //update order table, order status table, to Order reached laundry shop
            holder.actionButton.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Receive and proceed to order confirmation");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    holder.actionButton.setText("LOADING");
                    // update order status history, order current status
                    DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder").child(order.getOrderId());
                    DatabaseReference orderStatusRef = FirebaseDatabase.getInstance().getReference().child("orderStatus").child(order.getOrderId());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String dateTime = sdf.format(new Date());
                    OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime, "Order reached laundry shop");

                    userOrderRef.child("laundryId").setValue(currentUserId).addOnSuccessListener(aVoid -> { //update order table
                        userOrderRef.child("currentStatus").setValue("Order reached laundry shop").addOnSuccessListener(aVoid1 -> { //update order table
                            String orderStatusId = String.valueOf(UUID.randomUUID());
                            orderStatusRef.child(orderStatusId).setValue(mOrderStatusModel).addOnSuccessListener(aVoid2 -> { //add order status table
                                Toast.makeText(context, "Order reached laundry shop", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                //
                            });
                        }).addOnFailureListener(e -> {
                            //
                        });
                    }).addOnFailureListener(e -> {
                        //
                    });
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
            });
        } else if (Objects.equals(order.getCurrentStatus(), "Order reached laundry shop")) {
            holder.currentStatusTextView.setText("Pending Process");
            holder.actionButton.setText("Done");
            //update order table, order status table, to Laundry done process
            holder.actionButton.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Done order confirmation");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    holder.actionButton.setText("LOADING");
                    // update order status history, order current status
                    DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder").child(order.getOrderId());
                    DatabaseReference orderStatusRef = FirebaseDatabase.getInstance().getReference().child("orderStatus").child(order.getOrderId());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String dateTime = sdf.format(new Date());
                    OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime, "Laundry done process");

                    userOrderRef.child("laundryId").setValue(currentUserId).addOnSuccessListener(aVoid -> { //update order table
                        userOrderRef.child("currentStatus").setValue("Laundry done process").addOnSuccessListener(aVoid1 -> { //update order table
                            String orderStatusId = String.valueOf(UUID.randomUUID());
                            orderStatusRef.child(orderStatusId).setValue(mOrderStatusModel).addOnSuccessListener(aVoid2 -> { //add order status table
                                Toast.makeText(context, "Laundry done process", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                //
                            });
                        }).addOnFailureListener(e -> {
                            //
                        });
                    }).addOnFailureListener(e -> {
                        //
                    });
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
            });
        } else if (Objects.equals(order.getCurrentStatus(), "Laundry done process") || Objects.equals(order.getCurrentStatus(), "Order out of delivery")
                || Objects.equals(order.getCurrentStatus(), "Order delivered")) {
            holder.currentStatusTextView.setText("Pending Confirm");
            holder.actionButton.setVisibility(View.GONE);
        } else if (Objects.equals(order.getCurrentStatus(), "Order completed")) {
            holder.currentStatusTextView.setText("Completed");
            holder.actionButton.setVisibility(View.GONE);
        } else if (Objects.equals(order.getCurrentStatus(), "Order cancelled by customer") || (Objects.equals(order.getCurrentStatus(), "Order cancelled by laundry shop"))) {
            holder.currentStatusTextView.setText("Cancelled");
            holder.actionButton.setVisibility(View.GONE);
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

        holder.moreImageView.setOnClickListener(view -> {
            //intent to order status history
            Intent intent = new Intent(context, HistoryOrderStatusActivity.class);
            intent.putExtra("HistoryOrderData", order);
            intent.putExtra("isLaundry", true);
            context.startActivity(intent);
        });

        holder.viewNoteTextView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewNotesActivity.class);
            intent.putExtra("HistoryOrderData", order);
            context.startActivity(intent);
        });
    }

    public String formatDateTimeLaundry(String dateTime) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date date = originalFormat.parse(dateTime);

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");

            assert date != null;
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    public String formatDateTimeLaundryPickUp(String dateTime) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat originalFormat = new SimpleDateFormat("M/d/yyyy");
            Date date = originalFormat.parse(dateTime);

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");

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
        TextView laundryShopNameTextView, orderIdTextView, statusTextView, viewNoteTextView, deliveryAmountTextView, totalAmountTextView, pickUpDateTextView, dateTextView, currentStatusTextView, deliveryFeeTextView, deliveryFeeRMTextView;
        RecyclerView servicesRecyclerView;
        ImageView moreImageView, qrImageView, userRoleImageView;
        Button actionButton;

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
            pickUpDateTextView = itemView.findViewById(R.id.pick_up_date);
            currentStatusTextView = itemView.findViewById(R.id.huli_tv_status);
            actionButton = itemView.findViewById(R.id.huli_button);
            qrImageView = itemView.findViewById(R.id.huli_iv_qr);
            deliveryFeeTextView = itemView.findViewById(R.id.huli_tv_delivery_fee);
            deliveryFeeRMTextView = itemView.findViewById(R.id.huli_tv_delivery_fee_rm);
            userRoleImageView = itemView.findViewById(R.id.huli_iv_icon);
            viewNoteTextView = itemView.findViewById(R.id.huli_tv_view_note);
        }
    }
}
