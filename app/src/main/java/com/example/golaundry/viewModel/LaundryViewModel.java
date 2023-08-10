package com.example.golaundry.viewModel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.RiderModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Objects;

public class LaundryViewModel extends ViewModel {

    private final FirebaseDatabase db;
    private final FirebaseAuth mAuth;
    private Uri BLfilepath;
    private final Boolean validateImage = false;


    public LaundryViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
    }

    public LiveData<Boolean> signUpLaundryWithImage(String email, String password, LaundryModel newLaundry) {
        MutableLiveData<Boolean> signUpResult = new MutableLiveData<>();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String laundryId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                        uploadImageAndCreateRider(laundryId, newLaundry, signUpResult);
                    } else {
                        signUpResult.setValue(false);
                    }
                });
        return signUpResult;
    }

    private void uploadImageAndCreateRider(String laundryId, LaundryModel newLaundry, MutableLiveData<Boolean> signUpResult) {
        BLfilepath = Uri.parse(newLaundry.getBusinessLicensePhoto());

        if ( BLfilepath != null ) {
            StorageReference fpFileRef = FirebaseStorage.getInstance().getReference()
                    .child("Laundry/" + laundryId).child("FacePhoto");
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
}
