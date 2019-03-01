package com.example.ajdin.wishlist.repositories;

import com.example.ajdin.wishlist.models.Access;
import com.example.ajdin.wishlist.models.Login;
import com.example.ajdin.wishlist.models.Register;
import com.example.ajdin.wishlist.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AccessRepository {

    @POST("Access/Register")
    Call<ResponseBody> Regiser(@Body Register model);

    @GET("Access/CheckAccess")
    Call<ResponseBody> CheckAccess(@Header("AuthToken") String authToken);

    @GET("Access/Test")
    Call<List<User>> Test();

    @POST("Access/Login")
    Call<Access> Login(@Body Login model);
}

