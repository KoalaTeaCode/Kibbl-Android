package com.android.thehollidayinn.kibbl.data.remote;

import com.android.thehollidayinn.kibbl.data.models.Favorite;
import com.android.thehollidayinn.kibbl.data.models.GenericResponse;
import com.android.thehollidayinn.kibbl.data.models.Pet;
import com.android.thehollidayinn.kibbl.data.models.PetResponse;
import com.android.thehollidayinn.kibbl.data.models.UserResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by krh12 on 1/9/2017.
 */

public interface KibblAPIInterface {
    @POST("login")
    Observable<UserResponse> login(@Body Map<String, String> userLogin);

    @POST("register")
    Observable<UserResponse> register(@Body Map<String, String> userLogin);

    @GET("pets")
    Observable<PetResponse> getPets(@QueryMap Map<String, String> options);

    @GET("pets/{petId}")
    Observable<GenericResponse<Pet>> getPetDetail(@Path("petId") String petId);

    @POST("pets/{petId}/favorite")
    Observable<GenericResponse<Pet>> favoritePet(@Path("petId") String petId);

    @GET("favorites")
    Observable<GenericResponse<List<Favorite>>> getFavorites();
}
