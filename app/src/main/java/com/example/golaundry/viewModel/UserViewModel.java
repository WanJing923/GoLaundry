package com.example.golaundry.viewModel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golaundry.MainActivity;
import com.example.golaundry.UserSignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class UserViewModel extends ViewModel {

    private final FirebaseDatabase db;
    private final FirebaseAuth mAuth;

    //constructor
    public UserViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
    }

    public <User> LiveData<Boolean> createUser(String email, String password, User newUser) {
        MutableLiveData<Boolean> signUpResult = new MutableLiveData<>();

        //create user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                        db.getReference("users")
                                .child(userId).setValue(newUser)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        signUpResult.setValue(true);
                                    } else {
                                        signUpResult.setValue(false);
                                    }
                                });
                    } else {
                        signUpResult.setValue(false);
                    }
                });
        return signUpResult;
    }

    public LiveData<Boolean> loginUser(String email, String password) {
        MutableLiveData<Boolean> signInResult = new MutableLiveData<>();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                signInResult.setValue(true);
                            } else {
                                signInResult.setValue(false);
                            }
                        });
        return signInResult;
    }

}
