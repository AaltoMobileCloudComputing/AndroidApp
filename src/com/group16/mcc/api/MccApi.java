package com.group16.mcc.api;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MccApi {

    // NOTE: Use relative URLs (no / in the beginning)

    @GET("user")
    Call<User> loginUser(@Query("username") String username, @Query("password") String password);

    @GET("user/{user_token}")
    Call<User> getUser(@Path("user_token") String userToken, @Query("token") String token);

    @GET("event/{id}")
    Call<Event> getEvent(@Path("id") String id, @Query("token") String token);

    @GET("event")
    Call<List<Event>> getEvents(@Query("token") String token);
}
