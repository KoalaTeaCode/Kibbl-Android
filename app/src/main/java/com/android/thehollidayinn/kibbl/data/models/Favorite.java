package com.android.thehollidayinn.kibbl.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by krh12 on 1/30/2017.
 */

public class Favorite {
    @SerializedName("_id")
    @Expose
    public String id;

    @SerializedName("petID")
    @Expose
    public Pet pet;
}
