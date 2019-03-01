package com.example.ajdin.wishlist.repositories;

import com.example.ajdin.wishlist.models.Access;
import com.example.ajdin.wishlist.models.Login;
import com.example.ajdin.wishlist.models.Register;
import com.example.ajdin.wishlist.models.User;
import com.example.ajdin.wishlist.models.Wish;
import com.example.ajdin.wishlist.viewModels.WishDetailsVM;
import com.example.ajdin.wishlist.viewModels.WishPaymentsVM;
import com.example.ajdin.wishlist.viewModels.WishVM;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WishesRepository {

    @GET("Wishes/GetWishes")
    Call<List<WishVM>> Index(@Header("AuthToken") String authToken, @Query("userId") int userId);

    @GET("Wishes/GetDetails")
    Call<WishDetailsVM> GetDetails(@Header("AuthToken") String authToken, @Query("wishId") int wishId);

    @POST("Wishes/Add")
    Call<ResponseBody> Add(@Header("AuthToken") String authToken, @Body Wish model);

    @POST("Wishes/Edit")
    Call<ResponseBody> Edit(@Header("AuthToken") String authToken, @Body Wish model);

    @POST("Wishes/Delete")
    Call<ResponseBody> Delete(@Header("AuthToken") String authToken, @Query("wishId") int wishId);

}

