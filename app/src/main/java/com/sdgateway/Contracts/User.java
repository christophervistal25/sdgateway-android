package com.sdgateway.Contracts;

import com.sdgateway.Modules.Models.Login.UserLoginRequest;
import com.sdgateway.Modules.Models.Login.UserLoginResponse;
import com.sdgateway.Modules.Models.Register.UserRegisterRequest;
import com.sdgateway.Modules.Models.Register.UserRegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface User {
    @POST("/api/device/login")
    Call<UserLoginResponse> login(@Body UserLoginRequest userLoginRequest);

    @POST("/api/device/register")
    Call<UserRegisterResponse> register(@Body UserRegisterRequest userRegisterRequest);
}
