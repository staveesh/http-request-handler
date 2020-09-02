package com.taveeshsharma.requesthandler.utils;

public enum ApiErrorCode {

    API001("API001", "Invalid request type"),
    API002("API002", "Invalid measurement type"),
    API003("API003", "Invalid start/end date"),
    API004("API004", "User with the same role already exists."),
    API005("API005", "Invalid roles supplied"),
    API006("API006", "Incorrect credentials provided. Please login again");

    private String errorCode;
    private String errorMessage;

    private ApiErrorCode(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ApiErrorCode{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                "} ";
    }
}
