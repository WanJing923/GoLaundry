package com.example.golaundry.view;

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

import com.example.golaundry.R;
import com.example.golaundry.viewModel.ForgotPasswordViewModel;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {

    private ForgotPasswordViewModel mForgotPasswordViewModel;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mForgotPasswordViewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.rpa_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        findViewById(R.id.rpa_btn_done).setOnClickListener(v -> checkSendEmail());
    }

    private void checkSendEmail() {

        //get email address
        EditText emailEditText = findViewById(R.id.rpa_et_enter_email);
        String emailAddress = emailEditText.getText().toString();

        ProgressBar mProgressBar = findViewById(R.id.rpa_progressbar);
        // show the visibility of progress bar to show loading
        mProgressBar.setVisibility(View.VISIBLE);

        //validate to check if email is empty
        if (emailAddress.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            emailEditText.setError("Email address is required!");
            Toast.makeText(this, R.string.emailRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rpa_et_enter_email).requestFocus();
            return;

        }
        //validate to check if email format is invalid
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            mProgressBar.setVisibility(View.GONE);
            emailEditText.setError("Email address is invalid!");
            Toast.makeText(this, R.string.emailInvalidToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.rpa_et_enter_email).requestFocus();

        } else {

            mForgotPasswordViewModel.sendEmail(emailAddress).observe(this, sendEmailResult -> {
                if (sendEmailResult != null && sendEmailResult) {
                    //email sent success
                    Toast.makeText(ResetPasswordActivity.this, "Please check your email to reset password.", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                    finish();
                } else {
                    //email sending failed
                    Toast.makeText(ResetPasswordActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
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