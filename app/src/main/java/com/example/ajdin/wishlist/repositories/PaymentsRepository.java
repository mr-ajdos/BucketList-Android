package com.example.ajdin.wishlist.repositories;

import com.example.ajdin.wishlist.models.Payment;
import com.example.ajdin.wishlist.viewModels.PaymentVM;
import com.example.ajdin.wishlist.viewModels.WishPaymentsVM;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentsRepository {

    @GET("Payments/GetPayments")
    Call<WishPaymentsVM> GetPayments(@Header("AuthToken") String authToken, @Query("wishId") int wishId);

    @POST("Payments/Edit")
    Call<ResponseBody> Edit(@Header("AuthToken") String authToken, @Body Payment model);

    @POST("Payments/Add")
    Call<PaymentVM> Add(@Header("AuthToken") String authToken, @Body Payment model);

    @POST("Payments/Delete")
    Call<ResponseBody> Delete(@Header("AuthToken") String authToken, @Query("paymentId") int paymentId);
}
