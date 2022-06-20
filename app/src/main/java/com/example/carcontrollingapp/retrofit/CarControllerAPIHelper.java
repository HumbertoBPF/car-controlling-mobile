package com.example.carcontrollingapp.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarControllerAPIHelper {
    private static final String BASE_URL = "http://192.168.43.106:8000/api/";

    public static CarControllerAPI getApiObject() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(CarControllerAPI.class);
    }
}
