package com.hesabix.katebposapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.hesabix.katebposapp.adapter.BusinessAdapter;
import com.hesabix.katebposapp.databinding.ActivityUserDashboardBinding;
import com.hesabix.katebposapp.retrofit.ApiService;
import com.hesabix.katebposapp.retrofit.Business;
import com.hesabix.katebposapp.retrofit.RetrofitClient;
import com.hesabix.katebposapp.retrofit.UserInfo;
import com.hesabix.katebposapp.service.PreferencesManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import android.app.AlertDialog;

public class UserDashboardActivity extends AppCompatActivity implements BusinessAdapter.OnBusinessClickListener {
    private ActivityUserDashboardBinding binding;
    private BusinessAdapter businessAdapter;
    private List<Business> businessList = new ArrayList<>();
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // مدیریت Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // تنظیم OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });

        // مقداردهی PreferencesManager
        preferencesManager = new PreferencesManager(this);

        // تنظیم Toolbar
        setSupportActionBar(binding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // تنظیم RecyclerView
        binding.businessesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        businessAdapter = new BusinessAdapter(businessList, this);
        binding.businessesRecyclerView.setAdapter(businessAdapter);

        // تنظیم اطلاعات کاربر
        setupUserInfo();

        // تنظیم کلیک‌های Navigation Drawer
        setupNavigationDrawer();

        // تنظیم کلیک‌های FAB و منوی کسب و کار جدید
        setupClickListeners();

        // دریافت لیست کسب‌وکارها
        fetchBusinesses();
    }

    private void setupClickListeners() {
        // FAB Click Listener
        binding.fabAddBusiness.setOnClickListener(v -> navigateToNewBusiness());
    }

    private void navigateToNewBusiness() {
        Intent intent = new Intent(this, NewBusinessActivity.class);
        startActivity(intent);
    }

    private void setupUserInfo() {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        Call<UserInfo> call = apiService.getUserInfo();
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserInfo userInfo = response.body();
                    // نمایش نام کاربر
                    binding.userName.setText(userInfo.getFullname());
                    // نمایش شماره تلفن به جای ایمیل
                    binding.userEmail.setText(userInfo.getMobile());
                    
                    // به‌روزرسانی اطلاعات در منوی کشویی
                    TextView navUserName = binding.navView.findViewById(R.id.nav_user_name);
                    TextView navUserEmail = binding.navView.findViewById(R.id.nav_user_email);
                    if (navUserName != null) {
                        navUserName.setText(userInfo.getFullname());
                    }
                    if (navUserEmail != null) {
                        navUserEmail.setText(userInfo.getMobile());
                    }
                } else {
                    Toast.makeText(UserDashboardActivity.this,
                        "خطا در دریافت اطلاعات کاربر: " + response.message(), 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                Toast.makeText(UserDashboardActivity.this,
                    "خطا در اتصال: " + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNavigationDrawer() {
        binding.navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.nav_new_business) {
                // Handle new business click
                startActivity(new Intent(this, NewBusinessActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_businesses) {
                // Handle businesses click
                startActivity(new Intent(this, UserDashboardActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_change_password) {
                // Handle change password click
                startActivity(new Intent(this, ChangePasswordActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_support) {
                // باز کردن مرورگر و هدایت به صفحه پشتیبانی
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://abr.ikateb.ir"));
                startActivity(intent);
            } else if (id == R.id.nav_about) {
                // Handle about click
                startActivity(new Intent(this, AboutActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_printer_test) {
                // باز کردن صفحه آزمایش چاپگر
                startActivity(new Intent(this, PrinterTestActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_logout) {
                // Handle logout click
                showLogoutDialog();
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            
            return false;
        });
    }

    private void fetchBusinesses() {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        Call<List<Business>> call = apiService.getBusinesses();
        call.enqueue(new Callback<List<Business>>() {
            @Override
            public void onResponse(@NonNull Call<List<Business>> call, @NonNull Response<List<Business>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    businessList = response.body();
                    businessAdapter.updateList(businessList);
                } else {
                    Toast.makeText(UserDashboardActivity.this, "خطا در دریافت داده‌ها: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Business>> call, @NonNull Throwable t) {
                Toast.makeText(UserDashboardActivity.this, "خطا در اتصال: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onBusinessClick(Business business) {
        // ذخیره اطلاعات کسب‌وکار انتخاب‌شده در PreferencesManager
        preferencesManager.saveSelectedBusinessId(business.getId());
        preferencesManager.saveSelectedBusinessName(business.getName());
        preferencesManager.saveSelectedBusinessLegalName(business.getLegalName());
        preferencesManager.saveSelectedBusinessField(business.getField());
        preferencesManager.saveSelectedBusinessNationalId(business.getNationalId());
        preferencesManager.saveSelectedBusinessEconomicCode(business.getEconomicCode());
        preferencesManager.saveSelectedBusinessRegistrationNumber(business.getRegistrationNumber());
        preferencesManager.saveSelectedBusinessAddress(business.getAddress());
        preferencesManager.saveSelectedBusinessTel(business.getTel());
        preferencesManager.saveSelectedBusinessMobile(business.getMobile());
        preferencesManager.saveSelectedBusinessVat(business.getVat());
        preferencesManager.saveSelectedBusinessMainCurrency(business.getMainCurrency() != null ? business.getMainCurrency().getLabel() : "");
        preferencesManager.saveSelectedBusinessWalletEnabled(business.isWalletEnabled());
        preferencesManager.saveSelectedBusinessWalletMatchBank(business.getWalletMatchBank());
        preferencesManager.saveSelectedBusinessUpdateSellPrice(business.getUpdateSellPrice() != null ? business.getUpdateSellPrice() : false);
        preferencesManager.saveSelectedBusinessUpdateBuyPrice(business.isUpdateBuyPrice());
        preferencesManager.saveSelectedBusinessProfitCalcType(business.getProfitCalcType());
        
        // انتقال به صفحه داشبورد کسب و کار
        Intent intent = new Intent(this, BusinessDashboardActivity.class);
        intent.putExtra("business_id", business.getId());
        intent.putExtra("business_name", business.getName());
        startActivity(intent);
    }
}