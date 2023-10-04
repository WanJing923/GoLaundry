package com.example.golaundry.viewModel;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golaundry.model.CashOutModel;
import com.example.golaundry.model.RiderModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Objects;

public class RiderViewModel extends ViewModel {
    private final FirebaseDatabase db;
    private final FirebaseAuth mAuth;
    private final DatabaseReference riderRef,cashOutRef;

    public RiderViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        riderRef = FirebaseDatabase.getInstance().getReference().child("riders");
        cashOutRef = FirebaseDatabase.getInstance().getReference().child("riderCashOut");
    }

    public LiveData<Boolean> signUpRiderWithImage(String email, String password, RiderModel newRider) {
        MutableLiveData<Boolean> signUpResult = new MutableLiveData<>();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String riderId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                        uploadImageAndCreateRider(riderId, newRider, signUpResult);
                    } else {
                        signUpResult.setValue(false);
                    }
                });
        return signUpResult;
    }

    private void uploadImageAndCreateRider(String riderId, RiderModel newRider, MutableLiveData<Boolean> signUpResult) {
        Uri FPfilepath = Uri.parse(newRider.getFacePhoto());
        Uri DLfilepath = Uri.parse(newRider.getDrivingLicensePhoto());

        if (FPfilepath != null && DLfilepath != null) {
            StorageReference fpFileRef = FirebaseStorage.getInstance().getReference()
                    .child("Riders/" + riderId).child(Objects.requireNonNull(FPfilepath.getLastPathSegment()));
            UploadTask fpUploadTask = fpFileRef.putFile(FPfilepath);

            StorageReference dlFileRef = FirebaseStorage.getInstance().getReference()
                    .child("Riders/" + riderId).child(Objects.requireNonNull(DLfilepath.getLastPathSegment()));
            UploadTask dlUploadTask = dlFileRef.putFile(DLfilepath);

            Task<List<Object>> combinedUploadTask = Tasks.whenAllSuccess(fpUploadTask, dlUploadTask);

            combinedUploadTask.continueWithTask(uploadTask -> {
                Task<Uri> fpDownloadUrlTask = fpFileRef.getDownloadUrl();
                Task<Uri> dlDownloadUrlTask = dlFileRef.getDownloadUrl();

                return Tasks.whenAllSuccess(fpDownloadUrlTask, dlDownloadUrlTask);
            }).continueWithTask(downloadUrlTask -> {
                Uri fpDownloadUri = (Uri) downloadUrlTask.getResult().get(0);
                Uri dlDownloadUri = (Uri) downloadUrlTask.getResult().get(1);

                newRider.setFacePhoto(fpDownloadUri.toString());
                newRider.setDrivingLicensePhoto(dlDownloadUri.toString());

                return db.getReference("riders").child(riderId).setValue(newRider);
            }).addOnCompleteListener(dbTask -> {
                if (dbTask.isSuccessful()) {
                    signUpResult.setValue(true);
                } else {
                    signUpResult.setValue(false);
                }
            });
        } else {
            signUpResult.setValue(false);
        }
    }

    public LiveData<RiderModel> getRiderData(String currentUserId) {
        MutableLiveData<RiderModel> riderData = new MutableLiveData<>();
        riderRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    RiderModel rider = dataSnapshot.getValue(RiderModel.class);
                    riderData.setValue(rider);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        return riderData;
    }

    public LiveData<Boolean> updateNotificationData(String currentUserId, boolean updatedValue) {
        MutableLiveData<Boolean> notificationStatusData = new MutableLiveData<>();

        riderRef.child(currentUserId).child("notification").setValue(updatedValue).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notificationStatusData.setValue(true);
            } else {
                // Update failed
                Exception e = task.getException();
                if (e != null) {
                    notificationStatusData.setValue(false);
                }
            }
        });
        return notificationStatusData;
    }

    public MutableLiveData<Boolean> cashOutBalanceRider(String currentUserId, CashOutModel mCashOutModel, double newBalance) {
        MutableLiveData<Boolean> cashOutStatus = new MutableLiveData<>();

        cashOutRef.child(currentUserId).setValue(mCashOutModel).addOnSuccessListener(aVoid ->

                riderRef.child(currentUserId).child("balance").setValue(newBalance).addOnSuccessListener(aVoid1 -> {
                    cashOutStatus.setValue(true);
                }).addOnFailureListener(e -> cashOutStatus.setValue(false))

        ).addOnFailureListener(e -> cashOutStatus.setValue(false));

        return cashOutStatus;
    }
}
