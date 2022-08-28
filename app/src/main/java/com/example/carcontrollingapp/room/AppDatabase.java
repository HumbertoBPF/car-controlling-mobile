package com.example.carcontrollingapp.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.carcontrollingapp.BuildConfig;
import com.example.carcontrollingapp.daos.AdsDao;
import com.example.carcontrollingapp.daos.GameDao;
import com.example.carcontrollingapp.daos.ScoreDao;
import com.example.carcontrollingapp.models.Ad;
import com.example.carcontrollingapp.models.Game;
import com.example.carcontrollingapp.models.Score;

@Database(entities = {Game.class, Score.class, Ad.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String NAME_DB = BuildConfig.NAME_DB;

    public abstract GameDao getGameDao();
    public abstract ScoreDao getScoreDao();
    public abstract AdsDao getAdsDao();

    public static AppDatabase getInstance(Context context){
        return Room.databaseBuilder(context, AppDatabase.class, NAME_DB).build();
    }
}
