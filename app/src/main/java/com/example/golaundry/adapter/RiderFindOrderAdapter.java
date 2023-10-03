package com.example.golaundry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.R;
import com.example.golaundry.model.RiderFindOrderHolder;

import java.util.List;

public class RiderFindOrderAdapter extends RecyclerView.Adapter<RiderFindOrderAdapter.ViewHolder> {
//    private final List<LaundryServiceModel> serviceList;

    public RiderFindOrderAdapter(List<RiderFindOrderHolder> orderList) {
//        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rider_find_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        LaundryServiceModel hashtag = serviceList.get(position);
//        String name = hashtag.getName();
//        holder.serviceNameTextView.setText(name);
    }

    @Override
    public int getItemCount() {
//        return addressList != null ? addressList.size() : 0;
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fromAddressTextView,laundryNameTextView,toAddressTextView,earnAmountTextView,viewOrderTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            fromAddressTextView = itemView.findViewById(R.id.rfo_tv_from_address);
            laundryNameTextView = itemView.findViewById(R.id.rfo_tv_laundry_name);
            toAddressTextView = itemView.findViewById(R.id.rfo_tv_address_to);
            earnAmountTextView = itemView.findViewById(R.id.rfo_tv_money);
            viewOrderTextView = itemView.findViewById(R.id.rfo_tv_view_order);
        }
    }
}
