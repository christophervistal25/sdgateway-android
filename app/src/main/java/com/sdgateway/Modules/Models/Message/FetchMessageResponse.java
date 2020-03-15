package com.sdgateway.Modules.Models.Message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FetchMessageResponse {
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("code")
    @Expose
    private String code;


    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
