package com.rizieq.drinkshop.Model;

public class CheckUserResponse {

    private boolean exits;
    private String error_msg;

    public CheckUserResponse() {

    }

    public boolean isExits() {
        return exits;
    }

    public void setExits(boolean exits) {
        this.exits = exits;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
