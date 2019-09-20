package com.rizieq.drinkshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.rizieq.drinkshop.Model.User;
import com.rizieq.drinkshop.Retrofit.IDrinkShopAPI;
import com.rizieq.drinkshop.Utils.Common;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {


    TextInputEditText txt_name, txt_password;
    TextView txt_lupa_password, txt_daftar;
    Button btn_login;

    IDrinkShopAPI mService;

    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sm = new SessionManager(this);
        mService = Common.getAPI();

        txt_name = findViewById(R.id.txt_input_name);
        txt_password = findViewById(R.id.txt_input_password);
        txt_lupa_password = findViewById(R.id.txt_lupa_password);
        txt_daftar = findViewById(R.id.txt_daftar);
        btn_login = findViewById(R.id.btn_login);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(txt_name.getText().toString(), txt_password.getText().toString());


            }
        });

        txt_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });


    }

    private void loginUser(String name, String password) {
        mService.loginUser(name, password)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {


                        User user = response.body();
                        if (user.getError_msg() == null) {


                            Common.currentUser = response.body();

                            sm.storeLogin(user.getPhone(),
                                    user.getName(),
                                    user.getAddress(),
                                    user.getBrithdate(),
                                    user.getAvatarUrl(),
                                    user.getPassword());


                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();


                        } else {
                            Toast.makeText(LoginActivity.this, "Username and Password Can't Found!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                        Toast.makeText(LoginActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("ERROR_DATA ", t.getMessage());
                    }
                });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
        finish();
    }
}


