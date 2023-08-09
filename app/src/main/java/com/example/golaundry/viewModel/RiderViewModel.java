package com.example.golaundry.viewModel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golaundry.model.RiderModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Objects;

public class RiderViewModel extends ViewModel {
    private final FirebaseDatabase db;
    private final FirebaseAuth mAuth;
    private Uri FPfilepath, DLfilepath;
    private final Boolean validateImage = false;


    public RiderViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
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
        FPfilepath = Uri.parse(newRider.getFacePhoto());
        DLfilepath = Uri.parse(newRider.getDrivingLicensePhoto());

        if ( FPfilepath != null && DLfilepath != null) {
            StorageReference fpFileRef = FirebaseStorage.getInstance().getReference()
                    .child("Riders/" + riderId).child("FacePhoto");
            UploadTask fpUploadTask = fpFileRef.putFile(FPfilepath);

            StorageReference dlFileRef = FirebaseStorage.getInstance().getReference()
                    .child("Riders/" + riderId).child("DrivingLicense");
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

//    public <Rider> LiveData<Boolean> createRider(String email, String password, Rider newRider) {
//        MutableLiveData<Boolean> signUpRiderResult = new MutableLiveData<>();
//
//        //create user
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        String userId = Objects.requireNonNull(task.getResult().getUser()).getUid();
//                        db.getReference("riders")
//                                .child(userId).setValue(newRider)
//                                .addOnCompleteListener(task1 -> {
//                                    if (task1.isSuccessful()) {
//                                        signUpRiderResult.setValue(true);
//                                    } else {
//                                        signUpRiderResult.setValue(false);
//                                    }
//                                });
//                    } else {
//                        signUpRiderResult.setValue(false);
//                    }
//                });
//        return signUpRiderResult;
//    }

}
