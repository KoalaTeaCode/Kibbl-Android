package com.thehollidayinn.kibbl.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by krh12 on 4/27/2017.
 */

public class LatestResponse {
    @SerializedName("events")
    @Expose
    public List<Event> events;

    @SerializedName("shelters")
    @Expose
    public List<Shelter> shelters;

    @SerializedName("pets")
    @Expose
    public List<Pet> pets;
}
