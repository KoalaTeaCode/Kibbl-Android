package com.thehollidayinn.kibbl.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by krh12 on 5/24/2017.
 */

public class PetMedia {
    @SerializedName("urlSecureThumbnail")
    @Expose
    public String urlSecureThumbnail;

    @SerializedName("urlSecureFullsize")
    @Expose
    public String urlSecureFullsize;

}
