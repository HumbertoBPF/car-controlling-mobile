package com.example.carcontrollingapp.retrofit;

import com.example.carcontrollingapp.models.Ad;
import com.example.carcontrollingapp.models.Game;
import com.example.carcontrollingapp.models.Score;
import com.example.carcontrollingapp.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface CarControllerAPI {
    @GET("games")
    Call<List<Game>> getGames();

    @GET("scores")
    Call<List<Score>> getScores();

    @GET("ads")
    Call<List<Ad>> getAds();

    @GET("users")
    Call<User> login(@Header("Authorization") String authToken);

    @POST("users")
    Call<User> signup(@Body User user);

    @PUT("users")
    Call<User> updateUser(@Header("Authorization") String authToken,@Body User user);

    @DELETE("users")
    Call<Void> deleteUser(@Header("Authorization") String authToken);
}
