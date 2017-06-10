package com.thehollidayinn.kibbl.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by krh12 on 6/10/2017.
 */

public class CommentResponse {
    @SerializedName("comments")
    @Expose
    public List<Comment> comments;
}
