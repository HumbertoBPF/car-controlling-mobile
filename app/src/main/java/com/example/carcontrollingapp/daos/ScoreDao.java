package com.example.carcontrollingapp.daos;

import androidx.room.Dao;

import com.example.carcontrollingapp.models.Score;

@Dao
public abstract class ScoreDao extends BaseDao<Score> {
    public ScoreDao() {
        super("Score");
    }
}
