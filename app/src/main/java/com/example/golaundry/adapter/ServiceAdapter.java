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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.R;
import com.example.golaundry.model.LaundryServiceModel;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    private final ArrayList<LaundryServiceModel> serviceList;
    private final Context context;

    public ServiceAdapter(ArrayList<LaundryServiceModel> serviceList, Context context) {
        this.serviceList = serviceList;
        this.context = context;
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
        @SuppressLint("DefaultLocale")
        String price = String.format("%.2f", (service.getPrice()));
        holder.servicePriceEditText.setText(price);
        String quantity = String.valueOf(service.getQuantity());
        holder.servicePriceForEachEditText.setText(quantity);

        holder.deleteImageView.setOnClickListener(v -> {
            removeItemWithConfirmation(position);
        });
    }

    public void removeItemWithConfirmation(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this item?");

        SpannableString spannableStringYes = new SpannableString("Yes");
        spannableStringYes.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableStringYes.length(), 0);

        SpannableString spannableStringNo = new SpannableString("No");
        spannableStringNo.setSpan(new ForegroundColorSpan(Color.GRAY), 0, spannableStringNo.length(), 0);

        builder.setPositiveButton(spannableStringYes, (dialog, which) -> {
            serviceList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        });
        builder.setNegativeButton(spannableStringNo, (dialog, which) -> {
            //no action
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
        ImageView deleteImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            serviceNameEditText = itemView.findViewById(R.id.service_et_name);
            serviceDescriptionEditText = itemView.findViewById(R.id.service_et_desc);
            servicePriceEditText = itemView.findViewById(R.id.service_et_price);
            servicePriceForEachEditText = itemView.findViewById(R.id.service_et_price_for_each);
            deleteImageView = itemView.findViewById(R.id.service_iv_delete);
        }
    }
}
