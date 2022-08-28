package com.example.carcontrollingapp.retrofit;

import com.example.carcontrollingapp.models.Score;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CarControllerAPITest extends CarControllerAPI{

    @POST("scores")
    Call<Score> postScores(@Header("Authorization") String authToken, @Body Score score);

}
