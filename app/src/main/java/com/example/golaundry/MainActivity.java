package com.example.golaundry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;

import com.example.golaundry.adapter.MainAdapter;
import com.example.golaundry.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //implement binding method
        com.example.golaundry.databinding.ActivityMainBinding mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mActivityMainBinding.getRoot());

        mActivityMainBinding.mainTab.setupWithViewPager(mActivityMainBinding.mainVp);

        MainAdapter vpAdapter = new MainAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addfragment(new LoginFragment(), "LOGIN");
        vpAdapter.addfragment(new SignUpFragment(), "SIGN UP");
        mActivityMainBinding.mainVp.setAdapter(vpAdapter);

    }
}