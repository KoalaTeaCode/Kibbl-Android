package com.android.thehollidayinn.kibbl.data.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.thehollidayinn.kibbl.R;

/**
 * Created by krh12 on 1/23/2017.
 */

public class UserLogin {
    private static UserLogin instance;
    private String token = "";
    private Context context;

    private UserLogin(Context context) {
        this.context = context;
        loadTokenFromPreferences();
    }

    public static UserLogin getInstance(Context context) {
        if (instance == null) {
            instance = new UserLogin(context);
        }
        return instance;
    }

    private void loadTokenFromPreferences() {
        Activity activity = (Activity) this.context;
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.app_prefrences), Context.MODE_PRIVATE);
        this.token = sharedPref.getString(activity.getString(R.string.user_token_preference), "");
    }

    public void setToken(String token) {
        this.token = token;

        Activity activity = (Activity) this.context;
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.app_prefrences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(this.context.getString(R.string.user_token_preference), this.token);
        editor.apply();
    }

    public String getToken() {
        return this.token;
    }

    public Boolean isLoggedIn() {
        return !this.token.isEmpty();
    }
}
