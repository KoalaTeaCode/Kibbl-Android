package com.android.thehollidayinn.kibbl.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.thehollidayinn.kibbl.R;
import com.android.thehollidayinn.kibbl.ui.fragments.LoginFragment;
import com.android.thehollidayinn.kibbl.ui.fragments.RegisterFragment;

public class LoginRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        if (savedInstanceState == null) {
            showLoginFragment();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();

        String registerTitle = getResources().getString(R.string.action_register);
        String loginTitle = getResources().getString(R.string.action_login);

        if (title.equals(registerTitle)) {
            showRegisterFragment();
            item.setTitle(loginTitle);
            return true;
        } else if (title.equals(loginTitle)) {
            showLoginFragment();
            item.setTitle(registerTitle);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLoginFragment() {
        LoginFragment fragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
    }

    private void showRegisterFragment() {
        RegisterFragment fragment = new RegisterFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
    }
}
