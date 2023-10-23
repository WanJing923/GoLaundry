package com.example.golaundry.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class LogoutConfirmationDialogFragmentRider extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    ((ProfileRiderFragment) Objects.requireNonNull(getTargetFragment())).logout();
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
