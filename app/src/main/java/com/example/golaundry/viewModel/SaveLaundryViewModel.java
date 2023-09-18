package com.example.golaundry.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golaundry.model.LaundryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SaveLaundryViewModel extends ViewModel {

    private final FirebaseDatabase db;
    private final FirebaseAuth mAuth;
    private final DatabaseReference savedLaundryRef;

    public SaveLaundryViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        savedLaundryRef = FirebaseDatabase.getInstance().getReference().child("savedLaundry");
    }

    public LiveData<Boolean> isSavedLaundry(String laundryId, String userId) { // Check whether user saved laundry table have this laundry id or not
        MutableLiveData<Boolean> isSavedResult = new MutableLiveData<>();
        savedLaundryRef.child(userId).child(laundryId).child("saved").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    isSavedResult.setValue(true);
                } else {
                    isSavedResult.setValue(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return isSavedResult;
    }

    public LiveData<Boolean> saveLaundryRemove(String userId, String laundryId) {
        MutableLiveData<Boolean> unsavedLaundryStatus = new MutableLiveData<>();
        savedLaundryRef.child(userId).child(laundryId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                unsavedLaundryStatus.setValue(true);
            } else {
                Exception e = task.getException();
                if (e != null) {
                    unsavedLaundryStatus.setValue(false);
                }
            }
        });
        return unsavedLaundryStatus;
    }

    public LiveData<Boolean> saveLaundryAdd(String userId, String laundryId) {
        MutableLiveData<Boolean> saveLaundryStatus = new MutableLiveData<>();
        Map<String, Object> laundryIdMap = new HashMap<>();
        laundryIdMap.put("saved", true);

        savedLaundryRef.child(userId).child(laundryId).updateChildren(laundryIdMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saveLaundryStatus.setValue(true);
            } else {
                Exception e = task.getException();
                if (e != null) {
                    saveLaundryStatus.setValue(false);
                }
            }
        });
        return saveLaundryStatus;
    }


}
