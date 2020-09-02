package com.taveeshsharma.requesthandler.utils;

public class ApiError {
    private String error;
    private String code;
    private String detail;

    public ApiError() {
    }

    public ApiError(String error, String code, String detail) {
        this.error = error;
        this.code = code;
        this.detail = detail;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
