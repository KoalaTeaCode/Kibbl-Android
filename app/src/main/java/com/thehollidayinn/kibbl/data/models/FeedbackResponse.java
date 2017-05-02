package com.thehollidayinn.kibbl.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by krh12 on 5/1/2017.
 */

public class FeedbackResponse {
    @SerializedName("feedback")
    @Expose
    public List<Feedback> feedback;
}

