package com.hesabix.katebposapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hesabix.katebposapp.adapter.SellDocsAdapter;
import com.hesabix.katebposapp.model.SellDoc;
import com.hesabix.katebposapp.model.SellDocListRequest;
import com.hesabix.katebposapp.model.SellDocListResponse;
import com.hesabix.katebposapp.retrofit.ApiService;
import com.hesabix.katebposapp.retrofit.RetrofitClient;
import com.smartpos.sdk.SdkManager;
import com.smartpos.sdk.api.IPrinterApi;
import com.smartpos.sdk.api.PrinterListener;
import com.smartpos.sdk.api.SdkListener;
import com.smartpos.sdk.constant.Alignment;
import com.smartpos.sdk.constant.SdkResultCode;
import com.smartpos.sdk.entity.PrintData;
import com.smartpos.sdk.entity.SdkResult;
import saman.zamani.persiandate.PersianDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoicesActivity extends AppCompatActivity implements SellDocsAdapter.OnSellDocClickListener {

    private RecyclerView rvInvoices;
    private ProgressBar progressBar;
    private LinearLayout loadingContainer;
    private Button retryButton;
    private EditText etSearch;
    private FloatingActionButton fabAddInvoice;

    private SellDocsAdapter adapter;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String currentSearch = "";

    private IPrinterApi printerApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);

        // راه‌اندازی SDK چاپگر
        initPrinter();

        // تنظیم تولبار
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("فاکتورها");

        // مقداردهی اولیه
        rvInvoices = findViewById(R.id.rv_invoices);
        progressBar = findViewById(R.id.progress_bar);
        loadingContainer = findViewById(R.id.loading_container);
        retryButton = findViewById(R.id.retry_button);
        etSearch = findViewById(R.id.et_search);
        fabAddInvoice = findViewById(R.id.fab_add_invoice);

        // تنظیم RecyclerView
        rvInvoices.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SellDocsAdapter(this, this);
        rvInvoices.setAdapter(adapter);

        // تنظیم جستجو
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearch = s.toString();
                resetAndLoadInvoices();
            }
        });

        // تنظیم دکمه تلاش مجدد
        retryButton.setOnClickListener(v -> resetAndLoadInvoices());

        // تنظیم دکمه افزودن فاکتور
        fabAddInvoice.setOnClickListener(v -> {
            Toast.makeText(this, "افزودن فاکتور جدید", Toast.LENGTH_SHORT).show();
            // TODO: باز کردن صفحه افزودن فاکتور جدید
        });

        // لود کردن لیست فاکتورها
        resetAndLoadInvoices();
    }

    private void initPrinter() {
        SdkManager.getInstance().init(getApplicationContext(), new SdkListener() {
            @Override
            public void onResult(SdkResult sdkResult) {
                if (sdkResult.isSuccess()) {
                    printerApi = SdkManager.getInstance().getPrinterManager();
                }
            }
        });
    }

    private void resetAndLoadInvoices() {
        currentPage = 1;
        isLastPage = false;
        adapter.clear();
        loadInvoices();
    }

    private void loadInvoices() {
        if (isLoading || isLastPage) return;

        isLoading = true;
        adapter.setLoading(true);

        SellDocListRequest request = new SellDocListRequest(
            "sell",
            currentSearch,
            currentPage,
            20,
            new ArrayList<>(),
            null,
            new ArrayList<>()
        );

        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        Call<SellDocListResponse> call = apiService.searchSellDocs(request);
        call.enqueue(new Callback<SellDocListResponse>() {
            @Override
            public void onResponse(Call<SellDocListResponse> call, Response<SellDocListResponse> response) {
                isLoading = false;
                adapter.setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    SellDocListResponse listResponse = response.body();
                    List<SellDoc> sellDocs = listResponse.getItems();

                    if (currentPage == 1) {
                        adapter.setSellDocs(sellDocs);
                    } else {
                        adapter.addSellDocs(sellDocs);
                    }

                    if (sellDocs.size() < 20) {
                        isLastPage = true;
                    } else {
                        currentPage++;
                    }

                    if (currentPage == 1 && sellDocs.isEmpty()) {
                        Toast.makeText(InvoicesActivity.this, "فاکتوری یافت نشد", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showRetryButton();
                    Toast.makeText(InvoicesActivity.this, "خطا در دریافت لیست فاکتورها", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SellDocListResponse> call, Throwable t) {
                isLoading = false;
                adapter.setLoading(false);
                showRetryButton();
                Toast.makeText(InvoicesActivity.this, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRetryButton() {
        loadingContainer.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSellDocClick(SellDoc sellDoc) {
        Toast.makeText(this, "فاکتور " + sellDoc.getCode(), Toast.LENGTH_SHORT).show();
        // TODO: باز کردن صفحه جزئیات فاکتور
    }

    @Override
    public void onSellDocLongClick(SellDoc sellDoc) {
        String[] options = {"ویرایش", "حذف", "چاپ"};
        
        new AlertDialog.Builder(this)
            .setTitle("عملیات فاکتور")
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0: // ویرایش
                        Toast.makeText(this, "ویرایش فاکتور " + sellDoc.getCode(), Toast.LENGTH_SHORT).show();
                        // TODO: باز کردن صفحه ویرایش فاکتور
                        break;
                        
                    case 1: // حذف
                        showDeleteConfirmationDialog(sellDoc);
                        break;
                        
                    case 2: // چاپ
                        printInvoice(sellDoc.getCode());
                        break;
                }
            })
            .show();
    }

    private void showDeleteConfirmationDialog(SellDoc sellDoc) {
        new AlertDialog.Builder(this)
            .setTitle("حذف فاکتور")
            .setMessage("آیا از حذف فاکتور " + sellDoc.getCode() + " اطمینان دارید؟")
            .setPositiveButton("بله", (dialog, which) -> {
                deleteInvoice(sellDoc.getCode());
            })
            .setNegativeButton("خیر", null)
            .show();
    }

    private void deleteInvoice(String code) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("code", code);

            ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
            Call<JSONObject> call = apiService.removeInvoice(requestBody);
            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject result = response.body();
                            int resultCode = result.getInt("result");
                            
                            if (resultCode == 1) {
                                Toast.makeText(InvoicesActivity.this, "فاکتور با موفقیت حذف شد", Toast.LENGTH_SHORT).show();
                                resetAndLoadInvoices(); // بارگذاری مجدد لیست
                            } else {
                                String message = result.has("message") ? result.getString("message") : "خطا در حذف فاکتور";
                                Toast.makeText(InvoicesActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(InvoicesActivity.this, "خطا در پردازش پاسخ سرور", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(InvoicesActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    Toast.makeText(InvoicesActivity.this, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            Toast.makeText(this, "خطا در ساخت درخواست", Toast.LENGTH_SHORT).show();
        }
    }

    private void printInvoice(String code) {
        if (printerApi == null) {
            Toast.makeText(this, "لطفاً صبر کنید تا چاپگر آماده شود", Toast.LENGTH_SHORT).show();
            return;
        }

        // نمایش نشانگر پیشرفت
        loadingContainer.setVisibility(View.VISIBLE);
        rvInvoices.setVisibility(View.GONE);

        // دریافت اطلاعات فاکتور
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        Call<JSONObject> call = apiService.getInvoiceInfo(code);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                loadingContainer.setVisibility(View.GONE);
                rvInvoices.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject invoice = response.body();
                        printInvoiceData(invoice);
                    } catch (JSONException e) {
                        Toast.makeText(InvoicesActivity.this, "خطا در پردازش اطلاعات فاکتور", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InvoicesActivity.this, "خطا در دریافت اطلاعات فاکتور", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                loadingContainer.setVisibility(View.GONE);
                rvInvoices.setVisibility(View.VISIBLE);
                Toast.makeText(InvoicesActivity.this, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void printInvoiceData(JSONObject invoice) throws JSONException {
        try {
            // آماده‌سازی چاپگر
            printerApi.initPrinter();
            printerApi.setGrayLevel(12);

            // ایجاد داده‌های چاپ
            PrintData printData = new PrintData();
            Typeface vazirTypeface = ResourcesCompat.getFont(this, R.font.vazir);
            printData.setFont(vazirTypeface);

            // هدر
            printData.addTextLine(0, Alignment.CENTER, "=== فاکتور فروش ===", true, vazirTypeface);
            
            // تاریخ و زمان شمسی
            PersianDate persianDate = new PersianDate();
            String currentDate = persianDate.getShYear() + "/" + 
                               String.format("%02d", persianDate.getShMonth()) + "/" + 
                               String.format("%02d", persianDate.getShDay());
            String currentTime = String.format("%02d", persianDate.getHour()) + ":" + 
                               String.format("%02d", persianDate.getMinute()) + ":" + 
                               String.format("%02d", persianDate.getSecond());
            
            printData.addTextLine(0, Alignment.CENTER, "تاریخ چاپ: " + currentDate, true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "ساعت چاپ: " + currentTime, true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);

            // اطلاعات فاکتور و مشتری در دو ستون
            printData.addTextLine(0, Alignment.RIGHT, "شماره فاکتور: " + invoice.optString("code", ""), true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, "تاریخ فاکتور: " + invoice.optString("date", ""), true, vazirTypeface);
            
            // اطلاعات مشتری در سمت چپ
            JSONObject person = invoice.optJSONObject("person");
            if (person != null) {
                String prelabel = person.optString("prelabel", "");
                String nikename = person.optString("nikename", "");
                String mobile = person.optString("mobile", "");
                String address = person.optString("address", "");
                
                printData.addTextLine(0, Alignment.LEFT, "نام مستعار: " + prelabel + " " + nikename, true, vazirTypeface);
                if (!mobile.isEmpty()) {
                    printData.addTextLine(0, Alignment.LEFT, "موبایل: " + mobile, true, vazirTypeface);
                }
                if (!address.isEmpty()) {
                    printData.addTextLine(0, Alignment.LEFT, "آدرس: " + address, true, vazirTypeface);
                }
            }

            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);

            // عنوان ستون‌ها
            printData.addTextLine(0, Alignment.RIGHT, "ردیف | نام کالا | تعداد | قیمت واحد | قیمت کل", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);

            // چاپ اقلام فاکتور
            JSONArray rows = invoice.optJSONArray("rows");
            int rowNumber = 1;
            double totalAmount = 0;

            if (rows != null && rows.length() > 0) {
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject row = rows.getJSONObject(i);
                    JSONObject commodity = row.optJSONObject("commodity");
                    
                    if (commodity != null) {
                        String name = commodity.optString("name", "");
                        int count = commodity.optInt("count", 0);
                        double priceSell = commodity.optDouble("priceSell", 0);
                        double rowTotal = count * priceSell;
                        totalAmount += rowTotal;

                        String line = String.format("%d | %s | %d | %,d | %,d",
                            rowNumber++,
                            name,
                            count,
                            (long)priceSell,
                            (long)rowTotal);
                        printData.addTextLine(0, Alignment.RIGHT, line, true, vazirTypeface);
                    }
                }
            } else {
                printData.addTextLine(0, Alignment.CENTER, "فاکتور خالی است", true, vazirTypeface);
            }

            // جمع کل
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, String.format("جمع کل: %,d ریال", (long)totalAmount), true, vazirTypeface);

            // مالیات و تخفیف
            double tax = invoice.optDouble("tax", 0);
            double discount = invoice.optDouble("discountAll", 0);
            
            if (tax > 0) {
                printData.addTextLine(0, Alignment.RIGHT, String.format("مالیات: %,d ریال", (long)tax), true, vazirTypeface);
            }
            if (discount > 0) {
                printData.addTextLine(0, Alignment.RIGHT, String.format("تخفیف: %,d ریال", (long)discount), true, vazirTypeface);
            }

            // مبلغ قابل پرداخت
            double finalAmount = totalAmount + tax - discount;
            printData.addTextLine(0, Alignment.RIGHT, String.format("مبلغ قابل پرداخت: %,d ریال", (long)finalAmount), true, vazirTypeface);

            // اطلاعات ثبت کننده
            JSONObject submiter = invoice.optJSONObject("submiter");
            if (submiter != null) {
                printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);
                printData.addTextLine(0, Alignment.RIGHT, "ثبت کننده: " + submiter.optString("name", ""), true, vazirTypeface);
            }

            // فوتر
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "حسابداری کاتب", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "https://ikateb.ir", true, vazirTypeface);

            // خوراک کاغذ با فاصله بیشتر
            printData.feedLine(120);

            // شروع چاپ
            printerApi.startPrint(printData, new PrinterListener() {
                @Override
                public void onComplete() {
                    runOnUiThread(() -> {
                        Toast.makeText(InvoicesActivity.this, "فاکتور با موفقیت چاپ شد", Toast.LENGTH_SHORT).show();
                    });
                    printerApi.closePrinter();
                }

                @Override
                public void onError(SdkResultCode sdkResultCode) {
                    runOnUiThread(() -> {
                        Toast.makeText(InvoicesActivity.this, "خطا در چاپ: " + sdkResultCode.name(), Toast.LENGTH_LONG).show();
                    });
                    printerApi.closePrinter();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "خطا در چاپ: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (printerApi != null) {
            printerApi.closePrinter();
        }
    }
}