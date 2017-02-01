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
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "favorites activite");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "page");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
