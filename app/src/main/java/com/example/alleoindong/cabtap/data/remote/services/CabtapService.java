package com.example.alleoindong.cabtap.data.remote.services;

import com.example.alleoindong.cabtap.data.remote.models.Booking;
import com.example.alleoindong.cabtap.data.remote.models.Driver;
import com.example.alleoindong.cabtap.data.remote.models.User;
import com.example.alleoindong.cabtap.data.remote.models.Vehicle;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    @POST("bookings")
    Call<Booking> createBooking(@Header("Authorization") String apiToken, @Body Booking booking);

    @GET("vehicles/{plate_number}")
    Call<Driver> getVehicleByPlateNumber(@Path("plate_number") String plateNumber);

    @PATCH("bookings/{bookingId}/status/{status}")
    Call<Booking> changeStatus(@Header("Authorization") String apiToken, @Path("bookingId") Integer bookingId, @Path("status") String status);

}
