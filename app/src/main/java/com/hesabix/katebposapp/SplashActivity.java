package com.hesabix.katebposapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hesabix.katebposapp.databinding.ActivitySplashBinding;
import com.hesabix.katebposapp.service.PreferencesManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1000; // تاخیر ۱ ثانیه
    private ActivitySplashBinding binding;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // مقداردهی اولیه
        preferencesManager = new PreferencesManager(this);

        // لود انیمیشن‌ها
        try {
            Animation scaleFadeIn = AnimationUtils.loadAnimation(this, R.anim.scale_fade_in);
            Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

            // اعمال انیمیشن‌ها
            binding.splashLogo.startAnimation(scaleFadeIn);
            binding.progressBar.startAnimation(rotate);
            binding.loadingText.startAnimation(fadeIn);
        } catch (Exception e) {
            Toast.makeText(this, "خطا در لود انیمیشن‌ها: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // بررسی توکن و هدایت به صفحه مناسب پس از تاخیر
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String token = preferencesManager.getToken();
            Intent intent;
            if (token != null && !token.isEmpty()) {
                // توکن وجود دارد، هدایت به MainActivity
                intent = new Intent(SplashActivity.this, UserDashboardActivity.class);
            } else {
                // توکن وجود ندارد، هدایت به LoginActivity
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish(); // بستن SplashActivity
        }, SPLASH_DELAY);
    }
}