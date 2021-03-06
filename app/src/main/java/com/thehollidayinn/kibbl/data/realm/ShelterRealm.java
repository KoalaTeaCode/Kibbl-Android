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
    private String createdAt;
    private String city;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    private String state;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
