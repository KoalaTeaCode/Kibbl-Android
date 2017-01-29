package com.android.thehollidayinn.kibbl.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by krh12 on 1/27/2017.
 */

public class GenericResponse<T> {

    @SerializedName("data")
    @Expose
    public T data = null;

}
