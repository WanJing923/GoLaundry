package com.example.golaundry;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class HomeActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("users");
    DatabaseReference laundryRef = database.getReference("laundry");
    DatabaseReference riderRef = database.getReference("riders");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //get current user id
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        //different fragment based on user type
        userUserType(currentUserId);
        laundryUserType(currentUserId);
        riderUserType(currentUserId);
    }

    private void userUserType(String currentUserId) {
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userType = snapshot.child("userType").getValue(String.class);
                    if (Objects.requireNonNull(userType).equals("user")) {
                        replace(new HomeUserFragment<>());
                        SmoothBottomBar smoothBottomBar = findViewById(R.id.menu_bottombar);
                        smoothBottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {
                            switch (i) {
                                case 0:
                                    replace(new HomeUserFragment<>());
                                    break;

                                case 1:
                                    replace(new UserOrderFragment());
                                    break;

                                case 2:
                                    replace(new HistoryFragment());
                                    break;

                                case 3:
                                    replace(new ProfileUserFragment());
                                    break;
                            }
                            return true;
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
            }

        });

    }

    private void laundryUserType(String currentUserId) {
        laundryRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userType = snapshot.child("userType").getValue(String.class);

                    if (Objects.requireNonNull(userType).equals("laundry")) {
                        replace(new HomeLaundryFragment());
                        SmoothBottomBar smoothBottomBar = findViewById(R.id.menu_bottombar);
                        smoothBottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {
                            switch (i) {
                                case 0:
                                    replace(new HomeLaundryFragment());
                                    break;

                                case 1:
                                    replace(new UserOrderFragment());
                                    break;

                                case 2:
                                    replace(new HistoryFragment());
                                    break;

                                case 3:
                                    replace(new ProfileLaundryFragment());
                                    break;
                            }
                            return true;
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
            }

        });
    }

    private void riderUserType(String currentUserId) {
        riderRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userType = snapshot.child("userType").getValue(String.class);

                    if (Objects.requireNonNull(userType).equals("rider")) {
                        replace(new HomeRiderFragment());
                        SmoothBottomBar smoothBottomBar = findViewById(R.id.menu_bottombar);
                        smoothBottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {
                            switch (i) {
                                case 0:
                                    replace(new HomeRiderFragment());
                                    break;

                                case 1:
                                    replace(new UserOrderFragment());
                                    break;

                                case 2:
                                    replace(new HistoryFragment());
                                    break;

                                case 3:
                                    replace(new ProfileRiderFragment());
                                    break;
                            }
                            return true;
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
            }

        });

    }

    private void replace(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.home_container, fragment);
        fragmentTransaction.commit();
    }
}