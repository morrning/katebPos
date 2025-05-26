package com.hesabix.katebposapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.hesabix.katebposapp.databinding.ActivityPrinterTestBinding;
import com.smartpos.sdk.SdkManager;
import com.smartpos.sdk.api.IPrinterApi;
import com.smartpos.sdk.api.PrinterListener;
import com.smartpos.sdk.api.SdkListener;
import com.smartpos.sdk.constant.Alignment;
import com.smartpos.sdk.constant.SdkResultCode;
import com.smartpos.sdk.entity.PrintData;
import com.smartpos.sdk.entity.SdkResult;

public class PrinterTestActivity extends AppCompatActivity {
    private ActivityPrinterTestBinding binding;
    private IPrinterApi printerApi;
    private static final int MAX_WIDTH = 0x17e;
    private static final int DEFAULT_FEED = 23 * 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrinterTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // راه‌اندازی SDK
        initSdk();

        // تنظیم دکمه چاپ تست
        binding.btnTestPrint.setOnClickListener(v -> printTest());
    }

    private void initSdk() {
        SdkManager.getInstance().init(getApplicationContext(), new SdkListener() {
            @Override
            public void onResult(SdkResult sdkResult) {
                if (sdkResult.isSuccess()) {
                    printerApi = SdkManager.getInstance().getPrinterManager();
                    // بررسی وضعیت چاپگر
                    checkPrinterStatus();
                } else {
                    binding.tvPrinterStatus.setText("وضعیت چاپگر: خطا در راه‌اندازی");
                }
            }
        });
    }

    private void checkPrinterStatus() {
        if (printerApi != null) {
            try {
                SdkResult<Integer> result = printerApi.getPrinterStatus();
                if (result != null && result.isSuccess()) {
                    Integer status = result.getData();
                    if (status != null) {
                        switch (status) {
                            case 0:
                                binding.tvPrinterStatus.setText("وضعیت چاپگر: آماده");
                                break;
                            case 1:
                                binding.tvPrinterStatus.setText("وضعیت چاپگر: کاغذ تمام شده");
                                break;
                            case 2:
                                binding.tvPrinterStatus.setText("وضعیت چاپگر: درب چاپگر باز است");
                                break;
                            case 3:
                                binding.tvPrinterStatus.setText("وضعیت چاپگر: خطا در چاپگر");
                                break;
                            default:
                                binding.tvPrinterStatus.setText("وضعیت چاپگر: نامشخص (" + status + ")");
                                break;
                        }
                    } else {
                        binding.tvPrinterStatus.setText("وضعیت چاپگر: آماده (بدون وضعیت)");
                    }
                } else {
                    binding.tvPrinterStatus.setText("وضعیت چاپگر: آماده (بدون خطا)");
                }
            } catch (Exception e) {
                e.printStackTrace();
                binding.tvPrinterStatus.setText("وضعیت چاپگر: آماده (بدون خطا)");
            }
        } else {
            binding.tvPrinterStatus.setText("وضعیت چاپگر: در انتظار اتصال");
        }
    }

    private void printTest() {
        if (printerApi == null) {
            Toast.makeText(this, "لطفاً صبر کنید تا چاپگر آماده شود", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // آماده‌سازی چاپگر
            printerApi.initPrinter();
            
            // تنظیم سطح خاکستری چاپگر (0-15)
            printerApi.setGrayLevel(12); // افزایش سطح خاکستری برای کیفیت بهتر

            // ایجاد داده‌های چاپ
            PrintData printData = new PrintData();
            
            // تنظیم فونت وزیر
            Typeface vazirTypeface = ResourcesCompat.getFont(this, R.font.vazir);
            printData.setFont(vazirTypeface);


            // اضافه کردن متن با فونت وزیر
            printData.addTextLine(0, Alignment.CENTER, "=== تست چاپگر ===", true, vazirTypeface);
            
            // نمایش تاریخ و زمان با استفاده از SimpleDateFormat
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault());
            java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault());
            String currentDate = dateFormat.format(new java.util.Date());
            String currentTime = timeFormat.format(new java.util.Date());
            
            printData.addTextLine(0, Alignment.CENTER, "تاریخ: " + currentDate, true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "ساعت: " + currentTime, true, vazirTypeface);

            printData.addTextLine(0, Alignment.CENTER, "حسابداری کاتب", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "https://ikateb.ir", true, vazirTypeface);

            // اضافه کردن متن فارسی با فونت وزیر
            printData.addTextLine(0, Alignment.CENTER, "این یک متن تست است", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "برای آزمایش چاپگر", true, vazirTypeface);

            // اضافه کردن متن جدید با فونت وزیر
            printData.addTextLine(0, Alignment.CENTER, "این روزها مردم قیمت همه‌چیز را می‌دانند،", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "اما ارزش هیچ‌‌چیز را نمی‌دانند.", true, vazirTypeface);

            // اضافه کردن جدول با فونت وزیر
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "| آیتم | قیمت |", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "| تست 1 | 1000 |", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "| تست 2 | 2000 |", true, vazirTypeface);
            printData.addTextLine(0, Alignment.CENTER, "------------------", true, vazirTypeface);

            // اضافه کردن خط خوراک با فاصله کمتر
            printData.feedLine(5); // کاهش فاصله به حداقل

            // شروع چاپ
            printerApi.startPrint(printData, new PrinterListener() {
                @Override
                public void onComplete() {
                    runOnUiThread(() -> {
                        binding.tvPrintResult.setText("چاپ با موفقیت انجام شد");
                        checkPrinterStatus(); // بررسی وضعیت چاپگر بعد از چاپ
                    });
                    printerApi.closePrinter();
                }

                @Override
                public void onError(SdkResultCode sdkResultCode) {
                    runOnUiThread(() -> {
                        binding.tvPrintResult.setText("خطا در چاپ: " + sdkResultCode.name());
                        checkPrinterStatus(); // بررسی وضعیت چاپگر در صورت خطا
                    });
                    printerApi.closePrinter();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            binding.tvPrintResult.setText("خطا در چاپ: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (printerApi != null) {
            printerApi.closePrinter();
        }
        binding = null;
    }
} 