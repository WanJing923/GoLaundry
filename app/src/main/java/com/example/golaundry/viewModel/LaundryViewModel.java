package com.example.golaundry.viewModel;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.LaundryShopModel;
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

public class LaundryViewModel extends ViewModel {

    private final FirebaseDatabase db;
    private final FirebaseAuth mAuth;
    private final DatabaseReference laundryRef;
    private final DatabaseReference shopRef;

    public LaundryViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        laundryRef = FirebaseDatabase.getInstance().getReference().child("laundry");
        shopRef = FirebaseDatabase.getInstance().getReference().child("laundryShop");
    }

    public LiveData<Boolean> signUpLaundryWithImage(String email, String password, LaundryModel newLaundry) {
        MutableLiveData<Boolean> signUpResult = new MutableLiveData<>();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String laundryId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                        uploadImageAndCreateLaundry(laundryId, newLaundry, signUpResult);
                    } else {
                        signUpResult.setValue(false);
                    }
                });
        return signUpResult;
    }

    private void uploadImageAndCreateLaundry(String laundryId, LaundryModel newLaundry, MutableLiveData<Boolean> signUpResult) {
        Uri BLfilepath = Uri.parse(newLaundry.getBusinessLicensePhoto());

        if (BLfilepath != null) {
            StorageReference fpFileRef = FirebaseStorage.getInstance().getReference()
                    .child("Laundry/" + laundryId).child(Objects.requireNonNull(BLfilepath.getLastPathSegment()));
            UploadTask fpUploadTask = fpFileRef.putFile(BLfilepath);

            Task<List<Object>> combinedUploadTask = Tasks.whenAllSuccess(fpUploadTask);

            combinedUploadTask.continueWithTask(uploadTask -> {

                Task<Uri> fpDownloadUrlTask = fpFileRef.getDownloadUrl();
                return Tasks.whenAllSuccess(fpDownloadUrlTask);

            }).continueWithTask(downloadUrlTask -> {

                Uri blDownloadUri = (Uri) downloadUrlTask.getResult().get(0);
                newLaundry.setBusinessLicensePhoto(blDownloadUri.toString());
                return db.getReference("laundry").child(laundryId).setValue(newLaundry);

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

    //get laundry shop role data
    public LiveData<LaundryModel> getLaundryData(String currentUserId) {
        MutableLiveData<LaundryModel> laundryData = new MutableLiveData<>();
        laundryRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LaundryModel laundry = dataSnapshot.getValue(LaundryModel.class);
                    laundryData.setValue(laundry);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        return laundryData;
    }

    public LiveData<Boolean> updateNotificationData(String currentUserId, boolean updatedValue) {
        MutableLiveData<Boolean> notificationStatusData = new MutableLiveData<>();

        laundryRef.child(currentUserId).child("notification").setValue(updatedValue).addOnCompleteListener(task -> {
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

    public LiveData<Boolean> updateBreakData(String currentUserId, boolean updateValue) {
        MutableLiveData<Boolean> breakStatusData = new MutableLiveData<>();

        laundryRef.child(currentUserId).child("isBreak").setValue(updateValue).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                breakStatusData.setValue(true);
            } else {
                // Update failed
                Exception e = task.getException();
                if (e != null) {
                    breakStatusData.setValue(false);
                }
            }
        });
        return breakStatusData;
    }

    public LiveData<Boolean> updateShopInfo(String currentUserId, LaundryShopModel shopInfo) {
        MutableLiveData<Boolean> shopInfoStatus = new MutableLiveData<>();
        Uri laundryImageUri = Uri.parse(shopInfo.getImages());

        if (laundryImageUri != null) {
            StorageReference laundryFileRef = FirebaseStorage.getInstance().getReference()
                    .child("LaundryShop/" + currentUserId).child(Objects.requireNonNull(laundryImageUri.getLastPathSegment()));
            UploadTask laundryUploadTask = laundryFileRef.putFile(laundryImageUri);

            Task<List<Object>> combinedUploadTask = Tasks.whenAllSuccess(laundryUploadTask);

            combinedUploadTask.continueWithTask(uploadTask -> {
                Task<Uri> laundryDownloadUrlTask = laundryFileRef.getDownloadUrl();
                return Tasks.whenAllSuccess(laundryDownloadUrlTask);

            }).continueWithTask(downloadUrlTask -> {
                Uri DownloadUri = (Uri) downloadUrlTask.getResult().get(0);
                shopInfo.setImages(DownloadUri.toString());
                return db.getReference("laundryShop").child(currentUserId).setValue(shopInfo);

            }).addOnCompleteListener(dbTask -> {
                if (dbTask.isSuccessful()) {
                    shopInfoStatus.setValue(true);
                } else {
                    shopInfoStatus.setValue(false);
                }

            });
        } else {
            shopInfoStatus.setValue(false);
        }
        return shopInfoStatus;
    }

    public LiveData<Boolean> updateShopInfoNoImage(String currentUserId, List<String> allTimeRanges) {
        MutableLiveData<Boolean> timeRangesStatus = new MutableLiveData<>();
        shopRef.child(currentUserId).child("allTimeRanges").setValue(allTimeRanges).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                timeRangesStatus.setValue(true);
            } else {
                //failed
                Exception e = task.getException();
                if (e != null) {
                    timeRangesStatus.setValue(false);
                }
            }
        });
        return timeRangesStatus;
    };

    public LiveData<LaundryShopModel> getShopData(String currentUserId) {
        MutableLiveData<LaundryShopModel> shopData = new MutableLiveData<>();
        shopRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LaundryShopModel shop = dataSnapshot.getValue(LaundryShopModel.class);
                    shopData.setValue(shop);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        return shopData;
    }
}
