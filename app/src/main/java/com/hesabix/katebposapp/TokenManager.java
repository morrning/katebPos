package com.hesabix.katebposapp;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private final SharedPreferences sharedPreferences;
    private static final String AUTH_TOKEN_KEY = "auth_token";

    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN_KEY, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(AUTH_TOKEN_KEY, null);
    }

    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AUTH_TOKEN_KEY);
        editor.apply();
    }
}