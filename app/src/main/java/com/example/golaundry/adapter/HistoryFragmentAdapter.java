package com.example.golaundry.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
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
import java.util.Map;
import java.util.Objects;

public class HistoryFragmentAdapter extends RecyclerView.Adapter<HistoryFragmentAdapter.ViewHolder> {
    private final List<OrderModel> orderList;
    private final Context context;
    private final LaundryViewModel mLaundryViewModel;

    public HistoryFragmentAdapter(List<OrderModel> orderList, Context context, LaundryViewModel mLaundryViewModel) {
        this.orderList = orderList;
        this.context = context;
        this.mLaundryViewModel = mLaundryViewModel;
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

        //Pending collection
        if (Objects.equals(order.getCurrentStatus(), "Order created")) {
            holder.currentStatusTextView.setText("Pending Collection");
            holder.actionButton.setText("CANCEL");
        }
        //rider accept order, cant cancel order
        else if (Objects.equals(order.getCurrentStatus(), "Rider accept order")) {
            holder.actionButton.setVisibility(View.GONE);
        }
        //pending receiving
        else if (Objects.equals(order.getCurrentStatus(), "Rider pick up")
                && Objects.equals(order.getCurrentStatus(), "Order reached laundry shop")
                && Objects.equals(order.getCurrentStatus(), "Laundry done process")
                && Objects.equals(order.getCurrentStatus(), "Order out of delivery")
                && Objects.equals(order.getCurrentStatus(), "Order delivered")) {
            holder.currentStatusTextView.setText("Pending Receiving");
            holder.actionButton.setText("RECEIVE");
        }
        //completed
        else if (Objects.equals(order.getCurrentStatus(), "Order completed")) {
            holder.currentStatusTextView.setText("Completed");
            holder.actionButton.setText("RATE");
        }
        //cancelled
        else if (Objects.equals(order.getCurrentStatus(), "Order cancelled")) {
            holder.currentStatusTextView.setText("Cancelled");
        }

        //show services list view
        List<ServiceItem> servicesList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : order.getSelectedServices().entrySet()) {
            servicesList.add(new ServiceItem(entry.getKey(), entry.getValue()));
        }

        mLaundryViewModel.getServiceData(order.getLaundryId()).observe((LifecycleOwner) context, service -> {
            if (service!=null){
                HistoryFragmentServicesAdapter mHistoryFragmentServicesAdapter = new HistoryFragmentServicesAdapter(servicesList, service, context);
                holder.servicesRecyclerView.setAdapter(mHistoryFragmentServicesAdapter);
                holder.servicesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
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
        ImageView moreImageView;
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
        }
    }
}
