package com.hesabix.katebposapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hesabix.katebposapp.adapter.PersonsAdapter;
import com.hesabix.katebposapp.model.Person;
import com.hesabix.katebposapp.model.PersonListRequest;
import com.hesabix.katebposapp.model.PersonListResponse;
import com.hesabix.katebposapp.model.PersonPreLabel;
import com.hesabix.katebposapp.model.PersonType;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saman.zamani.persiandate.PersianDate;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import okhttp3.RequestBody;
import okhttp3.MediaType;

public class PersonsActivity extends AppCompatActivity implements PersonsAdapter.OnPersonClickListener {
    private static final String TAG = "PersonsActivity";
    private RecyclerView rvPersons;
    private ProgressBar progressBar;
    private View loadingContainer;
    private PersonsAdapter adapter;
    private EditText etSearch;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private static final int ITEMS_PER_PAGE = 10;
    private IPrinterApi printerApi;
    private List<PersonPreLabel> preLabels = new ArrayList<>();
    private Person personToPrint = null;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons);

        // راه‌اندازی SDK چاپگر
        initPrinter();
        
        // مقداردهی اولیه پیشوندهای نام
        initPreLabels();

        // تنظیم تولبار
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // مقداردهی اولیه
        rvPersons = findViewById(R.id.rv_persons);
        progressBar = findViewById(R.id.progress_bar);
        loadingContainer = findViewById(R.id.loading_container);
        etSearch = findViewById(R.id.et_search);
        FloatingActionButton fabAddPerson = findViewById(R.id.fab_add_person);

        // تنظیم فیلد جستجو
        setupSearchField();

        // تنظیم RecyclerView
        rvPersons.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PersonsAdapter(this);
        rvPersons.setAdapter(adapter);

        // اضافه کردن اسکرول لیسنر برای لود خودکار صفحه بعدی
        rvPersons.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            loadPersons();
                        }
                    }
                }
            }
        });

        // تنظیم کلیک روی دکمه افزودن
        fabAddPerson.setOnClickListener(v -> {
            Intent intent = new Intent(PersonsActivity.this, PersonEditActivity.class);
            startActivity(intent);
        });

        // دریافت لیست اشخاص
        resetAndLoadPersons();
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

    private void initPreLabels() {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        Call<List<PersonPreLabel>> call = apiService.getPersonPreLabels();
        
        call.enqueue(new Callback<List<PersonPreLabel>>() {
            @Override
            public void onResponse(Call<List<PersonPreLabel>> call, Response<List<PersonPreLabel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    preLabels.clear();
                    preLabels.addAll(response.body());
                    Log.d(TAG, "دریافت موفق لیست پیشوندها. تعداد: " + preLabels.size());
                    
                    // اگر در حال چاپ بودیم، چاپ را ادامه می‌دهیم
                    if (personToPrint != null) {
                        printPersonInfo(personToPrint);
                        personToPrint = null;
                    }
                } else {
                    Log.e(TAG, "خطا در دریافت لیست پیشوندها. کد خطا: " + response.code());
                    Toast.makeText(PersonsActivity.this, "خطا در دریافت لیست پیشوندها", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PersonPreLabel>> call, Throwable t) {
                Log.e(TAG, "خطا در اتصال به سرور برای دریافت لیست پیشوندها", t);
                Toast.makeText(PersonsActivity.this, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void printPersonInfo(Person person) {
        if (printerApi == null) {
            Toast.makeText(this, "لطفاً صبر کنید تا چاپگر آماده شود", Toast.LENGTH_SHORT).show();
            return;
        }

        // بررسی وجود لیست پیشوندها
        if (preLabels.isEmpty()) {
            Toast.makeText(this, "در حال دریافت لیست پیشوندها...", Toast.LENGTH_SHORT).show();
            personToPrint = person;
            initPreLabels();
            return;
        }

        try {
            // آماده‌سازی چاپگر
            printerApi.initPrinter();
            printerApi.setGrayLevel(12);

            // ایجاد داده‌های چاپ
            PrintData printData = new PrintData();
            Typeface vazirTypeface = ResourcesCompat.getFont(this, R.font.vazir);
            printData.setFont(vazirTypeface);

            // هدر
            printData.addTextLine(0, Alignment.CENTER, "=== اطلاعات شخص ===", true, vazirTypeface);
            
            // تاریخ و زمان شمسی
            PersianDate persianDate = new PersianDate();
            String currentDate = persianDate.getShYear() + "/" + 
                               String.format("%02d", persianDate.getShMonth()) + "/" + 
                               String.format("%02d", persianDate.getShDay());
            String currentTime = String.format("%02d", persianDate.getHour()) + ":" + 
                               String.format("%02d", persianDate.getMinute()) + ":" + 
                               String.format("%02d", persianDate.getSecond());
            
            printData.addTextLine(0, Alignment.CENTER, "تاریخ: " + currentDate, true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "ساعت: " + currentTime, true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);

            // اطلاعات شخص
            printData.addTextLine(0, Alignment.RIGHT, "کد: " + person.getCode(), true, vazirTypeface);
            
            // پیشوند نام
            String prelabel = "";
            if (person.getPrelabel() != null) {
                for (PersonPreLabel label : preLabels) {
                    if (label.getCode().equals(person.getPrelabel())) {
                        prelabel = label.getLabel() + " ";
                        break;
                    }
                }
            }
            
            printData.addTextLine(0, Alignment.RIGHT, "نام مستعار: " + prelabel + person.getNikename(), true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, "نام: " + prelabel + person.getName(), true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, "تلفن: " + person.getTel(), true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, "موبایل: " + person.getMobile(), true, vazirTypeface);
            if (person.getMobile2() != null && !person.getMobile2().isEmpty()) {
                printData.addTextLine(0, Alignment.RIGHT, "موبایل ۲: " + person.getMobile2(), true, vazirTypeface);
            }
            if (person.getAddress() != null && !person.getAddress().isEmpty()) {
                printData.addTextLine(0, Alignment.RIGHT, "آدرس: " + person.getAddress(), true, vazirTypeface);
            }
            if (person.getCompany() != null && !person.getCompany().isEmpty()) {
                printData.addTextLine(0, Alignment.RIGHT, "شرکت: " + person.getCompany(), true, vazirTypeface);
            }

            // اطلاعات مالی
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "اطلاعات مالی:", true, vazirTypeface);
            
            double balance = person.getBalance();
            double debtor = person.getBd();
            double creditor = person.getBs();
            
            printData.addTextLine(0, Alignment.RIGHT, String.format("تراز حساب: %,d ریال", (long)balance), true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, String.format("بدهکار: %,d ریال", (long)debtor), true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, String.format("بستانکار: %,d ریال", (long)creditor), true, vazirTypeface);
            
            // نمایش وضعیت بدهکار/بستانکار
            String status = "";
            if (balance > 0) {
                status = "وضعیت: بدهکار";
            } else if (balance < 0) {
                status = "وضعیت: بستانکار";
            } else {
                status = "وضعیت: صفر";
            }
            printData.addTextLine(0, Alignment.RIGHT, status, true, vazirTypeface);

            // انواع شخص
            if (person.getTypes() != null && !person.getTypes().isEmpty()) {
                printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);
                printData.addTextLine(0, Alignment.CENTER, "انواع شخص:", true, vazirTypeface);
                for (PersonType type : person.getTypes()) {
                    if (type.isChecked()) {
                        printData.addTextLine(0, Alignment.RIGHT, "- " + type.getLabel(), true, vazirTypeface);
                    }
                }
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
                        Toast.makeText(PersonsActivity.this, "اطلاعات با موفقیت چاپ شد", Toast.LENGTH_SHORT).show();
                    });
                    printerApi.closePrinter();
                }

                @Override
                public void onError(SdkResultCode sdkResultCode) {
                    runOnUiThread(() -> {
                        Toast.makeText(PersonsActivity.this, "خطا در چاپ: " + sdkResultCode.name(), Toast.LENGTH_LONG).show();
                    });
                    printerApi.closePrinter();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "خطا در چاپ: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
                    resetAndLoadPersons();
                };
                handler.postDelayed(searchRunnable, 500); // تاخیر 500 میلی‌ثانیه
            }
        });
    }

    private void resetAndLoadPersons() {
        currentPage = 1;
        isLastPage = false;
        isLoading = false;
        adapter.clear();
        loadPersons();
    }

    private void loadPersons() {
        if (isLoading || isLastPage) return;
        
        isLoading = true;
        if (currentPage == 1) {
            loadingContainer.setVisibility(View.VISIBLE);
            rvPersons.setVisibility(View.GONE);
            etSearch.setEnabled(false);
        } else {
            adapter.setLoading(true);
        }

        try {
            Log.d(TAG, "شروع دریافت لیست اشخاص - صفحه: " + currentPage);
            ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
            
            PersonListRequest request = new PersonListRequest(
                currentPage,
                ITEMS_PER_PAGE,
                searchQuery,
                null,
                null,
                null
            );

            Log.d(TAG, "درخواست ارسال شده: " + request.toString());

            Call<PersonListResponse> call = apiService.getPersonList(request);
            call.enqueue(new Callback<PersonListResponse>() {
                @Override
                public void onResponse(Call<PersonListResponse> call, Response<PersonListResponse> response) {
                    isLoading = false;
                    adapter.setLoading(false);
                    
                    if (currentPage == 1) {
                        loadingContainer.setVisibility(View.GONE);
                        rvPersons.setVisibility(View.VISIBLE);
                        etSearch.setEnabled(true);
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        PersonListResponse personListResponse = response.body();
                        List<Person> items = personListResponse.getItems();
                        Log.d(TAG, "دریافت موفق لیست اشخاص. تعداد: " + items.size());
                        
                        runOnUiThread(() -> {
                            if (currentPage == 1) {
                                adapter.setPersons(items);
                            } else {
                                adapter.addPersons(items);
                            }

                            // بررسی اینکه آیا صفحه آخر است یا خیر
                            isLastPage = items.size() < ITEMS_PER_PAGE;
                            if (!isLastPage) {
                                currentPage++;
                            }
                        });
                    } else {
                        String errorMessage = "خطا در دریافت لیست اشخاص";
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
                            } catch (IOException e) {
                                Log.e(TAG, "خطا در خواندن متن خطا", e);
                                errorMessage = "خطا در دریافت پاسخ از سرور";
                            }
                        }

                        String finalErrorMessage = errorMessage;
                        runOnUiThread(() -> {
                            Toast.makeText(PersonsActivity.this, finalErrorMessage, Toast.LENGTH_LONG).show();
                            showRetryButton();
                        });
                    }
                }

                @Override
                public void onFailure(Call<PersonListResponse> call, Throwable t) {
                    isLoading = false;
                    adapter.setLoading(false);
                    
                    if (currentPage == 1) {
                        loadingContainer.setVisibility(View.GONE);
                        rvPersons.setVisibility(View.VISIBLE);
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
                        Toast.makeText(PersonsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        showRetryButton();
                    });
                }
            });
        } catch (Exception e) {
            isLoading = false;
            adapter.setLoading(false);
            
            if (currentPage == 1) {
                loadingContainer.setVisibility(View.GONE);
                rvPersons.setVisibility(View.VISIBLE);
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
                resetAndLoadPersons();
            });
        }
    }

    @Override
    public void onPersonClick(Person person) {
        Intent intent = new Intent(this, PersonEditActivity.class);
        intent.putExtra(PersonEditActivity.EXTRA_PERSON_ID, person.getId());
        intent.putExtra(PersonEditActivity.EXTRA_PERSON_CODE, person.getCode());
        startActivity(intent);
    }

    @Override
    public void onPersonLongClick(Person person) {
        // نمایش منوی انتخاب عملیات
        String[] options = {"ویرایش", "چاپ اطلاعات", "چاپ گزارش تراکنش‌ها"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("انتخاب عملیات")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // ویرایش
                        onPersonClick(person);
                    } else if (which == 1) {
                        // چاپ اطلاعات
                        printPersonInfo(person);
                    } else if (which == 2) {
                        // چاپ گزارش تراکنش‌ها
                        printPersonTransactions(person);
                    }
                })
                .show();
    }

    private void printPersonTransactions(Person person) {
        if (printerApi == null) {
            Toast.makeText(this, "لطفاً صبر کنید تا چاپگر آماده شود", Toast.LENGTH_SHORT).show();
            return;
        }

        // نمایش نشانگر پیشرفت
        loadingContainer.setVisibility(View.VISIBLE);
        rvPersons.setVisibility(View.GONE);

        try {
            // ساخت درخواست
            JSONObject requestBody = new JSONObject();
            requestBody.put("type", "person");
            requestBody.put("id", String.valueOf(person.getCode()));

            // تبدیل به RequestBody
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, requestBody.toString());

            // ارسال درخواست به سرور
            ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
            Call<List<JSONObject>> call = apiService.searchAccountingRows(body);
            
            call.enqueue(new Callback<List<JSONObject>>() {
                @Override
                public void onResponse(@NonNull Call<List<JSONObject>> call, @NonNull Response<List<JSONObject>> response) {
                    loadingContainer.setVisibility(View.GONE);
                    rvPersons.setVisibility(View.VISIBLE);

                    if (response.isSuccessful() && response.body() != null) {
                        List<JSONObject> transactions = response.body();
                        printTransactionsReport(person, transactions);
                    } else {
                        String errorMessage = "خطا در دریافت تراکنش‌ها";
                        if (response.errorBody() != null) {
                            try {
                                errorMessage += ": " + response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(PersonsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<JSONObject>> call, Throwable t) {
                    loadingContainer.setVisibility(View.GONE);
                    rvPersons.setVisibility(View.VISIBLE);
                    Toast.makeText(PersonsActivity.this, "خطا در اتصال به سرور: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            loadingContainer.setVisibility(View.GONE);
            rvPersons.setVisibility(View.VISIBLE);
            Toast.makeText(this, "خطا در ساخت درخواست: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void printTransactionsReport(Person person, List<JSONObject> transactions) {
        try {
            // آماده‌سازی چاپگر
            printerApi.initPrinter();
            printerApi.setGrayLevel(12);

            // ایجاد داده‌های چاپ
            PrintData printData = new PrintData();
            Typeface vazirTypeface = ResourcesCompat.getFont(this, R.font.vazir);
            printData.setFont(vazirTypeface);

            // هدر
            printData.addTextLine(0, Alignment.CENTER, "=== گزارش تراکنش‌های شخص ===", true, vazirTypeface);
            
            // تاریخ و زمان شمسی
            PersianDate persianDate = new PersianDate();
            String currentDate = persianDate.getShYear() + "/" + 
                               String.format("%02d", persianDate.getShMonth()) + "/" + 
                               String.format("%02d", persianDate.getShDay());
            String currentTime = String.format("%02d", persianDate.getHour()) + ":" + 
                               String.format("%02d", persianDate.getMinute()) + ":" + 
                               String.format("%02d", persianDate.getSecond());
            
            printData.addTextLine(0, Alignment.CENTER, "تاریخ: " + currentDate, true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "ساعت: " + currentTime, true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);

            // اطلاعات شخص
            printData.addTextLine(0, Alignment.RIGHT, "کد: " + person.getCode(), true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, "نام: " + person.getName(), true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);

            // عنوان ستون‌ها
            printData.addTextLine(0, Alignment.RIGHT, "تاریخ | نوع | شرح | بدهکار | بستانکار | کد", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);

            // چاپ تراکنش‌ها
            double totalDebtor = 0;
            double totalCreditor = 0;

            for (JSONObject transaction : transactions) {
                String date = transaction.optString("date", "");
                String type = transaction.optString("type", "");
                String description = transaction.optString("des", "");
                double debtor = transaction.optDouble("bd", 0);
                double creditor = transaction.optDouble("bs", 0);
                String code = transaction.optString("code", "");

                // تبدیل نوع تراکنش به فارسی
                String persianType = "";
                switch (type) {
                    case "sell":
                        persianType = "فروش";
                        break;
                    case "sell_receive":
                        persianType = "دریافت وجه فاکتور";
                        break;
                    case "buy":
                        persianType = "خرید";
                        break;
                    case "person_receive":
                        persianType = "دریافت از اشخاص";
                        break;
                    case "person_send":
                        persianType = "پرداخت به اشخاص";
                        break;
                    case "cost":
                        persianType = "هزینه";
                        break;
                    default:
                        persianType = type;
                }

                // چاپ هر تراکنش
                String line = String.format("%s | %s | %s | %,d | %,d | %s",
                    date, persianType, description,
                    (long)debtor, (long)creditor, code);
                printData.addTextLine(0, Alignment.RIGHT, line, true, vazirTypeface);

                totalDebtor += debtor;
                totalCreditor += creditor;
            }

            // جمع کل
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, String.format("جمع کل بدهکار: %,d ریال", (long)totalDebtor), true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, String.format("جمع کل بستانکار: %,d ریال", (long)totalCreditor), true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, String.format("تراز حساب: %,d ریال", (long)(totalDebtor - totalCreditor)), true, vazirTypeface);

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
                        Toast.makeText(PersonsActivity.this, "گزارش با موفقیت چاپ شد", Toast.LENGTH_SHORT).show();
                    });
                    printerApi.closePrinter();
                }

                @Override
                public void onError(SdkResultCode sdkResultCode) {
                    runOnUiThread(() -> {
                        Toast.makeText(PersonsActivity.this, "خطا در چاپ: " + sdkResultCode.name(), Toast.LENGTH_LONG).show();
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