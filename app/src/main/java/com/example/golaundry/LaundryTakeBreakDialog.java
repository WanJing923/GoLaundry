package com.example.golaundry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.viewModel.LaundryViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LaundryTakeBreakDialog extends DialogFragment {

    LaundryViewModel mLaundryViewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure you want to take a break?").setMessage("Your laundry shop will become not available until you come back.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    //update data in firebase
                    boolean updateValue = true;
                    mLaundryViewModel.updateBreakData(currentUserId, updateValue).observe(this, breakStatusData -> {
                        if (breakStatusData) {
                            Toast.makeText(requireContext(), "Your shop is not available currently!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(requireContext(), "Update failed!", Toast.LENGTH_SHORT).show();
                        };
                    });
                })
                .setNegativeButton("No", (dialog, which) -> {
                    //user canceled, do nothing
                });

        //set the button text color
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
        });

        return alertDialog;
    }
}
