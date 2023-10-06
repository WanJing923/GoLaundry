package com.example.golaundry.adapter;

import static android.content.Context.WINDOW_SERVICE;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.golaundry.HistoryOrderStatusActivity;
import com.example.golaundry.OrderLocationActivity;
import com.example.golaundry.RiderViewOrderActivity;
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
                    holder.actionButton.setText("LOADING");
                    // update order status history, order current status, user balance, user spending, user total order
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(order.getUserId());
                    DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference().child("userOrder").child(order.getOrderId());
                    DatabaseReference orderStatusRef = FirebaseDatabase.getInstance().getReference().child("orderStatus").child(order.getOrderId());
                    DatabaseReference userSpendingRef = FirebaseDatabase.getInstance().getReference().child("userSpending").child(order.getUserId());
                    DatabaseReference userTotalOrderRef = FirebaseDatabase.getInstance().getReference().child("userTotalOrder").child(order.getUserId());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String dateTime = sdf.format(new Date());
                    OrderStatusModel mOrderStatusModel = new OrderStatusModel(dateTime, "Order cancelled");
                    String orderStatusId = String.valueOf(UUID.randomUUID());

                    orderStatusRef.child(orderStatusId).setValue(mOrderStatusModel).addOnSuccessListener(aVoid -> { //order status history
                        userOrderRef.child("currentStatus").setValue("Order cancelled").addOnSuccessListener(aVoid1 -> { //order current status
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
                                                                        if (!latestYear1.equals("") && !Objects.equals(latestMonth1, "") ) {
                                                                            userTotalOrderRef.child(latestYear1).child(latestMonth1).setValue(updatedNumber).addOnSuccessListener(aVoid4 -> {//deduct order total number
                                                                                Toast.makeText(context, "Order cancelled, please refresh.", Toast.LENGTH_SHORT).show();
                                                                                holder.actionButton.setText("DONE");
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
        }
        //rider accept order, cant cancel order
        else if (Objects.equals(order.getCurrentStatus(), "Rider accept order")) {
            holder.actionButton.setVisibility(View.GONE);
        }
        //pending receiving
        else if (Objects.equals(order.getCurrentStatus(), "Rider pick up") && Objects.equals(order.getCurrentStatus(), "Order reached laundry shop")
                && Objects.equals(order.getCurrentStatus(), "Laundry done process") && Objects.equals(order.getCurrentStatus(), "Order out of delivery")) {
            holder.currentStatusTextView.setText("Pending Receiving");
        } else if (Objects.equals(order.getCurrentStatus(), "Order delivered")) {
            holder.currentStatusTextView.setText("Pending Receiving");
            holder.actionButton.setText("RECEIVE");
            holder.actionButton.setOnClickListener(view -> {
                //received, update order status and order status history
                // add laundry_rider each 2 tables dashboard
            });
        }
        //completed
        else if (Objects.equals(order.getCurrentStatus(), "Order completed")) {
            holder.currentStatusTextView.setText("Completed");
            holder.actionButton.setText("RATE");
            holder.actionButton.setOnClickListener(view -> {
                // go ratings feature
            });
        }
        //cancelled
        else if (Objects.equals(order.getCurrentStatus(), "Order cancelled")) {
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
                HistoryFragmentServicesAdapter mHistoryFragmentServicesAdapter = new HistoryFragmentServicesAdapter(servicesList, service, context);
                holder.servicesRecyclerView.setAdapter(mHistoryFragmentServicesAdapter);
                holder.servicesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
        });

        holder.qrImageView.setOnClickListener(view -> {
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
            } catch (WriterException e) {
                Log.v(TAG, e.toString());
            }

        });

        holder.moreImageView.setOnClickListener(view -> {
            //intent to order status history
            Intent intent = new Intent(context, HistoryOrderStatusActivity.class);
            intent.putExtra("HistoryOrderData", order); //need to change
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
        }
    }
}
