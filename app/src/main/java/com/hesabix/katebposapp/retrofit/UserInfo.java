package com.hesabix.katebposapp.retrofit;

import com.google.gson.annotations.SerializedName;

public class UserInfo {
    @SerializedName("id")
    private int id;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("fullname")
    private String fullname;
    
    @SerializedName("businessCount")
    private int businessCount;
    
    @SerializedName("hash_email")
    private String hashEmail;
    
    @SerializedName("mobile")
    private String mobile;
    
    @SerializedName("invateCode")
    private String inviteCode;

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public int getBusinessCount() {
        return businessCount;
    }

    public String getHashEmail() {
        return hashEmail;
    }

    public String getMobile() {
        return mobile;
    }

    public String getInviteCode() {
        return inviteCode;
    }
} 