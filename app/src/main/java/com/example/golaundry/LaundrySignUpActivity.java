package com.example.golaundry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.golaundry.databinding.ActivityLaundrySignUpBinding;
import com.example.golaundry.databinding.ActivityUserSignUpBinding;

import java.util.Objects;

public class LaundrySignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //implement binding method
        com.example.golaundry.databinding.ActivityLaundrySignUpBinding mActivityLaundrySignUpBinding = ActivityLaundrySignUpBinding.inflate(getLayoutInflater());
        setContentView(mActivityLaundrySignUpBinding.getRoot());

        //toolbar function
        setSupportActionBar(mActivityLaundrySignUpBinding.usuaToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(mActivityLaundrySignUpBinding.usuaToolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mActivityLaundrySignUpBinding.usuaToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));

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