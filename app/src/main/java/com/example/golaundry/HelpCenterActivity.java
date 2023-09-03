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

import com.example.golaundry.model.HelpCenterModel;
import com.example.golaundry.viewModel.HelpCenterViewModel;

import java.util.Objects;

public class HelpCenterActivity extends AppCompatActivity {

    private HelpCenterViewModel mHelpCenterViewModel;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        mHelpCenterViewModel = new ViewModelProvider(this).get(HelpCenterViewModel.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ha_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back));

        findViewById(R.id.ha_btn_submit).setOnClickListener(v -> addHelpCenterMessage());
    }

    private void addHelpCenterMessage() {

        ProgressBar mProgressBar = findViewById(R.id.ha_progressbar);
        // show the visibility of progress bar to show loading
        mProgressBar.setVisibility(View.VISIBLE);

        //get user data
        EditText emailEditText = findViewById(R.id.ha_et_email);
        EditText titleEditText = findViewById(R.id.ha_et_title);
        EditText messageEditText = findViewById(R.id.ha_et_message);

        String emailAddress = emailEditText.getText().toString().trim();
        String title = titleEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();

        //validate to check if email is empty
        if (emailAddress.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            emailEditText.setError("Email address is required!");
            Toast.makeText(this, R.string.emailRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.ha_et_email).requestFocus();
            return;
        }
        //validate to check if email format is invalid
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            mProgressBar.setVisibility(View.GONE);
            emailEditText.setError("Email address is invalid!");
            Toast.makeText(this, R.string.emailInvalidToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.ha_et_email).requestFocus();
            return;
        }
        //validate to check if title is empty
        if (title.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            titleEditText.setError("Title is required!");
            Toast.makeText(this, R.string.titleRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.ha_et_title).requestFocus();
            return;
        }
        //validate to check if message is empty
        if (message.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            messageEditText.setError("Message is required!");
            Toast.makeText(this, R.string.msgRequiredToast, Toast.LENGTH_SHORT).show();
            findViewById(R.id.ha_et_message).requestFocus();
            return;
        } else {

            HelpCenterModel newHelp = new HelpCenterModel(emailAddress, title, message, "Pending");

            mHelpCenterViewModel.addHelpCenterMessage(newHelp)
                    .observe(this, addHelpResult -> {
                        if (addHelpResult != null && addHelpResult) {
                            // User registration success
                            Toast.makeText(HelpCenterActivity.this, "Your message has successfully submitted!", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                            finish();
                        } else {
                            // User registration failed
                            Toast.makeText(HelpCenterActivity.this, "Submit failed!", Toast.LENGTH_SHORT).show();
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