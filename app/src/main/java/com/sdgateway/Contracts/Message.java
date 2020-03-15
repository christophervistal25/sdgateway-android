package com.sdgateway.Contracts;

import com.sdgateway.Modules.Models.Login.UserLoginRequest;
import com.sdgateway.Modules.Models.Login.UserLoginResponse;
import com.sdgateway.Modules.Models.Message.FetchMessageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Message {
    @GET("/api/device/messages/{device_id}")
    Call<List<FetchMessageResponse>>  fetch(@Path("device_id") String device_id);


}

