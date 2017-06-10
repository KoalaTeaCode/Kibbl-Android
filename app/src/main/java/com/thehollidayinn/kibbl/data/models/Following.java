package com.thehollidayinn.kibbl.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by krh12 on 4/30/2017.
 */

public class Following {
    @SerializedName("_id")
    @Expose
    public String id;

    @SerializedName("userID")
    @Expose
    public String userID;

    @SerializedName("shelterId")
    @Expose
    public Shelter shelterId;

    @SerializedName("createdAt")
    @Expose
    public Date createdAt;

    @SerializedName("active")
    @Expose
    public Boolean active;
}
