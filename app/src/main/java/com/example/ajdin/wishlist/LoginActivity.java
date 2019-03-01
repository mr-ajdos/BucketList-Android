package com.example.ajdin.wishlist;

import android.content.ContentResolver;
import android.content.Intent;
import android.provider.Settings;
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
import com.example.ajdin.wishlist.helpers.GeneralHelper;
import com.example.ajdin.wishlist.helpers.RetrofitBuilder;
import com.example.ajdin.wishlist.models.Access;
import com.example.ajdin.wishlist.models.Login;
import com.example.ajdin.wishlist.repositories.AccessRepository;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private AccessRepository accessRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = (Button) findViewById(R.id.login_btn);
        final EditText username_entry = (EditText) findViewById(R.id.login_username_entry);
        final EditText password_entry = (EditText) findViewById(R.id.login_password_entry);

        TextView createAccount_btn = (TextView) findViewById(R.id.create_account_btn);

        SpannableString content = new SpannableString("Create account here!");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        createAccount_btn.setText(content);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!username_entry.getText().toString().isEmpty() && !password_entry.getText().toString().isEmpty())
                {
                    String android_id = Settings.Secure.getString((LoginActivity.this).getContentResolver(),Settings.Secure.ANDROID_ID);

                    final Login loginModel = new Login();

                    loginModel.DeviceId = android_id;
                    loginModel.Username = username_entry.getText().toString();
                    loginModel.Password = password_entry.getText().toString();

                    accessRepository = RetrofitBuilder.Build(LoginActivity.this).create(AccessRepository.class);
                    Call<Access> call = accessRepository.Login(loginModel);

                    call.enqueue(new Callback<Access>() {
                        @Override
                        public void onResponse(Call<Access> call, Response<Access> response) {

                            if(response.isSuccessful())
                            {
                                Access responseModel = response.body();

                                if(responseModel == null)
                                    Toast.makeText(LoginActivity.this, "An error occured, please try again later.", Toast.LENGTH_SHORT).show();
                                else
                                {
                                    GeneralHelper.fillAccessSharedPreferences(LoginActivity.this, responseModel);
                                    Intent in = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(in);
                                    finish();
                                }
                            }
                            else
                            {
                                try {
                                    ErrorResponse.ToastErrorMessage(LoginActivity.this, response.errorBody().string()).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Access> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "An error occured, please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"All fields are required",Toast.LENGTH_LONG).show();
                }
            }
        });

       createAccount_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(in);
                finish();
            }
        });


    }
}
