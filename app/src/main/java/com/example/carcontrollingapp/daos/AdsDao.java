package com.example.carcontrollingapp.daos;

import androidx.room.Dao;

import com.example.carcontrollingapp.models.Ad;

@Dao
public abstract class AdsDao extends BaseDao<Ad>{
    public AdsDao() {
        super("Ad");
    }
}
