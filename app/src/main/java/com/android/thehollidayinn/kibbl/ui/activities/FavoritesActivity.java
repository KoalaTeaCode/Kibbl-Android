package com.android.thehollidayinn.kibbl.ui.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.thehollidayinn.kibbl.R;
import com.android.thehollidayinn.kibbl.ui.fragments.FavoritesListFragment;
import com.android.thehollidayinn.kibbl.ui.fragments.ListContentFragment;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpListFragment();
    }

    private void setUpListFragment() {
        FavoritesListFragment favoritesList = FavoritesListFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.content_fragment, favoritesList);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
