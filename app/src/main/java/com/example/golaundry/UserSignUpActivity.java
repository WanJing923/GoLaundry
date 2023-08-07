package com.example.golaundry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.golaundry.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.golaundry.databinding.ActivityMainBinding;
import com.example.golaundry.databinding.ActivityUserSignUpBinding;

import java.util.Objects;

public class UserSignUpActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    ActivityUserSignUpBinding mActivityUserSignUpBinding;
    private ProgressBar mProgressBar;

    //connect to firebase authentication
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //implement binding method
        com.example.golaundry.databinding.ActivityUserSignUpBinding mActivityUserSignUpBinding = ActivityUserSignUpBinding.inflate(getLayoutInflater());
        setContentView(mActivityUserSignUpBinding.getRoot());

        //toolbar function
        setSupportActionBar(mActivityUserSignUpBinding.usuaToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(mActivityUserSignUpBinding.usuaToolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mActivityUserSignUpBinding.usuaToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
    }

    // return to the previous page. Kill the current page.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //close the current activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerUser() {
        //get user data
        String fullName = mActivityUserSignUpBinding.usuaEtFullName.getText().toString().trim();
        String gender = mActivityUserSignUpBinding.usuaSpinnerGender.toString().trim();
        String icNo = mActivityUserSignUpBinding.usuaEtIcNo.getText().toString().trim();
        String phoneNo = mActivityUserSignUpBinding.usuaEtPhoneNumber.getText().toString().trim();
        String emailAddress = mActivityUserSignUpBinding.usuaEtEmailAddress.getText().toString().trim();
        String password = mActivityUserSignUpBinding.usuaEtPassword.getText().toString().trim();
        String confirmPass = mActivityUserSignUpBinding.usuaEtConfirmPassword.getText().toString().trim();

        //validate to check if name is empty
        if (fullName.isEmpty()) {
            mActivityUserSignUpBinding.usuaEtFullName.setError("Full name is required!");
            mActivityUserSignUpBinding.usuaEtFullName.requestFocus();
            return;
        }
        //validate to check if gender is empty
        if (gender.isEmpty()) {
            Toast.makeText(this, "Gender is required!", Toast.LENGTH_SHORT).show();
            mActivityUserSignUpBinding.usuaSpinnerGender.requestFocus();
            return;
        }
        //validate to check if ic no is empty
        if (icNo.isEmpty()) {
            Toast.makeText(this, "NRIC number is required!", Toast.LENGTH_SHORT).show();
            mActivityUserSignUpBinding.usuaEtIcNo.requestFocus();
            return;
        }
        //validate to check if phone number is empty
        if (phoneNo.isEmpty()) {
            mActivityUserSignUpBinding.usuaEtPhoneNumber.setError("Phone number is required!");
            mActivityUserSignUpBinding.usuaEtPhoneNumber.requestFocus();
            return;
        }
        //validate to check if phone number is less than 10 characters
        if (phoneNo.length() < 10) {
            mActivityUserSignUpBinding.usuaEtPhoneNumber.setError("Phone number must be at least 10 characters!");
            mActivityUserSignUpBinding.usuaEtPhoneNumber.requestFocus();
            return;
        }
        //validate to check if email is empty
        if (emailAddress.isEmpty()) {
            mActivityUserSignUpBinding.usuaEtEmailAddress.setError("Email address is required!");
            mActivityUserSignUpBinding.usuaEtEmailAddress.requestFocus();
            return;
        }
        //validate to check if email format is invalid
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            mActivityUserSignUpBinding.usuaEtEmailAddress.setError("Email address is invalid!");
            mActivityUserSignUpBinding.usuaEtEmailAddress.requestFocus();
            return;
        }
        //validate to check if password is empty
        if (password.isEmpty()) {
            mActivityUserSignUpBinding.usuaEtPassword.setError("Password is required!");
            mActivityUserSignUpBinding.usuaEtPassword.requestFocus();
            return;
        }
        //validate to check if password is less than 8 characters
        if (password.length() < 8) {
            mActivityUserSignUpBinding.usuaEtPassword.setError("Password should be at least 8 characters!");
            mActivityUserSignUpBinding.usuaEtPassword.requestFocus();
            return;
        }
        //validate to check if confirm password is empty
        if (confirmPass.isEmpty()) {
            mActivityUserSignUpBinding.usuaEtConfirmPassword.setError("Confirm password is required!");
            mActivityUserSignUpBinding.usuaEtConfirmPassword.requestFocus();
            return;
        }
        //validate to check if both password match
        if (!confirmPass.equals(password)) {
            mActivityUserSignUpBinding.usuaEtConfirmPassword.setError("Both passwords does not match!");
            mActivityUserSignUpBinding.usuaEtConfirmPassword.requestFocus();
        }

        //show progress bar
        mProgressBar.setVisibility(View.VISIBLE);

        //create user in the firebase authentication
        firebaseAuth.createUserWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AuthResult authResult = task.getResult();
                        if (authResult != null) {
                            String userId = Objects.requireNonNull(authResult.getUser()).getUid();

                            User newUser = new User(fullName, gender, icNo, phoneNo, emailAddress,"active");

                            db.collection("users").document(userId)
                                    .set(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        //display message if user successfully registered
                                        Toast.makeText(UserSignUpActivity.this, "Account has successfully registered!", Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        //close activity
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        //display message if user fail to register
                                        Toast.makeText(UserSignUpActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                    });
                        }
                    } else {
                        //display error message if user already exist
                        Toast.makeText(UserSignUpActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
                    }
                });

    };
    }

