package com.hesabix.katebposapp.retrofit;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import org.json.JSONObject;

public interface ApiService {
    @POST("user/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("captcha/image")
    Call<ResponseBody> getCaptchaImage();

    @GET("business/list")
    Call<List<Business>> getBusinesses();

    @GET("user/current/info")
    Call<UserInfo> getUserInfo();

    @POST("user/change/password")
    Call<JSONObject> changePassword(@Body JSONObject passwordData);

    @GET("money/get/all")
    Call<CurrencyResponse> getCurrencies();

    @POST("business/insert")
    Call<JSONObject> insertBusiness(@Body JSONObject businessData);
}