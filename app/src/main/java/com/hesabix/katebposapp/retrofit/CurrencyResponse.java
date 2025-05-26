package com.hesabix.katebposapp.retrofit;

import java.util.List;

public class CurrencyResponse {
    private boolean Success;
    private int code;
    private List<Currency> data;
    private String message;

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Currency> getData() {
        return data;
    }

    public void setData(List<Currency> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 