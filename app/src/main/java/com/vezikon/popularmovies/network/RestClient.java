package com.vezikon.popularmovies.network;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by vezikon on 1/28/15.
 */
public class RestClient {

    private static String ROOT = "http://api.themoviedb.org/3/";
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
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()));
//                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        API = restAdapter.create(Api.class);


    }
}
