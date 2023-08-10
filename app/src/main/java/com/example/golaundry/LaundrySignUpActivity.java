package com.example.golaundry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.RiderModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.google.firebase.storage.StorageTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class LaundrySignUpActivity extends AppCompatActivity {

    private LaundryViewModel mLaundryViewModel;
    private final int SELECT_BUSINESS_LICENSE = 3;
    private Uri BLfilepath;
    private Uri BLUri;
    String myUrl = "";
    private Boolean validateImage = false;
    private StorageTask uploadTask;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_sign_up);

        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.lsua_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        findViewById(R.id.lsua_et_upload_BL).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Business License"), SELECT_BUSINESS_LICENSE);
            }
        });

        findViewById(R.id.lsua_btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerLaundry();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_BUSINESS_LICENSE && data != null && data.getData() != null) {
                // Handle selected face photo
                BLUri = data.getData();

                ImageView BLImageView = findViewById(R.id.lsua_iv_BL_done);
                BLImageView.setVisibility(View.VISIBLE);

                EditText BLEditText = findViewById(R.id.lsua_et_upload_BL);
                BLEditText.setHint("Uploaded Photo");
            }
        }
    }

    private void registerLaundry() {

        ProgressBar mProgressBar = findViewById(R.id.lsua_progressbar);
        // show the visibility of progress bar to show loading
        mProgressBar.setVisibility(View.VISIBLE);

        //get current date time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

        CheckBox mCheckBox = findViewById(R.id.lsua_checkBox);

        //get user data
        EditText shopNameEditText = findViewById(R.id.lsua_et_enter_shop_name);
        EditText contactNoEditText = findViewById(R.id.lsua_et_contact_num);
        EditText emailAddressEditText = findViewById(R.id.lsua_et_enter_email_address);
        EditText addressEditText = findViewById(R.id.lsua_et_choose_location);
        EditText addressDetailsEditText = findViewById(R.id.lsua_et_address_details);
        EditText fullNameEditText = findViewById(R.id.lsua_et_enter_full_name);
        EditText phoneNoEditText = findViewById(R.id.lsua_et_personal_num);
        EditText icNoEditText = findViewById(R.id.lsua_et_ic_no);
        EditText passwordEditText = findViewById(R.id.lsua_et_enter_password);
        EditText confirmPassEditText = findViewById(R.id.lsua_et_confirm_password);

        String shopName = shopNameEditText.getText().toString().trim();
        String contactNo = contactNoEditText.getText().toString().trim();
        String emailAddress = emailAddressEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String addressDetails = addressDetailsEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String phoneNo = phoneNoEditText.getText().toString().trim();
        String BusinessLicensePhoto = String.valueOf(BLUri);
        String icNo = icNoEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPass = confirmPassEditText.getText().toString().trim();
        String registerDateTime = sdf.format(new Date());

        if (!mCheckBox.isChecked()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, R.string.checkBoxToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.lsua_checkBox).requestFocus();
            return;
        }
        //validate to check if shop name is empty
        if (shopName.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            shopNameEditText.setError("Shop name is required");
            findViewById(R.id.lsua_et_enter_shop_name).requestFocus();
            return;
        }
        //validate to check if contact number is empty
        if (contactNo.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            contactNoEditText.setError("Contact number is required!");
            findViewById(R.id.lsua_et_contact_num).requestFocus();
            return;
        }
        //validate to check if contact number is less than 9 characters
        if (contactNo.length() < 9) {
            mProgressBar.setVisibility(View.GONE);
            contactNoEditText.setError("Contact number must be at least 9 characters!");
            findViewById(R.id.lsua_et_contact_num).requestFocus();
            return;
        }
        //validate to check if email is empty
        if (emailAddress.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            emailAddressEditText.setError("Email address is required!");
            findViewById(R.id.lsua_et_enter_email_address).requestFocus();
            return;
        }
        //validate to check if email format is invalid
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            mProgressBar.setVisibility(View.GONE);
            emailAddressEditText.setError("Email address is invalid!");
            findViewById(R.id.lsua_et_enter_email_address).requestFocus();
            return;
        }
        //validate to check if address is empty
        if (address.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            emailAddressEditText.setError("Address is required!");
            findViewById(R.id.lsua_et_choose_location).requestFocus();
            return;
        }
        //validate to check if address details is empty
        if (addressDetails.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            emailAddressEditText.setError("Address is required!");
            findViewById(R.id.lsua_et_address_details).requestFocus();
            return;
        }
        //validate to check if face photo is empty
        if (BusinessLicensePhoto.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            passwordEditText.setError("Business license photo is required!");
            findViewById(R.id.lsua_et_upload_BL).requestFocus();
            return;
        }
        //validate to check if full name is empty
        if (fullName.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            shopNameEditText.setError("Full name is required");
            findViewById(R.id.lsua_et_enter_full_name).requestFocus();
            return;
        }
        //validate to check if phone number is empty
        if (phoneNo.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            contactNoEditText.setError("Personal number is required!");
            findViewById(R.id.lsua_et_personal_num).requestFocus();
            return;
        }
        //validate to check if phone number is less than 9 characters
        if (phoneNo.length() < 9) {
            mProgressBar.setVisibility(View.GONE);
            contactNoEditText.setError("Personal number must be at least 9 characters!");
            findViewById(R.id.lsua_et_personal_num).requestFocus();
            return;
        }
        //validate to check if ic no is empty
        if (icNo.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            icNoEditText.setError("NRIC number is required!");
            findViewById(R.id.lsua_et_ic_no).requestFocus();
            return;
        }
        if (icNo.length() != 12) {
            mProgressBar.setVisibility(View.GONE);
            icNoEditText.setError("NRIC number is invalid!");
            findViewById(R.id.lsua_et_ic_no).requestFocus();
            return;
        }
        //validate to check if password is empty
        if (password.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            passwordEditText.setError("Password is required!");
            findViewById(R.id.lsua_et_enter_password).requestFocus();
            return;
        }
        //validate to check if password is less than 8 characters
        if (password.length() < 8) {
            mProgressBar.setVisibility(View.GONE);
            passwordEditText.setError("Password should be at least 8 characters!");
            findViewById(R.id.lsua_et_enter_password).requestFocus();
            return;
        }
        //validate to check if confirm password is empty
        if (confirmPass.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            confirmPassEditText.setError("Confirm password is required!");
            findViewById(R.id.lsua_et_confirm_password).requestFocus();
            return;
        }
        //validate to check if both password match
        if (!confirmPass.equals(password)) {
            mProgressBar.setVisibility(View.GONE);
            confirmPassEditText.setError("Both passwords does not match!");
            findViewById(R.id.lsua_et_confirm_password).requestFocus();
        } else {

            LaundryModel newLaundry = new LaundryModel(fullName, contactNo, emailAddress, address, addressDetails, BusinessLicensePhoto, fullName, phoneNo, icNo, registerDateTime, "terminated", "laundry");

            mLaundryViewModel.signUpLaundryWithImage(emailAddress, password, newLaundry)
                    .observe(this, signUpSuccess -> {
                        if (signUpSuccess) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LaundrySignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LaundrySignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                        }
                    });

//            if (FPfilepath != null) {
//                String riderID = UUID.randomUUID().toString();
//
//                StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("Riders/" + riderID).child("FacePhoto");
//                uploadTask = fileRef.putFile(FPfilepath);
//                uploadTask.continueWithTask(task -> {
//                    if (!task.isComplete()) {
//                        throw Objects.requireNonNull(task.getException());
//                    }
//                    return fileRef.getDownloadUrl();
//                }).addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        //get the download Uri of the image
//                        Uri downloadUri = (Uri) task.getResult();
//                        myUrl = downloadUri.toString();
//
//                        FirebaseDatabase db = FirebaseDatabase.getInstance();
//                        DatabaseReference dbRef = db.getReference();
//                        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                if (!(snapshot.child("CommunityPost").child(riderID).exists())) {
//                                    HashMap<String, Object> data = new HashMap<>();
//
//                                    data.put("riderId", riderID);
//                                    data.put("dateTime", registerDateTime);
//                                    data.put("url", myUrl);
//
//                                    dbRef.child("riders").child(riderID).updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
//
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
//                                                Intent intent = new Intent(RiderSignUpActivity.this, HomeActivity.class);
//                                                startActivity(intent);
//                                                finish();
//
//                                            } else {
//                                                Toast.makeText(getApplicationContext(), "Network Error. Please Try Again", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "Network Error. Please try Again", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(RiderSignUpActivity.this, HomeActivity.class);
//                                    startActivity(intent);
//                                    finish();
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
//                }).addOnFailureListener(e -> Toast.makeText(RiderSignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//            } else {
//                Toast.makeText(RiderSignUpActivity.this, "No Image Selected!", Toast.LENGTH_SHORT).show();
//            }
//
//            //vm create user
//            mRiderViewModel.createRider(emailAddress, password, newRider)
//                    .observe(this, signUpResult -> {
//                        if (signUpResult != null && signUpResult) {
//                            // User registration success
//                            Toast.makeText(RiderSignUpActivity.this, "Account has successfully registered!", Toast.LENGTH_SHORT).show();
//                            mProgressBar.setVisibility(View.GONE);
//                            finish();
//                        } else {
//                            // User registration failed
//                            Toast.makeText(RiderSignUpActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
//                            mProgressBar.setVisibility(View.GONE);
//                        }
//                    });

        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //close the current activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}