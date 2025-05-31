package com.hesabix.katebposapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hesabix.katebposapp.databinding.ActivitySplashBinding;
import com.hesabix.katebposapp.service.PreferencesManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1000; // تاخیر ۱ ثانیه
    private static final String SERVER_HOST = "app.ikateb.ir";
    private static final int SERVER_PORT = 443;
    private static final int TIMEOUT = 5000;
    
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
        setupAnimations();
        checkConnectivityAndProceed();
    }

    private void setupAnimations() {
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
    }

    private void checkConnectivityAndProceed() {
        if (!isNetworkAvailable()) {
            showErrorAndRetry("اتصال به اینترنت برقرار نیست");
            return;
        }

        new Thread(() -> {
            if (isServerReachable()) {
                runOnUiThread(this::proceedToNextScreen);
            } else {
                runOnUiThread(() -> showErrorAndRetry("سرور در دسترس نیست"));
            }
        }).start();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isServerReachable() {
        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(SERVER_HOST, SERVER_PORT);
            socket.connect(socketAddress, TIMEOUT);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void showErrorAndRetry(String message) {
        binding.progressBar.setVisibility(View.GONE);
        binding.loadingText.setText(message);
        binding.retryButton.setVisibility(View.VISIBLE);
        binding.retryButton.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.retryButton.setVisibility(View.GONE);
            binding.loadingText.setText("در حال بررسی اتصال...");
            checkConnectivityAndProceed();
        });
    }

    private void proceedToNextScreen() {
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