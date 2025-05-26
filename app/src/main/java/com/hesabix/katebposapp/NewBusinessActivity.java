package com.hesabix.katebposapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hesabix.katebposapp.retrofit.ApiService;
import com.hesabix.katebposapp.retrofit.Currency;
import com.hesabix.katebposapp.retrofit.CurrencyResponse;
import com.hesabix.katebposapp.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewBusinessActivity extends AppCompatActivity {

    private TextInputEditText nameInput, legalNameInput, addressInput, taxInput;
    private TextInputEditText financialYearStartInput, financialYearEndInput, financialYearLabelInput;
    private AutoCompleteTextView fieldInput, typeInput, currencyInput;
    private MaterialButton submitButton;
    private ApiService apiService;
    private List<Currency> currencies = new ArrayList<>();
    private TextInputEditText currentDateInput;
    private Dialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_business);
        
        setupViews();
        setupRetrofit();
        loadCurrencies();
        setupDropdowns();
        setupDatePickers();
        setupSubmitButton();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupViews() {
        nameInput = findViewById(R.id.nameInput);
        legalNameInput = findViewById(R.id.legalNameInput);
        fieldInput = findViewById(R.id.fieldInput);
        typeInput = findViewById(R.id.typeInput);
        addressInput = findViewById(R.id.addressInput);
        currencyInput = findViewById(R.id.currencyInput);
        taxInput = findViewById(R.id.taxInput);
        financialYearStartInput = findViewById(R.id.financialYearStartInput);
        financialYearEndInput = findViewById(R.id.financialYearEndInput);
        financialYearLabelInput = findViewById(R.id.financialYearLabelInput);
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupRetrofit() {
        apiService = RetrofitClient.getInstance(this).create(ApiService.class);
    }

    private void loadCurrencies() {
        apiService.getCurrencies().enqueue(new Callback<CurrencyResponse>() {
            @Override
            public void onResponse(@NonNull Call<CurrencyResponse> call, @NonNull Response<CurrencyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currencies = response.body().getData();
                    setupCurrencyDropdown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CurrencyResponse> call, @NonNull Throwable t) {
                Toast.makeText(NewBusinessActivity.this, "خطا در دریافت لیست ارزها", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDropdowns() {
        String[] fields = {"خدماتی", "فروشگاهی", "تولیدی", "سایر"};
        String[] types = {"مغازه", "شرکت", "کارگاه", "سایر"};

        ArrayAdapter<String> fieldAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, fields);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, types);

        fieldInput.setAdapter(fieldAdapter);
        typeInput.setAdapter(typeAdapter);
    }

    private void setupCurrencyDropdown() {
        List<String> currencyLabels = new ArrayList<>();
        for (Currency currency : currencies) {
            currencyLabels.add(currency.getLabel());
        }
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, currencyLabels);
        currencyInput.setAdapter(currencyAdapter);
    }

    private void setupDatePickers() {
        financialYearStartInput.setOnClickListener(v -> {
            currentDateInput = financialYearStartInput;
            showPersianDatePicker();
        });

        financialYearEndInput.setOnClickListener(v -> {
            currentDateInput = financialYearEndInput;
            showPersianDatePicker();
        });
    }

    private void showPersianDatePicker() {
        datePickerDialog = new Dialog(this);
        datePickerDialog.setContentView(R.layout.dialog_date_picker);
        Objects.requireNonNull(datePickerDialog.getWindow()).setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );

        DatePicker datePicker = datePickerDialog.findViewById(R.id.datePicker);
        MaterialButton confirmButton = datePickerDialog.findViewById(R.id.confirmButton);
        MaterialButton cancelButton = datePickerDialog.findViewById(R.id.cancelButton);

        Calendar calendar = Calendar.getInstance();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
            calendar.get(Calendar.DAY_OF_MONTH), (view, year, month, dayOfMonth) -> {
                @SuppressLint("DefaultLocale") String date = String.format("%04d/%02d/%02d", year, month + 1, dayOfMonth);
                if (currentDateInput != null) {
                    currentDateInput.setText(date);
                }
            });

        confirmButton.setOnClickListener(v -> datePickerDialog.dismiss());
        cancelButton.setOnClickListener(v -> datePickerDialog.dismiss());

        datePickerDialog.show();
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> submitBusiness());
    }

    private void submitBusiness() {
        String name = Objects.requireNonNull(nameInput.getText()).toString();
        String legalName = Objects.requireNonNull(legalNameInput.getText()).toString();
        String field = fieldInput.getText().toString();
        String type = typeInput.getText().toString();
        String address = Objects.requireNonNull(addressInput.getText()).toString();
        String currencyName = currencyInput.getText().toString();
        String taxStr = Objects.requireNonNull(taxInput.getText()).toString();
        String financialYearStart = Objects.requireNonNull(financialYearStartInput.getText()).toString();
        String financialYearEnd = Objects.requireNonNull(financialYearEndInput.getText()).toString();
        String financialYearLabel = Objects.requireNonNull(financialYearLabelInput.getText()).toString();

        if (name.isEmpty() || legalName.isEmpty() || field.isEmpty() || type.isEmpty() || 
            address.isEmpty() || currencyName.isEmpty() || taxStr.isEmpty() ||
            financialYearStart.isEmpty() || financialYearEnd.isEmpty() || financialYearLabel.isEmpty()) {
            Toast.makeText(this, "لطفا تمام فیلدها را پر کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        Currency selectedCurrency = null;
        for (Currency currency : currencies) {
            if (currency.getLabel().equals(currencyName)) {
                selectedCurrency = currency;
                break;
            }
        }

        if (selectedCurrency == null) {
            Toast.makeText(this, "لطفا واحد پول را انتخاب کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject businessData = new JSONObject();
            businessData.put("name", name);
            businessData.put("legal_name", legalName);
            businessData.put("field", field);
            businessData.put("type", type);
            businessData.put("address", address);
            businessData.put("maliyatafzode", Integer.parseInt(taxStr));
            businessData.put("financial_year_start", financialYearStart);
            businessData.put("financial_year_end", financialYearEnd);
            businessData.put("financial_year_label", financialYearLabel);
            
            JSONObject currencyObj = new JSONObject();
            currencyObj.put("name", selectedCurrency.getName());
            currencyObj.put("label", selectedCurrency.getLabel());
            businessData.put("arzmain", currencyObj);

            apiService.insertBusiness(businessData).enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(@NonNull Call<JSONObject> call, @NonNull Response<JSONObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject result = response.body();
                            if (result.has("result") && result.getInt("result") == 1) {
                                Toast.makeText(NewBusinessActivity.this, "کسب و کار با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(NewBusinessActivity.this, "خطا در ثبت کسب و کار", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(NewBusinessActivity.this, "خطا در پردازش پاسخ سرور", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable t) {
                    Toast.makeText(NewBusinessActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            Toast.makeText(this, "خطا در ساخت داده‌ها", Toast.LENGTH_SHORT).show();
        }
    }
}