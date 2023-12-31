package com.example.golaundry.viewModel;

import android.annotation.SuppressLint;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golaundry.model.AddressModel;
import com.example.golaundry.model.AllMembershipModel;
import com.example.golaundry.model.CurrentMembershipModel;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.OrderStatusModel;
import com.example.golaundry.model.TopUpModel;
import com.example.golaundry.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UserViewModel extends ViewModel {

    private final FirebaseDatabase db;
    private final DatabaseReference userRef;
    private final DatabaseReference userMembershipRef;
    private final DatabaseReference allMembershipRef;
    private final FirebaseAuth mAuth;
    private final DatabaseReference userAddressRef;
    private final DatabaseReference userOrderRef, orderStatusRef, topUpRef, membershipHistoryRef;

    //constructor
    public UserViewModel() {
        userMembershipRef = FirebaseDatabase.getInstance().getReference().child("currentMembership");
        allMembershipRef = FirebaseDatabase.getInstance().getReference().child("allMemberships");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userAddressRef = db.getReference().child("userAddress");
        userOrderRef = db.getReference().child("userOrder");
        orderStatusRef = db.getReference().child("orderStatus");
        topUpRef = db.getReference().child("topUpHistory");
        membershipHistoryRef = db.getReference().child("membershipHistory");
    }

    //create user auth
    public LiveData<Boolean> createUser(String email, String password, UserModel newUser) {
        MutableLiveData<Boolean> signUpResult = new MutableLiveData<>();

        //create user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                        newUser.setUserId(userId);
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

    //before login, check user status and email exist
    public LiveData<Boolean> checkUserRole(String email) {
        MutableLiveData<Boolean> roleLiveData = new MutableLiveData<>();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference laundryRef = FirebaseDatabase.getInstance().getReference("laundry");
        DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("riders");
        ValueEventListener emailListener = new ValueEventListener() {
            int searchCounter = 0;
            boolean userExists = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userExists = true;
                }
                searchCounter++;
                if (searchCounter == 3) {
                    checkStatus(userRef, laundryRef, riderRef, email, roleLiveData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                searchCounter++;
                if (searchCounter == 3) {
                    roleLiveData.setValue(false);
                }
            }
        };

        // Search in the userRef table
        userRef.orderByChild("emailAddress").equalTo(email).addListenerForSingleValueEvent(emailListener);

        // Search in the laundryRef table
        laundryRef.orderByChild("emailAddress").equalTo(email).addListenerForSingleValueEvent(emailListener);

        // Search in the riderRef table
        riderRef.orderByChild("emailAddress").equalTo(email).addListenerForSingleValueEvent(emailListener);

        return roleLiveData;
    }

    private void checkStatus(DatabaseReference userRef, DatabaseReference laundryRef, DatabaseReference riderRef, String email, MutableLiveData<Boolean> roleLiveData) {
        userRef.orderByChild("emailAddress").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userStatus = snapshot.child("status").getValue(String.class);
                        if ("active".equals(userStatus)) {
                            roleLiveData.setValue(true);
                            return;
                        }
                    }
                }
                checkLaundryStatus(laundryRef, email, roleLiveData, riderRef);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkLaundryStatus(laundryRef, email, roleLiveData, riderRef);
            }
        });
    }

    private void checkLaundryStatus(DatabaseReference laundryRef, String email, MutableLiveData<Boolean> roleLiveData, DatabaseReference riderRef) {
        laundryRef.orderByChild("emailAddress").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String laundryStatus = snapshot.child("status").getValue(String.class);
                        if ("active".equals(laundryStatus)) {
                            roleLiveData.setValue(true);
                            return;
                        }
                    }
                }
                checkRiderStatus(riderRef, email, roleLiveData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkRiderStatus(riderRef, email, roleLiveData);
            }
        });
    }

    private void checkRiderStatus(DatabaseReference riderRef, String email, MutableLiveData<Boolean> roleLiveData) {
        Query query = riderRef.orderByChild("emailAddress").equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String riderStatus = snapshot.child("status").getValue(String.class);
                        if ("active".equals(riderStatus)) {
                            roleLiveData.setValue(true);
                            return;
                        }
                    }
                }

                // If the loop completes without finding an "active" rider, set to false
                roleLiveData.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                roleLiveData.setValue(false);
            }
        });
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
                //
            }
        });

        return userData;
    }

    //get current membership
    public LiveData<CurrentMembershipModel> getCurrentMembershipData(String currentUserId) {
        MutableLiveData<CurrentMembershipModel> userCurrentMembershipData = new MutableLiveData<>();

        DatabaseReference currentUserRef = userMembershipRef.child(currentUserId);
        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CurrentMembershipModel currentMembership = dataSnapshot.getValue(CurrentMembershipModel.class);
                    userCurrentMembershipData.setValue(currentMembership);
                } else {
                    userCurrentMembershipData.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userCurrentMembershipData.setValue(null);
            }
        });

        return userCurrentMembershipData;
    }

    //all memberships data
    public LiveData<AllMembershipModel> getAllMembershipData(String membershipRate) {
        MutableLiveData<AllMembershipModel> AllMembershipData = new MutableLiveData<>();
        allMembershipRef.child(membershipRate).addListenerForSingleValueEvent(new ValueEventListener() {
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

    //check and get user address
    public LiveData<List<AddressModel>> getAllAddressesForUser(String currentUserId) {
        MutableLiveData<List<AddressModel>> addressesData = new MutableLiveData<>();

        userAddressRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<AddressModel> addressesList = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    AddressModel address = childSnapshot.getValue(AddressModel.class);
                    addressesList.add(address);
                }
                addressesData.setValue(addressesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                addressesData.setValue(null);
            }
        });
        return addressesData;
    }

    public MutableLiveData<Boolean> addUserAddress(String currentUserId, AddressModel newAddress) {
        MutableLiveData<Boolean> addressData = new MutableLiveData<>();

        String addressId = String.valueOf(UUID.randomUUID());
        newAddress.setAddressId(addressId);

        userAddressRef.child(currentUserId).child(addressId).setValue(newAddress)
                .addOnSuccessListener(aVoid ->
                        addressData.setValue(true))
                .addOnFailureListener(e ->
                        addressData.setValue(false)
                );

        return addressData;
    }

    public MutableLiveData<Boolean> addOrder(String currentUserId, OrderModel newOrder, OrderStatusModel mOrderStatusModel, double spending, double newBalance) {
        MutableLiveData<Boolean> orderData = new MutableLiveData<>();
        String orderId = String.valueOf(UUID.randomUUID());
        newOrder.setOrderId(orderId);

        Calendar calendar = Calendar.getInstance();
        String[] monthName = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String currentMonth = monthName[calendar.get(Calendar.MONTH)];
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String currentYear = sdf.format(new Date());

        DatabaseReference userSpendingRef = FirebaseDatabase.getInstance().getReference().child("userSpending").child(currentUserId).child(currentYear);
        DatabaseReference userTotalOrderRef = FirebaseDatabase.getInstance().getReference().child("userTotalOrder").child(currentUserId).child(currentYear);

        String orderStatusId = String.valueOf(UUID.randomUUID());
        orderStatusRef.child(orderId).child(orderStatusId).setValue(mOrderStatusModel).addOnSuccessListener(aVoid -> {
            userOrderRef.child(orderId).setValue(newOrder).addOnSuccessListener(aVoid1 -> {
                userSpendingRef.child(currentMonth).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Double currentValue = dataSnapshot.getValue(Double.class);
                            if (currentValue == null) {
                                currentValue = 0.0;
                            }
                            double updatedValue = currentValue + spending;
                            userSpendingRef.child(currentMonth).setValue(updatedValue).addOnSuccessListener(aVoid -> {
                                userTotalOrderRef.child(currentMonth).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Integer currentValue = dataSnapshot.getValue(Integer.class);
                                            if (currentValue == null) {
                                                currentValue = 0;
                                            }
                                            int updatedValue = currentValue + 1;

                                            userTotalOrderRef.child(currentMonth).setValue(updatedValue).addOnSuccessListener(aVoid -> {

                                                userRef.child(currentUserId).child("balance").setValue(newBalance).addOnSuccessListener(aVoid2 -> {
                                                    orderData.setValue(true);
                                                }).addOnFailureListener(e -> {
                                                    orderData.setValue(false);
                                                });

                                            }).addOnFailureListener(e -> orderData.setValue(false));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }).addOnFailureListener(e -> orderData.setValue(false));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }).addOnFailureListener(e -> orderData.setValue(false));
        }).addOnFailureListener(e -> orderData.setValue(false));

        return orderData;
    }

    public MutableLiveData<OrderModel> getLatestOrder(String currentUserId) {
        MutableLiveData<OrderModel> orderData = new MutableLiveData<>();

        Query query = userOrderRef.orderByChild("userId").equalTo(currentUserId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<OrderModel> orders = new ArrayList<>();

                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            orders.add(order);
                        }
                    }
                    orders.sort((order1, order2) -> order2.getDateTime().compareTo(order1.getDateTime()));

                    if (!orders.isEmpty()) {
                        OrderModel latestOrder = orders.get(0);
                        orderData.setValue(latestOrder);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return orderData;
    }


    public void deleteAddressForUser(String currentUserId, String addressKey) {
        MutableLiveData<Boolean> deleteStatus = new MutableLiveData<>();

        userAddressRef.child(currentUserId).child(addressKey).removeValue()
                .addOnSuccessListener(aVoid -> {
                    deleteStatus.setValue(true);
                })
                .addOnFailureListener(e -> {
                    deleteStatus.setValue(false);
                });
    }

    public MutableLiveData<Boolean> topUpBalance(String currentUserId, TopUpModel topUpModel) {
        MutableLiveData<Boolean> topUpStatus = new MutableLiveData<>();

        topUpRef.child(currentUserId).setValue(topUpModel)
                .addOnSuccessListener(aVoid ->
                        topUpStatus.setValue(true))
                .addOnFailureListener(e ->
                        topUpStatus.setValue(false)
                );

        return topUpStatus;
    }

    public LiveData<CurrentMembershipModel> getCurrentMembership(String currentUserId) {
        MutableLiveData<CurrentMembershipModel> membershipData = new MutableLiveData<>();
        userMembershipRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CurrentMembershipModel membershipModel = dataSnapshot.getValue(CurrentMembershipModel.class);
                    membershipData.setValue(membershipModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        });

        return membershipData;
    }

    public LiveData<Boolean> updateCurrentMembership(String currentUserId, CurrentMembershipModel currentMembershipModel) {
        MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();

        DatabaseReference currentUserRef = userMembershipRef.child(currentUserId);
        Map<String, Object> updates = new HashMap<>();

        updates.put("monthYear", currentMembershipModel.getMonthYear());
        updates.put("monthlyTopUp", currentMembershipModel.getMonthlyTopUp());

        currentUserRef.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateStatus.setValue(true);
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            updateStatus.setValue(false);
                        }
                    }
                });

        return updateStatus;
    }

    public LiveData<Boolean> updateMonthlyTopUp(String currentUserId, CurrentMembershipModel currentMembershipModel) {
        MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();

        userMembershipRef.child(currentUserId).setValue(currentMembershipModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateStatus.setValue(true);
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            updateStatus.setValue(false);
                        }
                    }
                });
        return updateStatus;
    }

    public LiveData<Boolean> getMembershipHistory(String currentUserId, String monthYear) {
        MutableLiveData<Boolean> membershipData = new MutableLiveData<>();
        membershipHistoryRef.child(currentUserId).child(monthYear).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    membershipData.setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                membershipData.setValue(false);
            }
        });

        return membershipData;
    }

    public LiveData<Boolean> updateMembershipHistory(String currentUserId, String monthYear, double monthlyTopUp) {
        MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();

        membershipHistoryRef.child(currentUserId).child(monthYear).child("monthlyTopUp").setValue(monthlyTopUp)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateStatus.setValue(true);
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            updateStatus.setValue(false);
                        }
                    }
                });
        return updateStatus;
    }

    public LiveData<Boolean> updateUserMembershipBalance(String currentUserId, double balance, String membershipRate) {
        MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();

        userRef.child(currentUserId).child("balance").setValue(balance).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                userRef.child(currentUserId).child("membershipRate").setValue(membershipRate)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                updateStatus.setValue(true);
                            } else {
                                Exception e = task1.getException();
                                if (e != null) {
                                    updateStatus.setValue(false);
                                }
                            }
                        });

            } else {
                Exception e = task.getException();
                if (e != null) {
                    updateStatus.setValue(false);
                }
            }
        });
        return updateStatus;
    }

    public LiveData<Boolean> updateUserMembership(String currentUserId, String membershipRate) {
        MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();

        userRef.child(currentUserId).child("membershipRate").setValue(membershipRate)
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        updateStatus.setValue(true);
                    } else {
                        Exception e = task1.getException();
                        if (e != null) {
                            updateStatus.setValue(false);
                        }
                    }
                });

        return updateStatus;
    }

}
