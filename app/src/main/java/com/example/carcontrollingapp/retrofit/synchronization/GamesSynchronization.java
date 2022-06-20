package com.example.carcontrollingapp.retrofit.synchronization;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.carcontrollingapp.models.Game;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelper;
import com.example.carcontrollingapp.room.AppDatabase;

import java.util.List;

import retrofit2.Call;

public class GamesSynchronization extends ResourcesSynchronization<Game>{
    public GamesSynchronization(Context context, ProgressDialog dialog) {
        super(context, "Game", new ResourcesCallable<List<Game>>() {
            @Override
            public Call<List<Game>> getCallable() {
                return CarControllerAPIHelper.getApiObject().getGames();
            }
        }, AppDatabase.getInstance(context).getGameDao(), dialog, new ScoresSynchronization(context, dialog));
    }
}
