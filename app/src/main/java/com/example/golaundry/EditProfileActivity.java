package com.example.golaundry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.viewModel.UserViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private UserViewModel mUserViewModel;
    private String selectedGender = "";
    EditText userNameEditText;
    EditText icNoEditText;
    EditText phoneNoEditText;
    String currentUserId;
    private final int SELECT_PROFILE_PICTURE = 5;
    private Uri profilePicUri;
    String imageURL;
    ImageView ProfilePictureImageView;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.aep_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        //get current user id
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        //setup spinner array
        Spinner spinner = findViewById(R.id.aep_spinner_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //when user choose gender
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

        //get user and membership data to show
        mUserViewModel.getUserData(currentUserId).observe(this, user -> {
            if (user != null) {
                //declare view id
                userNameEditText = findViewById(R.id.aep_et_name);
                icNoEditText = findViewById(R.id.aep_et_enter_ic_no);
                phoneNoEditText = findViewById(R.id.aep_et_phone_number);
                ProfilePictureImageView = findViewById(R.id.aep_iv_profile);

                //take away +60
                String phoneNoShow = user.getPhoneNo().replace("+60", "");

                //show edit text data
                userNameEditText.setText(user.getFullName());
                icNoEditText.setText(user.getIcNo());
                phoneNoEditText.setText(phoneNoShow);

                //show gender
                String userGender = user.getGender();
                int genderPosition = adapter.getPosition(userGender);
                spinner.setSelection(genderPosition);

                //show image
                String avatarUrl = user.getAvatar();
                if (!Objects.equals(avatarUrl, "")) {
                    setAvatar(avatarUrl,ProfilePictureImageView);
                }
            }
        });

        //check and register user
        findViewById(R.id.aep_btn_save).setOnClickListener(v -> saveUser());

        //let user select image
        findViewById(R.id.aep_btn_edit_profile_picture).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), SELECT_PROFILE_PICTURE);
        });

    }

    //show profile picture with getting the image url
    private void setAvatar(String avatarUrl, ImageView profilePictureImageView) {
        //referenceFromUrl to get StorageReference
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(avatarUrl);

        try {
            File localFile = File.createTempFile("tempfile", ".jpg");

            mStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                //show
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                profilePictureImageView.setImageBitmap(bitmap);

            }).addOnFailureListener(e -> {
                Toast.makeText(EditProfileActivity.this, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //process images
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PROFILE_PICTURE && data != null && data.getData() != null) {
                // Handle selected profile pic image
                profilePicUri = data.getData();

                //show what user select
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profilePicUri);
                    ProfilePictureImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void saveUser() {

        ProgressBar mProgressBar = findViewById(R.id.aep_progressbar);
        // show the visibility of progress bar to show loading
        mProgressBar.setVisibility(View.VISIBLE);

        String fullName = userNameEditText.getText().toString().trim();
        String icNo = icNoEditText.getText().toString().trim();
        String phoneNo = phoneNoEditText.getText().toString().trim();
//        String profilePicPhoto = String.valueOf(profilePicUri);

        //validate to check if name is empty
        if (fullName.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            userNameEditText.setError("Full name is required");
            Toast.makeText(this, R.string.fullNameRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.aep_et_name).requestFocus();
            return;
        }
        //validate to check if gender is selected
        else if (Objects.equals(selectedGender, "Select gender")) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, R.string.genderRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.aep_spinner_gender).requestFocus();
            return;

        }
        //validate to check if ic no is empty
        else if (icNo.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            icNoEditText.setError("NRIC number is required!");
            Toast.makeText(this, R.string.icRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.aep_et_enter_ic_no).requestFocus();
        } else if (icNo.length() != 12) {
            mProgressBar.setVisibility(View.GONE);
            icNoEditText.setError("NRIC number is invalid!");
            Toast.makeText(this, R.string.icInvalidToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.aep_et_enter_ic_no).requestFocus();
        }
        //validate to check if phone number is empty
        else if (phoneNo.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            phoneNoEditText.setError("Phone number is required!");
            Toast.makeText(this, R.string.phoneNoRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.aep_et_phone_number).requestFocus();
            return;
        }
        //validate to check if phone number is less than 9 characters
        else if (phoneNo.length() < 9) {
            mProgressBar.setVisibility(View.GONE);
            phoneNoEditText.setError("Phone number must be at least 9 characters!");
            Toast.makeText(this, R.string.phoneNoLessToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.aep_et_phone_number).requestFocus();
            return;
        } else {

            //if user didn't choose an image, will just upload db fields
            if (profilePicUri == null) {
                Map<String, Object> updates = new HashMap<>();

                updates.put("fullName", fullName);
                updates.put("gender", selectedGender);
                updates.put("phoneNo", phoneNo);

                //vm update user data
                mUserViewModel.updateUserData(currentUserId, updates).observe(this, updateUserResult -> {
                    if (updateUserResult != null && updateUserResult) {
                        Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                        finish();

                    } else {
                        Toast.makeText(EditProfileActivity.this, "Profile Update failed!", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    }
                });

            } else {
                //if user choose an image
                Map<String, Object> updates = new HashMap<>();

                updates.put("fullName", fullName);
                updates.put("gender", selectedGender);
                updates.put("phoneNo", phoneNo);

                imageURL = UUID.randomUUID().toString();

                //vm update user data & profile picture
                mUserViewModel.updateUserDataProfilePic(currentUserId, updates, profilePicUri).observe(this, updateUserProfilePicResult -> {
                    if (updateUserProfilePicResult != null && updateUserProfilePicResult) {
                        Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Profile Update failed!", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}