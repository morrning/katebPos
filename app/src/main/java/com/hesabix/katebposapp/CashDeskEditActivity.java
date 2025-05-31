package com.hesabix.katebposapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hesabix.katebposapp.model.CashDesk;
import com.hesabix.katebposapp.model.ApiResponse;
import com.hesabix.katebposapp.retrofit.ApiService;
import com.hesabix.katebposapp.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashDeskEditActivity extends AppCompatActivity {
    private static final String TAG = "CashDeskEditActivity";
    private EditText etCode, etName, etDescription;
    private Button btnSave;
    private ProgressBar progressBar;
    private String cashDeskCode;
    private boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_desk_edit);

        // تنظیم تولبار
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPersons));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // مقداردهی اولیه
        setupViews();

        // دریافت اطلاعات از اکتیویتی قبلی
        cashDeskCode = getIntent().getStringExtra("cash_desk_code");
        isEditMode = cashDeskCode != null;

        if (isEditMode) {
            getSupportActionBar().setTitle("ویرایش صندوق");
            loadCashDeskInfo();
        } else {
            getSupportActionBar().setTitle("افزودن صندوق");
        }
    }

    private void setupViews() {
        etCode = findViewById(R.id.et_code);
        etName = findViewById(R.id.et_name);
        etDescription = findViewById(R.id.et_description);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);

        // غیرفعال کردن فیلد کد در هر حالت
        etCode.setEnabled(false);
        
        // نمایش فیلد کد فقط در حالت ویرایش
        if (isEditMode) {
            etCode.setVisibility(View.VISIBLE);
        } else {
            etCode.setVisibility(View.GONE);
        }
        
        btnSave.setOnClickListener(v -> saveCashDesk());
    }

    private void loadCashDeskInfo() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        Call<CashDesk> call = apiService.getCashDeskInfo(cashDeskCode);
        call.enqueue(new Callback<CashDesk>() {
            @Override
            public void onResponse(Call<CashDesk> call, Response<CashDesk> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    CashDesk cashDesk = response.body();
                    etCode.setText(cashDesk.getCode());
                    etName.setText(cashDesk.getName());
                    etDescription.setText(cashDesk.getDes());
                } else {
                    Toast.makeText(CashDeskEditActivity.this, "خطا در دریافت اطلاعات صندوق", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CashDesk> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(CashDeskEditActivity.this, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveCashDesk() {
        String code = etCode.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "لطفاً نام صندوق را وارد کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode && code.isEmpty()) {
            Toast.makeText(this, "لطفاً کد صندوق را وارد کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        CashDesk cashDesk = new CashDesk();
        cashDesk.setCode(code);
        cashDesk.setName(name);
        cashDesk.setDes(description);

        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        Call<ApiResponse> call = apiService.modifyCashDesk(code, cashDesk);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getResult() == 1) {
                        Toast.makeText(CashDeskEditActivity.this, 
                            isEditMode ? "صندوق با موفقیت ویرایش شد" : "صندوق با موفقیت افزوده شد", 
                            Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(CashDeskEditActivity.this, "خطا در ذخیره اطلاعات", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CashDeskEditActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(CashDeskEditActivity.this, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 