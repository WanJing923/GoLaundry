package com.example.golaundry.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.view.HistoryOrderStatusActivity;
import com.example.golaundry.R;
import com.example.golaundry.view.RiderOrderDetailsActivity;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.RiderViewModel;
import com.example.golaundry.viewModel.UserViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class HistoryRiderFragmentAdapter extends RecyclerView.Adapter<HistoryRiderFragmentAdapter.ViewHolder> {
    private final List<OrderModel> orderList;
    private final Context context;
    private final LaundryViewModel mLaundryViewModel;
    private final UserViewModel mUserViewModel;

    public HistoryRiderFragmentAdapter(List<OrderModel> orderList, Context context, LaundryViewModel mLaundryViewModel, UserViewModel mUserViewModel, RiderViewModel mRiderViewModel) {
        this.orderList = orderList;
        this.context = context;
        this.mLaundryViewModel = mLaundryViewModel;
        this.mUserViewModel = mUserViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_rider_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        holder.statusContentTextView.setText(order.getCurrentStatus());

        double distance = order.getDeliveryFee();
        @SuppressLint("DefaultLocale")
        String distanceShow = String.format("%.2f", distance);
        holder.amountTextView.setText(distanceShow);

        String date = order.getDateTime();
        String formattedDateOrder = formatOrderDateTime(date);
        holder.dateTextView.setText("Order on " + formattedDateOrder);

        mUserViewModel.getUserData(order.getUserId()).observe((LifecycleOwner) context, userModel -> {
            if (userModel != null) {
                holder.userNameTextView.setText(userModel.getFullName());
            }
        });

        mLaundryViewModel.getLaundryData(order.getLaundryId()).observe((LifecycleOwner) context, laundryModel -> {
            if (laundryModel != null) {
                holder.laundryShopNameTextView.setText("Laundry Shop Name: " + laundryModel.getShopName());
            }
        });

        holder.moreImageView.setOnClickListener(view -> {
            Intent intent = new Intent(context, HistoryOrderStatusActivity.class);
            intent.putExtra("HistoryOrderData", order);
            intent.putExtra("isRider", true);
            context.startActivity(intent);
        });

        if (Objects.equals(order.getCurrentStatus(), "Rider accept order")) {
            holder.orderIdTextView.setText("Pending Pick Up");
            holder.currentStatusTextView.setText("Pending Pick Up");
        } else if (Objects.equals(order.getCurrentStatus(), "Rider pick up") || Objects.equals(order.getCurrentStatus(), "Order reached laundry shop")
                || Objects.equals(order.getCurrentStatus(), "Laundry done process") || Objects.equals(order.getCurrentStatus(), "Order out of delivery")
                || Objects.equals(order.getCurrentStatus(), "Order delivered")) {
            holder.orderIdTextView.setText(order.getOrderId());
            holder.currentStatusTextView.setText("Pending Deliver");
        } else if (Objects.equals(order.getCurrentStatus(), "Order completed")) {
            holder.orderIdTextView.setText(order.getOrderId());
            holder.currentStatusTextView.setText("Completed");
        } else if (Objects.equals(order.getCurrentStatus(), "Order cancelled by user") || (Objects.equals(order.getCurrentStatus(), "Order cancelled by laundry shop"))) {
            holder.orderIdTextView.setText(order.getOrderId());
            holder.currentStatusTextView.setText("Cancelled");
        }

        holder.viewOrderTextView.setOnClickListener(view -> {
            Intent intent = new Intent(context, RiderOrderDetailsActivity.class);
            intent.putExtra("RiderViewOrderDetailsData", order);
            context.startActivity(intent);
        });
    }

    public String formatOrderDateTime(String dateTime) {
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

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, viewOrderTextView, orderIdTextView,laundryShopNameTextView, amountTextView, dateTextView, currentStatusTextView, statusContentTextView;
        ImageView moreImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.hrli_user_name);
            viewOrderTextView = itemView.findViewById(R.id.hrli_iv_view_order);
            orderIdTextView = itemView.findViewById(R.id.hrli_tv_show_order_id);
            moreImageView = itemView.findViewById(R.id.hrli_iv_more_icon);
            laundryShopNameTextView = itemView.findViewById(R.id.hrli_tv_laundry_shop_name);
            statusContentTextView = itemView.findViewById(R.id.hrli_tv_status_content);
            dateTextView = itemView.findViewById(R.id.hrli_tv_date);
            amountTextView = itemView.findViewById(R.id.hrli_tv_delivery_amount);
            currentStatusTextView = itemView.findViewById(R.id.hrli_tv_status);
        }
    }
}
