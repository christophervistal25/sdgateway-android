package com.sdgateway.Modules.Models.Register;

public class UserRegisterRequest {
    public String primary_phone_number;

    public String password;

    public String getPrimary_phone_number() {
        return primary_phone_number;
    }

    public void setPrimary_phone_number(String primary_phone_number) {
        this.primary_phone_number = primary_phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
