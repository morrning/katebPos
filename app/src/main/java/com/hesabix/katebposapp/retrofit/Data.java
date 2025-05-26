package com.hesabix.katebposapp.retrofit;

import com.google.gson.annotations.SerializedName;
public class Data {
    @SerializedName("user")
    private String user;

    @SerializedName("token")
    private String token;

    @SerializedName("tokenID")
    private String tokenID;

    @SerializedName("active")
    private boolean active;

    @SerializedName("captcha_required")
    private boolean captcha_required;
}