package com.example.client;



import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface restApi {


    @GET("/user/")
    Call<UserResponse> getUserInfo(@Header("Authorization") String Auth);



    @GET("/users/{user_id}/token/")
    Call<TokenResponse> getUser(@Path("user_id") String userId);


//    @GET("/todos")
//    Call<List<Ticket>> getAllTicketsForUser(@Query("user_id") String userId);
//
//

    @Headers("Content-Type: application/json")
    @POST("/user/edit/")
    Call<UserResponse> insertNickname(@Header("Authorization") String Auth,@Body SetUserPrettyNameRequest request);

}
