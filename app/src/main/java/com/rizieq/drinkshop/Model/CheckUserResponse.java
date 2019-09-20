package com.rizieq.drinkshop.Model;

public class CheckUserResponse {

    private String exists;
    private String error_msg;

    public CheckUserResponse() {

    }

    public String getExists() {
        return exists;
    }

    public void setExists(String exists) {
        this.exists = exists;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
