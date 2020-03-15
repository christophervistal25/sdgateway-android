package com.sdgateway.Modules.Models.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserLoginResponse {

    @SerializedName("info")
    @Expose
    private Info info;
    @SerializedName("message")
    @Expose
    private List<Message> message = null;

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }

}
