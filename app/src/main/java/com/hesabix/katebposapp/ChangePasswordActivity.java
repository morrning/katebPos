package com.hesabix.katebposapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.hesabix.katebposapp.databinding.ActivityChangePasswordBinding;
import com.hesabix.katebposapp.retrofit.ApiService;
import com.hesabix.katebposapp.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // تنظیم دکمه برگشت در ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("تغییر کلمه عبور");
        }

        // تنظیم OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        binding.changePasswordButton.setOnClickListener(v -> {
            String password = binding.passwordEditText.getText().toString().trim();
            String repeatPassword = binding.repeatPasswordEditText.getText().toString().trim();

            if (validateInputs(password, repeatPassword)) {
                changePassword(password);
            }
        });
    }

    private boolean validateInputs(String password, String repeatPassword) {
        if (password.isEmpty()) {
            binding.passwordLayout.setError("لطفا کلمه عبور را وارد کنید");
            return false;
        }
        binding.passwordLayout.setError(null);

        if (repeatPassword.isEmpty()) {
            binding.repeatPasswordLayout.setError("لطفا تکرار کلمه عبور را وارد کنید");
            return false;
        }
        binding.repeatPasswordLayout.setError(null);

        if (password.length() < 10) {
            binding.passwordLayout.setError("کلمه عبور باید حداقل 10 کاراکتر باشد");
            return false;
        }
        binding.passwordLayout.setError(null);

        if (!password.equals(repeatPassword)) {
            binding.repeatPasswordLayout.setError("کلمه عبور و تکرار آن مطابقت ندارند");
            return false;
        }
        binding.repeatPasswordLayout.setError(null);

        return true;
    }

    private void changePassword(String password) {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pass", password);
            jsonObject.put("repass", password);

            Call<JSONObject> call = apiService.changePassword(jsonObject);
            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject responseBody = response.body();
                            boolean success = responseBody.getBoolean("Success");
                            if (success) {
                                Toast.makeText(ChangePasswordActivity.this, 
                                    "کلمه عبور با موفقیت تغییر یافت", 
                                    Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                String message = responseBody.getString("message");
                                Toast.makeText(ChangePasswordActivity.this, 
                                    "خطا: " + message, 
                                    Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, 
                                "خطا در پردازش پاسخ", 
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, 
                            "خطا در تغییر کلمه عبور", 
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    Toast.makeText(ChangePasswordActivity.this, 
                        "خطا در اتصال: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, 
                "خطا در ارسال درخواست", 
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
} 