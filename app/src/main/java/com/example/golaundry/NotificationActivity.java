package com.example.golaundry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.PorterDuff;
import android.os.Bundle;

import com.example.golaundry.databinding.ActivityNotificationBinding;
import com.example.golaundry.databinding.ActivityUserSignUpBinding;

import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //implement binding method
        com.example.golaundry.databinding.ActivityNotificationBinding mActivityNotificationBinding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(mActivityNotificationBinding.getRoot());

        //toolbar function
        setSupportActionBar(mActivityNotificationBinding.naToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(mActivityNotificationBinding.naToolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mActivityNotificationBinding.naToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
    }
}