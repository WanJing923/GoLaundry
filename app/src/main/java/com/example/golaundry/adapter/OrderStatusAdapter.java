package com.example.golaundry.adapter;

import com.example.golaundry.model.OrderStatusModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.ViewHolder> {
    private final List<OrderStatusModel> orderStatusList;
    private final Context context;

    public OrderStatusAdapter(List<OrderStatusModel> orderStatusList, Context context) {
        this.orderStatusList = orderStatusList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_status_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderStatusModel orderStatus = orderStatusList.get(position);
        holder.statusTextView.setText(orderStatus.getStatusContent());

        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        SimpleDateFormat userFriendlyDateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault());
        String dateTime = orderStatus.getDateTime();
        try {
            Date formattedDate = DateFormat.parse(dateTime);
            if (formattedDate != null) {
                String userFriendlyDateTime = userFriendlyDateFormat.format(formattedDate);
                holder.dateTimeTextView.setText(userFriendlyDateTime);
            } else {
                holder.dateTimeTextView.setText("Invalid Date");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return orderStatusList != null ? orderStatusList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTimeTextView, statusTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTimeTextView = itemView.findViewById(R.id.osi_tv_dateTime);
            statusTextView = itemView.findViewById(R.id.osi_tv_status);
        }
    }
}
