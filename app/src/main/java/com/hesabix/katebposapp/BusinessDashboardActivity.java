package com.hesabix.katebposapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.hesabix.katebposapp.service.PreferencesManager;
import com.hesabix.katebposapp.retrofit.ApiService;
import com.hesabix.katebposapp.retrofit.RetrofitClient;
import com.hesabix.katebposapp.retrofit.UserInfo;
import com.hesabix.katebposapp.model.BusinessInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_dashboard);

        // مقداردهی اولیه
        preferencesManager = new PreferencesManager(this);

        // تنظیم تولبار
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // تنظیم منوی کشویی
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_persons) {
                Intent intent = new Intent(BusinessDashboardActivity.this, PersonsActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
                return true;
            }
            // سایر گزینه‌های منو
            return false;
        });

        // اضافه کردن دکمه همبرگر به تولبار
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // تنظیم اطلاعات کسب و کار
        setupBusinessInfo();

        // تنظیم اطلاعات کاربر
        setupUserInfo();

        // تنظیم رویداد کلیک برای دکمه‌های داشبورد
        setupDashboardButtons();

        // تنظیم دکمه بازگشت
        setupBackButton();
    }

    private void setupBackButton() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });
    }

    private void setupBusinessInfo() {
        TextView tvBusinessName = findViewById(R.id.tv_business_name);
        String businessName = preferencesManager.getSelectedBusinessName();
        tvBusinessName.setText(businessName);

        // دریافت اطلاعات کسب و کار
        int businessId = preferencesManager.getSelectedBusinessId();
        if (businessId != -1) {
            ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
            Call<BusinessInfo> call = apiService.getBusinessInfo(businessId);
            call.enqueue(new Callback<BusinessInfo>() {
                @Override
                public void onResponse(@NonNull Call<BusinessInfo> call, @NonNull Response<BusinessInfo> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        BusinessInfo businessInfo = response.body();
                        
                        // ذخیره تنظیمات
                        preferencesManager.setActiveBusinessId(businessInfo.getId());
                        preferencesManager.setActiveMoney(businessInfo.getArzmain().getId());
                        preferencesManager.setActiveYear(businessInfo.getYear().getId());
                        
                        // به‌روزرسانی اطلاعات در رابط کاربری
                        tvBusinessName.setText(businessInfo.getName());
                    } else {
                        Toast.makeText(BusinessDashboardActivity.this,
                            "خطا در دریافت اطلاعات کسب و کار: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BusinessInfo> call, @NonNull Throwable t) {
                    Toast.makeText(BusinessDashboardActivity.this,
                        "خطا در اتصال: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupUserInfo() {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        Call<UserInfo> call = apiService.getUserInfo();
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserInfo userInfo = response.body();
                    
                    // به‌روزرسانی اطلاعات در منوی کشویی
                    View headerView = navigationView.getHeaderView(0);
                    TextView navUserName = headerView.findViewById(R.id.nav_user_name);
                    TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);
                    
                    if (navUserName != null) {
                        navUserName.setText(userInfo.getFullname());
                    }
                    if (navUserEmail != null) {
                        navUserEmail.setText(userInfo.getMobile());
                    }
                } else {
                    Toast.makeText(BusinessDashboardActivity.this,
                        "خطا در دریافت اطلاعات کاربر: " + response.message(), 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                Toast.makeText(BusinessDashboardActivity.this,
                    "خطا در اتصال: " + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDashboardButtons() {
        // دکمه اشخاص
        findViewById(R.id.btn_persons).setOnClickListener(v -> {
            Intent intent = new Intent(BusinessDashboardActivity.this, PersonsActivity.class);
            startActivity(intent);
        });

        // دکمه کالاها
        findViewById(R.id.btn_products).setOnClickListener(v -> {
            Intent intent = new Intent(BusinessDashboardActivity.this, ProductsActivity.class);
            startActivity(intent);
        });

        // دکمه صندوق
        findViewById(R.id.btn_cash_register).setOnClickListener(v -> {
            Intent intent = new Intent(BusinessDashboardActivity.this, CashDesksActivity.class);
            startActivity(intent);
        });

        // دکمه فاکتورها
        findViewById(R.id.btn_invoices).setOnClickListener(v -> {
            Intent intent = new Intent(BusinessDashboardActivity.this, InvoicesActivity.class);
            startActivity(intent);
        });

        // دکمه فاکتور جدید
        findViewById(R.id.btn_new_invoice).setOnClickListener(v -> {
            Intent intent = new Intent(BusinessDashboardActivity.this, NewInvoiceActivity.class);
            startActivity(intent);
        });

        // دکمه گزارشات
        findViewById(R.id.btn_reports).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("هشدار")
                .setMessage("چاپگر فعالی یافت نشد")
                .setPositiveButton("باشه", null)
                .show();
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // در حال حاضر در داشبورد هستیم
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_persons) {
            // باز کردن صفحه اشخاص
            Intent intent = new Intent(BusinessDashboardActivity.this, PersonsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_products) {
            // باز کردن صفحه کالاها
            Intent intent = new Intent(BusinessDashboardActivity.this, ProductsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_cash_register) {
            // باز کردن صفحه صندوق
            Intent intent = new Intent(BusinessDashboardActivity.this, CashDesksActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_invoices) {
            // باز کردن صفحه فاکتورها
            Intent intent = new Intent(BusinessDashboardActivity.this, InvoicesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_new_invoice) {
            // باز کردن صفحه فاکتور جدید
            Intent intent = new Intent(BusinessDashboardActivity.this, NewInvoiceActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_reports) {
            // باز کردن صفحه گزارشات
            Toast.makeText(this, "گزارشات", Toast.LENGTH_SHORT).show();
            // TODO: باز کردن صفحه گزارشات
        } else if (id == R.id.nav_change_password) {
            // باز کردن صفحه تغییر کلمه عبور
            startActivity(new Intent(this, ChangePasswordActivity.class));
        } else if (id == R.id.nav_support) {
            // باز کردن مرورگر و هدایت به صفحه پشتیبانی
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://abr.ikateb.ir"));
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            // باز کردن صفحه درباره ما
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.nav_logout) {
            // نمایش دیالوگ خروج
            showLogoutDialog();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("خروج")
            .setMessage("آیا از خروج از حساب کاربری خود اطمینان دارید؟")
            .setPositiveButton("بله", (dialog, which) -> {
                // پاک کردن تمام اطلاعات کاربر
                preferencesManager.clearToken();
                preferencesManager.clearUserInfo();
                preferencesManager.clearSelectedBusinessId();
                preferencesManager.clearAllPreferences();
                
                // انتقال به صفحه ورود
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("خیر", null)
            .show();
    }
}