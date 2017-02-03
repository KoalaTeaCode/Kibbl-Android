package com.android.thehollidayinn.kibbl.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.thehollidayinn.kibbl.MainActivity;
import com.android.thehollidayinn.kibbl.R;
import com.android.thehollidayinn.kibbl.data.models.Filters;

public class FiltersActivity extends AppCompatActivity {

    private Filters filters;

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            // your code here
            TextView selectedTextView = (TextView) selectedItemView;
            Spinner spinnerView = (Spinner) parentView;

            Integer spinnerID = spinnerView.getId();
            String spinnerText = selectedTextView.getText().toString();
            if (spinnerID == R.id.breedSpinner) {
                filters.breed = spinnerText;
            } else if (spinnerID == R.id.ageSpinner) {
                filters.age = spinnerText;
            } else if (spinnerID == R.id.genderSpinner) {
                filters.gender = spinnerText;
            }

            Button findButton = (Button) findViewById(R.id.findButton);
            findButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateHome();
                }
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
        }

    };

    private void navigateHome () {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filters = Filters.getSharedInstance();

        setUpView();
    }

    private void setUpView() {
        Spinner spinner = (Spinner) findViewById(R.id.breedSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.breeds, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(onItemSelectedListener);

        Spinner ageSpinner = (Spinner) findViewById(R.id.ageSpinner);
        ArrayAdapter<CharSequence> ageAdpater = ArrayAdapter.createFromResource(this,
                R.array.age, android.R.layout.simple_spinner_item);
        ageAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdpater);
        ageSpinner.setOnItemSelectedListener(onItemSelectedListener);

        Spinner genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(onItemSelectedListener);
    }

}
