package com.example.golaundry.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.R;
import com.example.golaundry.view.ReportActivity;
import com.example.golaundry.model.RateRiderModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.RiderViewModel;
import com.example.golaundry.viewModel.UserViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RatingsRiderAdapter extends RecyclerView.Adapter<RatingsRiderAdapter.ViewHolder> {
    private final List<RateRiderModel> ratingsList;
    private final Context context;
    private final LaundryViewModel mLaundryViewModel;
    private final UserViewModel mUserViewModel;
    private final RiderViewModel mRiderViewModel;
    private AppCompatActivity activity;

    public RatingsRiderAdapter(AppCompatActivity activity, List<RateRiderModel> ratingsList, Context context, LaundryViewModel mLaundryViewModel, UserViewModel mUserViewModel, RiderViewModel mRiderViewModel) {
        this.activity = activity;
        this.ratingsList = ratingsList;
        this.context = context;
        this.mLaundryViewModel = mLaundryViewModel;
        this.mUserViewModel = mUserViewModel;
        this.mRiderViewModel = mRiderViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RateRiderModel ratings = ratingsList.get(position);

        mUserViewModel.getUserData(ratings.getUserId()).observe((LifecycleOwner) context, userModel -> {
            if (userModel != null) {
                holder.userNameTextView.setText(userModel.getFullName());
            }
        });

        holder.orderIdTextView.setText(ratings.getOrderId());
        holder.commentTextView.setText(ratings.getCommentToRider());
        float rateAmount = ratings.getRateToRider();
        holder.starRatingBar.setRating(rateAmount);

        if (rateAmount <= 3){
            holder.reportTextView.setVisibility(View.VISIBLE);
            holder.reportTextView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ReportActivity.class);
                intent.putExtra("isRider", true);
                intent.putExtra("RiderRateData", ratings);
                context.startActivity(intent);
            });
        } else {
            holder.reportTextView.setVisibility(View.GONE);
        }

        String date = ratings.getDateTime();
        String formattedDate = formatDateTimeRiderRate(date);
        holder.dateTimeTextView.setText(formattedDate);
    }

    public String formatDateTimeRiderRate(String dateTime) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date date = originalFormat.parse(dateTime);

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            assert date != null;
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    @Override
    public int getItemCount() {
        return ratingsList != null ? ratingsList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, orderIdTextView, reportTextView, dateTimeTextView, commentTextView;
        RatingBar starRatingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.review_tv_from_name);
            orderIdTextView = itemView.findViewById(R.id.review_tv_order_id);
            reportTextView = itemView.findViewById(R.id.review_tv_report);
            starRatingBar = itemView.findViewById(R.id.review_rating_star);
            dateTimeTextView = itemView.findViewById(R.id.review_tv_date);
            commentTextView = itemView.findViewById(R.id.review_tv_comment);
        }
    }
}
