package com.example.golaundry.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.R;
import com.example.golaundry.model.RiderModel;
import com.example.golaundry.viewModel.RiderViewModel;
import com.google.firebase.storage.StorageTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RiderSignUpActivity extends AppCompatActivity {

    private RiderViewModel mRiderViewModel;
    private final int SELECT_FACE_PHOTO = 1;
    private final int SELECT_DRIVING_LICENSE = 2;
    private Uri FPfilepath, DLfilepath;
    private Uri facePhotoUri;
    private Uri drivingLicenseUri;
    String myUrl = "";
    private Boolean validateImage = false;
    private StorageTask uploadTask;


    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_sign_up);

        //setup rider view model
        mRiderViewModel = new ViewModelProvider(this).get(RiderViewModel.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.rsua_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        findViewById(R.id.rsua_et_upload_face_photo).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Face Photo"), SELECT_FACE_PHOTO);
        });

        findViewById(R.id.rsua_et_upload_driving_license).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Driving License"), SELECT_DRIVING_LICENSE);
        });

        //check and register user
        findViewById(R.id.rsua_btn_register).setOnClickListener(v -> registerRider());
    }

    //process images
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FACE_PHOTO && data != null && data.getData() != null) {
                // Handle selected face photo
                facePhotoUri = data.getData();

                ImageView facePhotoImageView = findViewById(R.id.rsua_face_photo_done);
                facePhotoImageView.setVisibility(View.VISIBLE);

                EditText facePhotoEditText = findViewById(R.id.rsua_et_upload_face_photo);
                facePhotoEditText.setHint("Uploaded Face Photo");
            }

            if (requestCode == SELECT_DRIVING_LICENSE && data != null && data.getData() != null) {
                // Handle selected driving license photo
                drivingLicenseUri = data.getData();

                ImageView drivingLicenseImageView = findViewById(R.id.rsua_driving_license_done);
                drivingLicenseImageView.setVisibility(View.VISIBLE);

                EditText drivingLicenseEditText = findViewById(R.id.rsua_et_upload_driving_license);
                drivingLicenseEditText.setHint("Uploaded Driving License");
            }
        }
    }

    private void registerRider() {

        ProgressBar mProgressBar = findViewById(R.id.rsua_progressbar);
        // show the visibility of progress bar to show loading
        mProgressBar.setVisibility(View.VISIBLE);

        //get current date time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

        CheckBox mCheckBox = findViewById(R.id.rsua_checkBox);

        //get user data
        EditText fullNameEditText = findViewById(R.id.rsua_et_enter_full_name);
        EditText contactNoEditText = findViewById(R.id.rsua_et_contact_num);
        EditText emailAddressEditText = findViewById(R.id.rsua_et_enter_email_address);
        EditText plateNumberEditText = findViewById(R.id.rsua_et_enter_plate_num);
        EditText icNoEditText = findViewById(R.id.rsua_et_ic_no);
        EditText passwordEditText = findViewById(R.id.rsua_et_enter_password);
        EditText confirmPassEditText = findViewById(R.id.rsua_et_confirm_password);
        EditText drivingLicenseEditText = findViewById(R.id.rsua_et_upload_driving_license);
        EditText facePhotoEditText = findViewById(R.id.rsua_et_upload_face_photo);

        String fullName = fullNameEditText.getText().toString().trim();
        String contactNo = contactNoEditText.getText().toString().trim();
        String emailAddress = emailAddressEditText.getText().toString().trim();
        String plateNumber = plateNumberEditText.getText().toString().trim();
        String facePhoto = String.valueOf(facePhotoUri);
        String drivingLicensePhoto = String.valueOf(drivingLicenseUri);
        String icNo = icNoEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPass = confirmPassEditText.getText().toString().trim();
        String registerDateTime = sdf.format(new Date());

        if (!mCheckBox.isChecked()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, R.string.checkBoxToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_checkBox).requestFocus();
            return;
        }
        //validate to check if name is empty
        if (fullName.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            fullNameEditText.setError("Full name is required");
            Toast.makeText(this, R.string.fullNameRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_enter_full_name).requestFocus();
            return;
        }
        //validate to check if phone number is empty
        if (contactNo.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            contactNoEditText.setError("Contact number is required!");
            Toast.makeText(this, R.string.contactNoRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_contact_num).requestFocus();
            return;
        }
        //validate to check if phone number is less than 9 characters
        if (contactNo.length() < 9) {
            mProgressBar.setVisibility(View.GONE);
            contactNoEditText.setError("Contact number must be at least 9 characters!");
            Toast.makeText(this, R.string.contactNoLessToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_contact_num).requestFocus();
            return;
        }
        //validate to check if email is empty
        if (emailAddress.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            emailAddressEditText.setError("Email address is required!");
            Toast.makeText(this, R.string.emailRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_enter_email_address).requestFocus();
            return;
        }
        //validate to check if email format is invalid
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            mProgressBar.setVisibility(View.GONE);
            emailAddressEditText.setError("Email address is invalid!");
            Toast.makeText(this, R.string.emailInvalidToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_enter_email_address).requestFocus();
            return;
        }
        //validate to check if email is empty
        if (plateNumber.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            plateNumberEditText.setError("Plate number is required!");
            Toast.makeText(this, R.string.plateNumberRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_enter_plate_num).requestFocus();
            return;
        }
        //validate to check if plate number is less than 7 characters
        if (plateNumber.length() < 7) {
            mProgressBar.setVisibility(View.GONE);
            plateNumberEditText.setError("Plate number should be more than 6 characters!");
            Toast.makeText(this, R.string.plateNumberLessToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_enter_plate_num).requestFocus();
            return;
        }
        //validate to check if face photo is empty
        if (facePhoto.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            facePhotoEditText.setError("Face photo is required!");
            Toast.makeText(this, R.string.facePhotoRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_upload_face_photo).requestFocus();
            return;
        }
        //validate to check if license photo is empty
        if (drivingLicensePhoto.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            drivingLicenseEditText.setError("Driving license photo is required!");
            Toast.makeText(this, R.string.drivingLicensePhotoRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_upload_driving_license).requestFocus();
            return;
        }
        //validate to check if ic no is empty
        if (icNo.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            icNoEditText.setError("NRIC number is required!");
            Toast.makeText(this, R.string.icRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_ic_no).requestFocus();
            return;
        }
        if (icNo.length() != 12) {
            mProgressBar.setVisibility(View.GONE);
            icNoEditText.setError("NRIC number is invalid!");
            Toast.makeText(this, R.string.icInvalidToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_ic_no).requestFocus();
            return;
        }
        //validate to check if password is empty
        if (password.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            passwordEditText.setError("Password is required!");
            Toast.makeText(this, R.string.passwordRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_enter_password).requestFocus();
            return;
        }
        //validate to check if password is less than 8 characters
        if (password.length() < 8) {
            mProgressBar.setVisibility(View.GONE);
            passwordEditText.setError("Password should be at least 8 characters!");
            Toast.makeText(this, R.string.passwordLessToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_enter_password).requestFocus();
            return;
        }
        //validate to check if confirm password is empty
        if (confirmPass.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            confirmPassEditText.setError("Confirm password is required!");
            Toast.makeText(this, R.string.confirmPasswordRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_confirm_password).requestFocus();
            return;
        }
        //validate to check if both password match
        if (!confirmPass.equals(password)) {
            mProgressBar.setVisibility(View.GONE);
            confirmPassEditText.setError("Both passwords does not match!");
            Toast.makeText(this, R.string.confirmPasswordNotMatchToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rsua_et_confirm_password).requestFocus();
        } else {

            RiderModel newRider = new RiderModel("", fullName, "+60" + contactNo, emailAddress, plateNumber, facePhoto, drivingLicensePhoto, icNo, registerDateTime, "terminated", "rider", true, 0.0, 0, true);

            mRiderViewModel.signUpRiderWithImage(emailAddress, password, newRider).observe(this, signUpSuccess -> {
                if (signUpSuccess) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RiderSignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Pending approve from admin")
                            .setMessage("Rider account may need admin to validate information. This action may take 1 to 3 working days. You will receive an email after approving.");

                    SpannableString spannableString = new SpannableString("OK");
                    spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableString.length(), 0);

                    builder.setPositiveButton(spannableString, (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    }).show();

                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RiderSignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                }
            });

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