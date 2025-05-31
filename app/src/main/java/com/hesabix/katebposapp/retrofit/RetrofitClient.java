package com.hesabix.katebposapp.retrofit;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hesabix.katebposapp.service.PreferencesManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://next.hesabix.ir/api/";
    private static Retrofit retrofit;
    private static final String TAG = "RetrofitClient";

    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            // ایجاد Gson با تنظیمات انعطاف‌پذیر
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // ایجاد OkHttpClient با Interceptor برای افزودن هدر
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request originalRequest = chain.request();
                            PreferencesManager prefs = new PreferencesManager(context);
                            String token = prefs.getToken();

                            Request.Builder requestBuilder = originalRequest.newBuilder();
                            if (token != null && !token.isEmpty()) {
                                requestBuilder.addHeader("X-Auth-Token", token);
                            }

                            int activeBusinessId = prefs.getActiveBusinessId();
                            int activeMoney = prefs.getActiveMoney();
                            int activeYear = prefs.getActiveYear();

                            if (activeBusinessId != -1) {
                                requestBuilder.addHeader("activeBid", String.valueOf(activeBusinessId));
                            }

                            Request request = requestBuilder.build();
                            
                            // لاگ کردن درخواست
                            Log.d(TAG, "درخواست به آدرس: " + request.url());
                            Log.d(TAG, "متد: " + request.method());
                            Log.d(TAG, "هدرهای ارسالی:");
                            for (String name : request.headers().names()) {
                                Log.d(TAG, name + ": " + request.header(name));
                            }
                            
                            // ارسال درخواست
                            Response response = chain.proceed(request);
                            
                            // لاگ کردن پاسخ
                            Log.d(TAG, "کد پاسخ: " + response.code());
                            Log.d(TAG, "پیام پاسخ: " + response.message());
                            
                            // لاگ کردن هدرهای دریافتی
                            Log.d(TAG, "هدرهای دریافتی:");
                            for (String name : response.headers().names()) {
                                Log.d(TAG, name + ": " + response.header(name));
                            }
                            
                            // لاگ کردن بدنه پاسخ
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                String responseString = responseBody.string();
                                Log.d(TAG, "بدنه پاسخ: " + responseString);
                                
                                // ایجاد یک ResponseBody جدید با همان محتوا
                                ResponseBody newResponseBody = ResponseBody.create(
                                    responseBody.contentType(),
                                    responseString
                                );
                                
                                // ایجاد یک Response جدید با ResponseBody جدید
                                response = response.newBuilder()
                                    .body(newResponseBody)
                                    .build();
                            }
                            
                            return response;
                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}