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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.R;
import com.example.golaundry.model.LaundryServiceModel;

import java.util.ArrayList;

public class ShowServiceAdapter extends RecyclerView.Adapter<ShowServiceAdapter.ViewHolder> {
    private final ArrayList<LaundryServiceModel> laundryServiceList;
    private final Context context;

    public ShowServiceAdapter(ArrayList<LaundryServiceModel> laundryServiceList, Context context) {
        this.laundryServiceList = laundryServiceList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_service_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LaundryServiceModel service = laundryServiceList.get(position);

        //bind data
        holder.serviceNameTextView.setText(service.getName());
        holder.serviceDescriptionTextView.setText(service.getDescription());
        @SuppressLint("DefaultLocale")
        String price = String.format("%.2f", (service.getPrice()));
        holder.servicePriceTextView.setText(price);
        String quantity = String.valueOf(service.getQuantity());
        holder.servicePriceForEachTextView.setText(quantity);
    }

    @Override
    public int getItemCount() {
        return laundryServiceList != null ? laundryServiceList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView;
        TextView serviceDescriptionTextView;
        TextView servicePriceTextView;
        TextView servicePriceForEachTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.ls_tv_name);
            serviceDescriptionTextView = itemView.findViewById(R.id.ls_tv_desc);
            servicePriceTextView = itemView.findViewById(R.id.ls_tv_price);
            servicePriceForEachTextView = itemView.findViewById(R.id.ls_tv_price_for_each);
        }
    }
}
