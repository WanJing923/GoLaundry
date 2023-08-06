package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.golaundry.databinding.ActivityHelpCenterBinding;
import com.example.golaundry.databinding.ActivityUserSignUpBinding;

import java.util.Objects;

public class HelpCenterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //implement binding method
        com.example.golaundry.databinding.ActivityHelpCenterBinding mActivityHelpCenterBinding = ActivityHelpCenterBinding.inflate(getLayoutInflater());
        setContentView(mActivityHelpCenterBinding.getRoot());

        //toolbar function
        setSupportActionBar(mActivityHelpCenterBinding.haToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(mActivityHelpCenterBinding.haToolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mActivityHelpCenterBinding.haToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
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