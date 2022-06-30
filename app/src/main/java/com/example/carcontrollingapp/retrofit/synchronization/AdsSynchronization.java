package com.example.carcontrollingapp.retrofit.synchronization;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.carcontrollingapp.models.Ad;
import com.example.carcontrollingapp.retrofit.CarControllerAPIHelper;
import com.example.carcontrollingapp.room.AppDatabase;

import java.util.List;

import retrofit2.Call;

public class AdsSynchronization extends ResourcesSynchronization<Ad>{
    public AdsSynchronization(Context context, ProgressDialog dialog) {
        super(context, "Ad", new ResourcesCallable<List<Ad>>() {
            @Override
            public Call<List<Ad>> getCallable() {
                return CarControllerAPIHelper.getApiObject().getAds();
            }
        }, AppDatabase.getInstance(context).getAdsDao(), dialog, null);
    }
}
