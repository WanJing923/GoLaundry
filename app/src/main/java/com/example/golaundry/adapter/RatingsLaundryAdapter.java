package com.example.golaundry.adapter;

import com.example.golaundry.HistoryFragment;
import com.example.golaundry.HistoryLaundryFragment;
import com.example.golaundry.HomeActivity;
import com.example.golaundry.RatingsActivity;
import com.example.golaundry.ReportActivity;
import com.example.golaundry.model.RateModel;
import com.example.golaundry.viewModel.RiderViewModel;
import com.example.golaundry.viewModel.UserViewModel;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.R;
import com.example.golaundry.viewModel.LaundryViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RatingsLaundryAdapter extends RecyclerView.Adapter<RatingsLaundryAdapter.ViewHolder> {
    private final List<RateModel> ratingsList;
    private final Context context;
    private final LaundryViewModel mLaundryViewModel;
    private final UserViewModel mUserViewModel;
    private final RiderViewModel mRiderViewModel;
    private AppCompatActivity activity;

    public RatingsLaundryAdapter(AppCompatActivity activity,List<RateModel> ratingsList, Context context, LaundryViewModel mLaundryViewModel, UserViewModel mUserViewModel, RiderViewModel mRiderViewModel) {
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
        RateModel ratings = ratingsList.get(position);

        mUserViewModel.getUserData(ratings.getUserId()).observe((LifecycleOwner) context, userModel -> {
            if (userModel != null) {
                holder.userNameTextView.setText(userModel.getFullName());
            }
        });

        holder.orderIdTextView.setText(ratings.getOrderId());
        holder.commentTextView.setText(ratings.getCommentToLaundry());
        float rateAmount = ratings.getRateToLaundry();
        holder.starRatingBar.setRating(rateAmount);

        String date = ratings.getDateTime();
        String formattedDate = formatDateTimeLaundryRate(date);
        holder.dateTimeTextView.setText(formattedDate);

        holder.reportTextView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ReportActivity.class);
            intent.putExtra("isLaundry", true);
            intent.putExtra("RateData", ratings);
            context.startActivity(intent);
        });

    }

    public String formatDateTimeLaundryRate(String dateTime) {
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
