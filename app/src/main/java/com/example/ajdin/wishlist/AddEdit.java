package com.example.ajdin.wishlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ajdin.wishlist.helpers.ErrorResponse;
import com.example.ajdin.wishlist.helpers.GeneralHelper;
import com.example.ajdin.wishlist.helpers.RetrofitBuilder;
import com.example.ajdin.wishlist.models.Access;
import com.example.ajdin.wishlist.models.Wish;
import com.example.ajdin.wishlist.repositories.AccessRepository;
import com.example.ajdin.wishlist.repositories.WishesRepository;
import com.example.ajdin.wishlist.viewModels.WishVM;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEdit extends AppCompatActivity {

    private Button save_btn;
    private EditText name_entry;
    private EditText amount_entry;
    private Access access;
    private Wish wish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        access = GeneralHelper.readAccessSharedPreferences(AddEdit.this);

        if(access == null)
        {
            GeneralHelper.logoutUser(AddEdit.this);
            finish();
        }

        save_btn = (Button) findViewById(R.id.btn_save_wish);
        name_entry = (EditText) findViewById(R.id.txt_wish_name);
        amount_entry = (EditText) findViewById(R.id.txt_wish_amount);

        Intent in = getIntent();

        wish = (Wish) in.getSerializableExtra("edit_wish");

        if(wish != null && wish.getId() > 0)
        {
            setTitle("Edit wish");
            name_entry.setText(wish.getName());
            name_entry.setSelection(wish.getName().length());

            amount_entry.setText(wish.getAmount().toString());
            amount_entry.setSelection(wish.getAmount().toString().length());
        }
        else
        {
            setTitle("Add new wish");
        }

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ValidateInputs())
                {
                    WishesRepository wishesRepository = RetrofitBuilder.Build(AddEdit.this).create(WishesRepository.class);

                    if(wish != null && wish.getId() > 0)
                    {
                        wish = PopulateWishModel(false);
                        Call<ResponseBody> editResponse = wishesRepository.Edit(access.AuthToken, wish);

                        editResponse.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.isSuccessful())
                                {
                                    startActivity(new Intent(AddEdit.this,Details.class).putExtra("wish_id",wish.getId()));
                                    finish();
                                }
                                else
                                {
                                    if(response.code() == 401)
                                    {
                                        GeneralHelper.logoutUser(AddEdit.this);
                                    }
                                    else
                                    {
                                        try {
                                            ErrorResponse.ToastErrorMessage(AddEdit.this, response.errorBody().string()).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(AddEdit.this, "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        Wish newWish = PopulateWishModel(true);
                        Call<ResponseBody> addResponse = wishesRepository.Add(access.AuthToken, newWish);

                        addResponse.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                if(response.isSuccessful())
                                {
                                    Intent in = new Intent(AddEdit.this, MainActivity.class);
                                    startActivity(in);
                                    finish();
                                }
                                else
                                {
                                    if(response.code() == 401)
                                    {
                                        GeneralHelper.logoutUser(AddEdit.this);
                                    }
                                    else
                                    {
                                        try {
                                            ErrorResponse.ToastErrorMessage(AddEdit.this, response.errorBody().string()).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(AddEdit.this, "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(AddEdit.this,"All fields are required.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean ValidateInputs()
    {
        if(name_entry.getText().toString().isEmpty() || amount_entry.getText().toString().isEmpty())
            return false;

        return true;
    }

    private Wish PopulateWishModel(boolean isNew)
    {
        if(isNew)
        {
            Wish newWish = new Wish(0, name_entry.getText().toString(), Double.parseDouble(amount_entry.getText().toString()), access.UserId);

            return newWish;
        }
        else
        {
            wish.setAmount(Double.parseDouble(amount_entry.getText().toString()));
            wish.setName(name_entry.getText().toString());

            return wish;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(wish != null && wish.getId() > 0)
        {
            startActivity(new Intent(AddEdit.this,Details.class).putExtra("wish_id",wish.getId()));
        }
        finish();
    }
}
