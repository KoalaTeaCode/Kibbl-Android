package com.thehollidayinn.kibbl.data.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.thehollidayinn.kibbl.R;
import com.facebook.login.LoginManager;

/**
 * Created by krh12 on 1/23/2017.
 */

public class UserLogin {
    private static UserLogin instance;
    private String token = "";
    private Context context;
    private String place = "";
    private String zipCode = "";
    private Boolean hasMessages = false;

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
        this.hasMessages = sharedPref.getBoolean("user-has-messages", false);
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

    public void logOut() {
        this.setToken("");
        LoginManager.getInstance().logOut();
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlace() {
        return this.place;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public void setHasMessages(Boolean hasMessages) {
        this.hasMessages = hasMessages;

        Activity activity = (Activity) this.context;
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.app_prefrences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("user-has-messages", this.hasMessages);
        editor.apply();
    }

    public Boolean getHasMessages () {
        SharedPreferences sharedPref = this.context.getSharedPreferences(this.context.getString(R.string.app_prefrences), Context.MODE_PRIVATE);
        this.hasMessages = sharedPref.getBoolean("user-has-messages", false);
        return this.hasMessages;
    }
}
