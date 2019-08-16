package com.rizieq.drinkshop.Model;

public class User {

    private String phone;
    private String address;
    private String name;
    private String brithdate;
    private String error_msg;

    public User() {

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrithdate() {
        return brithdate;
    }

    public void setBrithdate(String brithdate) {
        this.brithdate = brithdate;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
