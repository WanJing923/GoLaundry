package com.example.golaundry.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.OrderActivity;
import com.example.golaundry.R;
import com.example.golaundry.model.CombineLaundryData;
import com.example.golaundry.model.LaundryServiceModel;
import com.example.golaundry.viewModel.SaveLaundryViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.SphericalUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SavedLaundryAdapter extends RecyclerView.Adapter<SavedLaundryAdapter.ViewHolder>{

    private final List<CombineLaundryData> laundryList;
    private final Context context;
    String imageUrl, currentUserId;
    SaveLaundryViewModel mSaveLaundryViewModel;
    boolean isSavedLaundry;

    public SavedLaundryAdapter(List<CombineLaundryData> laundryList, Context context) {
        this.laundryList = laundryList;
        this.context = context;
    }

    @NonNull
    @Override
    public SavedLaundryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_info_card, parent, false);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mSaveLaundryViewModel = new ViewModelProvider((FragmentActivity) context).get(SaveLaundryViewModel.class);
        return new SavedLaundryAdapter.ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull SavedLaundryAdapter.ViewHolder holder, int position) {
        CombineLaundryData laundry = laundryList.get(position);
        //bind data
        holder.shopNameTextView.setText(laundry.getLaundry().getShopName());
        holder.ratingsTextView.setText("0");
        holder.ratingsBar.setRating(0);

        //show image
        imageUrl = laundry.getShop().getImages();
        if (!Objects.equals(imageUrl, "")) {
            setImages(imageUrl, holder.laundryImageView);
        }

        //load laundry services
        List<LaundryServiceModel> serviceList = laundry.getServiceList();
        HashtagAdapter hashtagAdapter = new HashtagAdapter(serviceList);
        holder.servicesRecyclerView.setAdapter(hashtagAdapter);
        holder.servicesRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        holder.kmTextView.setVisibility(View.GONE);
        holder.rightImageView.setVisibility(View.GONE);
        holder.distanceTextView.setVisibility(View.GONE);

        //saved laundry before or not
        isSaveLaundry(laundry.getLaundry().getLaundryId(), holder.savedImageView);
        //save and unsaved laundry shop
        holder.savedImageView.setOnClickListener(v -> {
            String laundryId = laundry.getLaundry().getLaundryId();
            saveLaundryShop(laundryId, holder.savedImageView);
        });
    }

    //show the user whether is saved or not
    private void isSaveLaundry(String laundryId, ImageView savedLaundryImageView) {
        mSaveLaundryViewModel.isSavedLaundry(laundryId, currentUserId).observe((LifecycleOwner) context, isSavedResult -> {
            if (isSavedResult != null && isSavedResult) {
                isSavedLaundry = true;
                savedLaundryImageView.setImageResource(R.drawable.ic_love);
            } else {
                isSavedLaundry = false;
                savedLaundryImageView.setImageResource(R.drawable.ic_love_grey);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void saveLaundryShop(String laundryId, ImageView savedImageView) { //have laundry id and user id, add laundry id to that table
        if (isSavedLaundry) {
            mSaveLaundryViewModel.saveLaundryRemove(currentUserId, laundryId).observe((LifecycleOwner) context, unsavedLaundryStatus -> {
                if (unsavedLaundryStatus != null && unsavedLaundryStatus) {
                    isSavedLaundry = false;
                    Toast.makeText(context, "Removed saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love_grey);
                } else {
                    Toast.makeText(context, "Fail to removed saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love);
                }
            });
        } else {
            mSaveLaundryViewModel.saveLaundryAdd(currentUserId, laundryId).observe((LifecycleOwner) context, saveLaundryStatus -> {
                if (saveLaundryStatus != null && saveLaundryStatus) {
                    isSavedLaundry = true;
                    Toast.makeText(context, "Saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love);
                } else {
                    Toast.makeText(context, "Fail to saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love_grey);
                }
            });
        }
    }

    private void setImages(String imageUrl, ImageView laundryImageView) {
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            mStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                //show
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                laundryImageView.setImageBitmap(bitmap);
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return laundryList != null ? laundryList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView savedImageView, rightImageView;
        TextView shopNameTextView, ratingsTextView, kmTextView,distanceTextView;
        RatingBar ratingsBar;
        RecyclerView servicesRecyclerView;
        ImageView laundryImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            laundryImageView = itemView.findViewById(R.id.lic_laundry_image);
            shopNameTextView = itemView.findViewById(R.id.lic_tv_laundry_name);
            ratingsTextView = itemView.findViewById(R.id.lic_tv_rating);
            ratingsBar = itemView.findViewById(R.id.lic_rating_star);
            distanceTextView = itemView.findViewById(R.id.lic_tv_distance);
            kmTextView = itemView.findViewById(R.id.lic_tv_distance_km);
            savedImageView = itemView.findViewById(R.id.lic_iv_saved);
            rightImageView = itemView.findViewById(R.id.lic_iv_right);
            servicesRecyclerView = itemView.findViewById(R.id.lic_rv_hashtags);
        }
    }


}
