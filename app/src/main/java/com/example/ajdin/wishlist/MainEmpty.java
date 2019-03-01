package com.example.ajdin.wishlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ajdin.wishlist.helpers.GeneralHelper;
import com.example.ajdin.wishlist.helpers.RetrofitBuilder;
import com.example.ajdin.wishlist.models.Access;
import com.example.ajdin.wishlist.repositories.AccessRepository;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainEmpty extends AppCompatActivity {
    private AccessRepository accessRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_empty);

        accessRepository = RetrofitBuilder.Build(MainEmpty.this).create(AccessRepository.class);

        Access access = GeneralHelper.readAccessSharedPreferences(MainEmpty.this);
        if(access != null)
        {
            Call<ResponseBody> accessLoginResponse = accessRepository.CheckAccess(access.AuthToken);
            accessLoginResponse.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful())
                    {
                        Intent in = new Intent(MainEmpty.this,MainActivity.class);
                        startActivity(in);
                        finish();
                    }
                    else
                    {
                        GeneralHelper.clearAccessSharedPreferences(MainEmpty.this);
                        Intent in = new Intent(MainEmpty.this, LoginActivity.class);
                        startActivity(in);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}
