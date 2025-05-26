package com.hesabix.katebposapp.retrofit;

public class LoginRequest {
    private String mobile;
    private String password;
    private boolean standard;
    private String captcha_answer;

    public LoginRequest(String mobile, String password, boolean standard, String captcha_answer) {
        this.mobile = mobile;
        this.password = password;
        this.standard = standard;
        this.captcha_answer = captcha_answer;
    }
}