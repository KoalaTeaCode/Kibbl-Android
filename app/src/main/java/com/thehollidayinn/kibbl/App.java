package com.thehollidayinn.kibbl;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by krh12 on 8/21/2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded() // @TODO: Change this
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
