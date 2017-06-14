package com.thehollidayinn.kibbl.data.remote;

import com.thehollidayinn.kibbl.data.models.Comment;
import com.thehollidayinn.kibbl.data.models.CommentResponse;
import com.thehollidayinn.kibbl.data.models.CommonModel;
import com.thehollidayinn.kibbl.data.models.Event;
import com.thehollidayinn.kibbl.data.models.Favorite;
import com.thehollidayinn.kibbl.data.models.Feedback;
import com.thehollidayinn.kibbl.data.models.FeedbackResponse;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.LatestResponse;
import com.thehollidayinn.kibbl.data.models.Following;
import com.thehollidayinn.kibbl.data.models.Pet;
import com.thehollidayinn.kibbl.data.models.PetResponse;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.models.Updates;
import com.thehollidayinn.kibbl.data.models.UserResponse;

import java.util.List;
import java.util.Map;

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

    @POST("favorites")
    Observable<GenericResponse<Pet>> favorite(@Body Map<String, String> options);

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

    @GET("notifications")
    Observable<GenericResponse<List<Following>>> getNotifications();

    @POST("notifications")
    Observable<Following> subscribe(@Body Map<String, String> options);

    @GET("notifications/user-notifications")
    Observable<GenericResponse<List<Updates>>> getUpdates();

    @GET("feedback")
    Observable<FeedbackResponse> getFeedbacks();

    @POST("feedback")
    Observable<Feedback> postFeedback(@Body Map<String, String> feedback);

    @GET("comments")
    Observable<CommentResponse> getComments(@QueryMap Map<String, String> options);

    @POST("comments")
    Observable<Comment> postComment(@Body Map<String, String> options);

    @POST("users/push-notification")
    Observable<GenericResponse<Void>> pushNotification(@Body Map<String, String> options);
}
