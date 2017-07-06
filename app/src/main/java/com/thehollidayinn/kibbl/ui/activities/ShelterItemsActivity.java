package com.thehollidayinn.kibbl.ui.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.ui.fragments.EventListFragment;
import com.thehollidayinn.kibbl.ui.fragments.PetListFragment;

public class ShelterItemsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        String contentType = extras.getString("CONTENT_TYPE");
        String shelterId = extras.getString("SHELTER_ID");

        getSupportActionBar().setTitle("Shelter " + contentType);

        if (contentType.equals("Events")) {
            EventListFragment favoritesList = EventListFragment.newInstance(shelterId, null);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, favoritesList).commit();
        } else if (contentType.equals("Pets")) {
            PetListFragment favoritesList = PetListFragment.newInstance(shelterId, null);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, favoritesList).commit();
        }

    }

}
