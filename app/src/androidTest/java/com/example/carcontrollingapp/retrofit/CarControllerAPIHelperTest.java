package com.example.carcontrollingapp.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarControllerAPIHelperTest extends CarControllerAPIHelper{
    @Override
    public CarControllerAPI getApiObject() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(CarControllerAPITest.class);
    }
}
