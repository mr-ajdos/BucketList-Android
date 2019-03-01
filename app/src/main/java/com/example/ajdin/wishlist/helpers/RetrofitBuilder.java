package com.example.ajdin.wishlist.helpers;

import android.content.Context;

import com.example.ajdin.wishlist.R;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {
    static Retrofit retrofit = null;

    public static Retrofit Build(Context context) {
        if(retrofit == null)
                retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.ApiBaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return  retrofit;
    }
}
