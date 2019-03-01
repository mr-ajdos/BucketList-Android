package com.example.ajdin.wishlist.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.ajdin.wishlist.LoginActivity;
import com.example.ajdin.wishlist.R;
import com.example.ajdin.wishlist.models.Access;


public class GeneralHelper {
    public static void fillAccessSharedPreferences(Context context, Access model) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.AuthTokenPreferencesFile), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(context.getString(R.string.UserId), model.UserId);
        editor.putString(context.getString(R.string.AuthTokenKey), model.AuthToken);
        editor.putString(context.getString(R.string.UsernameKey), model.Username);

        editor.apply();
    }

    public static Access readAccessSharedPreferences(Context context) {
        Access model = new Access();

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.AuthTokenPreferencesFile), Context.MODE_PRIVATE);

        model.UserId = sharedPreferences.getInt(context.getString(R.string.UserId), 0);
        model.AuthToken = sharedPreferences.getString(context.getString(R.string.AuthTokenKey), null);
        model.Username = sharedPreferences.getString(context.getString(R.string.UsernameKey), "");

        return model;
    }

    public static void clearAccessSharedPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.AuthTokenPreferencesFile), Context.MODE_PRIVATE);
        sharedPref.edit().clear().commit();
    }

    public static void logoutUser(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.AuthTokenPreferencesFile), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.remove(context.getString(R.string.AuthTokenKey));
        editor.apply();

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
    }
}
