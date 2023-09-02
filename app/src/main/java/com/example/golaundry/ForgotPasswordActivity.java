package com.example.golaundry;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.viewModel.ForgotPasswordViewModel;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ForgotPasswordViewModel mForgotPasswordViewModel;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mForgotPasswordViewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        //toolbar function
        Toolbar toolbar = (Toolbar) findViewById(R.id.fpa_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon((getResources().getDrawable(R.drawable.ic_toolbar_back)));

        findViewById(R.id.fpa_btn_submit).setOnClickListener(v -> checkSendEmail());
    }

    private void checkSendEmail() {

        //get email address
        EditText emailEditText = findViewById(R.id.fpa_et_email_address);
        String emailAddress = emailEditText.getText().toString();

        ProgressBar mProgressBar = findViewById(R.id.fpa_progressbar);
        // show the visibility of progress bar to show loading
        mProgressBar.setVisibility(View.VISIBLE);

        //validate to check if email is empty
        if (emailAddress.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            emailEditText.setError("Email address is required!");
            Toast.makeText(this, R.string.emailRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.fpa_et_email_address).requestFocus();
            return;

        }
        //validate to check if email format is invalid
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            mProgressBar.setVisibility(View.GONE);
            emailEditText.setError("Email address is invalid!");
            Toast.makeText(this, R.string.emailInvalidToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.fpa_et_email_address).requestFocus();

        } else {

            mForgotPasswordViewModel.sendEmail(emailAddress).observe(this, sendEmailResult -> {
                if (sendEmailResult != null && sendEmailResult) {
                    //email sent success
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your email to reset password.", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            });

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