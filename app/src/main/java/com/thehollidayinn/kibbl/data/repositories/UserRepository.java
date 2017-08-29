package com.thehollidayinn.kibbl.data.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by krh12 on 6/11/2017.
 */

public class UserRepository {
    private static UserRepository instance = null;

    private String TOKEN_KEY = "token-key";
    private String SUBSCRIBED_KEY = "subscribe-key";
    private Context context;
    private boolean isSubscribedToNotifications = false;
    private String token = "";
    private SharedPreferences preferences;
    private Date lastEventUpdate;
    private Date lastPetUpdate;

    protected UserRepository(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.token = preferences.getString(TOKEN_KEY, "");

        String lastEventUpdateString = preferences.getString("LastEventUpdate", "");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            this.lastEventUpdate = format.parse(lastEventUpdateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String lastPetEventString = preferences.getString("LastPetUpdate", "");
        try {
            this.lastPetUpdate = format.parse(lastPetEventString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.isSubscribedToNotifications = preferences.getBoolean(SUBSCRIBED_KEY, false);
    }

    public static UserRepository getInstance(Context context) {
        if(instance == null) {
            instance = new UserRepository(context);
        }
        return instance;
    }

    public void setToken (String token) {
        this.token = token;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public String getToken () {
        return this.token;
    }

    public void setSubscribed (Boolean isSubscribedToNotifications) {
        this.isSubscribedToNotifications = isSubscribedToNotifications;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SUBSCRIBED_KEY, isSubscribedToNotifications);
        editor.apply();
    }

    public Boolean getSubscribed () {
        return this.isSubscribedToNotifications;
    }

    public void setLastEventUpdate (Date date) {
        this.lastEventUpdate = date;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("LastEventUpdate", date.toString());
        editor.apply();
    }

    public Date getLastPetUpdate () {
        return this.lastPetUpdate;
    }

    public void setLastPetUpdate (Date date) {
        this.lastPetUpdate = date;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("LastPetUpdate", date.toString());
        editor.apply();
    }

    public Date getLastEventUpdate () {
        return this.lastPetUpdate;
    }
}
