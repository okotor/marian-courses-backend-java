package com.tehacko.backend_java.dto;

public class ResponseWrapper<T> {

    private boolean success;
    private T data;
    private String error;

    public ResponseWrapper(boolean success, T data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}