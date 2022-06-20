package com.example.carcontrollingapp.retrofit;

import com.example.carcontrollingapp.models.Game;
import com.example.carcontrollingapp.models.Score;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CarControllerAPI {
    @GET("games")
    Call<List<Game>> getGames();

    @GET("scores")
    Call<List<Score>> getScores();
}
