package com.example.golaundry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.golaundry.adapter.MainAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.main_tab);
        ViewPager viewPager = findViewById(R.id.main_vp);

        tabLayout.setupWithViewPager(viewPager);

        MainAdapter vpAdapter = new MainAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addfragment(new LoginFragment(), "LOGIN");
        vpAdapter.addfragment(new SignUpFragment(), "SIGN UP");
        viewPager.setAdapter(vpAdapter);

    }
}