package com.hesabix.katebposapp;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class CustomCaptureActivity extends CaptureActivity {
    private boolean isFlashOn = false;
    private ImageButton flashButton;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_capture);

        // اضافه کردن دکمه فلش به صفحه
        LinearLayout rootLayout = findViewById(R.id.root_layout);
        flashButton = new ImageButton(this);
        flashButton.setId(View.generateViewId());
        flashButton.setBackgroundResource(R.drawable.flash_button_background);
        flashButton.setImageResource(R.drawable.ic_flash_off);
        flashButton.setContentDescription("فلش");

        // تنظیم پارامترهای دکمه
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 16, 16, 16);
        params.gravity = android.view.Gravity.TOP | android.view.Gravity.END;
        flashButton.setLayoutParams(params);

        // اضافه کردن دکمه به صفحه
        rootLayout.addView(flashButton);

        // تنظیم کلیک لیسنر برای دکمه فلش
        flashButton.setOnClickListener(v -> toggleFlash());
    }

    private void toggleFlash() {
        try {
            if (camera == null) {
                camera = Camera.open();
            }
            Camera.Parameters parameters = camera.getParameters();
            if (isFlashOn) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                flashButton.setImageResource(R.drawable.ic_flash_off);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                flashButton.setImageResource(R.drawable.ic_flash_on);
            }
            camera.setParameters(parameters);
            isFlashOn = !isFlashOn;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
} 