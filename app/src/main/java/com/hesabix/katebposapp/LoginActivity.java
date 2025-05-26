package com.hesabix.katebposapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.hesabix.katebposapp.databinding.ActivityLoginBinding;
import com.hesabix.katebposapp.retrofit.ApiService;
import com.hesabix.katebposapp.retrofit.LoginRequest;
import com.hesabix.katebposapp.retrofit.LoginResponse;
import com.hesabix.katebposapp.retrofit.RetrofitClient;
import com.hesabix.katebposapp.service.PreferencesManager;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private ApiService apiService;
    private PreferencesManager preferencesManager;
    private boolean isCaptchaRequired = false;
    private boolean isLoginInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // مقداردهی اولیه
        apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        preferencesManager = new PreferencesManager(this);

        // تنظیم زیرخط برای لینک عضویت
        String registerText = "عضویت در برنامه";
        SpannableString spannableString = new SpannableString(registerText);
        spannableString.setSpan(new UnderlineSpan(), 0, registerText.length(), 0);
        binding.registerLink.setText(spannableString);

        // تنظیم کلیک برای لینک عضویت (باز کردن در مرورگر)
        binding.registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://abr.ikateb.ir"));
            startActivity(intent);
        });

        // تنظیم کلیک دکمه ورود
        binding.loginButton.setOnClickListener(v -> {
            if (isLoginInProgress) {
                return;
            }

            String phoneNumber = binding.phoneNumberEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();
            String captchaAnswer = isCaptchaRequired ? binding.captchaEditText.getText().toString().trim() : null;
            boolean standard = true;

            // اعتبارسنجی فیلدها
            if (phoneNumber.isEmpty()) {
                binding.phoneNumberInputLayout.setError("شماره تلفن را وارد کنید");
                return;
            } else if (!isValidPhoneNumber(phoneNumber)) {
                binding.phoneNumberInputLayout.setError("شماره تلفن باید با 09 شروع شود و 11 رقم باشد");
                return;
            } else {
                binding.phoneNumberInputLayout.setError(null);
            }

            if (password.isEmpty()) {
                binding.passwordInputLayout.setError("رمز عبور را وارد کنید");
                return;
            } else {
                binding.passwordInputLayout.setError(null);
            }

            if (isCaptchaRequired && captchaAnswer.isEmpty()) {
                binding.captchaInputLayout.setError("کد کپچا را وارد کنید");
                return;
            } else {
                binding.captchaInputLayout.setError(null);
            }

            // فعال کردن لودینگ و غیرفعال کردن دکمه
            isLoginInProgress = true;
            binding.loginButton.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.captchaImageView.setVisibility(View.GONE);
            binding.captchaInputLayout.setVisibility(View.GONE);

            LoginRequest request = new LoginRequest(phoneNumber, password, standard, captchaAnswer);
            apiService.login(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    isLoginInProgress = false;
                    binding.loginButton.setEnabled(true);
                    binding.progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Raw Response: " + response.toString());

                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        // لاگ JSON خام برای دیباگ
                        Log.d(TAG, "Raw JSON: " + new Gson().toJson(loginResponse));
                        Log.d(TAG, "Success: " + loginResponse.isSuccess());
                        Log.d(TAG, "Data: " + (loginResponse.getData() != null));
                        Log.d(TAG, "Token: " + (loginResponse.getData() != null ? loginResponse.getData().getToken() : "null"));
                        Log.d(TAG, "Message: " + loginResponse.getMessage());
                        Log.d(TAG, "Error: " + loginResponse.getError());
                        Log.d(TAG, "Captcha Required: " + loginResponse.isCaptchaRequired());
                        Log.d(TAG, "Attempts: " + loginResponse.getAttempts());
                        Log.d(TAG, "Last Attempt Time: " + loginResponse.getLastAttemptTime());
                        Log.d(TAG, "Session ID: " + loginResponse.getSessionId());

                        if (loginResponse.isSuccess() && loginResponse.getData() != null && loginResponse.getData().getToken() != null) {
                            String token = loginResponse.getData().getToken();
                            preferencesManager.saveToken(token);
                            Log.d(TAG, "Saved Token: " + preferencesManager.getToken());

                            Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            // استفاده از message به جای error برای نمایش خطا
                            String errorMessage = loginResponse.getMessage() != null ? loginResponse.getMessage() : "خطای ناشناخته";
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            if (loginResponse.isCaptchaRequired()) {
                                isCaptchaRequired = true;
                                binding.captchaImageView.setVisibility(View.VISIBLE);
                                binding.captchaInputLayout.setVisibility(View.VISIBLE);
                                loadCaptchaImage();
                            }
                        }
                    } else {
                        String errorMessage = "خطای سرور: " + response.code();
                        if (response.errorBody() != null) {
                            try {
                                errorMessage = response.errorBody().string();
                                Log.e(TAG, "Error Body: " + errorMessage);
                            } catch (IOException e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    isLoginInProgress = false;
                    binding.loginButton.setEnabled(true);
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "خطای شبکه: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Network error: " + t.getMessage(), t);
                }
            });
        });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^09\\d{9}$");
    }

    private void loadCaptchaImage() {
        new Thread(() -> {
            apiService.getCaptchaImage().enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            InputStream inputStream = response.body().byteStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            runOnUiThread(() -> binding.captchaImageView.setImageBitmap(bitmap));
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "خطا در بارگذاری تصویر کپچا", Toast.LENGTH_SHORT).show());
                            Log.e(TAG, "Error loading captcha image: " + e.getMessage(), e);
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "خطا در دریافت تصویر کپچا", Toast.LENGTH_SHORT).show());
                        Log.e(TAG, "Captcha image request failed: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "خطای شبکه: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                    Log.e(TAG, "Network error loading captcha: " + t.getMessage(), t);
                }
            });
        }).start();
    }
}