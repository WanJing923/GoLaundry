package com.example.golaundry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.R;
import com.example.golaundry.model.LaundryServiceModel;

import java.util.List;

public class HashtagAdapter extends RecyclerView.Adapter<HashtagAdapter.ViewHolder> {
    private final List<LaundryServiceModel> serviceList;

    public HashtagAdapter(List<LaundryServiceModel> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hashtag_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LaundryServiceModel hashtag = serviceList.get(position);
        String name = hashtag.getName();
        holder.serviceNameTextView.setText(name);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.hashtag_tv_service_name);
        }
    }
}
