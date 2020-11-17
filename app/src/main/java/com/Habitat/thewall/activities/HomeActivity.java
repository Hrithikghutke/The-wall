package com.Habitat.thewall.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.Habitat.thewall.R;
import com.Habitat.thewall.fragments.CategoriesFragment;
import com.Habitat.thewall.fragments.FavouritesFragment;
import com.Habitat.thewall.fragments.HomeFragment;
import com.Habitat.thewall.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbarlayout);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        displayFragment(new HomeFragment());
    }



    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_area, fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_categories:
                fragment = new CategoriesFragment();
                break;
            case R.id.nav_favourites:
                fragment = new FavouritesFragment();
                break;
            case R.id.nav_set:
                fragment = new SettingsFragment();
                break;



            default:
                fragment = new HomeFragment();
                break;
        }
        displayFragment(fragment);
        return true;


    }
}
