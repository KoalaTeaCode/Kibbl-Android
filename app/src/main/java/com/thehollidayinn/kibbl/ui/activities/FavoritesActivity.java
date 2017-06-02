package com.thehollidayinn.kibbl.ui.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.ui.fragments.FavoritesListFragment;
import com.thehollidayinn.kibbl.ui.fragments.ListContentFragment;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


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
        getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, favoritesList).commit();
    }
}
