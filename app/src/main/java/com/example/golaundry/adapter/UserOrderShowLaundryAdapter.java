package com.example.golaundry.adapter;

import static java.security.AccessController.getContext;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.OrderActivity;
import com.example.golaundry.R;
import com.example.golaundry.model.CombineLaundryData;
import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.LaundryServiceModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.SphericalUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserOrderShowLaundryAdapter extends RecyclerView.Adapter<UserOrderShowLaundryAdapter.ViewHolder> {

    private final List<CombineLaundryData> laundryList;
    private final Context context;
    private String fullAddress;
    String imageUrl;
    @SuppressLint("StaticFieldLeak")
    static ImageView laundryImageView;
    Double distance;

    public UserOrderShowLaundryAdapter(List<CombineLaundryData> laundryList, Context context, String fullAddress) {
        this.laundryList = laundryList;
        this.context = context;
        this.fullAddress = fullAddress;
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
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CombineLaundryData laundry = laundryList.get(position);
        //bind data
        holder.shopNameTextView.setText(laundry.getLaundry().getShopName());
        holder.ratingsTextView.setText("0");
        holder.ratingsBar.setRating(0);

        //show image
        imageUrl = laundry.getShop().getImages();
        if (!Objects.equals(imageUrl, "")) {
            setImages(imageUrl);
        }

        //go to laundry info
        holder.rightImageView.setOnClickListener(view -> { //go to laundry info, order activity
            Context context = view.getContext();
            Intent intent = new Intent(context, OrderActivity.class);
            intent.putExtra("laundryId", laundry.getLaundry().getLaundryId());
            context.startActivity(intent);
        });

        //load laundry services
        List<LaundryServiceModel> serviceList = laundry.getServiceList();
        HashtagAdapter hashtagAdapter = new HashtagAdapter(serviceList);
        holder.servicesRecyclerView.setAdapter(hashtagAdapter);
        holder.servicesRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        //show distance
        String laundryAddress = laundry.getLaundry().getAddress();
        LatLng laundryLatLng = getLocationFromAddress(context,laundryAddress);
        LatLng userLatLng = getLocationFromAddress(context,fullAddress);
        if (laundryLatLng != null && userLatLng != null) {
            distance = SphericalUtil.computeDistanceBetween(laundryLatLng, userLatLng);
            holder.kmTextView.setText(String.format("%.2f",distance/1000)+"km");
        }

        //save laundry shop
//        savedImageView
    }



    public LatLng getLocationFromAddress(Context context, String theAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(theAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    private void setImages(String imageUrl) {
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
        TextView shopNameTextView, ratingsTextView, kmTextView;
        RatingBar ratingsBar;
        RecyclerView servicesRecyclerView;

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
