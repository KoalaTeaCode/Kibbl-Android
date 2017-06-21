package com.thehollidayinn.kibbl.ui.activities;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.thehollidayinn.kibbl.MainActivity;
import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.Filters;
import com.thehollidayinn.kibbl.data.models.UserLogin;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FiltersActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Filters filters;
    private UserLogin userLogin;
    private DatePickerDialog datePickerDialog;
    private AutoCompleteTextView autocompleteTextView;
    private Spinner ageSpinner;
    private Spinner typeSpinner;
    private Spinner genderSpinner;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;

    ArrayAdapter<String> breedsAdapter;
    ArrayAdapter<String> dogBreedsAdapter;
    ArrayAdapter<String> catBreedsAdapter;


    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            // your code here
            TextView selectedTextView = (TextView) selectedItemView;
            Spinner spinnerView = (Spinner) parentView;

            Integer spinnerID = spinnerView.getId();
            String spinnerText = selectedTextView.getText().toString();
            if (spinnerID == R.id.ageSpinner) {
                filters.age = spinnerText;
                filters.ageIndex = position;
            } else if (spinnerID == R.id.genderSpinner) {
                filters.gender = spinnerText;
                filters.genderIndex = position;
            } else if (spinnerID == R.id.typeSpinner) {
                filters.type = spinnerText;
                filters.typeIndex = position;
                
                if (filters.type.equals("Dog")) {
                    autocompleteTextView.setAdapter(dogBreedsAdapter);
                } else if (filters.type.equals("Cat")) {
                    autocompleteTextView.setAdapter(catBreedsAdapter);
                } else {
                    autocompleteTextView.setAdapter(breedsAdapter);
                }
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

    public String[] concat(String[] a, String[] b) {
        int aLen = a.length;
        int bLen = b.length;
        String[] c = new String[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        filters = Filters.getSharedInstance();
        filters.locale = getCurrentLocale();
        userLogin = UserLogin.getInstance(this);

        // Check which page we are filtering
        int activePage = 0;
        Bundle b = getIntent().getExtras();
        if(b != null) {
            activePage = b.getInt("activePage");
        }

        String[] dogBreeds = getResources().getStringArray(R.array.breeds_dog);
        String[] catBreeds = getResources().getStringArray(R.array.breeds_cats);
        String[] allBreeds = concat(dogBreeds, catBreeds);
        breedsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allBreeds);
        catBreedsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, catBreeds);
        dogBreedsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dogBreeds);

        setUpView();
        hideFilters(activePage);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePickerDialog = new DatePickerDialog(
                this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }

    private void navigateHome () {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    };

    private void setZipCode(Place place) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    place.getLatLng().latitude,
                    place.getLatLng().longitude,
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
//            errorMessage = getString(R.string.service_not_available);
//            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
//            errorMessage = getString(R.string.invalid_lat_long_used);
//            Log.e(TAG, errorMessage + ". " +
//                    "Latitude = " + location.getLatitude() +
//                    ", Longitude = " +
//                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
//            if (errorMessage.isEmpty()) {
//                errorMessage = getString(R.string.no_address_found);
//                Log.e(TAG, errorMessage);
//            }
//            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }

//            userLogin.setZipCode(address.getPostalCode());
//            filters.location = address.getPostalCode();
            userLogin.setZipCode(place.getName().toString());
            filters.location = place.getName().toString();
        }
    }

    private void setUpView() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setText(userLogin.getPlace());

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                userLogin.setPlace(place.getName().toString());
                setZipCode(place);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("test", "An error occurred: " + status);
            }
        });

        autocompleteTextView = (AutoCompleteTextView) findViewById(R.id.breedAutoComplete);
        autocompleteTextView.setAdapter(breedsAdapter);
        autocompleteTextView.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {

            @Override
            public void onDismiss() {
                filters.breed = autocompleteTextView.getText().toString();
            }
        });

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(onItemSelectedListener);
        typeSpinner.setSelection(filters.typeIndex);

        ageSpinner = (Spinner) findViewById(R.id.ageSpinner);
        ArrayAdapter<CharSequence> ageAdpater = ArrayAdapter.createFromResource(this,
                R.array.age, android.R.layout.simple_spinner_item);
        ageAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdpater);
        ageSpinner.setOnItemSelectedListener(onItemSelectedListener);
        ageSpinner.setSelection(filters.ageIndex);

        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(onItemSelectedListener);
        genderSpinner.setSelection(filters.genderIndex);

        startDatePicker = (DatePicker) findViewById(R.id.startDatePicker);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (filters.startDate != null) {
            calendar = filters.startDate;
        }
        startDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                filters.startDate = calendar;
            }
        });

        endDatePicker = (DatePicker) findViewById(R.id.endDatePicker);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());
        if (filters.endDate != null) {
            calendar2 = filters.endDate;
        }
        endDatePicker.init(calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                filters.endDate = calendar;
            }
        });
    }

    private void hideFilters(int activePage) {
        if (activePage == 0) {
            hidePetFilters();
        } else if (activePage == 1) {
            hideEventFilters();
        } else if (activePage == 2) {
            hidePetFilters();
            hideEventFilters();
        }
    }

    public void hidePetFilters() {
        TextView breedView = (TextView) findViewById(R.id.breedText);
        TextView ageTextView = (TextView) findViewById(R.id.ageTextView);
        TextView genderTextView = (TextView) findViewById(R.id.genderTextView);
        TextView typeTextView = (TextView) findViewById(R.id.typeTextView);

        breedView.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) breedView.getLayoutParams();
        params.height = 0;
        params.topMargin = 0;
        breedView.setLayoutParams(params);

        autocompleteTextView.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) autocompleteTextView.getLayoutParams();
        params1.height = 0;
        params1.topMargin = 0;
        autocompleteTextView.setLayoutParams(params1);

        typeTextView.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams paramsType = (RelativeLayout.LayoutParams) typeTextView.getLayoutParams();
        paramsType.height = 0;
        paramsType.topMargin = 0;
        typeTextView.setLayoutParams(paramsType);

        typeSpinner.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams paramsTypeSpinner = (RelativeLayout.LayoutParams) typeSpinner.getLayoutParams();
        paramsTypeSpinner.height = 0;
        paramsTypeSpinner.topMargin = 0;
        typeSpinner.setLayoutParams(paramsTypeSpinner);

        ageTextView.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) ageTextView.getLayoutParams();
        params2.height = 0;
        params2.topMargin = 0;
        ageTextView.setLayoutParams(params2);

        ageSpinner.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) ageSpinner.getLayoutParams();
        params3.height = 0;
        params3.topMargin = 0;
        ageSpinner.setLayoutParams(params3);

        genderTextView.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) genderTextView.getLayoutParams();
        params4.height = 0;
        params4.topMargin = 0;
        genderTextView.setLayoutParams(params4);

        genderSpinner.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params5 = (RelativeLayout.LayoutParams) genderSpinner.getLayoutParams();
        params5.height = 0;
        params5.topMargin = 0;
        genderSpinner.setLayoutParams(params5);
    }

    public void hideEventFilters() {
        TextView startDateTextView = (TextView) findViewById(R.id.startDateTextView);
        TextView endDateTextView = (TextView) findViewById(R.id.endDateTextView);

        startDateTextView.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) startDateTextView.getLayoutParams();
        params3.height = 0;
        params3.topMargin = 0;
        startDateTextView.setLayoutParams(params3);

        startDatePicker.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) startDatePicker.getLayoutParams();
        params.height = 0;
        params.topMargin = 0;
        startDatePicker.setLayoutParams(params);

        endDateTextView.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) endDateTextView.getLayoutParams();
        params4.height = 0;
        params4.topMargin = 0;
        endDateTextView.setLayoutParams(params4);

        endDatePicker.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) endDatePicker.getLayoutParams();
        params2.height = 0;
        params2.topMargin = 0;
        endDatePicker.setLayoutParams(params2);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
