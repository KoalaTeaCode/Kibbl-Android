package com.android.thehollidayinn.kibbl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.thehollidayinn.kibbl.data.models.Favorite;
import com.android.thehollidayinn.kibbl.data.models.GenericResponse;
import com.android.thehollidayinn.kibbl.data.models.PetResponse;
import com.android.thehollidayinn.kibbl.data.models.UserLogin;
import com.android.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.android.thehollidayinn.kibbl.data.remote.KibblAPIInterface;
import com.android.thehollidayinn.kibbl.ui.activities.FavoritesActivity;
import com.android.thehollidayinn.kibbl.ui.activities.FiltersActivity;
import com.android.thehollidayinn.kibbl.ui.activities.LoginRegisterActivity;
import com.android.thehollidayinn.kibbl.ui.adapters.MainTabsAdapter;
import com.android.thehollidayinn.kibbl.ui.fragments.ListContentFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Context context;
    private UserLogin user;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        user = UserLogin.getInstance(this);

        setUpNavBar();
        setUpTabs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent filterIntent = new Intent(this, FiltersActivity.class);
            startActivity(filterIntent);
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpNavBar() {
        // Create Navigation drawer and inlfate layout
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator =
                    VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(),R.color.white,getTheme()));
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
                        } else if (menuItem.getItemId() == R.id.logout) {
                            user.logOut();
                            navigationView.getMenu().findItem(R.id.login_register).setVisible(true);
                            navigationView.getMenu().findItem(R.id.logout).setVisible(false);
                        }

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void setUpTabs() {
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        MainTabsAdapter adapter = new MainTabsAdapter(getSupportFragmentManager());

        adapter.addFragment(ListContentFragment.newInstance("All"), "Recommended");
        adapter.addFragment(ListContentFragment.newInstance("Dog"), "Dogs");
        adapter.addFragment(ListContentFragment.newInstance("Cat"), "Cats");
        adapter.addFragment(ListContentFragment.newInstance("Small & Furry"), "Birds");
        adapter.addFragment(ListContentFragment.newInstance("Rabit"), "Rabit");
        adapter.addFragment(ListContentFragment.newInstance("Horse"), "Horse");
//        adapter.addFragment(new TileContentFragment(), "Tile");
//        adapter.addFragment(new CardContentFragment(), "Card");
        viewPager.setAdapter(adapter);
    }
}
