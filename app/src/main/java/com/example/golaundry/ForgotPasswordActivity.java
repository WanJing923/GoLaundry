package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.golaundry.databinding.ActivityForgotPasswordBinding;
import com.example.golaundry.databinding.ActivityHelpCenterBinding;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //implement binding method
        com.example.golaundry.databinding.ActivityForgotPasswordBinding mActivityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(mActivityForgotPasswordBinding.getRoot());

        //toolbar function
        setSupportActionBar(mActivityForgotPasswordBinding.fpaToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(mActivityForgotPasswordBinding.fpaToolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mActivityForgotPasswordBinding.fpaToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}