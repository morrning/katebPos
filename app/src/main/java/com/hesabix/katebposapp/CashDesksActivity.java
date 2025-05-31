package com.hesabix.katebposapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hesabix.katebposapp.adapter.CashDesksAdapter;
import com.hesabix.katebposapp.model.CashDesk;
import com.hesabix.katebposapp.retrofit.ApiService;
import com.hesabix.katebposapp.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashDesksActivity extends AppCompatActivity implements CashDesksAdapter.OnCashDeskClickListener {
    private static final String TAG = "CashDesksActivity";
    private RecyclerView rvCashDesks;
    private ProgressBar progressBar;
    private View loadingContainer;
    private CashDesksAdapter adapter;

    private static final int REQUEST_ADD_CASH_DESK = 1001;
    private static final int REQUEST_EDIT_CASH_DESK = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_desks);

        // تنظیم تولبار
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPersons));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("صندوق‌ها");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // مقداردهی اولیه
        rvCashDesks = findViewById(R.id.rv_cash_desks);
        progressBar = findViewById(R.id.progress_bar);
        loadingContainer = findViewById(R.id.loading_container);
        FloatingActionButton fabAddCashDesk = findViewById(R.id.fab_add_cash_desk);

        // تنظیم RecyclerView
        rvCashDesks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CashDesksAdapter(this, this);
        rvCashDesks.setAdapter(adapter);

        // تنظیم کلیک روی دکمه افزودن
        fabAddCashDesk.setOnClickListener(v -> {
            Intent intent = new Intent(this, CashDeskEditActivity.class);
            startActivityForResult(intent, REQUEST_ADD_CASH_DESK);
        });

        // دریافت لیست صندوق‌ها
        loadCashDesks();
    }

    private void loadCashDesks() {
        loadingContainer.setVisibility(View.VISIBLE);
        rvCashDesks.setVisibility(View.GONE);

        try {
            Log.d(TAG, "شروع دریافت لیست صندوق‌ها");
            ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
            
            Call<List<CashDesk>> call = apiService.getCashDesks();
            call.enqueue(new Callback<List<CashDesk>>() {
                @Override
                public void onResponse(Call<List<CashDesk>> call, Response<List<CashDesk>> response) {
                    loadingContainer.setVisibility(View.GONE);
                    rvCashDesks.setVisibility(View.VISIBLE);

                    if (response.isSuccessful() && response.body() != null) {
                        List<CashDesk> cashDesks = response.body();
                        Log.d(TAG, "دریافت موفق لیست صندوق‌ها. تعداد: " + cashDesks.size());
                        adapter.setCashDesks(cashDesks);
                    } else {
                        String errorMessage = "خطا در دریافت لیست صندوق‌ها";
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

                        Toast.makeText(CashDesksActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        showRetryButton();
                    }
                }

                @Override
                public void onFailure(Call<List<CashDesk>> call, Throwable t) {
                    loadingContainer.setVisibility(View.GONE);
                    rvCashDesks.setVisibility(View.VISIBLE);
                    Log.e(TAG, "خطا در اتصال به سرور", t);
                    
                    String errorMessage;
                    if (t instanceof java.net.UnknownHostException) {
                        errorMessage = "خطا در اتصال به اینترنت. لطفاً اتصال خود را بررسی کنید.";
                    } else if (t instanceof java.net.SocketTimeoutException) {
                        errorMessage = "زمان اتصال به سرور به پایان رسید. لطفاً دوباره تلاش کنید.";
                    } else {
                        errorMessage = "خطا در اتصال به سرور: " + t.getMessage();
                    }
                    
                    Toast.makeText(CashDesksActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    showRetryButton();
                }
            });
        } catch (Exception e) {
            loadingContainer.setVisibility(View.GONE);
            rvCashDesks.setVisibility(View.VISIBLE);
            Log.e(TAG, "خطا در ساخت درخواست", e);
            Toast.makeText(this, "خطا در ساخت درخواست", Toast.LENGTH_SHORT).show();
            showRetryButton();
        }
    }

    private void showRetryButton() {
        View retryButton = findViewById(R.id.retry_button);
        if (retryButton != null) {
            retryButton.setVisibility(View.VISIBLE);
            retryButton.setOnClickListener(v -> {
                retryButton.setVisibility(View.GONE);
                loadCashDesks();
            });
        }
    }

    @Override
    public void onCashDeskClick(CashDesk cashDesk) {
        Intent intent = new Intent(this, CashDeskEditActivity.class);
        intent.putExtra("cash_desk_code", cashDesk.getCode());
        startActivityForResult(intent, REQUEST_EDIT_CASH_DESK);
    }

    @Override
    public void onCashDeskLongClick(CashDesk cashDesk) {
        // نمایش منوی انتخاب عملیات
        String[] options = {"ویرایش", "حذف"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("انتخاب عملیات")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // ویرایش
                        onCashDeskClick(cashDesk);
                    } else if (which == 1) {
                        // حذف
                        // TODO: پیاده‌سازی حذف صندوق
                        Toast.makeText(this, "حذف صندوق: " + cashDesk.getName(), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == REQUEST_ADD_CASH_DESK || requestCode == REQUEST_EDIT_CASH_DESK)) {
            loadCashDesks();
        }
    }
} 