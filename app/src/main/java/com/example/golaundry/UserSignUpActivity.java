package com.example.golaundry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.golaundry.databinding.ActivityMainBinding;
import com.example.golaundry.databinding.ActivityUserSignUpBinding;

import java.util.Objects;

public class UserSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //implement binding method
        com.example.golaundry.databinding.ActivityUserSignUpBinding mActivityUserSignUpBinding = ActivityUserSignUpBinding.inflate(getLayoutInflater());
        setContentView(mActivityUserSignUpBinding.getRoot());

        //toolbar function
        setSupportActionBar(mActivityUserSignUpBinding.usuaToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(mActivityUserSignUpBinding.usuaToolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mActivityUserSignUpBinding.usuaToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
    }

    // return to the previous page. Kill the current page.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //close the current activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Check if there are any fragments in the back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // Pop the fragment from the back stack to navigate back to the previous fragment
            getSupportFragmentManager().popBackStack();
        } else {
            // If there are no fragments in the back stack, finish the activity to navigate back to the parent activity or previous activity
            super.onBackPressed();
        }
    }

}