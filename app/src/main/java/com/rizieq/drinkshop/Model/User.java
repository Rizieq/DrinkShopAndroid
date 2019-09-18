package com.rizieq.drinkshop.Model;

public class User {

    private String Phone;
    private String Address;
    private String Name;
    private String Brithdate;
    private String error_msg;
    private String avatarUrl;
    private String Password;

    public User() {

    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBrithdate() {
        return Brithdate;
    }

    public void setBrithdate(String brithdate) {
        Brithdate = brithdate;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
