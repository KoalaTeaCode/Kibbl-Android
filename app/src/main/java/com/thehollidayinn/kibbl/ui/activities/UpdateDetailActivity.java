package com.thehollidayinn.kibbl.ui.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.Updates;
import com.thehollidayinn.kibbl.data.repositories.UpdatesRepository;
import com.thehollidayinn.kibbl.ui.fragments.EventListFragment;
import com.thehollidayinn.kibbl.ui.fragments.PetListFragment;

public class UpdateDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        UpdatesRepository updatesRepository = UpdatesRepository.getInstance();
        Bundle extras = getIntent().getExtras();
        String updateId = extras.getString("UPDATE_ID");
        if (updateId.isEmpty()) {
            return;
        }

        Updates update = updatesRepository.getUpdateById(updateId);
        if (update == null) {
            return;
        }

        EventListFragment favoritesList = EventListFragment.newInstance("", update.newEvents);
        getSupportFragmentManager().beginTransaction().replace(R.id.new_events_container, favoritesList).commit();
        if (update.newEvents.size() == 0) {
            RelativeLayout newEventsLayout = (RelativeLayout) findViewById(R.id.newEventsLayout);
            newEventsLayout.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) newEventsLayout.getLayoutParams();
            params.height = 0;
            params.topMargin = 0;
            newEventsLayout.setLayoutParams(params);
        }

        EventListFragment fragment2 = EventListFragment.newInstance("", update.updatedEvents);
        getSupportFragmentManager().beginTransaction().replace(R.id.updated_events_container, fragment2).commit();
        if (update.updatedEvents.size() == 0) {
            RelativeLayout updatedEventsLayout = (RelativeLayout) findViewById(R.id.updateEventsLayout);
            updatedEventsLayout.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) updatedEventsLayout.getLayoutParams();
            params.height = 0;
            params.topMargin = 0;
            updatedEventsLayout.setLayoutParams(params);
        }

        PetListFragment fragment3 = PetListFragment.newInstance("", update.newPets);
        getSupportFragmentManager().beginTransaction().replace(R.id.new_pets_continer, fragment3).commit();
        if (update.newPets.size() == 0) {
            RelativeLayout updatedEventsLayout = (RelativeLayout) findViewById(R.id.newPetsLayout);
            updatedEventsLayout.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) updatedEventsLayout.getLayoutParams();
            params.height = 0;
            params.topMargin = 0;
            updatedEventsLayout.setLayoutParams(params);
        }

        PetListFragment fragment4 = PetListFragment.newInstance("", update.updatedPets);
        getSupportFragmentManager().beginTransaction().replace(R.id.updated_pets_continer, fragment4).commit();
        if (update.updatedPets.size() == 0) {
            RelativeLayout updatedEventsLayout = (RelativeLayout) findViewById(R.id.updatedPetsLayout);
            updatedEventsLayout.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) updatedEventsLayout.getLayoutParams();
            params.height = 0;
            params.topMargin = 0;
            updatedEventsLayout.setLayoutParams(params);
        }
    }
}
