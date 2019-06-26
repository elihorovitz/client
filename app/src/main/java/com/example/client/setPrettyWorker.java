package com.example.client;

import android.content.Context;

import org.json.JSONObject;
import com.google.gson.Gson;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Response;

public class setPrettyWorker extends Worker {
    public setPrettyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        restApi serverInterface = serverHolder.getInstance().serverInterface;

        String token = getInputData().getString("key_user_token");
        String nicknameAsJson = getInputData().getString("key_user_nickname");
        SetUserPrettyNameRequest request = new Gson().fromJson(nicknameAsJson, SetUserPrettyNameRequest.class);
        try {
            Response<UserResponse> response = serverInterface.insertNickname(token, request).execute();
            UserResponse responseBody = response.body();
            String responseAsJson = new Gson().toJson(responseBody);
            Data outputData = new Data.Builder()
                    .putString("key_output_user", responseAsJson)
                    .build();

            return Result.success(outputData);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

}
