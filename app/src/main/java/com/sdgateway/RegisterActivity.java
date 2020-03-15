package com.sdgateway;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sdgateway.Contracts.User;
import com.sdgateway.Helper.SharedPref;
import com.sdgateway.Modules.Models.Register.UserRegisterRequest;
import com.sdgateway.Modules.Models.Register.UserRegisterResponse;
import com.sdgateway.Modules.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        EditText primaryPhoneNumber = findViewById(R.id.userPhoneNumber);
        EditText password = findViewById(R.id.userPassword);
        EditText confirmPassword = findViewById(R.id.userConfirmPassword);
        Button btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(v -> {
            if (primaryPhoneNumber.getText().toString().isEmpty() || password.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please check all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.getText().toString().equals(confirmPassword.getText().toString()) || !confirmPassword.getText().toString().equals(password.getText().toString())) {
                Toast.makeText(this, "Password must be the same with confirm password", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.show();

            Retrofit retrofit   = RetrofitService.RetrofitInstance(getApplicationContext());
            User services = retrofit.create(User.class);

            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
            userRegisterRequest.setPrimary_phone_number(primaryPhoneNumber.getText().toString());
            userRegisterRequest.setPrimary_phone_number(password.getText().toString());

            Call<UserRegisterResponse> userRegisterResponseCall = services.register(userRegisterRequest);
            userRegisterResponseCall.enqueue(new Callback<UserRegisterResponse>() {
                @Override
                public void onResponse(Call<UserRegisterResponse> call, Response<UserRegisterResponse> response) {
                    if (response.code() == 200) {
                        progressDialog.dismiss();
                        UserRegisterResponse result = response.body();
                        SharedPref.setSharedPreferenceString(getApplicationContext(), "device_id", String.valueOf(result.getId()));
                        SharedPref.setSharedPreferenceString(getApplicationContext(), "phone_number", result.getPrimaryPhoneNumber());
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Phone number is already register.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        primaryPhoneNumber.setError("Phone number is already exists");
                    }
                }

                @Override
                public void onFailure(Call<UserRegisterResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });




            // Retrofit send
        });

    }
}
