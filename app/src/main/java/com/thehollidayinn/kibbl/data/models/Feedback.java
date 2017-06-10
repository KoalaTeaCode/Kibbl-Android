package com.thehollidayinn.kibbl.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by krh12 on 5/1/2017.
 */

public class Feedback {
    @SerializedName("_id")
    @Expose
    public String id;

    @SerializedName("userID")
    @Expose
    public String userID;

    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("createdAt")
    @Expose
    public Date createdAt;
}
