package com.example.golaundry.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.R;
import com.example.golaundry.model.CombineLaundryData;
import com.example.golaundry.model.LaundryModel;

import java.util.ArrayList;
import java.util.List;

public class UserOrderShowLaundryAdapter extends RecyclerView.Adapter<UserOrderShowLaundryAdapter.ViewHolder> {

    private final List<CombineLaundryData> laundryList;
    private final Context context;

    public UserOrderShowLaundryAdapter(List<CombineLaundryData> laundryList, Context context) {
        this.laundryList = laundryList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_info_card, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CombineLaundryData laundry = laundryList.get(position);

        //bind data
        holder.shopNameTextView.setText(laundry.getLaundry().getShopName());
        holder.ratingsTextView.setText("5");
        holder.kmTextView.setText("5km");

    }

    @Override
    public int getItemCount() {
        return laundryList != null ? laundryList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView laundryImageView,savedImageView;
        TextView shopNameTextView, ratingsTextView, kmTextView;
        RatingBar ratingsBar;

        public ViewHolder(View itemView) {
            super(itemView);
            laundryImageView = itemView.findViewById(R.id.lic_laundry_image);
            shopNameTextView = itemView.findViewById(R.id.lic_tv_laundry_name);
            ratingsTextView = itemView.findViewById(R.id.lic_tv_rating);
            ratingsBar = itemView.findViewById(R.id.lic_rating_star);
            kmTextView = itemView.findViewById(R.id.lic_tv_distance_km);
            savedImageView = itemView.findViewById(R.id.lic_iv_saved);
        }
    }
}
