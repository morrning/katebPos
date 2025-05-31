package com.hesabix.katebposapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface;
import android.content.pm.ActivityInfo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hesabix.katebposapp.adapters.CommoditySearchAdapter;
import com.hesabix.katebposapp.adapters.CustomerSearchAdapter;
import com.hesabix.katebposapp.adapters.InvoiceItemAdapter;
import com.hesabix.katebposapp.model.Commodity;
import com.hesabix.katebposapp.model.CommoditySearchRequest;
import com.hesabix.katebposapp.model.InvoiceItem;
import com.hesabix.katebposapp.model.Person;
import com.hesabix.katebposapp.model.PersonListRequest;
import com.hesabix.katebposapp.model.PersonListResponse;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import saman.zamani.persiandate.PersianDate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

public class NewInvoiceActivity extends AppCompatActivity implements InvoiceItemAdapter.OnItemClickListener {
    private AutoCompleteTextView customerSearchAutoComplete;
    private AutoCompleteTextView commoditySearchAutoComplete;
    private AutoCompleteTextView paymentMethodAutoComplete;
    private TextInputEditText quantityEditText;
    private TextInputEditText priceEditText;
    private MaterialButton addItemButton;
    private MaterialButton submitButton;
    private MaterialButton printButton;
    private RecyclerView invoiceItemsRecyclerView;
    private TextView totalTextView;

    private CustomerSearchAdapter customerSearchAdapter;
    private CommoditySearchAdapter commoditySearchAdapter;
    private InvoiceItemAdapter invoiceItemAdapter;
    private ApiService apiService;

    private Person selectedPerson;
    private Commodity selectedCommodity;
    private List<InvoiceItem> invoiceItems;
    private String selectedPaymentMethod;

    private static final String[] PAYMENT_METHODS = {"پرداخت مستقیم", "پرداخت اعتباری"};

    private IPrinterApi printerApi;

    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    private static final int BARCODE_SCANNER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invoice);

        // راه‌اندازی SDK چاپگر
        initPrinter();

        apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        customerSearchAdapter = new CustomerSearchAdapter(this);
        commoditySearchAdapter = new CommoditySearchAdapter(this);
        invoiceItems = new ArrayList<>();
        invoiceItemAdapter = new InvoiceItemAdapter(this, invoiceItems, this);

        initializeViews();
        setupListeners();
        setupPaymentMethods();
        setupBarcodeScanner();
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

    private void initializeViews() {
        customerSearchAutoComplete = findViewById(R.id.customerSearchAutoComplete);
        commoditySearchAutoComplete = findViewById(R.id.commoditySearchAutoComplete);
        paymentMethodAutoComplete = findViewById(R.id.paymentMethodAutoComplete);
        quantityEditText = findViewById(R.id.quantityEditText);
        priceEditText = findViewById(R.id.priceEditText);
        addItemButton = findViewById(R.id.addItemButton);
        submitButton = findViewById(R.id.submitButton);
        printButton = findViewById(R.id.printButton);
        invoiceItemsRecyclerView = findViewById(R.id.invoiceItemsRecyclerView);
        totalTextView = findViewById(R.id.totalTextView);

        customerSearchAutoComplete.setAdapter(customerSearchAdapter);
        commoditySearchAutoComplete.setAdapter(commoditySearchAdapter);
        invoiceItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        invoiceItemsRecyclerView.setAdapter(invoiceItemAdapter);
    }

    private void setupPaymentMethods() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, PAYMENT_METHODS);
        paymentMethodAutoComplete.setAdapter(adapter);

        // تنظیم روش پرداخت پیش‌فرض
        selectedPaymentMethod = PAYMENT_METHODS[0]; // پرداخت مستقیم
        paymentMethodAutoComplete.setText(selectedPaymentMethod, false);

        paymentMethodAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            selectedPaymentMethod = PAYMENT_METHODS[position];
        });

        // اضافه کردن قابلیت کلیک برای نمایش لیست
        paymentMethodAutoComplete.setOnClickListener(v -> {
            paymentMethodAutoComplete.showDropDown();
        });
    }

    private void setupListeners() {
        customerSearchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    searchCustomers(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        customerSearchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            selectedPerson = customerSearchAdapter.getItem(position);
            if (selectedPerson != null) {
                customerSearchAutoComplete.setText(selectedPerson.getNikename());
                Toast.makeText(this, "مشتری انتخاب شد: " + selectedPerson.getNikename(), Toast.LENGTH_SHORT).show();
            }
        });

        commoditySearchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (s.length() >= 1) {
                            searchCommodities(s.toString());
                        }
                    }
                };
                searchHandler.postDelayed(searchRunnable, 300); // تاخیر 300 میلی‌ثانیه
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        commoditySearchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            selectedCommodity = commoditySearchAdapter.getItem(position);
            if (selectedCommodity != null) {
                commoditySearchAutoComplete.setText(selectedCommodity.getName());
                priceEditText.setText(String.valueOf(selectedCommodity.getPriceSell()));
                quantityEditText.requestFocus();
            }
        });

        addItemButton.setOnClickListener(v -> addItemToInvoice());
        submitButton.setOnClickListener(v -> submitInvoice());
        printButton.setOnClickListener(v -> printInvoice());
    }

    private void searchCustomers(String query) {
        PersonListRequest request = new PersonListRequest(1, 10, query, null, null, null);

        apiService.getPersonList(request).enqueue(new Callback<PersonListResponse>() {
            @Override
            public void onResponse(Call<PersonListResponse> call, Response<PersonListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    customerSearchAdapter.updateData(response.body().getItems());
                }
            }

            @Override
            public void onFailure(Call<PersonListResponse> call, Throwable t) {
                Toast.makeText(NewInvoiceActivity.this, "خطا در دریافت اطلاعات مشتریان", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchCommodities(String query) {
        CommoditySearchRequest request = new CommoditySearchRequest(1, 10, query, null);

        apiService.searchCommodities(request).enqueue(new Callback<List<Commodity>>() {
            @Override
            public void onResponse(Call<List<Commodity>> call, Response<List<Commodity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Commodity> commodities = response.body();
                    commoditySearchAdapter.updateData(commodities);
                    if (commodities.isEmpty()) {
                        Toast.makeText(NewInvoiceActivity.this, "کالایی یافت نشد", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewInvoiceActivity.this, "خطا در دریافت اطلاعات کالاها", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Commodity>> call, Throwable t) {
                Toast.makeText(NewInvoiceActivity.this, "خطا در دریافت اطلاعات کالاها", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItemToInvoice() {
        if (selectedCommodity == null) {
            Toast.makeText(this, "لطفا یک کالا انتخاب کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantityStr = quantityEditText.getText().toString();
        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "لطفا تعداد را وارد کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity = Double.parseDouble(quantityStr);
        if (quantity <= 0) {
            Toast.makeText(this, "تعداد باید بزرگتر از صفر باشد", Toast.LENGTH_SHORT).show();
            return;
        }

        String priceStr = priceEditText.getText().toString();
        if (priceStr.isEmpty()) {
            Toast.makeText(this, "لطفا قیمت را وارد کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        if (price < 0) {
            Toast.makeText(this, "قیمت نمی‌تواند منفی باشد", Toast.LENGTH_SHORT).show();
            return;
        }

        InvoiceItem item = new InvoiceItem(selectedCommodity, quantity);
        item.setPrice(price);
        invoiceItems.add(item);
        invoiceItemAdapter.updateData(invoiceItems);
        updateTotal();

        // Clear fields
        commoditySearchAutoComplete.setText("");
        quantityEditText.setText("");
        priceEditText.setText("");
        selectedCommodity = null;
    }

    private void updateTotal() {
        double total = 0;
        for (InvoiceItem item : invoiceItems) {
            total += item.getTotal();
        }
        totalTextView.setText(String.format(Locale.getDefault(), "%,.0f ریال", total));
    }

    private void submitInvoice() {
        if (selectedPerson == null) {
            Toast.makeText(this, "لطفا یک مشتری انتخاب کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        if (invoiceItems.isEmpty()) {
            Toast.makeText(this, "لطفا حداقل یک کالا به فاکتور اضافه کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPaymentMethod == null) {
            Toast.makeText(this, "لطفا روش پرداخت را انتخاب کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implement invoice submission
        Toast.makeText(this, "در حال ثبت فاکتور...", Toast.LENGTH_SHORT).show();
    }

    private void printInvoice() {
        if (selectedPerson == null) {
            Toast.makeText(this, "لطفا یک مشتری انتخاب کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        if (invoiceItems.isEmpty()) {
            Toast.makeText(this, "لطفا حداقل یک کالا به فاکتور اضافه کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPaymentMethod == null) {
            Toast.makeText(this, "لطفا روش پرداخت را انتخاب کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        if (printerApi == null) {
            Toast.makeText(this, "لطفاً صبر کنید تا چاپگر آماده شود", Toast.LENGTH_SHORT).show();
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
            printData.addTextLine(0, Alignment.CENTER, "=== فاکتور فروش ===", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);
            
            // تاریخ و زمان شمسی
            PersianDate persianDate = new PersianDate();
            String currentDate = persianDate.getShYear() + "/" + 
                               String.format("%02d", persianDate.getShMonth()) + "/" + 
                               String.format("%02d", persianDate.getShDay());
            String currentTime = String.format("%02d", persianDate.getHour()) + ":" + 
                               String.format("%02d", persianDate.getMinute()) + ":" + 
                               String.format("%02d", persianDate.getSecond());
            
            printData.addTextLine(0, Alignment.RIGHT, "تاریخ: " + currentDate, true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, "ساعت: " + currentTime, true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);

            // اطلاعات مشتری
            printData.addTextLine(0, Alignment.RIGHT, "مشتری: " + selectedPerson.getName(), true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, "کد مشتری: " + selectedPerson.getCode(), true, vazirTypeface);
            if (selectedPerson.getTel() != null && !selectedPerson.getTel().isEmpty()) {
                printData.addTextLine(0, Alignment.RIGHT, "تلفن: " + selectedPerson.getTel(), true, vazirTypeface);
            }
            if (selectedPerson.getMobile() != null && !selectedPerson.getMobile().isEmpty()) {
                printData.addTextLine(0, Alignment.RIGHT, "موبایل: " + selectedPerson.getMobile(), true, vazirTypeface);
            }
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);

            // روش پرداخت
            printData.addTextLine(0, Alignment.RIGHT, "روش پرداخت: " + selectedPaymentMethod, true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);

            // عنوان بخش کالاها
            printData.addTextLine(0, Alignment.CENTER, "کالاهای فاکتور", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);

            // چاپ اقلام فاکتور
            double total = 0;
            for (int i = 0; i < invoiceItems.size(); i++) {
                InvoiceItem item = invoiceItems.get(i);
                
                // شماره ردیف و نام کالا
                printData.addTextLine(0, Alignment.RIGHT, String.format("%d. %s", i + 1, item.getCommodity().getName()), true, vazirTypeface);
                
                // تعداد و قیمت واحد
                printData.addTextLine(0, Alignment.RIGHT, String.format("تعداد: %s", String.format(Locale.getDefault(), "%.2f", item.getQuantity())), true, vazirTypeface);
                printData.addTextLine(0, Alignment.RIGHT, String.format("قیمت واحد: %,d ریال", (long)item.getPrice()), true, vazirTypeface);
                
                // جمع کل این آیتم
                printData.addTextLine(0, Alignment.RIGHT, String.format("جمع: %,d ریال", (long)item.getTotal()), true, vazirTypeface);
                
                // خط جداکننده بین آیتم‌ها
                if (i < invoiceItems.size() - 1) {
                    printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);
                }
                
                total += item.getTotal();
            }

            // جمع کل
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);
            printData.addTextLine(0, Alignment.RIGHT, String.format("جمع کل: %,d ریال", (long)total), true, vazirTypeface);

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
                        Toast.makeText(NewInvoiceActivity.this, "فاکتور با موفقیت چاپ شد", Toast.LENGTH_SHORT).show();
                    });
                    printerApi.closePrinter();
                }

                @Override
                public void onError(SdkResultCode sdkResultCode) {
                    runOnUiThread(() -> {
                        Toast.makeText(NewInvoiceActivity.this, "خطا در چاپ: " + sdkResultCode.name(), Toast.LENGTH_LONG).show();
                    });
                    printerApi.closePrinter();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "خطا در چاپ: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupBarcodeScanner() {
        MaterialButton scanBarcodeButton = findViewById(R.id.scanBarcodeButton);
        scanBarcodeButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("بارکد یا کیوآرکد کالا را اسکن کنید");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.setOrientationLocked(true);
            integrator.setCaptureActivity(CustomCaptureActivity.class);
            integrator.initiateScan();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "اسکن لغو شد", Toast.LENGTH_LONG).show();
            } else {
                String scannedCode = result.getContents();
                commoditySearchAutoComplete.setText(scannedCode);
                searchCommodities(scannedCode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (printerApi != null) {
            printerApi.closePrinter();
        }
        if (searchHandler != null) {
            searchHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onEditClick(InvoiceItem item, int position) {
        showEditDialog(item, position);
    }

    @Override
    public void onDeleteClick(InvoiceItem item, int position) {
        new AlertDialog.Builder(this)
            .setTitle("حذف آیتم")
            .setMessage("آیا از حذف این آیتم اطمینان دارید؟")
            .setPositiveButton("بله", (dialog, which) -> {
                invoiceItems.remove(position);
                invoiceItemAdapter.updateData(invoiceItems);
                updateTotal();
            })
            .setNegativeButton("خیر", null)
            .show();
    }

    private void showEditDialog(InvoiceItem item, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_invoice_item, null);
        TextInputEditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        TextInputEditText priceEditText = dialogView.findViewById(R.id.priceEditText);

        quantityEditText.setText(String.valueOf(item.getQuantity()));
        priceEditText.setText(String.valueOf(item.getPrice()));

        new AlertDialog.Builder(this)
            .setTitle("ویرایش آیتم")
            .setView(dialogView)
            .setPositiveButton("ذخیره", (dialog, which) -> {
                String quantityStr = quantityEditText.getText().toString();
                String priceStr = priceEditText.getText().toString();

                if (!quantityStr.isEmpty() && !priceStr.isEmpty()) {
                    double quantity = Double.parseDouble(quantityStr);
                    double price = Double.parseDouble(priceStr);

                    if (quantity > 0 && price >= 0) {
                        item.setQuantity(quantity);
                        item.setPrice(price);
                        invoiceItemAdapter.updateData(invoiceItems);
                        updateTotal();
                    } else {
                        Toast.makeText(this, "مقادیر نامعتبر", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("انصراف", null)
            .show();
    }
} 