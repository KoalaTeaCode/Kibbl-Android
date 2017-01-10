package com.android.thehollidayinn.kibbl.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

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
        Log.v("test", "Sdf");
//        if (savedInstanceState == null) {
            showLoginFragment();
//        }
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
