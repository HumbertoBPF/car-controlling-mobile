package com.example.carcontrollingapp.retrofit.synchronization;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.carcontrollingapp.models.Score;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelper;
import com.example.carcontrollingapp.room.AppDatabase;

import java.util.List;

import retrofit2.Call;

public class ScoresSynchronization extends ResourcesSynchronization<Score> {
    public ScoresSynchronization(Context context, ProgressDialog dialog) {
        super(context, "Score", new ResourcesCallable<List<Score>>() {
            @Override
            public Call<List<Score>> getCallable() {
                return CarControllerAPIHelper.getApiObject().getScores();
            }
        }, AppDatabase.getInstance(context).getScoreDao(), dialog, null);
    }
}
