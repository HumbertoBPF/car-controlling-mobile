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
    protected abstract List<Score> getRankingByGame(Long gameId);

    @Query("SELECT * FROM score WHERE gameId = :gameId AND user = :username")
    protected abstract List<Score> getScoresByGameAndByUser(Long gameId, String username);

    public AsyncTask<Void, Void, List<Score>> getRankingByGameTask(Long gameId,
                                                                   OnResultListener<List<Score>> onResultListener){
        return new AsyncTask<Void, Void, List<Score>>() {
            @Override
            protected List<Score> doInBackground(Void... voids) {
                return getRankingByGame(gameId);
            }

            @Override
            protected void onPostExecute(List<Score> scores) {
                super.onPostExecute(scores);
                onResultListener.onResult(scores);
            }
        };
    }

    public AsyncTask<Void, Void, List<Score>> getScoresByGameAndByUserTask(Long gameId, String username,
                                                                           OnResultListener<List<Score>> onResultListener){
        return new AsyncTask<Void, Void, List<Score>>() {
            @Override
            protected List<Score> doInBackground(Void... voids) {
                return getScoresByGameAndByUser(gameId, username);
            }

            @Override
            protected void onPostExecute(List<Score> scores) {
                super.onPostExecute(scores);
                onResultListener.onResult(scores);
            }
        };
    }

}
