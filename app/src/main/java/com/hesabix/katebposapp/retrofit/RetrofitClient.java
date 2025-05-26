package com.hesabix.katebposapp.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hesabix.katebposapp.service.PreferencesManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://abr.ikateb.ir/api/";
    private static Retrofit retrofit;

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
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request originalRequest = chain.request();
                            PreferencesManager prefs = new PreferencesManager(context);
                            String token = prefs.getToken();

                            Request.Builder requestBuilder = originalRequest.newBuilder();
                            if (token != null && !token.isEmpty()) {
                                requestBuilder.addHeader("x-auth-token", token);
                            }

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
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