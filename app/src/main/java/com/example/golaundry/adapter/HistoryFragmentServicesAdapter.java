package com.example.golaundry.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.R;
import com.example.golaundry.model.LaundryServiceModel;
import com.example.golaundry.model.ServiceItem;

import java.util.List;

public class HistoryFragmentServicesAdapter extends RecyclerView.Adapter<HistoryFragmentServicesAdapter.ViewHolder> {
    private final List<ServiceItem> servicesList;
    private final Context context;
    private final List<LaundryServiceModel> serviceLaundry;

    public HistoryFragmentServicesAdapter(List<ServiceItem> servicesList, List<LaundryServiceModel> serviceLaundry, Context context) {
        this.servicesList = servicesList;
        this.serviceLaundry = serviceLaundry;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceItem service = servicesList.get(position);
        LaundryServiceModel serviceItem = null;

        holder.serviceNameTextView.setText(service.getKey());
        String qty = String.valueOf(service.getValue());
        holder.qtyTextView.setText(qty);
        double selectedQty = service.getValue();

        for (LaundryServiceModel serviceAllItem : serviceLaundry) {
            if (serviceAllItem.getName().equals(service.getKey())) {
                serviceItem = serviceAllItem;
                break;
            }
        }

        if (serviceItem != null) {
            double shopServicePrice = serviceItem.getPrice();
            double subTotal = shopServicePrice * 2 * selectedQty;
            @SuppressLint("DefaultLocale")
            String totalShow = String.format("%.2f", subTotal);
            holder.amountTextView.setText(totalShow);
        }

    }

    @Override
    public int getItemCount() {
        return servicesList != null ? servicesList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView, qtyTextView, amountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.row_service);
            qtyTextView = itemView.findViewById(R.id.row_tv_show_qty);
            amountTextView = itemView.findViewById(R.id.row_tv_show_rm);
        }
    }
}
