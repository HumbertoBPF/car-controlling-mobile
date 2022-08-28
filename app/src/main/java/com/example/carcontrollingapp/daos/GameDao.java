package com.example.carcontrollingapp.daos;

import androidx.room.Dao;

import com.example.carcontrollingapp.models.Game;

@Dao
public abstract class GameDao extends BaseDao<Game>{
    public GameDao() {
        super("Game");
    }
}
