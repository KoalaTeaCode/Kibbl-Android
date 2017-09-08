package com.thehollidayinn.kibbl.data.realm;

import com.thehollidayinn.kibbl.data.models.Facebook;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by krh12 on 8/21/2017.
 */

public class EventRealm extends RealmObject {
    @PrimaryKey
    private String _id;
    private String name;
    private FacebookRealm facebook;
    private Date startTime;
    private String city;
    private String state;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    private Date endTime;

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
