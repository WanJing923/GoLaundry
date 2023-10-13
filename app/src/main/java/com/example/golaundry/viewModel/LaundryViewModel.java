package com.example.golaundry.viewModel;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golaundry.model.CashOutModel;
import com.example.golaundry.model.CombineLaundryData;
import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.LaundryServiceModel;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LaundryViewModel extends ViewModel {

    private final FirebaseDatabase db;
    private final FirebaseAuth mAuth;
    private final DatabaseReference laundryRef;
    private final DatabaseReference shopRef;
    private final DatabaseReference serviceRef, cashOutRef;

    public LaundryViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        laundryRef = FirebaseDatabase.getInstance().getReference().child("laundry");
        shopRef = FirebaseDatabase.getInstance().getReference().child("laundryShop");
        serviceRef = FirebaseDatabase.getInstance().getReference().child("laundryService");
        cashOutRef = FirebaseDatabase.getInstance().getReference().child("laundryCashOut");
    }

    public LiveData<Boolean> signUpLaundryWithImage(String email, String password, LaundryModel newLaundry) {
        MutableLiveData<Boolean> signUpResult = new MutableLiveData<>();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String laundryId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                        newLaundry.setLaundryId(laundryId);
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
        laundryRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LaundryModel laundry = dataSnapshot.getValue(LaundryModel.class);
                    laundryData.setValue(laundry);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error in Firebase ValueEventListener: " + databaseError.getMessage());
            }
        });

        return laundryData;
    }

    //laundry on or off notification
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

    //laundry break or end break
    public LiveData<Boolean> updateBreakData(String currentUserId, boolean updateValue) {
        MutableLiveData<Boolean> breakStatusData = new MutableLiveData<>();

        laundryRef.child(currentUserId).child("isBreak").setValue(updateValue).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                breakStatusData.setValue(true);
            } else {
                // failed
                Exception e = task.getException();
                if (e != null) {
                    breakStatusData.setValue(false);
                }
            }
        });
        return breakStatusData;
    }

    //edit shop info
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
    }

    //get laundry shop image and opening hours
    public LiveData<LaundryShopModel> getShopData(String currentUserId) {
        MutableLiveData<LaundryShopModel> shopData = new MutableLiveData<>();
        shopRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LaundryShopModel shop = dataSnapshot.getValue(LaundryShopModel.class);
                    shopData.setValue(shop);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//
            }
        });
        return shopData;
    }

    //get services
    public LiveData<List<LaundryServiceModel>> getServiceData(String currentUserId) {
        MutableLiveData<List<LaundryServiceModel>> serviceData = new MutableLiveData<>();

        serviceRef.child(currentUserId).child("services").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LaundryServiceModel> services = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LaundryServiceModel service = snapshot.getValue(LaundryServiceModel.class);
                    if (service != null) {
                        services.add(service);
                    }
                }
                serviceData.setValue(services);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return serviceData;
    }

    //edit services
    public LiveData<Boolean> uploadServiceData(String currentUserId, ArrayList<LaundryServiceModel> service) {
        MutableLiveData<Boolean> uploadServiceStatus = new MutableLiveData<>();

        serviceRef.child(currentUserId).child("services").setValue(service)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        uploadServiceStatus.setValue(true);
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            uploadServiceStatus.setValue(false);
                        }
                    }
                });
        return uploadServiceStatus;
    }

    //after all info given
    public LiveData<Boolean> updateSetupData(String currentUserId, boolean updateValue) {
        MutableLiveData<Boolean> updateSetupStatus = new MutableLiveData<>();

        laundryRef.child(currentUserId).child("setup").setValue(updateValue).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateSetupStatus.setValue(true);
            } else {
                // Update failed
                Exception e = task.getException();
                if (e != null) {
                    updateSetupStatus.setValue(false);
                }
            }
        });
        return updateSetupStatus;
    }

    public LiveData<List<LaundryModel>> getFilteredLaundryData() {
        MutableLiveData<List<LaundryModel>> filteredLaundryData = new MutableLiveData<>();

        laundryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LaundryModel> filteredLaundry = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LaundryModel laundry = snapshot.getValue(LaundryModel.class);

                    if (laundry != null) {
                        if (!laundry.getIsBreak() && laundry.getSetup()) {
                            filteredLaundry.add(laundry);
                        }
                    }
                }

                filteredLaundryData.setValue(filteredLaundry);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return filteredLaundryData;
    }

    public LiveData<List<LaundryShopModel>> getAllShopData() {
        MutableLiveData<List<LaundryShopModel>> allShopData = new MutableLiveData<>();

        shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LaundryShopModel> allShop = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LaundryShopModel eachShop = snapshot.getValue(LaundryShopModel.class);
                    if (eachShop != null) {
                        allShop.add(eachShop);
                    }
                }
                allShopData.setValue(allShop);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return allShopData;
    }

    public LiveData<List<LaundryServiceModel>> getAllServiceData(String laundryId) {
        MutableLiveData<List<LaundryServiceModel>> allServicesData = new MutableLiveData<>();
        serviceRef.child(laundryId).child("services").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LaundryServiceModel> allServices = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LaundryServiceModel service = snapshot.getValue(LaundryServiceModel.class);
                    if (service != null) {
                        allServices.add(service);
                    }
                }
                allServicesData.setValue(allServices);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return allServicesData;
    }

    public LiveData<List<CombineLaundryData>> combineAndNotifyData(List<LaundryModel> laundryData, List<LaundryShopModel> shopData) {
        MutableLiveData<List<CombineLaundryData>> combinedLaundryDataLiveData = new MutableLiveData<>();
        List<CombineLaundryData> combinedDataList = new ArrayList<>();

        for (LaundryModel laundry : laundryData) {
            for (LaundryShopModel shop : shopData) {
                if (laundry.getLaundryId().equals(shop.getLaundryId())) {
                    String laundryId = laundry.getLaundryId();
                    LiveData<List<LaundryServiceModel>> serviceData = getAllServiceData(laundryId);

                    serviceData.observeForever(servicesData -> {
                        if (servicesData != null && !servicesData.isEmpty()) {
                            CombineLaundryData mCombinedData = new CombineLaundryData(laundry, shop, servicesData);
                            combinedDataList.add(mCombinedData);
                            combinedLaundryDataLiveData.setValue(combinedDataList);
                        }
                    });
                }
            }
        }
        return combinedLaundryDataLiveData;
    }

    public MutableLiveData<Boolean> cashOutBalance(String currentUserId, CashOutModel mCashOutModel, double newBalance) {
        MutableLiveData<Boolean> cashOutStatus = new MutableLiveData<>();

        cashOutRef.child(currentUserId).setValue(mCashOutModel).addOnSuccessListener(aVoid ->

                laundryRef.child(currentUserId).child("balance").setValue(newBalance).addOnSuccessListener(aVoid1 -> {
                    cashOutStatus.setValue(true);
                }).addOnFailureListener(e -> cashOutStatus.setValue(false))

        ).addOnFailureListener(e -> cashOutStatus.setValue(false));

        return cashOutStatus;
    }

}


