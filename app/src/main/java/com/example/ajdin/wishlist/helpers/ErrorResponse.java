package com.example.ajdin.wishlist.helpers;

import android.content.Context;
import android.widget.Toast;

import com.example.ajdin.wishlist.models.ErrorModel;
import com.google.gson.Gson;

public final class ErrorResponse {

    public static Toast ToastErrorMessage(Context context, String errorBody)
    {
        Gson gson = new Gson();
        ErrorModel errorModel = gson.fromJson(errorBody, ErrorModel.class);

        return Toast.makeText(context,errorModel.Message,Toast.LENGTH_LONG);
    }
}
