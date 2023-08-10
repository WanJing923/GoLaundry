package com.example.golaundry.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golaundry.model.HelpCenterModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class ForgotPasswordViewModel extends ViewModel {

    private final FirebaseAuth mAuth;

    public ForgotPasswordViewModel() {
        mAuth = FirebaseAuth.getInstance();

    }

    public LiveData<Boolean> sendEmail(String emailAddress) {
        MutableLiveData<Boolean> sendEmailResult = new MutableLiveData<>();
        mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                sendEmailResult.setValue(true);
            }
            else {
                sendEmailResult.setValue(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sendEmailResult.setValue(false);

            }
        });
        return sendEmailResult;
    }

}
