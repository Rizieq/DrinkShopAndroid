package com.rizieq.drinkshop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {


    TextInputEditText txt_input_name, txt_input_password;
    TextView txt_lupa_password, txt_daftar;
    Button btn_login;

    IDrinkShopAPI mService;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_input_name = findViewById(R.id.txt_input_name);
        txt_input_password = findViewById(R.id.txt_input_password);
        txt_lupa_password = findViewById(R.id.txt_lupa_password);
        txt_daftar = findViewById(R.id.txt_daftar);
        btn_login = findViewById(R.id.btn_login);

        mService = Common.getAPI();
        sm = new SessionManager(LoginActivity.this);

        final HashMap<String, String> map = sm.getDataLogin();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txt_input_name.getText().toString();
                String password = txt_input_password.getText().toString();

                loginUser(name,password);

                if (Common.currentUser != null){

                    Log.d("NAME_USER ",Common.currentUser.getName());
                }
            }
        });

    }

    private void loginUser(String name, String password) {
        final AlertDialog alertDialog = new SpotsDialog(LoginActivity.this);
        alertDialog.show();
        alertDialog.setMessage("Please Waiting...");
        mService.loginUser(name, password)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        alertDialog.dismiss();
                        if (response.isSuccessful()){

                            User user = response.body();
                            /*sm.storeLogin(user.getPhone(),
                                    user.getName(),
                                    user.getAddress(),
                                    user.getBrithdate(),
                                    user.getAvatarUrl(),
                                    user.getPassword());*/
                        Common.currentUser = user;


                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        alertDialog.dismiss();
                        Log.d("MESSAGE_LOGIN ",t.getMessage());
                        Toast.makeText(LoginActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
