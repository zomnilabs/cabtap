package com.alleoindong.cabtap.data.remote;

import com.alleoindong.cabtap.data.remote.services.CabtapService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private static RetrofitHelper instance;
    private static final String BASE_URL = "http://cabtap.zomnilabs.com/api/";

    private CabtapService cabtapService;

    public static RetrofitHelper getInstance() {
        if (instance == null) {
            instance = new RetrofitHelper();
        }

        return instance;
    }

    private RetrofitHelper() {
        Gson gson = new GsonBuilder().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // Register service
        this.cabtapService = retrofit.create(CabtapService.class);
    }

    public CabtapService getService()
    {
        return this.cabtapService;
    }

}
