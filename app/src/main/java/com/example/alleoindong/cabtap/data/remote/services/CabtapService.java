package com.example.alleoindong.cabtap.data.remote.services;

import com.example.alleoindong.cabtap.data.remote.models.User;
import com.example.alleoindong.cabtap.data.remote.models.Vehicle;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by alleoindong on 3/7/17.
 */

public interface CabtapService {

    @FormUrlEncoded
    @POST("auth")
    Call<User> login(@Field("email") String email, @Field("password") String password);

    @GET("vehicle")
    Call<Vehicle> getAssignedVehicle(@Header("Authorization") String apiToken);

    @POST("register")
    Call<User> register(@Body User user);
}
