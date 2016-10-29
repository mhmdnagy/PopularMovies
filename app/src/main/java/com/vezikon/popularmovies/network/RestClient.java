package com.vezikon.popularmovies.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;



/**
 * Created by vezikon on 1/28/15.
 */
public class RestClient {

    private static String ROOT = "http://api.themoviedb.org/";
    private static Api API;


    static {
        setupRestClient();
    }

    private RestClient() {
    }

    public static Api get() {
        return API;
    }

    private static void setupRestClient() {
        Gson gson = new GsonBuilder().create();

        HttpLoggingInterceptor.Level logLevel = HttpLoggingInterceptor.Level.BODY;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(logLevel);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(ROOT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        API = retrofit.create(Api.class);

    }
}
