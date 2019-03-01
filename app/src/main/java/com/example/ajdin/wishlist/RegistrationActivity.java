package com.example.ajdin.wishlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajdin.wishlist.helpers.ErrorResponse;
import com.example.ajdin.wishlist.helpers.RetrofitBuilder;
import com.example.ajdin.wishlist.models.Register;
import com.example.ajdin.wishlist.repositories.AccessRepository;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Button registerBtn = (Button) findViewById(R.id.create_account_btn);
        final EditText username_entry = (EditText) findViewById(R.id.login_username_entry);
        final EditText password_entry = (EditText) findViewById(R.id.login_password_entry);

        TextView redirectToLoginBtn = (TextView) findViewById(R.id.go_to_login_page_btn);

        final SpannableString content = new SpannableString("Log in to your account!");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        redirectToLoginBtn.setText(content);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(username_entry.getText().toString().isEmpty() || password_entry.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"All fields are required!",Toast.LENGTH_LONG).show();
                    return;
                }

                Register newUser = new Register();
                newUser.Username = username_entry.getText().toString();
                newUser.Password = password_entry.getText().toString();

                AccessRepository accessRepository = RetrofitBuilder.Build(getApplicationContext()).create(AccessRepository.class);
                Call<ResponseBody> register = accessRepository.Regiser(newUser);

                register.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200)
                        {
                            Toast.makeText(RegistrationActivity.this, "Now you can login with your username & password!", Toast.LENGTH_LONG).show();

                            Intent in = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(in);
                            finish();
                        }
                        else
                        {
                            try {
                                ErrorResponse.ToastErrorMessage(RegistrationActivity.this, response.errorBody().string()).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(RegistrationActivity.this, "An error occured, please try again later!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        redirectToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(in);
                finish();
            }
        });
    }
}
