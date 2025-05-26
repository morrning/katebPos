package com.hesabix.katebposapp.retrofit;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("Success")
    private Boolean success;

    @SerializedName("code")
    private Integer code;

    @SerializedName("data")
    private Data data;

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    @SerializedName("attempts")
    private Integer attempts;

    @SerializedName("last_attempt_time")
    private Long lastAttemptTime;

    @SerializedName("session_id")
    private String sessionId;

    public boolean isSuccess() {
        return success != null && success;
    }

    public Integer getCode() {
        return code;
    }

    public Data getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public boolean isCaptchaRequired() {
        return data != null && data.isCaptchaRequired();
    }

    public Integer getAttempts() {
        return attempts;
    }

    public Long getLastAttemptTime() {
        return lastAttemptTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public static class Data {
        @SerializedName("user")
        private String user;

        @SerializedName("token")
        private String token;

        @SerializedName("tokenID")
        private String tokenID;

        @SerializedName("active")
        private Boolean active;

        @SerializedName("captcha_required")
        private Boolean captchaRequired;

        public String getUser() {
            return user;
        }

        public String getToken() {
            return token;
        }

        public String getTokenID() {
            return tokenID;
        }

        public boolean isActive() {
            return active != null && active;
        }

        public boolean isCaptchaRequired() {
            return captchaRequired != null && captchaRequired;
        }
    }
}