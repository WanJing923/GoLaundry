package com.example.golaundry.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golaundry.model.HelpCenterModel;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.UUID;

public class HelpCenterViewModel extends ViewModel {

    private final FirebaseDatabase db;

    public HelpCenterViewModel() {
        db = FirebaseDatabase.getInstance();
    }

    public LiveData<Boolean> addHelpCenterMessage(HelpCenterModel newHelp) {
        MutableLiveData<Boolean> addHelpResult = new MutableLiveData<>();

        String helpId = String.valueOf(UUID.randomUUID());
        db.getReference("helpCenter")
                .child(helpId).setValue(newHelp)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addHelpResult.setValue(true);
                    } else {
                        addHelpResult.setValue(false);
                    }
                });

        return addHelpResult;
    }
}
