package com.example.golaundry.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.OrderActivity;
import com.example.golaundry.OrderLocationActivity;
import com.example.golaundry.R;
import com.example.golaundry.RiderViewOrderActivity;
import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.RiderFindOrderHolder;
import com.example.golaundry.viewModel.LaundryViewModel;

import java.util.List;

public class RiderFindOrderAdapter extends RecyclerView.Adapter<RiderFindOrderAdapter.ViewHolder> {
    private final List<RiderFindOrderHolder> orderList;
    private final Context context;
    private LaundryViewModel mLaundryViewModel;
    private LaundryModel laundryData;

    public RiderFindOrderAdapter(List<RiderFindOrderHolder> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rider_find_order, parent, false);
        mLaundryViewModel = new ViewModelProvider((FragmentActivity) context).get(LaundryViewModel.class);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RiderFindOrderHolder order = orderList.get(position);

        double distance = order.getDistance();
        @SuppressLint("DefaultLocale")
        String distanceShow = String.format("%.2f", distance);
        holder.distanceTextView.setText(distanceShow);

        String fromAddress = order.getOrderData().getAddressInfo().get("address");
        holder.fromAddressTextView.setText(fromAddress);

        mLaundryViewModel.getLaundryData(order.getOrderData().getLaundryId()).observe((LifecycleOwner) context, laundry -> {
            if (laundry != null) {
                String laundryName = laundry.getShopName();
                holder.laundryNameTextView.setText(laundryName);
                String laundryAddress = laundry.getAddress();
                holder.toAddressTextView.setText(laundryAddress);
            }
        });

        double earnings = order.getOrderData().getDeliveryFee();
        @SuppressLint("DefaultLocale")
        String earnAmount = String.format("%.2f", earnings);
        holder.earnAmountTextView.setText(earnAmount);

        holder.viewOrderTextView.setOnClickListener(view -> {
            Intent intent = new Intent(context, RiderViewOrderActivity.class);
            OrderModel mOrderModel = order.getOrderData();
            intent.putExtra("RiderViewOrderData", mOrderModel);
            intent.putExtra("distance", distance);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fromAddressTextView, laundryNameTextView, toAddressTextView, earnAmountTextView, viewOrderTextView, distanceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            fromAddressTextView = itemView.findViewById(R.id.rfo_tv_from_address);
            laundryNameTextView = itemView.findViewById(R.id.rfo_tv_laundry_name);
            toAddressTextView = itemView.findViewById(R.id.rfo_tv_address_to);
            earnAmountTextView = itemView.findViewById(R.id.rfo_tv_money);
            viewOrderTextView = itemView.findViewById(R.id.rfo_tv_view_order);
            distanceTextView = itemView.findViewById(R.id.rfo_tv_distance_number);
        }
    }
}
