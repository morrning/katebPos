package com.hesabix.katebposapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupWebsiteLink();
    }

    private void setupWebsiteLink() {
        TextView tvWebsite = findViewById(R.id.tvWebsite);
        String websiteUrl = "https://kateb.ir";
        SpannableString spannableString = new SpannableString(websiteUrl);
        
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
                startActivity(intent);
            }
        };
        
        spannableString.setSpan(clickableSpan, 0, websiteUrl.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvWebsite.setText(spannableString);
        tvWebsite.setMovementMethod(LinkMovementMethod.getInstance());
    }
}