package com.mtsd.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mtsd.R;
import com.mtsd.fragment.MovementsFragment;
import com.mtsd.fragment.ProductListFragment;
import com.mtsd.fragment.HomeFragment;
import com.mtsd.fragment.ProfileFragment;
import com.mtsd.fragment.ReportsFragment;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = new Locale("es");
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        setContentView(R.layout.activity_base); // Base layout contains BottomNavigationView


        // Check login status only once when activity is created
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;  // Ensure the rest of onCreate is skipped if redirecting
        }

        // Setup Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        // Load the default fragment (HomeFragment) initially
        loadFragment(new HomeFragment());
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.nav_home:
                selectedFragment = new HomeFragment();
                break;
            case R.id.nav_profile:
                selectedFragment = new ProfileFragment();
                break;
            case R.id.nav_movements:
                selectedFragment = new MovementsFragment();
                break;
            case R.id.nav_products:
                selectedFragment = new ProductListFragment();
                break;
            case R.id.nav_reports:
                selectedFragment = new ReportsFragment();
                break;
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            item.setChecked(true);
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentFrame, fragment);
        transaction.addToBackStack(null); // Optional
        transaction.commit();
    }

    public void loadFragmentFromFragment(Fragment fragment){
        loadFragment(fragment);
    }
}
