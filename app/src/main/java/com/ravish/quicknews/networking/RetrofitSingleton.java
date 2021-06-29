package com.ravish.quicknews.networking;

import com.ravish.quicknews.constants.NewsUrls;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {


    private static RetrofitSingleton mInstance;
    private Retrofit retrofit;

    private RetrofitSingleton(){
        retrofit = new Retrofit.Builder()
                .baseUrl(NewsUrls.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RetrofitSingleton getInstance(){

        if (mInstance == null){
            synchronized (RetrofitSingleton.class){
                if (mInstance == null){
                    mInstance = new RetrofitSingleton();
                }
            }
        }

        return mInstance;
    }

    public NewsApiInterface getAllApi(){
        return retrofit.create(NewsApiInterface.class);
    }
}
