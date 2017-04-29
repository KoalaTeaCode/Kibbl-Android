package com.thehollidayinn.kibbl.data.remote;

import com.thehollidayinn.kibbl.data.models.Event;
import com.thehollidayinn.kibbl.data.models.Favorite;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.LatestResponse;
import com.thehollidayinn.kibbl.data.models.Pet;
import com.thehollidayinn.kibbl.data.models.PetResponse;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.models.UserResponse;

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

    @POST("auth/social")
    Observable<UserResponse> registerSocial(@Body Map<String, String> userLogin);

    @GET("pets")
    Observable<PetResponse> getPets(@QueryMap Map<String, String> options);

    @GET("pets/{petId}")
    Observable<GenericResponse<Pet>> getPetDetail(@Path("petId") String petId);

    @POST("pets/{petId}/favorite")
    Observable<GenericResponse<Pet>> favoritePet(@Path("petId") String petId);

    @GET("favorites")
    Observable<GenericResponse<List<Favorite>>> getFavorites();

    @GET("latest")
    Observable<LatestResponse> getLatest();

    @GET("events")
    Observable<GenericResponse<List<Event>>> getEvents(@QueryMap Map<String, String> options);

    @GET("events/{eventId}")
    Observable<GenericResponse<Event>> getEventDetail(@Path("eventId") String eventId);

    @GET("shelters")
    Observable<GenericResponse<List<Shelter>>> getShelters(@QueryMap Map<String, String> options);

    @GET("shelters/{shelterId}")
    Observable<GenericResponse<Shelter>> getShelterDetail(@Path("shelterId") String shelterId);
}
