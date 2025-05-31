package com.hesabix.katebposapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hesabix.katebposapp.model.Person;
import com.hesabix.katebposapp.model.PersonAccount;
import com.hesabix.katebposapp.model.PersonPreLabel;
import com.hesabix.katebposapp.model.PersonType;
import com.hesabix.katebposapp.retrofit.ApiService;
import com.hesabix.katebposapp.retrofit.RetrofitClient;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonEditActivity extends AppCompatActivity {
    public static final String EXTRA_PERSON_ID = "extra_person_id";
    public static final String EXTRA_PERSON_CODE = "extra_person_code";
    private static final String TAG = "PersonEditActivity";

    private TextInputEditText editNikename, editName, editTel, editMobile, editMobile2;
    private TextInputEditText editAddress, editCompany;
    private AutoCompleteTextView spinnerPrelabel;
    private LinearLayout layoutTypes;
    private Button btnSave;
    private List<PersonType> personTypes = new ArrayList<>();
    private List<PersonPreLabel> preLabels = new ArrayList<>();
    private long personId = -1;
    private String personCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_edit);

        // تنظیم تولبار
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // مقداردهی اولیه ویوها
        initViews();

        // دریافت شناسه و کد شخص از اکتیویتی قبلی (اگر در حالت ویرایش باشیم)
        personId = getIntent().getLongExtra(EXTRA_PERSON_ID, -1);
        personCode = getIntent().getStringExtra(EXTRA_PERSON_CODE);
        if (personId != -1 && personCode != null) {
            // حالت ویرایش
            getSupportActionBar().setTitle("ویرایش شخص");
            loadPersonInfo();
        } else {
            // حالت افزودن
            getSupportActionBar().setTitle("افزودن شخص جدید");
        }

        // دریافت لیست عناوین
        loadPreLabels();

        // دریافت لیست انواع شخص
        loadPersonTypes();

        // تنظیم کلیک روی دکمه ذخیره
        btnSave.setOnClickListener(v -> savePerson());
    }

    private void initViews() {
        editNikename = findViewById(R.id.edit_nikename);
        editName = findViewById(R.id.edit_name);
        editTel = findViewById(R.id.edit_tel);
        editMobile = findViewById(R.id.edit_mobile);
        editMobile2 = findViewById(R.id.edit_mobile2);
        editAddress = findViewById(R.id.edit_address);
        editCompany = findViewById(R.id.edit_company);
        spinnerPrelabel = findViewById(R.id.spinner_prelabel);
        layoutTypes = findViewById(R.id.layout_types);
        btnSave = findViewById(R.id.btn_save);
    }

    private void loadPreLabels() {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        Call<List<PersonPreLabel>> call = apiService.getPersonPreLabels();
        call.enqueue(new Callback<List<PersonPreLabel>>() {
            @Override
            public void onResponse(Call<List<PersonPreLabel>> call, Response<List<PersonPreLabel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    preLabels = response.body();
                    setupPreLabelsSpinner();
                }
            }

            @Override
            public void onFailure(Call<List<PersonPreLabel>> call, Throwable t) {
                Log.e(TAG, "خطا در دریافت لیست عناوین", t);
            }
        });
    }

    private void setupPreLabelsSpinner() {
        List<String> labels = new ArrayList<>();
        for (PersonPreLabel label : preLabels) {
            labels.add(label.getLabel());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, labels);
        spinnerPrelabel.setAdapter(adapter);
    }

    private void loadPersonTypes() {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        Call<List<PersonType>> call = apiService.getPersonTypes();
        call.enqueue(new Callback<List<PersonType>>() {
            @Override
            public void onResponse(Call<List<PersonType>> call, Response<List<PersonType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    personTypes = response.body();
                    setupTypeCheckboxes();
                }
            }

            @Override
            public void onFailure(Call<List<PersonType>> call, Throwable t) {
                Log.e(TAG, "خطا در دریافت لیست انواع شخص", t);
            }
        });
    }

    private void setupTypeCheckboxes() {
        layoutTypes.removeAllViews();
        for (PersonType type : personTypes) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(type.getLabel());
            checkBox.setTag(type);
            checkBox.setChecked(type.isChecked());
            layoutTypes.addView(checkBox);
        }
    }

    private void loadPersonInfo() {
        Log.d(TAG, "شروع دریافت اطلاعات شخص با کد: " + personCode);
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        Call<Person> call = apiService.getPersonInfo(personCode);
        call.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                Log.d(TAG, "پاسخ دریافت شد - کد: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Person person = response.body();
                    Log.d(TAG, "اطلاعات شخص دریافت شد: " + person.getNikename());
                    Log.d(TAG, "اطلاعات کامل شخص: " + new Gson().toJson(person));
                    fillPersonData(person);
                } else {
                    String errorMessage = "خطا در دریافت اطلاعات شخص";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "متن خطای سرور: " + errorBody);
                            errorMessage += ": " + errorBody;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "خطا در خواندن متن خطا", e);
                    }
                    Log.e(TAG, "کد خطای سرور: " + response.code());
                    Toast.makeText(PersonEditActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                Log.e(TAG, "خطا در اتصال به سرور", t);
                String errorMessage = "خطا در اتصال به سرور";
                if (t instanceof java.net.UnknownHostException) {
                    errorMessage = "خطا در اتصال به اینترنت. لطفاً اتصال خود را بررسی کنید.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMessage = "زمان اتصال به سرور به پایان رسید. لطفاً دوباره تلاش کنید.";
                }
                Toast.makeText(PersonEditActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fillPersonData(Person person) {
        try {
            Log.d(TAG, "شروع پر کردن اطلاعات در فرم");
            
            // پر کردن فیلدهای اصلی
            editNikename.setText(person.getNikename() != null ? person.getNikename() : "");
            editName.setText(person.getName() != null ? person.getName() : "");
            editTel.setText(person.getTel() != null ? person.getTel() : "");
            editMobile.setText(person.getMobile() != null ? person.getMobile() : "");
            editMobile2.setText(person.getMobile2() != null ? person.getMobile2() : "");
            editAddress.setText(person.getAddress() != null ? person.getAddress() : "");
            editCompany.setText(person.getCompany() != null ? person.getCompany() : "");

            // تنظیم عنوان
            if (person.getPrelabel() != null) {
                Log.d(TAG, "تنظیم عنوان: " + person.getPrelabel());
                for (PersonPreLabel label : preLabels) {
                    if (label.getCode().equals(person.getPrelabel())) {
                        spinnerPrelabel.setText(label.getLabel(), false);
                        break;
                    }
                }
            }

            // تنظیم انواع شخص
            if (person.getTypes() != null) {
                Log.d(TAG, "تعداد انواع شخص: " + person.getTypes().size());
                for (int i = 0; i < layoutTypes.getChildCount(); i++) {
                    CheckBox checkBox = (CheckBox) layoutTypes.getChildAt(i);
                    PersonType type = (PersonType) checkBox.getTag();
                    for (PersonType personType : person.getTypes()) {
                        if (personType.getCode().equals(type.getCode())) {
                            checkBox.setChecked(personType.isChecked());
                            break;
                        }
                    }
                }
            } else {
                Log.d(TAG, "لیست انواع شخص خالی است");
            }
            
            Log.d(TAG, "پایان پر کردن اطلاعات در فرم");
        } catch (Exception e) {
            Log.e(TAG, "خطا در پر کردن اطلاعات در فرم", e);
            Toast.makeText(this, "خطا در نمایش اطلاعات", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePerson() {
        // ساخت آبجکت شخص
        Person person = new Person();
        person.setNikename(editNikename.getText().toString());
        person.setName(editName.getText().toString());
        person.setTel(editTel.getText().toString());
        person.setMobile(editMobile.getText().toString());
        person.setMobile2(editMobile2.getText().toString());
        person.setAddress(editAddress.getText().toString());
        person.setCompany(editCompany.getText().toString());

        // تنظیم عنوان
        String selectedLabel = spinnerPrelabel.getText().toString();
        for (PersonPreLabel label : preLabels) {
            if (label.getLabel().equals(selectedLabel)) {
                person.setPrelabel(label.getCode());
                break;
            }
        }

        // تنظیم انواع شخص
        List<PersonType> selectedTypes = new ArrayList<>();
        for (int i = 0; i < layoutTypes.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) layoutTypes.getChildAt(i);
            if (checkBox.isChecked()) {
                PersonType type = (PersonType) checkBox.getTag();
                type.setChecked(true);
                selectedTypes.add(type);
            }
        }
        person.setTypes(selectedTypes);

        // ارسال به سرور
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        Call<Person> call;
        if (personId != -1 && personCode != null) {
            // ویرایش
            call = apiService.updatePerson(personCode, person);
        } else {
            // افزودن
            call = apiService.createPerson(person);
        }

        call.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PersonEditActivity.this, "اطلاعات با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "خطا در ذخیره اطلاعات";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "متن خطای سرور: " + errorBody);
                            errorMessage += ": " + errorBody;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "خطا در خواندن متن خطا", e);
                    }
                    Log.e(TAG, "کد خطای سرور: " + response.code());
                    Toast.makeText(PersonEditActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                Log.e(TAG, "خطا در ارسال اطلاعات", t);
                String errorMessage = "خطا در ارسال اطلاعات";
                if (t instanceof java.net.UnknownHostException) {
                    errorMessage = "خطا در اتصال به اینترنت. لطفاً اتصال خود را بررسی کنید.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMessage = "زمان اتصال به سرور به پایان رسید. لطفاً دوباره تلاش کنید.";
                }
                Toast.makeText(PersonEditActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
} 