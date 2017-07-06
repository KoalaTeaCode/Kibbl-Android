package com.thehollidayinn.kibbl.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by krh12 on 4/27/2017.
 */


public class Event extends CommonModel {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("end_time")
    @Expose
    private Date endTime;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("start_time")
    @Expose
    private Date startTime;
    @SerializedName("id")
    @Expose
    private String facebookid;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("place")
    @Expose
    private Place place;
    @SerializedName("locationCoords")
    @Expose
    private LocationCoords locationCoords;
    @SerializedName("facebook")
    @Expose
    private Facebook facebook;

    public Shelter shelterId;

    public Facebook getFacebook() {
        return facebook;
    }

    public void setFacebook(Facebook facebook) {
        this.facebook = facebook;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getFacebookid() {
        return facebookid;
    }

    public void setFacebookid(String facebookid) {
        this.facebookid = facebookid;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public LocationCoords getLocationCoords() {
        return locationCoords;
    }

    public void setLocationCoords(LocationCoords locationCoords) {
        this.locationCoords = locationCoords;
    }

}

