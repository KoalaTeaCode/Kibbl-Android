package com.android.thehollidayinn.kibbl.data.remote;

import com.android.thehollidayinn.kibbl.data.models.Pet;
import com.android.thehollidayinn.kibbl.data.models.PetResponse;
import com.android.thehollidayinn.kibbl.data.models.UserResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by krh12 on 1/9/2017.
 */

public interface KibblAPIInterface {
    @GET("pets")
    Observable<List<Pet>> getPets();

    @POST("login")
    Observable<UserResponse> login(@Body Map<String, String> userLogin);

    @POST("register")
    Observable<UserResponse> register(@Body Map<String, String> userLogin);
}
