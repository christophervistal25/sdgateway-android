package com.sdgateway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sdgateway.Contracts.Message;
import com.sdgateway.Contracts.User;
import com.sdgateway.Helper.SharedPref;
import com.sdgateway.Modules.Models.Login.UserLoginRequest;
import com.sdgateway.Modules.Models.Login.UserLoginResponse;
import com.sdgateway.Modules.Models.Message.FetchMessageResponse;
import com.sdgateway.Modules.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
  private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 2);

        if (SharedPref.getSharedPreferenceString(this, "device_id", null) != null)  {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);

        EditText phoneNumber = findViewById(R.id.phoneNumber);
        EditText password = findViewById(R.id.passsword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {

            if (phoneNumber.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please check your phone number/password", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.show();
            Retrofit retrofit   = RetrofitService.RetrofitInstance(getApplicationContext());
            User services = retrofit.create(User.class);

            UserLoginRequest userLoginRequest = new UserLoginRequest();
            userLoginRequest.setPrimary_phone_number(phoneNumber.getText().toString());
            userLoginRequest.setPassword(password.getText().toString());

            Call<UserLoginResponse> loginResponseCall = services.login(userLoginRequest);

            loginResponseCall.enqueue(new Callback<UserLoginResponse>() {
                @Override
                public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                    UserLoginResponse result = response.body();
                    if (response.code() == 200) {
                        SharedPref.setSharedPreferenceString(getApplicationContext(), "device_id", String.valueOf(result.getInfo().getId()));
                        SharedPref.setSharedPreferenceString(getApplicationContext(), "phone_number", result.getInfo().getPrimaryPhoneNumber());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Please check your phone number/password", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private boolean checkPermission(String sendSms) {

        int checkpermission = ContextCompat.checkSelfPermission(this,sendSms);
        return checkpermission == PackageManager.PERMISSION_GRANTED;
    }

}
