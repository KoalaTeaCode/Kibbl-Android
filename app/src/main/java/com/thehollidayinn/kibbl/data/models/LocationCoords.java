package com.thehollidayinn.kibbl.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by krh12 on 4/29/2017.
 */

public class LocationCoords {

    @SerializedName("coordinates")
    @Expose
    private List<Float> coordinates = null;
    @SerializedName("type")
    @Expose
    private String type;

    public List<Float> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Float> coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
