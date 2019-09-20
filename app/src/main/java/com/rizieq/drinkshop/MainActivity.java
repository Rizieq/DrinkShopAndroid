
package com.rizieq.drinkshop;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rizieq.drinkshop.Model.CheckUserResponse;
import com.rizieq.drinkshop.Model.User;
import com.rizieq.drinkshop.Retrofit.IDrinkShopAPI;
import com.rizieq.drinkshop.Utils.Common;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;

    MaterialEditText edt_name, edt_address, edt_brithdate, edt_password, edt_repeat_password;
    Button btn_register;

    IDrinkShopAPI mService;
    SessionManager sm;


    String name, address, brithdate, password, repeat_password, phone_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mService = Common.getAPI();
        sm = new SessionManager(this);

        edt_name = (MaterialEditText) findViewById(R.id.edt_name);
        edt_address = (MaterialEditText) findViewById(R.id.edt_address);
        edt_brithdate = (MaterialEditText) findViewById(R.id.edt_brithdate);
        edt_password = (MaterialEditText) findViewById(R.id.edt_password);
        edt_repeat_password = (MaterialEditText) findViewById(R.id.edt_repeat_password);


        btn_register = (Button) findViewById(R.id.btn_register);
        edt_brithdate.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        startLoginPage(LoginType.PHONE);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edt_name.getText().toString();
                address = edt_address.getText().toString();
                brithdate = edt_brithdate.getText().toString();
                password = edt_password.getText().toString();
                repeat_password = edt_repeat_password.getText().toString();
                loginUser(address, brithdate, name, password, repeat_password, phone_data);


            }
        });
    }

    private void startLoginPage(LoginType loginType) {
        Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, builder.build());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if (result.getError() != null) {
                Toast.makeText(this, "" + result.getError().getErrorType(), Toast.LENGTH_SHORT).show();
            } else if (result.wasCancelled()) {
                Log.d("READ_DATA_CANCEL ", String.valueOf(result.wasCancelled()));
            } else {
                if (result.getAccessToken() != null) {


                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {

                            phone_data = String.valueOf(account.getPhoneNumber());

                            Log.d("DATA_PHONE ", phone_data);
                            mService.checkUserExits(account.getPhoneNumber().toString())
                                    .enqueue(new Callback<CheckUserResponse>() {
                                        @Override
                                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {

                                            CheckUserResponse checkUserResponse = response.body();
                                            if (checkUserResponse.getExists() != null) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setTitle("Opps Phone has been registed !")
                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                                                finish();
                                                            }
                                                        });
                                                builder.show();
                                            } else {
                                                Log.d("onResponse_error_message ", checkUserResponse.getError_msg());

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                                            Log.d("READ_DATA_FAILURE ", t.getMessage());
                                        }
                                    });


                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {

                            Log.d("READ_DATA_ERROR_ACCOUNTKIT2 ", accountKitError.getErrorType().getMessage());
                        }
                    });
                }
            }
        }
    }

    private void loginUser(final String address, final String brithdate, final String name, final String password, String repeat_password, final String phone) {


        Log.d("READ_DATA_ADDRESS ", address);
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(MainActivity.this, "Please enter your address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(brithdate)) {
            Toast.makeText(MainActivity.this, "Please enter your brithdate", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(repeat_password)) {
            Toast.makeText(MainActivity.this, "Please enter your repeat password", Toast.LENGTH_SHORT).show();
            return;
        }


        if (repeat_password.equals(password)) {

            final AlertDialog alertDialog = new SpotsDialog(MainActivity.this);
            alertDialog.show();
            alertDialog.setMessage("Please Waiting...");
            mService.registerNewUser(phone, name, address, brithdate, password)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                            final User user = response.body();
                            if (TextUtils.isEmpty(user.getError_msg())) {

                                mService.getUserInformation(password)
                                        .enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response) {

                                                alertDialog.dismiss();

                                                Common.currentUser = user;
                                                sm.storeLogin(user.getPhone(),
                                                        user.getName(),
                                                        user.getAddress(),
                                                        user.getBrithdate(),
                                                        user.getAvatarUrl(),
                                                        user.getPassword());

                                                Toast.makeText(MainActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                finish();

                                            }

                                            @Override
                                            public void onFailure(Call<User> call, Throwable t) {
                                                alertDialog.dismiss();
                                                Log.d("READ_DATA_FAILURE ", t.getMessage());
                                            }
                                        });


                            } else {
                                alertDialog.dismiss();
                                Log.d("ERROR_DATA_MSG ", user.getError_msg());
                                Toast.makeText(MainActivity.this, "" + user.getError_msg(), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            alertDialog.dismiss();
                            Log.d("ERROR_DATA_FAILRE ", t.getMessage());
                        }
                    });
        } else {
            Log.d("READ_DATA_PASSWORD ", repeat_password + " = " + password);
            edt_repeat_password.setError("Password is different !");
        }
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.rizieq.drinkshop",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Exit Application when click BACK Button

   /* boolean isBackButtonClicked = true;

    @Override
    public void onBackPressed() {
        if (isBackButtonClicked) {
            super.onBackPressed();
            return;
        }
        this.isBackButtonClicked = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        isBackButtonClicked = false;
        super.onResume();
    }*/


}




