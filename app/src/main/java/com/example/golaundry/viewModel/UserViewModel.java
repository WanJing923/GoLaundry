package com.example.golaundry.viewModel;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golaundry.model.AllMembershipModel;
import com.example.golaundry.model.CurrentMembershipModel;
import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.RiderModel;
import com.example.golaundry.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Map;
import java.util.Objects;

public class UserViewModel extends ViewModel {

    private final FirebaseDatabase db;
    private final DatabaseReference userRef;
    private final DatabaseReference userMembershipRef;
    private final DatabaseReference allMembershipRef;
    private final FirebaseAuth mAuth;

    //constructor
    public UserViewModel() {
        userMembershipRef = FirebaseDatabase.getInstance().getReference().child("currentMembership");
        allMembershipRef = FirebaseDatabase.getInstance().getReference().child("allMemberships");
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

    //before login, check user status and email exist
    public LiveData<Boolean> checkUserRole(String email) {
        MutableLiveData<Boolean> roleLiveData = new MutableLiveData<>();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference laundryRef = FirebaseDatabase.getInstance().getReference("laundry");
        DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("riders");

        ValueEventListener listener = new ValueEventListener() {
            int searchCounter = 0;
            boolean userExists = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userExists = true;
                }

                // Increment the search counter
                searchCounter++;

                // Check if we have searched all three tables
                if (searchCounter == 3) {
                    roleLiveData.setValue(userExists);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error

                // Increment the search counter
                searchCounter++;

                // Check if we have searched all three tables
                if (searchCounter == 3) {
                    roleLiveData.setValue(false);
                }
            }
        };

        // Search in the userRef table
        userRef.orderByChild("emailAddress").equalTo(email).addListenerForSingleValueEvent(listener);

        // Search in the laundryRef table
        laundryRef.orderByChild("emailAddress").equalTo(email).addListenerForSingleValueEvent(listener);

        // Search in the riderRef table
        riderRef.orderByChild("emailAddress").equalTo(email).addListenerForSingleValueEvent(listener);

        return roleLiveData;
    }



    //get user role data
    public LiveData<UserModel> getUserData(String currentUserId) {
        MutableLiveData<UserModel> userData = new MutableLiveData<>();
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
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

    //get current membership
    public LiveData<CurrentMembershipModel> getCurrentMembershipData(String currentUserId) {
        MutableLiveData<CurrentMembershipModel> userCurrentMembershipData = new MutableLiveData<>();
        userMembershipRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
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

    //all memberships data
    public LiveData<AllMembershipModel> getAllMembershipData(String membershipRate) {
        MutableLiveData<AllMembershipModel> AllMembershipData = new MutableLiveData<>();
        allMembershipRef.child(membershipRate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    AllMembershipModel allMemberships = dataSnapshot.getValue(AllMembershipModel.class);
                    AllMembershipData.setValue(allMemberships);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        return AllMembershipData;
    }

    //update notification data
    public LiveData<Boolean> updateNotificationData(String currentUserId, boolean updatedValue) {
        MutableLiveData<Boolean> notificationStatusData = new MutableLiveData<>();

        userRef.child(currentUserId).child("notification").setValue(updatedValue).addOnCompleteListener(task -> {
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

    //edit profile, if no select profile pic
    public LiveData<Boolean> updateUserData(String currentUserId, Map<String, Object> updates) {
        MutableLiveData<Boolean> updateUserResult = new MutableLiveData<>();

        userRef.child(currentUserId).updateChildren(updates, (databaseError, db) -> {
            if (databaseError != null) {
                updateUserResult.setValue(false);
            } else {
                updateUserResult.setValue(true);
            }
        });
        return updateUserResult;
    }

    //if select profile pic
    public LiveData<Boolean> updateUserDataProfilePic(String currentUserId, Map<String, Object> updates, Uri profilePicUri) {
        MutableLiveData<Boolean> updateUserProfilePicResult = new MutableLiveData<>();

        StorageReference avatarFileRef = FirebaseStorage.getInstance().getReference()
                .child("Users").child(currentUserId).child(Objects.requireNonNull(profilePicUri.getLastPathSegment()));

        UploadTask avatarUploadTask = avatarFileRef.putFile(profilePicUri);

        avatarUploadTask.continueWithTask(uploadTask -> {
            //download url
            return avatarFileRef.getDownloadUrl();
        }).addOnCompleteListener(downloadUrlTask -> {
            if (downloadUrlTask.isSuccessful()) {
                Uri avatarDownloadUri = downloadUrlTask.getResult();

                //add map
                updates.put("avatar", avatarDownloadUri.toString());

                //update db
                userRef.child(currentUserId).updateChildren(updates).addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        updateUserProfilePicResult.setValue(true);
                    } else {
                        updateUserProfilePicResult.setValue(false);
                    }
                });

            } else {
                updateUserProfilePicResult.setValue(false);
            }
        });
        return updateUserProfilePicResult;
    }




}
