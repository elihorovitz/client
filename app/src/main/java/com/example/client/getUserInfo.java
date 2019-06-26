package com.example.client;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Response;

public class getUserInfo extends Worker {
    public getUserInfo(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        restApi serverInterface = serverHolder.getInstance().serverInterface;
        String token = getInputData().getString("key_user_token");
        try {
            Response<UserResponse> response = serverInterface.getUserInfo(token).execute();
            UserResponse userInfo = response.body();
            String infoAsJson = new Gson().toJson(userInfo);

            Data outputData = new Data.Builder()
                    .putString("key_user_token", infoAsJson)
                    .build();
            Log.d("abcde5", infoAsJson);
            return Result.success(outputData);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
