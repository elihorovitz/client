package com.example.client;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import javax.xml.transform.Result;

import io.reactivex.Scheduler;
import retrofit2.Call;
import retrofit2.Response;

public class GetUserWorker extends Worker {
    public GetUserWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        restApi serverInterface = serverHolder.getInstance().serverInterface;

        String userId = getInputData().getString("key_user_id");
        Log.d("abcde1", userId);
        try {
            Call<TokenResponse> response = serverInterface.getUser(userId);
            TokenResponse token = response.execute().body();
            String tokenAsJson = new Gson().toJson(token);
            Log.d("abcde2", "round2: " + tokenAsJson);
            Data outputData = new Data.Builder()
                    .putString("key_output_user", tokenAsJson)
                    .build();
            return Result.success(outputData);

        } catch (IOException e) {
            Log.d("round2", "round2: failed" );
            e.printStackTrace();
            return Result.retry();
        }
    }
}