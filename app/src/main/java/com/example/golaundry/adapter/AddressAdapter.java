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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.OrderLocationActivity;
import com.example.golaundry.R;
import com.example.golaundry.model.AddressModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private final List<AddressModel> addressList;
    private final Context context;
    private final AddressModel selectedAddresses;
    UserViewModel mUserViewModel;
    String currentUserId;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public AddressAdapter(List<AddressModel> addressList, Context context, UserViewModel mUserViewModel) {
        this.addressList = addressList;
        this.context = context;
        this.mUserViewModel = mUserViewModel;
        this.selectedAddresses = new AddressModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_address, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        AddressModel address = addressList.get(position);
        String addressId = address.getAddressId();

        String name = address.getName();
        boolean defaultAddress = address.isDefaultAddress();
        if (defaultAddress) {
            holder.nameTextView.setText("Default Address: " + name);
        } else {
            holder.nameTextView.setText(name);
        }
        String details = address.getDetails();
        String fullAddress = address.getAddress();
        String showAddress = details + ", " + fullAddress;
        holder.addressTextView.setText(showAddress);

        holder.deleteImageView.setOnClickListener(view -> {
            if (defaultAddress){
                Toast.makeText(context, "Default address is not able to be removed. Please contact help center if needed.", Toast.LENGTH_LONG).show();
            } else {
                removeItemWithConfirmation(position, addressId);
            }
        });

        boolean isSelected = (position == selectedPosition);
        if (isSelected) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.cyan));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(view -> toggleSelection(address));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void toggleSelection(AddressModel address) {
        int clickedPosition = addressList.indexOf(address);
        if (clickedPosition == selectedPosition) {
            selectedPosition = RecyclerView.NO_POSITION;
        } else {
            selectedPosition = clickedPosition;
        }
        notifyDataSetChanged();
    }

    public AddressModel getSelectedAddresses() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            return addressList.get(selectedPosition);
        }
        return null;
    }

    public void removeItemWithConfirmation(int position, String addressId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this address?");

        SpannableString spannableStringYes = new SpannableString("Yes");
        spannableStringYes.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableStringYes.length(), 0);

        SpannableString spannableStringNo = new SpannableString("No");
        spannableStringNo.setSpan(new ForegroundColorSpan(Color.GRAY), 0, spannableStringNo.length(), 0);

        builder.setPositiveButton(spannableStringYes, (dialog, which) -> {
            addressList.remove(position);
            mUserViewModel.deleteAddressForUser(currentUserId, addressId);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        });
        builder.setNegativeButton(spannableStringNo, (dialog, which) -> {
            // No action
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView;
        ImageView deleteImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.la_tv_name);
            addressTextView = itemView.findViewById(R.id.la_tv_address);
            deleteImageView = itemView.findViewById(R.id.la_iv_delete);
        }
    }
}
