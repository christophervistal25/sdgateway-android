package com.sdgateway.Modules.Models.Register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserRegisterResponse {
    @SerializedName("primary_phone_number")
    @Expose
    private String primaryPhoneNumber;
    @SerializedName("id")
    @Expose
    private Integer id;

    public String getPrimaryPhoneNumber() {
        return primaryPhoneNumber;
    }

    public void setPrimaryPhoneNumber(String primaryPhoneNumber) {
        this.primaryPhoneNumber = primaryPhoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;

    }
}
