package com.thehollidayinn.kibbl;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.thehollidayinn.kibbl.data.models.Favorite;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Notification;
import com.thehollidayinn.kibbl.data.models.PetResponse;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.models.UserLogin;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;
import com.thehollidayinn.kibbl.ui.activities.FavoritesActivity;
import com.thehollidayinn.kibbl.ui.activities.FeedbackActivity;
import com.thehollidayinn.kibbl.ui.activities.FiltersActivity;
import com.thehollidayinn.kibbl.ui.activities.LoginRegisterActivity;
import com.thehollidayinn.kibbl.ui.activities.NotificationsActivity;
import com.thehollidayinn.kibbl.ui.adapters.MainTabsAdapter;
import com.thehollidayinn.kibbl.ui.fragments.EventListFragment;
import com.thehollidayinn.kibbl.ui.fragments.ListContentFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.thehollidayinn.kibbl.ui.fragments.ShelterListFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout mDrawerLayout;
    private Context context;
    private UserLogin user;
    private NavigationView navigationView;
    private GoogleApiClient mGoogleApiClient;
    private int activePage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        user = UserLogin.getInstance(this);

        setUpNavBar();
//        setUpTabs();
        setUpBottomBar();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        findViewById(R.id.filter_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filterIntent = new Intent(MainActivity.this, FiltersActivity.class);
                startActivity(filterIntent);
            }
        });

        if (savedInstanceState != null) {
            Log.v("keithtest", String.valueOf(savedInstanceState.getInt("active-page")));
            activePage = savedInstanceState.getInt("active-page");
            loadPage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent filterIntent = new Intent(this, NotificationsActivity.class);
            startActivity(filterIntent);
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void setUpBottomBar() {
        // Initial fragment
        EventListFragment fragment = EventListFragment.newInstance("");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_events:
                                activePage = 0;
                                break;
                            case R.id.action_pets:
                                activePage = 1;
                                break;
                            case R.id.action_shelters:
                                activePage = 2;
                                break;
                        }
                        loadPage();
                        return true;
                    }
                });
    }

    private void loadPage() {
        if (this.activePage == 0) {
            EventListFragment fragment = EventListFragment.newInstance("");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else if (this.activePage == 1) {
            ListContentFragment listContentFragment = ListContentFragment.newInstance("");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, listContentFragment)
                    .commit();
        } else if (this.activePage == 2) {
            ShelterListFragment shelterListFragment = ShelterListFragment.newInstance("");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, shelterListFragment)
                    .commit();
        }
    }

    private void setUpNavBar() {
        // Create Navigation drawer and inlfate layout
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
//        supportActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
//        supportActionBar.setElevation(0);

        if (supportActionBar != null) {
            VectorDrawableCompat indicator =
                    VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.button_grey, getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (user.isLoggedIn()) {
            navigationView.getMenu().findItem(R.id.login_register).setVisible(false);
            navigationView.getMenu().findItem(R.id.logout).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.login_register).setVisible(true);
            navigationView.getMenu().findItem(R.id.logout).setVisible(false);
        }

        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);

                        // TODO: handle navigation
                        if (menuItem.getTitle().equals("Login/Register")) {
                            Intent loginRegisterIntent = new Intent(context, LoginRegisterActivity.class);
                            startActivity(loginRegisterIntent);
                        } else if (menuItem.getItemId() == R.id.favorites) {
                            Intent filterIntent = new Intent(context, FavoritesActivity.class);
                            startActivity(filterIntent);
                        } else if (menuItem.getItemId() == R.id.feedback) {
                            Intent filterIntent = new Intent(context, FeedbackActivity.class);
                            startActivity(filterIntent);
                        } else if (menuItem.getItemId() == R.id.logout) {
                            user.logOut();
                            navigationView.getMenu().findItem(R.id.login_register).setVisible(true);
                            navigationView.getMenu().findItem(R.id.logout).setVisible(false);
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void setUpTabs() {
//        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
//        setupViewPager(viewPager);

//        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
//        tabs.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        MainTabsAdapter adapter = new MainTabsAdapter(getSupportFragmentManager());

//        adapter.addFragment(ListContentFragment.newInstance("All"), "Recommended");
//        adapter.addFragment(ListContentFragment.newInstance("Dog"), "Dogs");
//        adapter.addFragment(ListContentFragment.newInstance("Cat"), "Cats");
        adapter.addFragment(EventListFragment.newInstance("Events"), "Events");
        adapter.addFragment(EventListFragment.newInstance("Pets"), "Pets");
        adapter.addFragment(ShelterListFragment.newInstance("Shelters"), "Shelters");
        viewPager.setAdapter(adapter);
    }

    private boolean checkForLocationPermission()
    {

        String permission = "android.permission.ACCESS_COARSE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!checkForLocationPermission()) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            setLocation(mLastLocation);
        }
    }

    public void setLocation (Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
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

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
//            Log.i(TAG, getString(R.string.address_found));
//            deliverResultToReceiver(Constants.SUCCESS_RESULT,
//                    TextUtils.join(System.getProperty("line.separator"),
//
//            System.getProperty("line.separator")
//            String locationString = TextUtils.join(", ");
            Log.v("test", String.valueOf(addressFragments));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
