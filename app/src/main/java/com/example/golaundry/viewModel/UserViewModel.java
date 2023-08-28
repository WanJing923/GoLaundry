package com.example.golaundry.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golaundry.model.CurrentMembershipModel;
import com.example.golaundry.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserViewModel extends ViewModel {

    private final FirebaseDatabase db;
    private final DatabaseReference userRef;
    private final DatabaseReference userMembershipRef;
    private final FirebaseAuth mAuth;

    //constructor
    public UserViewModel() {
        userMembershipRef = FirebaseDatabase.getInstance().getReference().child("currentMembership");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    //create user auth
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

    //login into account, all users(not using cause of the check status function)
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

    //check status here, login user in front
    public LiveData<Boolean> checkUserStatus(String email) {
        MutableLiveData<Boolean> statusLiveData = new MutableLiveData<>();
        userRef.orderByChild("emailAddress").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        UserModel user = userSnapshot.getValue(UserModel.class);
                        if (user != null && user.getStatus().equals("active")) {
                            statusLiveData.setValue(true);
                            return;
                        }
                    }
                }
                statusLiveData.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                statusLiveData.setValue(false);
            }
        });

        return statusLiveData;
    }

    //get user role data
    public LiveData<UserModel> getUserData(String currentUserId) {
        MutableLiveData<UserModel> userData = new MutableLiveData<>();
        userRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    userData.setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        return userData;
    }

    public LiveData<CurrentMembershipModel> getCurrentMembershipData(String currentUserId) {
        MutableLiveData<CurrentMembershipModel> userCurrentMembershipData = new MutableLiveData<>();
        userMembershipRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CurrentMembershipModel currentMembership = dataSnapshot.getValue(CurrentMembershipModel.class);
                    userCurrentMembershipData.setValue(currentMembership);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        return userCurrentMembershipData;
    }

}
