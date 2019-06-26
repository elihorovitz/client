package com.example.client;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class serverHolder {
    private static serverHolder instance = null;


    public synchronized static serverHolder getInstance() {
        if (instance != null)
            return instance;

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://hujipostpc2019.pythonanywhere.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restApi serverInterface = retrofit.create(restApi.class);
        instance = new serverHolder(serverInterface);
        return instance;
    }


    public final restApi serverInterface;

    private serverHolder(restApi serverInterface) {
        this.serverInterface = serverInterface;
    }
}
