package com.thehollidayinn.kibbl.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.ui.fragments.FavoritesListFragment;
import com.thehollidayinn.kibbl.ui.fragments.NotificationListFragment;

public class NotificationsActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        NotificationListFragment favoritesList = NotificationListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, favoritesList).commit();
    }
}
