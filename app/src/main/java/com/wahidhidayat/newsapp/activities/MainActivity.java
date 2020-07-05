package com.wahidhidayat.newsapp.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.fragments.CategoryFragment;
import com.wahidhidayat.newsapp.fragments.FavoriteFragment;
import com.wahidhidayat.newsapp.fragments.HomeFragment;
import com.wahidhidayat.newsapp.notifications.DailyReminderReceiver;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        loadFragment(new HomeFragment());
        bottomNav.setOnNavigationItemSelectedListener(this);

        DailyReminderReceiver dailyReminderReceiver = new DailyReminderReceiver();
        dailyReminderReceiver.dailyReminderOn(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.home_page:
                fragment = new HomeFragment();
                break;
            case R.id.favorites_page:
                fragment = new FavoriteFragment();
                break;
            case R.id.categories_page:
                fragment = new CategoryFragment();
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}