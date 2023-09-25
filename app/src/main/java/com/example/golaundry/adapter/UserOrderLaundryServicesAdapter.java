package com.example.golaundry.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.OrderActivity;
import com.example.golaundry.R;
import com.example.golaundry.holder.OrderServicesHolder;
import com.example.golaundry.model.LaundryServiceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UserOrderLaundryServicesAdapter extends RecyclerView.Adapter<UserOrderLaundryServicesAdapter.ViewHolder> {
    private final ArrayList<LaundryServiceModel> laundryServiceList;
    private final Context context;
    private final Map<LaundryServiceModel, Integer> serviceQuantityMap = new HashMap<>();
    double price;

    public UserOrderLaundryServicesAdapter(ArrayList<LaundryServiceModel> laundryServiceList, Context context) {
        this.laundryServiceList = laundryServiceList;
        this.context = context;

        for (LaundryServiceModel service : laundryServiceList) {
            serviceQuantityMap.put(service, 0);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_service, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LaundryServiceModel service = laundryServiceList.get(position);

        Integer currentQty = serviceQuantityMap.get(service);

        if (currentQty == null) {
            currentQty = 0;
            serviceQuantityMap.put(service, 0);
        }

        //bind data
        holder.serviceNameTextView.setText(service.getName());
        holder.serviceDescriptionTextView.setText(service.getDescription());
        price = service.getPrice() * 2; //multiply by 2
        @SuppressLint("DefaultLocale")
        String finalPrice = String.format("%.2f", price);
        String quantity = service.getQuantity();
        holder.servicePricePerQtyTextView.setText("RM" + finalPrice + " for each " + quantity);

        final AtomicInteger finalQty = new AtomicInteger(currentQty);
        holder.addQtyImageView.setOnClickListener(view -> {
            int newQty = finalQty.incrementAndGet();
            holder.qtyTextView.setText(String.valueOf(newQty));
            serviceQuantityMap.put(service, newQty);
        });

        holder.lessQtyImageView.setOnClickListener(view -> {
            if (finalQty.get() > 0) {
                int newQty = finalQty.decrementAndGet();
                holder.qtyTextView.setText(String.valueOf(newQty));
                serviceQuantityMap.put(service, newQty);
            }
        });

    }

    public Map<String, Integer> getSelectedServices() {
        Map<String, Integer> selectedServices = new HashMap<>();
        boolean hasNonZeroQty = false;

        for (LaundryServiceModel service : laundryServiceList) {
            Integer currentQty = serviceQuantityMap.get(service);
            if (currentQty == null || currentQty == 0) {
                //
            } else if (currentQty > 0) {
                selectedServices.put(service.getName(), currentQty);
                hasNonZeroQty = true;
            }
        }
        if (!hasNonZeroQty) {
            Toast.makeText(context.getApplicationContext(), "Please select at least one service", Toast.LENGTH_SHORT).show();
        }
        return selectedServices;
    }

    public Map<String, OrderServicesHolder> getServicesInfo() {
        Map<String, OrderServicesHolder> servicesInfo = new HashMap<>();

        for (LaundryServiceModel service : laundryServiceList) {
            Integer currentQty = serviceQuantityMap.get(service);
            if (currentQty == null || currentQty == 0) {
                //
            } else if (currentQty > 0) {
                servicesInfo.put(service.getName(), new OrderServicesHolder(service.getName(),price,service.getQuantity(),currentQty));
            }
        }

        return servicesInfo;
    }

    @Override
    public int getItemCount() {
        return laundryServiceList != null ? laundryServiceList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView;
        TextView serviceDescriptionTextView;
        TextView servicePricePerQtyTextView,qtyTextView;
        ImageView addQtyImageView,lessQtyImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.os_tv_service_name);
            serviceDescriptionTextView = itemView.findViewById(R.id.os_tv_description);
            servicePricePerQtyTextView = itemView.findViewById(R.id.os_tv_per_qty);
            addQtyImageView = itemView.findViewById(R.id.os_iv_add);
            lessQtyImageView = itemView.findViewById(R.id.os_iv_less);
            qtyTextView = itemView.findViewById(R.id.os_tv_number);
        }
    }
}
