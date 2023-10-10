package com.example.golaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.golaundry.adapter.MainAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_SETTINGS_REQUEST_CODE = 123;

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

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        boolean areNotificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!areNotificationsEnabled) {
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
            startActivityForResult(intent, NOTIFICATION_SETTINGS_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NOTIFICATION_SETTINGS_REQUEST_CODE) {
            if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}