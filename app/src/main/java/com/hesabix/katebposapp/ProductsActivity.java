package com.hesabix.katebposapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hesabix.katebposapp.adapter.CommoditiesAdapter;
import com.hesabix.katebposapp.model.Commodity;
import com.hesabix.katebposapp.model.CommodityListRequest;
import com.hesabix.katebposapp.model.CommodityListResponse;
import com.hesabix.katebposapp.retrofit.ApiService;
import com.hesabix.katebposapp.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends AppCompatActivity implements CommoditiesAdapter.OnCommodityClickListener {
    private static final String TAG = "ProductsActivity";
    private RecyclerView rvCommodities;
    private ProgressBar progressBar;
    private View loadingContainer;
    private CommoditiesAdapter adapter;
    private EditText etSearch;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private static final int ITEMS_PER_PAGE = 10;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        // تنظیم تولبار
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPersons));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("کالاها");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // مقداردهی اولیه
        rvCommodities = findViewById(R.id.rv_commodities);
        progressBar = findViewById(R.id.progress_bar);
        loadingContainer = findViewById(R.id.loading_container);
        etSearch = findViewById(R.id.et_search);
        FloatingActionButton fabAddCommodity = findViewById(R.id.fab_add_commodity);

        // تنظیم فیلد جستجو
        setupSearchField();

        // تنظیم RecyclerView
        rvCommodities.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommoditiesAdapter(this, this);
        rvCommodities.setAdapter(adapter);

        // اضافه کردن اسکرول لیسنر برای لود خودکار صفحه بعدی
        rvCommodities.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0) {
                            loadCommodities();
                        }
                    }
                }
            }
        });

        // تنظیم کلیک روی دکمه افزودن
        fabAddCommodity.setOnClickListener(v -> {
            // TODO: باز کردن صفحه افزودن کالا
            Toast.makeText(this, "افزودن کالا", Toast.LENGTH_SHORT).show();
        });

        // دریافت لیست کالاها
        resetAndLoadCommodities();
    }

    private void setupSearchField() {
        etSearch.addTextChangedListener(new TextWatcher() {
            private android.os.Handler handler = new android.os.Handler();
            private Runnable searchRunnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    searchQuery = s.toString().trim();
                    resetAndLoadCommodities();
                };
                handler.postDelayed(searchRunnable, 500); // تاخیر 500 میلی‌ثانیه
            }
        });
    }

    private void resetAndLoadCommodities() {
        currentPage = 1;
        isLastPage = false;
        isLoading = false;
        adapter.clear();
        loadCommodities();
    }

    private void loadCommodities() {
        if (isLoading || isLastPage) return;
        
        isLoading = true;
        if (currentPage == 1) {
            loadingContainer.setVisibility(View.VISIBLE);
            rvCommodities.setVisibility(View.GONE);
            etSearch.setEnabled(false);
        } else {
            adapter.setLoading(true);
        }

        try {
            Log.d(TAG, "شروع دریافت لیست کالاها - صفحه: " + currentPage);
            ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
            
            CommodityListRequest request = new CommodityListRequest(
                currentPage,
                ITEMS_PER_PAGE,
                searchQuery
            );

            Log.d(TAG, "درخواست ارسال شده: " + request.toString());

            Call<CommodityListResponse> call = apiService.searchCommodities(request);
            call.enqueue(new Callback<CommodityListResponse>() {
                @Override
                public void onResponse(Call<CommodityListResponse> call, Response<CommodityListResponse> response) {
                    isLoading = false;
                    adapter.setLoading(false);
                    
                    if (currentPage == 1) {
                        loadingContainer.setVisibility(View.GONE);
                        rvCommodities.setVisibility(View.VISIBLE);
                        etSearch.setEnabled(true);
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        CommodityListResponse commodityListResponse = response.body();
                        List<Commodity> items = commodityListResponse.getResults();
                        Log.d(TAG, "دریافت موفق لیست کالاها. تعداد: " + items.size());
                        
                        runOnUiThread(() -> {
                            if (currentPage == 1) {
                                adapter.setCommodities(items);
                            } else {
                                adapter.addCommodities(items);
                            }

                            // بررسی اینکه آیا صفحه آخر است یا خیر
                            isLastPage = items.size() < ITEMS_PER_PAGE;
                            if (!isLastPage) {
                                currentPage++;
                            }
                        });
                    } else {
                        String errorMessage = "خطا در دریافت لیست کالاها";
                        Log.e(TAG, "خطا در پاسخ سرور. کد خطا: " + response.code());
                        
                        if (response.errorBody() != null) {
                            try {
                                String errorBody = response.errorBody().string();
                                Log.e(TAG, "متن خطا: " + errorBody);
                                
                                if (response.code() == 500) {
                                    errorMessage = "خطای داخلی سرور. لطفاً با پشتیبانی تماس بگیرید.";
                                } else if (response.code() == 404) {
                                    errorMessage = "آدرس درخواستی یافت نشد.";
                                } else if (response.code() == 401) {
                                    errorMessage = "دسترسی غیرمجاز. لطفاً مجدداً وارد شوید.";
                                } else if (response.code() == 403) {
                                    errorMessage = "شما دسترسی به این بخش را ندارید.";
                                } else {
                                    errorMessage += ": " + errorBody;
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "خطا در خواندن متن خطا", e);
                                errorMessage = "خطا در دریافت پاسخ از سرور";
                            }
                        }

                        String finalErrorMessage = errorMessage;
                        runOnUiThread(() -> {
                            Toast.makeText(ProductsActivity.this, finalErrorMessage, Toast.LENGTH_LONG).show();
                            showRetryButton();
                        });
                    }
                }

                @Override
                public void onFailure(Call<CommodityListResponse> call, Throwable t) {
                    isLoading = false;
                    adapter.setLoading(false);
                    
                    if (currentPage == 1) {
                        loadingContainer.setVisibility(View.GONE);
                        rvCommodities.setVisibility(View.VISIBLE);
                        etSearch.setEnabled(true);
                    }
                    Log.e(TAG, "خطا در اتصال به سرور", t);
                    
                    String errorMessage;
                    if (t instanceof java.net.UnknownHostException) {
                        errorMessage = "خطا در اتصال به اینترنت. لطفاً اتصال خود را بررسی کنید.";
                    } else if (t instanceof java.net.SocketTimeoutException) {
                        errorMessage = "زمان اتصال به سرور به پایان رسید. لطفاً دوباره تلاش کنید.";
                    } else {
                        errorMessage = "خطا در اتصال به سرور: " + t.getMessage();
                    }
                    
                    runOnUiThread(() -> {
                        Toast.makeText(ProductsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        showRetryButton();
                    });
                }
            });
        } catch (Exception e) {
            isLoading = false;
            adapter.setLoading(false);
            
            if (currentPage == 1) {
                loadingContainer.setVisibility(View.GONE);
                rvCommodities.setVisibility(View.VISIBLE);
                etSearch.setEnabled(true);
            }
            Log.e(TAG, "خطا در ساخت درخواست", e);
            runOnUiThread(() -> {
                Toast.makeText(this, "خطا در ساخت درخواست", Toast.LENGTH_SHORT).show();
                showRetryButton();
            });
        }
    }

    private void showRetryButton() {
        // اضافه کردن دکمه تلاش مجدد به رابط کاربری
        View retryButton = findViewById(R.id.retry_button);
        if (retryButton != null) {
            retryButton.setVisibility(View.VISIBLE);
            retryButton.setOnClickListener(v -> {
                retryButton.setVisibility(View.GONE);
                resetAndLoadCommodities();
            });
        }
    }

    @Override
    public void onCommodityClick(Commodity commodity) {
        // TODO: باز کردن صفحه ویرایش کالا
        Toast.makeText(this, "ویرایش کالا: " + commodity.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCommodityLongClick(Commodity commodity) {
        // نمایش منوی انتخاب عملیات
        String[] options = {"ویرایش", "حذف"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("انتخاب عملیات")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // ویرایش
                        onCommodityClick(commodity);
                    } else if (which == 1) {
                        // حذف
                        // TODO: پیاده‌سازی حذف کالا
                        Toast.makeText(this, "حذف کالا: " + commodity.getName(), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
} 