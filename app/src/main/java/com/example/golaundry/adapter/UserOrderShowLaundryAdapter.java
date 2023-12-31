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

import com.example.golaundry.view.OrderActivity;
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

public class UserOrderShowLaundryAdapter extends RecyclerView.Adapter<UserOrderShowLaundryAdapter.ViewHolder> {

    private List<CombineLaundryData> laundryList;
    private final Context context;
    private String fullAddress;
    String imageUrl, currentUserId, finalDistance;
    double distance;
    SaveLaundryViewModel mSaveLaundryViewModel;
    List<String> savedLaundryIds;

    public UserOrderShowLaundryAdapter(List<CombineLaundryData> laundryList, Context context, String fullAddress) {
        this.laundryList = laundryList;
        this.context = context;
        this.fullAddress = fullAddress;
        savedLaundryIds = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateFullAddress(String newFullAddress) {
        fullAddress = newFullAddress;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_info_card, parent, false);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mSaveLaundryViewModel = new ViewModelProvider((FragmentActivity) context).get(SaveLaundryViewModel.class);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CombineLaundryData laundry = laundryList.get(position);
        //bind data
        holder.shopNameTextView.setText(laundry.getLaundry().getShopName());
        holder.ratingsTextView.setText(String.format("%.2f", laundry.getLaundry().getRatingsAverage()));
        holder.ratingsBar.setRating(laundry.getLaundry().getRatingsAverage());

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

        //show distance
        String laundryAddress = laundry.getLaundry().getAddress();
        LatLng laundryLatLng = getLocationFromAddress(context, laundryAddress);
        LatLng userLatLng = getLocationFromAddress(context, fullAddress);
        if (laundryLatLng != null && userLatLng != null) {
            double dis = SphericalUtil.computeDistanceBetween(laundryLatLng, userLatLng);
            distance = dis / 1000;
            finalDistance = String.format("%.2f", distance);
            holder.kmTextView.setText(finalDistance + "km");
        }

        //go to laundry info
        holder.rightImageView.setOnClickListener(view -> { //go to laundry info, order activity
            Context context = view.getContext();
            Intent intent = new Intent(context, OrderActivity.class);
            intent.putExtra("laundryId", laundry.getLaundry().getLaundryId());
            intent.putExtra("distance", distance);
            context.startActivity(intent);
        });

        //saved laundry before or not
        isSaveLaundry(laundry.getLaundry().getLaundryId(), holder.savedImageView);
        //save and unsaved laundry shop
        holder.savedImageView.setOnClickListener(v -> {
            String laundryId = laundry.getLaundry().getLaundryId();
            saveLaundryShop(laundryId, holder.savedImageView);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<CombineLaundryData> filteredList) {
        laundryList = filteredList;
        notifyDataSetChanged();
    }

    //show the user whether is saved or not
    @SuppressLint("NotifyDataSetChanged")
    private void isSaveLaundry(String laundryId, ImageView savedImageView) {
        mSaveLaundryViewModel.isSavedLaundry(laundryId, currentUserId).observe((LifecycleOwner) context, isSavedResult -> {
            if (isSavedResult != null && isSavedResult) {
                savedImageView.setImageResource(R.drawable.ic_love);
                savedLaundryIds.add(laundryId);
            } else {
                savedImageView.setImageResource(R.drawable.ic_love_grey);
                savedLaundryIds.remove(laundryId);
            }
            notifyDataSetChanged();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void saveLaundryShop(String laundryId, ImageView savedImageView) { //have laundry id and user id, add laundry id to that table
        if (savedLaundryIds.contains(laundryId)) {
            mSaveLaundryViewModel.saveLaundryRemove(currentUserId, laundryId).observe((LifecycleOwner) context, unsavedLaundryStatus -> {
                if (unsavedLaundryStatus != null && unsavedLaundryStatus) {
                    savedLaundryIds.remove(laundryId);
                    Toast.makeText(context, "Removed saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love_grey);
                } else {
                    Toast.makeText(context, "Fail to removed saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love);
                }
                notifyDataSetChanged();
            });
        } else {
            mSaveLaundryViewModel.saveLaundryAdd(currentUserId, laundryId).observe((LifecycleOwner) context, saveLaundryStatus -> {
                if (saveLaundryStatus != null && saveLaundryStatus) {
                    savedLaundryIds.add(laundryId);
                    Toast.makeText(context, "Saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love);
                } else {
                    Toast.makeText(context, "Fail to saved laundry shop", Toast.LENGTH_SHORT).show();
                    savedImageView.setImageResource(R.drawable.ic_love_grey);
                }
                notifyDataSetChanged();
            });
        }
    }

    public LatLng getLocationFromAddress(Context context, String theAddress) {

        if (theAddress == null || theAddress.isEmpty()) {
            return null;
        }

        if (!Geocoder.isPresent()) {
            return null;
        }
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(theAddress, 5);
            if (address == null || address.isEmpty()) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    private void setImages(String imageUrl, ImageView laundryImageView) {
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        laundryImageView.setTag(mStorageReference);
        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            mStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                if (laundryImageView.getTag() == mStorageReference) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    laundryImageView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(e -> {
                if (laundryImageView.getTag() == mStorageReference) {
                    Toast.makeText(context, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
                }
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
        TextView shopNameTextView, ratingsTextView, kmTextView;
        RatingBar ratingsBar;
        RecyclerView servicesRecyclerView;
        ImageView laundryImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            laundryImageView = itemView.findViewById(R.id.lic_laundry_image);
            shopNameTextView = itemView.findViewById(R.id.lic_tv_laundry_name);
            ratingsTextView = itemView.findViewById(R.id.lic_tv_rating);
            ratingsBar = itemView.findViewById(R.id.lic_rating_star);
            kmTextView = itemView.findViewById(R.id.lic_tv_distance_km);
            savedImageView = itemView.findViewById(R.id.lic_iv_saved);
            rightImageView = itemView.findViewById(R.id.lic_iv_right);
            servicesRecyclerView = itemView.findViewById(R.id.lic_rv_hashtags);
        }
    }
}
