package com.example.golaundry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.R;
import com.example.golaundry.model.LaundryServiceModel;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    private final ArrayList<LaundryServiceModel> serviceList;

    public ServiceAdapter(ArrayList<LaundryServiceModel> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LaundryServiceModel service = serviceList.get(position);

        //bind data
        holder.serviceNameEditText.setText(service.getName());
        holder.serviceDescriptionEditText.setText(service.getDescription());
        String price = String.valueOf(service.getPrice());
        holder.servicePriceEditText.setText(price);
        String quantity = String.valueOf(service.getQuantity());
        holder.servicePriceForEachEditText.setText(quantity);
    }

    @Override
    public int getItemCount() {
        return serviceList != null ? serviceList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText serviceNameEditText;
        EditText serviceDescriptionEditText;
        EditText servicePriceEditText;
        EditText servicePriceForEachEditText;

        public ViewHolder(View itemView) {
            super(itemView);
            serviceNameEditText = itemView.findViewById(R.id.service_et_name);
            serviceDescriptionEditText = itemView.findViewById(R.id.service_et_desc);
            servicePriceEditText = itemView.findViewById(R.id.service_et_price);
            servicePriceForEachEditText = itemView.findViewById(R.id.service_et_price_for_each);
        }
    }
}
