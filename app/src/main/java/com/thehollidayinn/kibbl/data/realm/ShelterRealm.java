package com.thehollidayinn.kibbl.data.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by krh12 on 8/29/2017.
 */

public class ShelterRealm extends RealmObject {
    @PrimaryKey
    private String _id;
    private String name;
    private FacebookRealm facebook;

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FacebookRealm getFacebook() {
        return facebook;
    }

    public void setFacebook(FacebookRealm facebook) {
        this.facebook = facebook;
    }
}
