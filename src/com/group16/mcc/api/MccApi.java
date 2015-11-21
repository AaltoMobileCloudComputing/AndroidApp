package com.group16.mcc.api;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MccApi {

    // NOTE: Use relative URLs (no / in the beginning)

    @GET("user")
    Call<User> loginUser(@Query("username") String username, @Query("password") String password);

    @GET("user/{token}")
    Call<User> getUser(@Path("token") String token);
}
