package com.thehollidayinn.kibbl.ui.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.UserLogin;
import com.thehollidayinn.kibbl.ui.fragments.FollowingListFragment;
import com.thehollidayinn.kibbl.ui.fragments.UpdatesListFragment;

/**
 * Created by krh12 on 6/10/2017.
 */

public class UpdatesActivity extends AppCompatActivity {

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

        UserLogin userLogin = UserLogin.getInstance(this);
        userLogin.setHasMessages(false);

        logStat();

        setUpListFragment();
    }

    private void logStat() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "updates activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "page");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void setUpListFragment() {
        UpdatesListFragment favoritesList = UpdatesListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, favoritesList).commit();
    }
}

