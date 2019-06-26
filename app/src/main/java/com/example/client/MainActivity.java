package com.example.client;

//import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TOKEN = "token";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static String TAG = "abcde3";
    private static String username;
    private Gson gson = new Gson();
    private Button visibleButton;
    private Button prettyButton;
    private EditText setUsername;
    private EditText editPretty;
    private ImageView imageView;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        visibleButton = findViewById(R.id.action);
        prettyButton = findViewById(R.id.pretty);
        setUsername = findViewById(R.id.username);
        editPretty = findViewById(R.id.prettyName);
        imageView = findViewById(R.id.image);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void sendUser(View view) {
        EditText editText = findViewById(R.id.username);
        username = editText.getText().toString();
        TextView textView = findViewById(R.id.loading);
        textView.setVisibility(View.VISIBLE);
        visibleButton.setVisibility(View.INVISIBLE);
        setUsername.setVisibility(View.INVISIBLE);

        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest getUserWork = new OneTimeWorkRequest.Builder(GetUserWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_user_id", username).build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager manager = WorkManager.getInstance();
        manager.enqueue(getUserWork);

        manager.getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                // we know there will be only 1 work info in this list - the 1 work with that specific tag!
                // there might be some time until this worker is finished to work (in the mean team we will get an empty list
                // so check for that
                if (workInfos == null || workInfos.isEmpty())
                    return;
//                try {
//                    Thread.sleep(4000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                WorkInfo info = workInfos.get(0);
                if (info.getState() != WorkInfo.State.SUCCEEDED)
                {
                    return;
                }
                Log.d("abcde4", info.getState().toString());
                // now we can use it

                String tokenAsJson = info.getOutputData().getString("key_output_user");
                Log.d(TAG, "got token: " + tokenAsJson);

                TokenResponse token = new Gson().fromJson(tokenAsJson, TokenResponse.class);
                // update UI with the user we got

                editor.putString(TOKEN, token.data);
                editor.apply();
                getAllUserInfo();
            }
        });
    }

    private void getAllUserInfo(){
        UUID workTagUniqueId = UUID.randomUUID();
        String token = sp.getString(TOKEN, "");
        OneTimeWorkRequest checkConnectivityWork = new OneTimeWorkRequest.Builder(getUserInfo.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_user_token", "token " + token).build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager.getInstance().enqueue(checkConnectivityWork);

        WorkManager.getInstance().getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                // we know there will be only 1 work info in this list - the 1 work with that specific tag!
                // there might be some time until this worker is finished to work (in the mean team we will get an empty list
                // so check for that
                if (workInfos == null || workInfos.isEmpty())
                    return;

                WorkInfo info = workInfos.get(0);
                if (info.getState() != WorkInfo.State.SUCCEEDED)
                {
                    return;
                }
                Log.d(TAG, "got userresponse");
                // now we can use it
                String infoAsJson = info.getOutputData().getString("key_user_token");
                UserResponse allInfo = new Gson().fromJson(infoAsJson, UserResponse.class);
                TextView text = findViewById(R.id.Token);
                TextView textView = findViewById(R.id.loading);
                textView.setVisibility(View.INVISIBLE);


                prettyButton.setVisibility(View.VISIBLE);

                editPretty.setVisibility(View.VISIBLE);
                String nameToWrite;
                if (allInfo.data.pretty_name.isEmpty())
                {
                    nameToWrite = allInfo.data.username;
                }else{
                     nameToWrite= allInfo.data.pretty_name;
                }
                text.setText("welcome again, " + nameToWrite + "!");
                if (!allInfo.data.image_url.isEmpty())
                {
                    Picasso.get().load("http://hujipostpc2019.pythonanywhere.com" + allInfo.data.image_url).into(imageView);

                }
//                spinner.setVisibility(View.VISIBLE);
            }
        });
    }


    public void setPretty(View view) {
        UUID workTagUniqueId = UUID.randomUUID();
        String token = sp.getString(TOKEN, "");
        SetUserPrettyNameRequest nickname = new SetUserPrettyNameRequest();
        nickname.pretty_name = editPretty.getText().toString();
        String nickNameAsJson = new Gson().toJson(nickname);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OneTimeWorkRequest checkConnectivityWork = new OneTimeWorkRequest.Builder(setPrettyWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_user_token", "token " + token).putString("key_user_nickname", nickNameAsJson).build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager.getInstance().enqueue(checkConnectivityWork);

        WorkManager.getInstance().getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                // we know there will be only 1 work info in this list - the 1 work with that specific tag!
                // there might be some time until this worker is finished to work (in the mean team we will get an empty list
                // so check for that
                if (workInfos == null || workInfos.isEmpty())
                    return;

                WorkInfo info = workInfos.get(0);
                if (info.getState() != WorkInfo.State.SUCCEEDED)
                {
                    return;
                }
                Log.d(TAG, "got userresponse");
                // now we can use it
                String infoAsJson = info.getOutputData().getString("key_output_user");
                UserResponse allInfo = new Gson().fromJson(infoAsJson, UserResponse.class);
                String nameToWrite;
                if (allInfo.data.pretty_name.isEmpty())
                {
                    nameToWrite = "Unable to place nickname, Sorry!";
                }else{
                    nameToWrite= allInfo.data.pretty_name;
                }
                TextView text = findViewById(R.id.Token);
                text.setText("welcome again, " + nameToWrite + "!");
            }
        });
    }
}
