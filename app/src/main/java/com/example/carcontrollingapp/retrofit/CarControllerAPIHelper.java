package com.example.carcontrollingapp.retrofit;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarControllerAPIHelper {
    private static final String BASE_URL = "http://192.168.15.2:8000/api/";

    public static CarControllerAPI getApiObject() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(CarControllerAPI.class);
    }

    public static String getAuthToken(String username, String password) {
        byte[] data = new byte[0];
        try {
            data = (username + ":" + password).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
    }
}
