package com.example.golaundry;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.model.UserModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;


import java.util.Objects;

public class UserSignUpActivity extends AppCompatActivity {

    private UserViewModel mUserViewModel;
    private String selectedGender = "";

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        //toolbar function
        Toolbar toolbar = (Toolbar) findViewById(R.id.usua_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        //setup spinner array
        Spinner spinner = (Spinner) findViewById(R.id.usua_spinner_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parentView.getChildAt(0)).setTextSize(14);
                selectedGender = spinner.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        //check and register user
        findViewById(R.id.usua_btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
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

        ProgressBar mProgressBar = findViewById(R.id.usua_progressbar);
        // show the visibility of progress bar to show loading
        mProgressBar.setVisibility(View.VISIBLE);

        CheckBox mCheckBox = findViewById(R.id.usua_checkBox);

        //get user data
        EditText fullNameEditText = findViewById(R.id.usua_et_full_name);
        EditText icNoEditText = findViewById(R.id.usua_et_ic_no);
        EditText phoneNoEditText = findViewById(R.id.usua_et_phone_number);
        EditText emailAddressEditText = findViewById(R.id.usua_et_email_address);
        EditText passwordEditText = findViewById(R.id.usua_et_password);
        EditText confirmPassEditText = findViewById(R.id.usua_et_confirm_password);

        String fullName = fullNameEditText.getText().toString().trim();
        String icNo = icNoEditText.getText().toString().trim();
        String phoneNo = phoneNoEditText.getText().toString().trim();
        String emailAddress = emailAddressEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPass = confirmPassEditText.getText().toString().trim();

        if (!mCheckBox.isChecked()){
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, R.string.checkBoxToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_checkBox).requestFocus();
            return;
        }
        //validate to check if name is empty
        if (fullName.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            fullNameEditText.setError("Full name is required");
            Toast.makeText(this, R.string.fullNameRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_et_full_name).requestFocus();
            return;
        }
        //validate to check if gender is selected
        if (Objects.equals(selectedGender, "Select gender")) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, R.string.genderRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_spinner_gender).requestFocus();
            return;
        }
        //validate to check if ic no is empty
        if (icNo.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            icNoEditText.setError("NRIC number is required!");
            Toast.makeText(this, R.string.icRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_et_ic_no).requestFocus();
            return;
        }
        if (icNo.length() != 12) {
            mProgressBar.setVisibility(View.GONE);
            icNoEditText.setError("NRIC number is invalid!");
            Toast.makeText(this, R.string.icInvalidToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_et_ic_no).requestFocus();
            return;
        }
        //validate to check if phone number is empty
        if (phoneNo.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            phoneNoEditText.setError("Phone number is required!");
            Toast.makeText(this, R.string.phoneNoRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_et_phone_number).requestFocus();
            return;
        }
        //validate to check if phone number is less than 9 characters
        if (phoneNo.length() < 9) {
            mProgressBar.setVisibility(View.GONE);
            phoneNoEditText.setError("Phone number must be at least 9 characters!");
            Toast.makeText(this, R.string.phoneNoLessToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_et_phone_number).requestFocus();
            return;
        }
        //validate to check if email is empty
        if (emailAddress.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            emailAddressEditText.setError("Email address is required!");
            Toast.makeText(this, R.string.emailRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_et_email_address).requestFocus();
            return;
        }
        //validate to check if email format is invalid
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            mProgressBar.setVisibility(View.GONE);
            emailAddressEditText.setError("Email address is invalid!");
            Toast.makeText(this, R.string.emailInvalidToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_et_email_address).requestFocus();
            return;
        }
        //validate to check if password is empty
        if (password.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            passwordEditText.setError("Password is required!");
            Toast.makeText(this, R.string.passwordRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_et_password).requestFocus();
            return;
        }
        //validate to check if password is less than 8 characters
        if (password.length() < 8) {
            mProgressBar.setVisibility(View.GONE);
            passwordEditText.setError("Password should be at least 8 characters!");
            Toast.makeText(this, R.string.passwordLessToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_et_password).requestFocus();
            return;
        }
        //validate to check if confirm password is empty
        if (confirmPass.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            confirmPassEditText.setError("Confirm password is required!");
            Toast.makeText(this, R.string.confirmPasswordRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_et_confirm_password).requestFocus();
            return;
        }
        //validate to check if both password match
        if (!confirmPass.equals(password)) {
            mProgressBar.setVisibility(View.GONE);
            confirmPassEditText.setError("Both passwords does not match!");
            Toast.makeText(this, R.string.confirmPasswordNotMatchToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.usua_et_confirm_password).requestFocus();
        } else {

            UserModel newUser = new UserModel(fullName, selectedGender, icNo, phoneNo, emailAddress, "active", "user");

            //vm create user
            mUserViewModel.createUser(emailAddress, password, newUser)
                    .observe(this, signUpResult -> {
                        if (signUpResult != null && signUpResult) {
                            // User registration success
                            Toast.makeText(UserSignUpActivity.this, "Account has successfully registered!", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                            finish();
                        } else {
                            // User registration failed
                            Toast.makeText(UserSignUpActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });

            // create new user
//            mAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if (task.isSuccessful()) {
//                        // Registration successful
//                        Toast.makeText(getApplicationContext(),
//                                "Registration successful!",
//                                Toast.LENGTH_LONG).show();
//
//                        // Get the UID of the newly created user
//                        String uid = Objects.requireNonNull(task.getResult().getUser()).getUid();
//
//                        // Initialize Firebase Realtime Database reference
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
//
//                        // Create a new child node with the UID and set the user data
//                        databaseReference.child(uid).setValue(newUser);
//
//                        // Hide the progress bar
//                        mProgressBar.setVisibility(View.GONE);
//                    } else {
//                        // Registration failed
//                        String errorMessage = Objects.requireNonNull(task.getException()).getMessage(); // Get the error message
//                        Toast.makeText(
//                                getApplicationContext(),
//                                "Registration failed: " + errorMessage,
//                                Toast.LENGTH_LONG).show();
//                        mProgressBar.setVisibility(View.GONE);
//                    }
//                }
//            });


        }
    }
}