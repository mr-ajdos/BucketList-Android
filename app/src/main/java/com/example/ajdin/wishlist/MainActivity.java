package com.example.ajdin.wishlist;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ajdin.wishlist.adapters.WishAdapter;
import com.example.ajdin.wishlist.helpers.ErrorResponse;
import com.example.ajdin.wishlist.helpers.GeneralHelper;
import com.example.ajdin.wishlist.helpers.RetrofitBuilder;
import com.example.ajdin.wishlist.models.Access;
import com.example.ajdin.wishlist.repositories.WishesRepository;
import com.example.ajdin.wishlist.viewModels.WishVM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private List<WishVM> wishes = new ArrayList<>();
    private Access access;
    private WishesRepository wishesRepository;
    private WishAdapter adapter;
    private ListView wishList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("My wishes");
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_addNewWish);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddEdit.class));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        LoadWishes();
    }

    private void LoadWishes()
    {
        access = GeneralHelper.readAccessSharedPreferences(MainActivity.this);

        if(access == null)
        {
            GeneralHelper.logoutUser(MainActivity.this);
            finish();
        }

        wishList = (ListView) findViewById(R.id.list_wishListItems);

        wishesRepository = RetrofitBuilder.Build(MainActivity.this).create(WishesRepository.class);
        Call<List<WishVM>> call = wishesRepository.Index(access.AuthToken, access.UserId);

        call.enqueue(new Callback<List<WishVM>>() {
            @Override
            public void onResponse(Call<List<WishVM>> call, Response<List<WishVM>> response) {
                if(response.isSuccessful())
                {
                    wishes = response.body();

                    if(wishes == null)
                        Toast.makeText(MainActivity.this, "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                    else
                    {
                        adapter = new WishAdapter(MainActivity.this,R.layout.wish_list_item ,wishes);
                        wishList.setAdapter(adapter);

                        wishList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                WishVM clickedWish = wishes.get(position);

                                Intent in = new Intent(MainActivity.this, Details.class);
                                in.putExtra("wish_id",clickedWish.Id);

                                startActivity(in);
                            }
                        });
                    }
                }
                else
                {
                    if(response.code() == 401)
                    {
                        GeneralHelper.logoutUser(MainActivity.this);
                    }
                    else
                    {
                        try {
                            ErrorResponse.ToastErrorMessage(MainActivity.this, response.errorBody().string()).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<WishVM>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
