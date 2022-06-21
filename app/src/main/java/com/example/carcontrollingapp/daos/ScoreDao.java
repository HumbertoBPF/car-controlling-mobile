package com.example.carcontrollingapp.daos;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.carcontrollingapp.interfaces.OnResultListener;
import com.example.carcontrollingapp.models.Score;

import java.util.List;

@Dao
public abstract class ScoreDao extends BaseDao<Score> {
    public ScoreDao() {
        super("Score");
    }

    @Query("SELECT id, min(score) AS score, date, user, gameId FROM score WHERE gameId = :gameId GROUP BY user")
    protected abstract List<Score> getScoreByGame(Long gameId);

    public AsyncTask<Void, Void, List<Score>> getScoreByGameTask(Long gameId,
                                                                 OnResultListener<List<Score>> onResultListener){
        return new AsyncTask<Void, Void, List<Score>>() {
            @Override
            protected List<Score> doInBackground(Void... voids) {
                return getScoreByGame(gameId);
            }

            @Override
            protected void onPostExecute(List<Score> scores) {
                super.onPostExecute(scores);
                onResultListener.onResult(scores);
            }
        };
    }

}
